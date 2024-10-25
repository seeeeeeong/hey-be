package hey.io.heybackend.domain.file.service;

import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public List<File> findFiles(EntityType entityType, Long entityId) {
        return fileRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<File> findThumbnailFiles(EntityType entityType, List<Long> entityIds) {
        return fileRepository.findByEntityTypeAndEntityIdInAndFileCategory(entityType, entityIds, FileCategory.THUMBNAIL);
    }

}
