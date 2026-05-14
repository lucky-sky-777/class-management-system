package com.mezon.classmanagement.backend.domain.point.repository;

import com.mezon.classmanagement.backend.domain.point.dto.MonthPointRankingResponseDto;
import com.mezon.classmanagement.backend.domain.point.dto.PointResponseDto;
import com.mezon.classmanagement.backend.domain.point.dto.WeekPointRankingResponseDto;
import com.mezon.classmanagement.backend.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
	Optional<Point> findByClazz_IdAndId(Long classId, Long pointId);

	List<Point> findByClazz_IdAndGroup_IdOrderByCreatedAtDesc(Long classId, Long groupId);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.point.dto.PointResponseDto (
		point.id,
		class.id,
		group.id,
		point.description,
		point.point,
		actor.id,
		actor.displayName,
		actor.avatarUrl,
		point.createdAt
	)
	from Point point
	join point.clazz class
	join point.group group
	join point.actor actor
	where class.id = :classId and group.id = :groupId
	order by point.createdAt desc
	""")
	List<PointResponseDto> getByClazz_IdAndGroup_IdOrderByCreatedAtDesc(Long classId, Long groupId);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.point.dto.PointResponseDto (
		point.id,
		class.id,
		group.id,
		point.description,
		point.point,
		actor.id,
		actor.displayName,
		actor.avatarUrl,
		point.createdAt
	)
	from Point point
	join point.clazz class
	join point.group group
	join point.actor actor
	where
		class.id = :classId and (point.createdAt between :startAt and :endAt)
	order by group.id asc , point.createdAt desc
	""")
	List<PointResponseDto> getByClazz_IdOrderByGroup_IdAscCreatedAtDescFilterByStartAtAndEndAt(
			Long classId,
			Instant startAt,
			Instant endAt
	);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.point.dto.PointResponseDto (
		point.id,
		class.id,
		group.id,
		point.description,
		point.point,
		actor.id,
		actor.displayName,
		actor.avatarUrl,
		point.createdAt
	)
	from Point point
	join point.clazz class
	join point.group group
	join point.actor actor
	where
		class.id = :classId and group.id = :groupId and (point.createdAt between :startAt and :endAt)
	order by point.createdAt desc
	""")
	List<PointResponseDto> getByClazz_IdAndGroup_IdOrderByCreatedAtDescFilterByStartAtAndEndAt(
			Long classId,
			Long groupId,
			Instant startAt,
			Instant endAt
	);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.point.dto.WeekPointRankingResponseDto(
		null,
		group.name,
		cast(sum(point.point) as short)
	)
	from Point point
	join point.group group
	join point.clazz class
	where class.id = :classId
		and point.createdAt >= :currentWeekStartAt
		and point.createdAt <= :now
	group by group.id, group.name
	order by sum(point.point) desc
	""")
	List<WeekPointRankingResponseDto> getWeekRankingHasPointGroupByClass(
			Long classId,
			Instant currentWeekStartAt,
			Instant now
	);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.point.dto.WeekPointRankingResponseDto(
		null,
		group.name,
		cast(coalesce(sum(point.point), 0) as short)
	)
	from Group group
	left join Point point
		on point.group.id = group.id
		and point.createdAt >= :currentWeekStartAt
		and point.createdAt <= :now
		and point.clazz.id = :classId
	where group.clazz.id = :classId
	group by group.id, group.name
	order by coalesce(sum(point.point), 0) desc
	""")
	List<WeekPointRankingResponseDto> getWeekRankingAllGroupByClass(
			Long classId,
			Instant currentWeekStartAt,
			Instant now
	);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.point.dto.MonthPointRankingResponseDto(
		null,

		cast(coalesce(sum(
			case
				when point.createdAt >= :week1StartAt
				 and point.createdAt <= :week1EndAt
				then point.point
				else 0
			end
		), 0) as short),

		cast(coalesce(sum(
			case
				when point.createdAt >= :week2StartAt
				 and point.createdAt <= :week2EndAt
				then point.point
				else 0
			end
		), 0) as short),

		cast(coalesce(sum(
			case
				when point.createdAt >= :week3StartAt
				 and point.createdAt <= :week3EndAt
				then point.point
				else 0
			end
		), 0) as short),

		cast(coalesce(sum(
			case
				when point.createdAt >= :week4StartAt
				 and point.createdAt <= :week4EndAt
				then point.point
				else 0
			end
		), 0) as short),

		group.name,

		cast(coalesce(sum(point.point), 0) as short)
	)
	from Group group
	left join Point point
		on point.group.id = group.id
		and point.clazz.id = :classId
		and point.createdAt >= :week1StartAt
		and point.createdAt <= :week4EndAt
	where group.clazz.id = :classId
	group by group.id, group.name
	order by coalesce(sum(point.point), 0) desc
	""")
	List<MonthPointRankingResponseDto> getMonthRankingByClass(
			Long classId,

			Instant week1StartAt,
			Instant week1EndAt,

			Instant week2StartAt,
			Instant week2EndAt,

			Instant week3StartAt,
			Instant week3EndAt,

			Instant week4StartAt,
			Instant week4EndAt
	);

}