package com.mezon.classmanagement.backend.common.util;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings(value = {WarningConstant.UNUSED, WarningConstant.SPELL_CHECKING_INSPECTION})
/**
 * Bộ giới hạn tốc độ <b>chặt</b> theo cửa sổ trượt (sliding window log): trong <i>bất kỳ</i> khoảng
 * {@code window} nào cũng có tối đa {@code maxCalls} lượt được cấp phép. Khác với token bucket hay
 * fixed window, không có chuyện "dồn gấp đôi" quanh ranh giới cửa sổ.
 *
 * <p><b>Cách hoạt động.</b> Một mảng vòng {@code long[maxCalls]} giữ thời điểm cấp phép của
 * {@code maxCalls} lượt gần nhất, nên lượt kế tiếp chỉ được phép sau {@code slots[head] + window}.
 * Mỗi lần gọi tốn O(1) thời gian và không cấp phát đối tượng. Lock chỉ được giữ để <i>đặt chỗ</i> một
 * thời điểm (có thể nằm trong tương lai); việc chờ diễn ra ngoài lock và mỗi luồng ngủ đúng đến thời
 * điểm của riêng mình, nên không có thundering herd và thứ tự cấp phép chính là thứ tự đặt chỗ (FIFO)
 * mà không cần lock công bằng.
 *
 * <p><b>Hành vi.</b>
 * <ul>
 *   <li>{@link #acquire()}: chặn đến khi có lượt; phản hồi interrupt.</li>
 *   <li>{@link #tryAcquire()}: không bao giờ chặn.</li>
 *   <li>{@link #tryAcquire(long, TimeUnit)}: nếu biết trước phải chờ quá hạn thì từ chối ngay và
 *       không tốn lượt nào.</li>
 *   <li>Luồng bị interrupt (hoặc limiter bị {@link #close() đóng}) khi đang chờ sẽ trả lại lượt đã đặt
 *       nếu chưa ai đặt chỗ sau nó; nếu đã có thì lượt đó bị bỏ phí. Limiter chỉ thêm thận trọng,
 *       không bao giờ nới lỏng.</li>
 *   <li>Dùng {@code ReentrantLock} và {@code LockSupport.parkNanos} nên không làm kẹt carrier thread
 *       của virtual thread.</li>
 * </ul>
 *
 * <p><b>Giới hạn cần biết.</b>
 * <ul>
 *   <li>Giới hạn tính trên thời điểm <i>cấp phép</i>. Độ trễ đánh thức luồng (thường dưới 1 ms) và
 *       khoảng từ lúc {@code acquire()} trả về đến lúc request thật sự rời đi không được bù. Nếu phía
 *       nhận đếm rất sát, hãy chừa biên (ví dụ 95 lượt/phút thay vì 100).</li>
 *   <li>Trạng thái nằm trong bộ nhớ của một JVM: chạy N bản sao ứng dụng thì tổng tốc độ là N lần.
 *       Muốn giới hạn chung cho nhiều instance phải dùng kho dùng chung như Redis.</li>
 * </ul>
 *
 * <p>Cấu hình qua {@code mezon.rate-limiter.max-calls} (mặc định 100) và
 * {@code mezon.rate-limiter.window} (mặc định {@code 1m}); có thể tạo thêm instance riêng bằng
 * {@code new StrictRateLimiter(maxCalls, window)}.
 *
 * <pre>{@code
 * limiter.acquire();                                // gọi API ngoài: chờ đến khi có lượt
 * mezonClient.send(message);
 *
 * if (!limiter.tryAcquire(2, TimeUnit.SECONDS)) {   // trong controller: đừng để request treo cả phút
 *     throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
 * }
 * }</pre>
 */
@Component
public class StrictRateLimiter implements AutoCloseable {

	public static final int DEFAULT_MAX_CALLS = 100;
	public static final Duration DEFAULT_WINDOW = Duration.ofMinutes(1);

	// ---- Cấu hình (bất biến) ----
	private final int maxCalls;
	private final Duration window;
	private final long windowNanos;
	private final TimeSource time;

