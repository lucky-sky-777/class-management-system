package com.mezon.classmanagement.backend;

import com.mezon.classmanagement.backend.common.util.StrictRateLimiter;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrictRateLimiterTest {

	private static final long SEC = TimeUnit.SECONDS.toNanos(1);

	/** Đồng hồ ảo: "ngủ" chỉ là tua thời gian tới, nên test chạy tức thì và hoàn toàn xác định. */
	private static final class FakeTime implements StrictRateLimiter.TimeSource {
		long now;
		long parks;
		boolean interruptOnNextPark;

		FakeTime(long start) {
			this.now = start;
		}

		@Override
		public long nanoTime() {
			return now;
		}

		@Override
		public void parkNanos(long nanos) {
			parks++;
			if (interruptOnNextPark) { // giả lập luồng bị interrupt giữa lúc đang ngủ
				interruptOnNextPark = false;
				Thread.currentThread().interrupt();
				return;
			}
			now += nanos;
		}
	}

	private static StrictRateLimiter limiter(int maxCalls, long windowNanos, FakeTime time) {
		return new StrictRateLimiter(maxCalls, Duration.ofNanos(windowNanos), time);
	}

	@Test
	void burstUpToMaxCallsIsImmediate() throws Exception {
		FakeTime t = new FakeTime(0);
		StrictRateLimiter limiter = limiter(5, SEC, t);

		for (int i = 0; i < 5; i++) {
			limiter.acquire();
		}

		assertEquals(0, t.parks);
		assertEquals(0, t.now);
	}

	@Test
	void callBeyondTheLimitWaitsUntilTheOldestLeavesTheWindow() throws Exception {
		FakeTime t = new FakeTime(0);
		StrictRateLimiter limiter = limiter(5, SEC, t);
		for (int i = 0; i < 5; i++) {
			limiter.acquire();
		}

		t.now = SEC / 4; // đã trôi 250 ms, còn thiếu 750 ms
		limiter.acquire();

		assertEquals(SEC, t.now);
		assertEquals(1, t.parks);
	}

	@Test
	void neverExceedsMaxCallsInAnySlidingWindowAndNeverWaitsLongerThanNecessary() throws Exception {
		final int maxCalls = 7;
		final long window = TimeUnit.MILLISECONDS.toNanos(500);
		// Bắt đầu sát Long.MAX_VALUE để nanoTime tràn số giữa chừng: so sánh phải dựa trên hiệu số.
		FakeTime t = new FakeTime(Long.MAX_VALUE - SEC);
		StrictRateLimiter limiter = limiter(maxCalls, window, t);
		Random random = new Random(42);

		final int total = 2_000;
		long[] grants = new long[total];
		for (int i = 0; i < total; i++) {
			t.now += random.nextInt(20_000_000); // 0 đến 20 ms giữa hai lần gọi: luôn quá tải
			long arrival = t.now;
			limiter.acquire();
			grants[i] = t.now;

			// Đáp án tham chiếu: không sớm hơn lúc gọi, và không sớm hơn lúc lượt thứ i-maxCalls rời cửa sổ.
			long expected = arrival;
			if (i >= maxCalls) {
				long free = grants[i - maxCalls] + window;
				if (free - expected > 0) {
					expected = free;
				}
			}
			assertEquals(expected, grants[i], "lượt " + i);
		}

		// Tính chất cần chứng minh: maxCalls + 1 lượt liên tiếp luôn trải dài ít nhất một cửa sổ.
		for (int i = 0; i + maxCalls < total; i++) {
			assertTrue(grants[i + maxCalls] - grants[i] >= window, "vi phạm cửa sổ tại lượt " + i);
		}
	}

	@Test
	void doesNotThrottleWhenCallsAreEvenlySpaced() throws Exception {
		FakeTime t = new FakeTime(0);
		StrictRateLimiter limiter = limiter(5, SEC, t);

		for (int i = 0; i < 1_000; i++) {
			limiter.acquire();
			t.now += SEC / 5; // đúng nhịp window / maxCalls
		}

		assertEquals(0, t.parks);
	}

	@Test
	void tryAcquireNeverBlocks() {
		FakeTime t = new FakeTime(0);
		StrictRateLimiter limiter = limiter(3, SEC, t);

		assertTrue(limiter.tryAcquire());
		assertTrue(limiter.tryAcquire());
		assertTrue(limiter.tryAcquire());
		assertFalse(limiter.tryAcquire());
		assertEquals(0, t.parks);

		t.now = SEC;
		assertTrue(limiter.tryAcquire());
	}

	@Test
	void tryAcquireWithTimeoutRejectsWithoutConsumingAPermit() throws Exception {
		FakeTime t = new FakeTime(0);
		StrictRateLimiter limiter = limiter(1, SEC, t);
		limiter.acquire();

		assertFalse(limiter.tryAcquire(500, TimeUnit.MILLISECONDS)); // phải chờ 1 s, lâu hơn 500 ms
		assertEquals(0, t.parks);

		assertTrue(limiter.tryAcquire(2, TimeUnit.SECONDS)); // chờ 1 s, nằm trong 2 s
		assertEquals(SEC, t.now); // đúng 1 s: lần bị từ chối ở trên không làm tốn lượt nào
	}

	@Test
	void weightedAcquireTakesSeveralPermitsAtOnce() throws Exception {
		FakeTime t = new FakeTime(0);
		StrictRateLimiter limiter = limiter(10, SEC, t);

		limiter.acquire(10); // dùng hết ngay lập tức
		assertEquals(0, t.now);

		limiter.acquire(4); // phải chờ cả một cửa sổ
		assertEquals(SEC, t.now);

		limiter.acquire(6); // 6 slot còn lại cũng hết hạn đúng lúc này
		assertEquals(SEC, t.now);
		assertEquals(1, t.parks);

		limiter.acquire(); // lượt kế tiếp phải đợi lô 4 lượt vừa rồi hết hạn
		assertEquals(2 * SEC, t.now);
	}

	@Test
	void weightedAcquireWaitsForTheLastSlotItNeeds() throws Exception {
		FakeTime t = new FakeTime(0);
		StrictRateLimiter limiter = limiter(4, SEC, t);
		for (int i = 0; i < 4; i++) {
			limiter.acquire(); // bốn lượt rải ở 0, 100, 200, 300 ms
			t.now += SEC / 10;
		}

		// Lúc này là 400 ms. Ba slot cũ nhất hết hạn lần lượt ở 1000, 1100, 1200 ms: phải đợi slot cuối.
		limiter.acquire(3);

		assertEquals(SEC + SEC / 5, t.now);
	}

	@Test
	void rejectsInvalidArguments() {
		assertThrows(IllegalArgumentException.class, () -> new StrictRateLimiter(0, Duration.ofSeconds(1)));
		assertThrows(IllegalArgumentException.class, () -> new StrictRateLimiter(1, Duration.ZERO));
		assertThrows(IllegalArgumentException.class, () -> new StrictRateLimiter(1, Duration.ofSeconds(-1)));
		assertThrows(NullPointerException.class, () -> new StrictRateLimiter(1, null));

		StrictRateLimiter limiter = new StrictRateLimiter(3, Duration.ofSeconds(1));
		assertThrows(IllegalArgumentException.class, () -> limiter.acquire(0));
		assertThrows(IllegalArgumentException.class, () -> limiter.acquire(4));
		assertThrows(IllegalArgumentException.class, () -> limiter.tryAcquire(4));
	}

	@Test
	void interruptedWaiterGivesItsPermitBack() throws Exception {
		FakeTime t = new FakeTime(0);
		StrictRateLimiter limiter = limiter(1, SEC, t);
		limiter.acquire();

		t.interruptOnNextPark = true;
		assertThrows(InterruptedException.class, () -> limiter.acquire());
		assertFalse(Thread.currentThread().isInterrupted());

		limiter.acquire(); // không bị phạt thêm một cửa sổ nữa
		assertEquals(SEC, t.now);
	}

	@Test
	void statsReflectActivity() throws Exception {
		FakeTime t = new FakeTime(0);
		StrictRateLimiter limiter = limiter(2, SEC, t);

		limiter.acquire();
		limiter.acquire();
		assertFalse(limiter.tryAcquire());
		limiter.acquire(); // phải chờ 1 s

		StrictRateLimiter.Stats s = limiter.stats();
		assertEquals(3, s.granted());
		assertEquals(1, s.throttled());
		assertEquals(1, s.rejected());
		assertEquals(0, s.waiting());
		assertEquals(Duration.ofSeconds(1), s.totalWaited());
		assertEquals(1, s.availablePermits());
		assertEquals(Duration.ZERO, s.estimatedWait());
	}

	@Test
	void manyThreadsStillRespectTheLimit() throws Exception {
		final int maxCalls = 20;
		final int threads = 8;
		final int callsPerThread = 25; // tổng 200 lượt = 10 lô, mỗi lô 20 lượt
		final Duration window = Duration.ofMillis(100);
		final StrictRateLimiter limiter = new StrictRateLimiter(maxCalls, window);

		final ExecutorService pool = Executors.newFixedThreadPool(threads);
		try {
			final CountDownLatch go = new CountDownLatch(1);
			final List<Future<Object>> futures = new ArrayList<>();
			final long start = System.nanoTime();
			for (int i = 0; i < threads; i++) {
				futures.add(pool.submit(() -> {
					go.await();
					for (int c = 0; c < callsPerThread; c++) {
						limiter.acquire();
					}
					return null;
				}));
			}
			go.countDown();
			for (Future<Object> f : futures) {
				f.get(30, TimeUnit.SECONDS);
			}
			final long elapsed = System.nanoTime() - start;

			final int total = threads * callsPerThread;
			// Lô cuối cùng không thể được cấp sớm hơn 9 cửa sổ kể từ lô đầu tiên.
			final long minimum = (total / maxCalls - 1) * window.toNanos();
			assertTrue(elapsed >= minimum, "vượt giới hạn: chạy xong quá nhanh, elapsed=" + elapsed);
			assertTrue(elapsed < minimum + TimeUnit.SECONDS.toNanos(5), "chậm bất thường: " + elapsed);
			assertEquals(total, limiter.stats().granted());
		} finally {
			pool.shutdownNow();
		}
	}

	@Test
	void closeWakesBlockedCallersAndRejectsNewOnes() throws Exception {
		final StrictRateLimiter limiter = new StrictRateLimiter(1, Duration.ofMinutes(10));
		limiter.acquire(); // dùng hết lượt, luồng sau sẽ phải chờ rất lâu

		final AtomicReference<Throwable> outcome = new AtomicReference<>();
		final Thread blocked = new Thread(() -> {
			try {
				limiter.acquire();
			} catch (Throwable e) {
				outcome.set(e);
			}
		});
		blocked.setDaemon(true);
		blocked.start();

		final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
		while (limiter.stats().waiting() == 0 && System.nanoTime() - deadline < 0) {
			Thread.sleep(5);
		}
		assertEquals(1, limiter.stats().waiting());

		limiter.close();
		blocked.join(5_000);

		assertFalse(blocked.isAlive());
		assertTrue(outcome.get() instanceof IllegalStateException, "luồng đang chờ phải nhận IllegalStateException");
		assertThrows(IllegalStateException.class, () -> limiter.tryAcquire());
	}
}