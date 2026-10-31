package com.mezon.classmanagement.backend.domain_document.main.directory.repository;

import com.mezon.classmanagement.backend.domain_document.main.directory.dto.DirectoryResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.directory.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {

	@Query(value = """
    SELECT pg_advisory_xact_lock(
    	hashtextextended('directories:slug:' || :slug, 0)
    )
    """, nativeQuery = true
	)
	void lockSlug(String slug);

	@Query(value = """
    SELECT pg_advisory_xact_lock(
    	hashtextextended('directories:classId:' || :classId || ':slug' || :slug, 0)
    )
    """, nativeQuery = true
	)
	void lockClassIdAndSlug(Long classId, String slug);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain_document.main.directory.dto.DirectoryResponseDto(
		directory.id,
		class.id,
		directory.name,
		directory.slug,
		creator.id,
		directory.createdAt
	)
	from Directory directory
	join directory.clazz class
	join directory.creator creator
	where class.id = :classId
	order by directory.name asc
	""")
	List<DirectoryResponseDto> getByClazz_Id(Long classId);

	boolean existsByClazz_IdAndId(Long classId, Long id);

	Optional<Directory> findByClazz_IdAndId(Long classId, Long id);

	boolean existsBySlug(String slug);

	boolean existsByClazz_IdAndSlug(Long classId, String slug);

}