	// ---- Trạng thái được bảo vệ bởi lock ----
	private final ReentrantLock lock = new ReentrantLock();
	/** Thời điểm cấp phép của các lượt gần nhất. Đọc từ {@code head} vòng quanh: cũ nhất đến mới nhất (không giảm). */
	private final long[] slots;
	/** Vị trí slot cũ nhất, cũng là slot sẽ bị ghi đè ở lần đặt chỗ kế tiếp. */
	private int head;
	/** Tổng số lượt đã đặt chỗ; dùng để nhận ra "lượt mới nhất" khi hoàn trả. */
	private long sequence;

	// ---- Trạng thái không cần lock ----
	private volatile boolean closed;
	private final Set<Thread> waiters = ConcurrentHashMap.newKeySet();
	private final AtomicInteger waiting = new AtomicInteger();
	private final LongAdder grantedPermits = new LongAdder();
	private final LongAdder throttledCalls = new LongAdder();
	private final LongAdder rejectedCalls = new LongAdder();
	private final LongAdder waitedNanos = new LongAdder();

	/** Giá trị mặc định: 100 lượt mỗi phút, giống bản cũ. */
	public StrictRateLimiter() {
		this(DEFAULT_MAX_CALLS, DEFAULT_WINDOW);
	}

	/**
	 * @param maxCalls số lượt tối đa trong mỗi cửa sổ, tối thiểu 1
	 * @param window   độ dài cửa sổ trượt, phải lớn hơn 0
	 */
	@Autowired
	public StrictRateLimiter(
			@Value("${mezon.rate-limiter.max-calls:100}") int maxCalls,
			@Value("${mezon.rate-limiter.window:1m}") Duration window) {
		this(maxCalls, window, TimeSource.SYSTEM);
	}

	/** Dành cho test: cho phép thay đồng hồ và cách "ngủ" bằng bản giả lập. */
	public StrictRateLimiter(int maxCalls, Duration window, TimeSource time) {
		if (maxCalls < 1) {
			throw new IllegalArgumentException("maxCalls phải tối thiểu 1, nhận được: " + maxCalls);
		}
		Objects.requireNonNull(window, "window");
		if (window.isZero() || window.isNegative()) {
			throw new IllegalArgumentException("window phải lớn hơn 0, nhận được: " + window);
		}
		final long nanos;
		try {
			nanos = window.toNanos();
		} catch (ArithmeticException e) {
			throw new IllegalArgumentException("window quá lớn: " + window, e);
		}
		this.maxCalls = maxCalls;
		this.window = window;
		this.windowNanos = nanos;
		this.time = Objects.requireNonNull(time, "time");
		this.slots = new long[maxCalls];
		// Mọi slot "đã hết hạn" ngay từ đầu, nên burst đầu tiên được dùng đủ maxCalls lượt.
		Arrays.fill(slots, time.nanoTime() - nanos);
	}

	// ================================================================ lấy lượt (chặn)

	/**
	 * Chặn đến khi được cấp một lượt.
	 *
	 * @throws InterruptedException nếu luồng bị interrupt trước hoặc trong lúc chờ
	 * @throws IllegalStateException nếu limiter đã đóng
	 */
	public void acquire() throws InterruptedException {
		acquire(1);
	}

	/**
	 * Chặn đến khi được cấp {@code permits} lượt cùng một lúc (ví dụ API tính phí nhiều lượt).
	 *
	 * @param permits số lượt, từ 1 đến {@code maxCalls}
	 */
	public void acquire(int permits) throws InterruptedException {
		acquireInternal(permits, Long.MAX_VALUE);
	}

	// =============================================================== lấy lượt (không chặn)

	/** Lấy một lượt nếu có sẵn ngay; không bao giờ chặn. */
	public boolean tryAcquire() {
		return tryAcquire(1);
	}

	/** Lấy {@code permits} lượt nếu có sẵn ngay; không bao giờ chặn. */
	public boolean tryAcquire(int permits) {
		checkPermits(permits);
		lock.lock();
		try {
			ensureOpen();
			final long now = time.nanoTime();
			if (earliestGrant(permits, now) != now) {
				rejectedCalls.increment();
				return false;
			}
			commit(permits, now, null);
			grantedPermits.add(permits);
			return true;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Chờ tối đa {@code timeout} để lấy một lượt. Nếu biết trước phải chờ lâu hơn thì trả về
	 * {@code false} ngay, không ngủ và không tốn lượt nào.
	 */
	public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
		return tryAcquire(1, timeout, unit);
	}

	/** Như {@link #tryAcquire(long, TimeUnit)} nhưng lấy {@code permits} lượt cùng lúc. */
	public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
		return acquireInternal(permits, Math.max(0L, unit.toNanos(timeout)));
	}

