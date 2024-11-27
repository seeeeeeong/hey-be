package hey.io.heybackend.domain.file.service;

import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.repository.FileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    /**
     * <p>엔티티별 썸네일 목록</p>
     *
     * @param entityType 엔티티 유형
     * @param entityIds  엔티티 ID 목록
     * @return 썸네일 목록
     */
    public List<FileDto> getThumbnailFileList(EntityType entityType, List<Long> entityIds) {
        return fileRepository.selectThumbnailFileList(entityType, entityIds);
    }

    /**
     * <p>특정 엔티티 상세 파일 목록</p>
     *
     * @param entityType 엔티티 유형
     * @param entityId   엔티티 ID
     * @return 상세 파일 목록
     */
    public List<FileDto> getDetailFileList(EntityType entityType, Long entityId) {
        return fileRepository.selectDetailFileList(entityType, entityId);
    }
}
