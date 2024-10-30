package hey.io.heybackend.domain.file.repository;

import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;

import java.util.List;

public interface FileQueryRepository {

    List<File> findFilesByEntityAndId(Long entityId, EntityType entityType, FileCategory fileCategory);

    List<File> findFilesByEntityAndIds(List<Long> entityIds, EntityType entityType, FileCategory fileCategory);


}