	// ===================================================================== quan sát

	/** Số lượt có thể lấy ngay lúc này mà không phải chờ (chỉ mang tính tham khảo, như {@code Semaphore}). */
	public int availablePermits() {
		lock.lock();
		try {
			return countAvailable(time.nanoTime());
		} finally {
			lock.unlock();
		}
	}

	/** Thời gian một lượt mới phải chờ nếu gọi ngay bây giờ; dùng làm {@code Retry-After} khi trả 429. */
	public Duration estimatedWait() {
		return estimatedWait(1);
	}

	/** Thời gian {@code permits} lượt mới phải chờ nếu gọi ngay bây giờ. */
	public Duration estimatedWait(int permits) {
		checkPermits(permits);
		lock.lock();
		try {
			final long now = time.nanoTime();
			return Duration.ofNanos(earliestGrant(permits, now) - now);
		} finally {
			lock.unlock();
		}
	}

	/** Ảnh chụp nhanh để đưa lên metrics hoặc health check. */
	public Stats stats() {
		final int available;
		final long wait;
		lock.lock();
		try {
			final long now = time.nanoTime();
			available = countAvailable(now);
			wait = earliestGrant(1, now) - now;
		} finally {
			lock.unlock();
		}
		return new Stats(
				maxCalls,
				window,
				available,
				Duration.ofNanos(wait),
				waiting.get(),
				grantedPermits.sum(),
				throttledCalls.sum(),
				rejectedCalls.sum(),
				Duration.ofNanos(waitedNanos.sum()));
	}

	/**
	 * @param maxCalls         số lượt tối đa mỗi cửa sổ
	 * @param window           độ dài cửa sổ
	 * @param availablePermits số lượt lấy được ngay lúc này
	 * @param estimatedWait    thời gian một lượt mới phải chờ lúc này
	 * @param waiting          số luồng đang bị chặn
	 * @param granted          tổng số lượt đã cấp
	 * @param throttled        số lần gọi phải chờ trước khi được cấp
	 * @param rejected         số lần {@code tryAcquire} bị từ chối
	 * @param totalWaited      tổng thời gian các lần gọi đã phải chờ
	 */
	public record Stats(
			int maxCalls,
			Duration window,
			int availablePermits,
			Duration estimatedWait,
			int waiting,
			long granted,
			long throttled,
			long rejected,
			Duration totalWaited) {
	}

	// ===================================================================== vòng đời

	/**
	 * Từ chối mọi lần lấy lượt mới và đánh thức các luồng đang chờ (chúng nhận
	 * {@link IllegalStateException}). Spring tự gọi khi tắt ứng dụng vì lớp này là {@code AutoCloseable}.
	 */
	@Override
	public void close() {
		closed = true;
		for (Thread t : waiters) {
			LockSupport.unpark(t);
		}
	}

	@Override
	public String toString() {
		return "StrictRateLimiter[" + maxCalls + " lượt / " + window + (closed ? ", đã đóng" : "") + "]";
	}

	// ===================================================================== nội bộ

	private boolean acquireInternal(int permits, long maxWaitNanos) throws InterruptedException {
		checkPermits(permits);
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}

		final Reservation reservation;
		lock.lock();
		try {
			ensureOpen();
			final long now = time.nanoTime();
			final long grantAt = earliestGrant(permits, now);
			final long wait = grantAt - now;

			if (wait > maxWaitNanos) {
				rejectedCalls.increment();
				return false;
			}
			if (wait == 0L) {
				commit(permits, now, null);
				grantedPermits.add(permits);
				return true;
			}
			reservation = new Reservation(permits, now, grantAt);
			commit(permits, grantAt, reservation);
		} finally {
			lock.unlock();
		}

