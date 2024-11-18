package hey.io.heybackend.domain.file.service;

import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    /**
     * <p>여러 엔티티의 파일 목록 조회</p>
     *
     * @param entityType  엔티티 유형
     * @param artistId 아티스트 ID
     * @param fileCategory 파일 카테고리
     * @return 각 ID에 해당하는 파일 목록
     */
    public List<FileDto> getFilesById(EntityType entityType, Long artistId, FileCategory fileCategory) {
        return fileRepository.findFilesByEntityId(entityType, artistId, fileCategory);
    }

    /**
     * <p>여러 엔티티의 파일 목록 조회</p>
     *
     * @param entityType  엔티티 유형
     * @param entityIds ID 목록
     * @param fileCategory 파일 카테고리
     * @return 각 ID에 해당하는 파일 목록
     */
    public Map<Long, List<FileDto>> getFilesByIds(EntityType entityType, List<Long> entityIds, FileCategory fileCategory) {
        if (entityIds.isEmpty()) return Collections.emptyMap();

        List<FileDto> files = fileRepository.findFilesByEntityIds(entityType, entityIds, fileCategory);

        return files.stream()
                .collect(Collectors.groupingBy(FileDto::getFileId));
    }

}
