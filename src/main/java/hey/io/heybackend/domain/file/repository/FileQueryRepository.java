package hey.io.heybackend.domain.file.repository;

import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;

import java.util.List;

public interface FileQueryRepository {

    /**
     * <p>여러 엔티티의 파일 목록 조회</p>
     *
     * @param entityType  엔티티 유형
     * @param entityId ID
     * @param fileCategory 파일 카테고리
     * @return 각 ID에 해당하는 파일 목록
     */
    List<FileDto> findFilesByEntityId(EntityType entityType, Long entityId, FileCategory fileCategory);

    /**
     * <p>여러 엔티티의 파일 목록 조회</p>
     *
     * @param entityType  엔티티 유형
     * @param entityIds ID 목록
     * @param fileCategory 파일 카테고리
     * @return 각 ID에 해당하는 파일 목록
     */
    List<FileDto> findFilesByEntityIds(EntityType entityType, List<Long> entityIds, FileCategory fileCategory);


}