		awaitGrant(reservation);
		return true;
	}

	/** Ngủ ngoài lock đến đúng thời điểm đã đặt chỗ. Nếu không thành công thì trả lại lượt. */
	private void awaitGrant(Reservation r) throws InterruptedException {
		final Thread self = Thread.currentThread();
		boolean success = false;
		waiters.add(self);
		waiting.incrementAndGet();
		try {
			for (;;) {
				final long remaining = r.grantAt - time.nanoTime();
				if (remaining <= 0L) {
					success = true;
					break;
				}
				if (closed) {
					throw closedException();
				}
				time.parkNanos(remaining); // có thể thức sớm (unpark, interrupt, spurious): vòng lặp kiểm tra lại
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
			}
		} finally {
			waiting.decrementAndGet();
			waiters.remove(self);
			if (!success) {
				refund(r);
			}
		}
		grantedPermits.add(r.permits);
		throttledCalls.increment();
		waitedNanos.add(Math.max(0L, time.nanoTime() - r.requestedAt));
	}

	/**
	 * Thời điểm sớm nhất (nano) mà {@code permits} lượt kế tiếp được phép; luôn không sớm hơn {@code now}.
	 * Chỉ so sánh bằng hiệu số vì {@code nanoTime()} có thể tràn số. Gọi khi đang giữ lock.
	 */
	private long earliestGrant(int permits, long now) {
		long grantAt = now;
		int idx = head;
		for (int i = 0; i < permits; i++) {
			final long free = slots[idx] + windowNanos;
			if (free - grantAt > 0L) {
				grantAt = free;
			}
			if (++idx == maxCalls) {
				idx = 0;
			}
		}
		return grantAt;
	}

	/** Ghi {@code grantAt} vào {@code permits} slot cũ nhất. Có {@code undo} thì lưu giá trị cũ để hoàn trả. */
	private void commit(int permits, long grantAt, Reservation undo) {
		int idx = head;
		for (int i = 0; i < permits; i++) {
			if (undo != null) {
				undo.previous[i] = slots[idx];
			}
			slots[idx] = grantAt;
			if (++idx == maxCalls) {
				idx = 0;
			}
		}
		head = idx;
		sequence += permits;
		if (undo != null) {
			undo.sequenceAfter = sequence;
		}
	}

	/** Trả lại lượt đã đặt chỗ, nhưng chỉ khi đó vẫn là lượt mới nhất (chưa ai đặt chỗ sau). */
	private void refund(Reservation r) {
		lock.lock();
		try {
			if (sequence != r.sequenceAfter) {
				return; // đã có người đặt chỗ sau: giữ nguyên, chỉ làm limiter thận trọng hơn
			}
			int idx = head;
			for (int i = r.permits - 1; i >= 0; i--) {
				if (--idx < 0) {
					idx = maxCalls - 1;
				}
				slots[idx] = r.previous[i];
			}
			head = idx;
			sequence -= r.permits;
		} finally {
			lock.unlock();
		}
	}

	/** Các slot đã hết hạn luôn nằm thành một đoạn liền ngay từ {@code head}. */
	private int countAvailable(long now) {
		int count = 0;
		int idx = head;
		while (count < maxCalls && slots[idx] + windowNanos - now <= 0L) {
			count++;
			if (++idx == maxCalls) {
				idx = 0;
			}
		}
		return count;
	}

	private void checkPermits(int permits) {
		if (permits < 1 || permits > maxCalls) {
			throw new IllegalArgumentException(
					"permits phải nằm trong khoảng [1, " + maxCalls + "], nhận được: " + permits);
		}
	}

	private void ensureOpen() {
		if (closed) {
			throw closedException();
		}
	}

	private static IllegalStateException closedException() {
		return new IllegalStateException("StrictRateLimiter đã bị đóng");
	}

	/** Một lần đặt chỗ phải chờ; chỉ được tạo ở đường chậm nên đường nhanh không cấp phát gì. */
	private static final class Reservation {
		final int permits;
		final long requestedAt;
		final long grantAt;
		final long[] previous;
		long sequenceAfter;

		Reservation(int permits, long requestedAt, long grantAt) {
			this.permits = permits;
			this.requestedAt = requestedAt;
			this.grantAt = grantAt;
			this.previous = new long[permits];
		}
	}

	/** Nguồn thời gian và cách "ngủ"; tách ra để test chạy tức thì và xác định. */
	public interface TimeSource {

		TimeSource SYSTEM = new TimeSource() {
			@Override
			public long nanoTime() {
				return System.nanoTime();
			}

			@Override
			public void parkNanos(long nanos) {
				LockSupport.parkNanos(nanos);
			}
		};

		long nanoTime();

		/** Có thể trả về sớm (unpark, interrupt, spurious wakeup), giống {@link LockSupport#parkNanos(long)}. */
		void parkNanos(long nanos);
	}
}