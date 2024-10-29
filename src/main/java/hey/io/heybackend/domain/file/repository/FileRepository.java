package hey.io.heybackend.domain.file.repository;

import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query("SELECT f FROM File f WHERE f.entityType = :entityType AND f.entityId IN :entityIds AND f.fileCategory = :fileCategory ORDER BY f.fileOrder ASC")
    List<File> findByEntityTypeAndEntityIdInAndFileCategory(EntityType entityType, List<Long> entityIds, FileCategory fileCategory);

    @Query("SELECT f FROM File f WHERE f.entityType = :entityType AND f.entityId = :entityId AND f.fileCategory = :fileCategory ORDER BY f.fileOrder ASC")
    List<File> findByEntityTypeAndEntityIdAndFileCategory(EntityType entityType, Long entityId, FileCategory fileCategory);

}
