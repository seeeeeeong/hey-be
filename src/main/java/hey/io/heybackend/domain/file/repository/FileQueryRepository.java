package hey.io.heybackend.domain.file.repository;

import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import java.util.List;

public interface FileQueryRepository {

    /**
     * <p>엔티티별 썸네일 목록</p>
     *
     * @param entityType 엔티티 유형
     * @param entityIds  엔티티 ID 목록
     * @return 썸네일 목록
     */
    List<FileDto> selectThumbnailFileList(EntityType entityType, List<Long> entityIds);

    /**
     * <p>특정 엔티티 상세 파일 목록</p>
     *
     * @param entityType 엔티티 유형
     * @param entityId   엔티티 ID
     * @return 상세 파일 목록
     */
    List<FileDto> selectDetailFileList(EntityType entityType, Long entityId);
}
