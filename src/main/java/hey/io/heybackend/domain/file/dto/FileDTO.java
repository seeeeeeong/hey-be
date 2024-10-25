package hey.io.heybackend.domain.file.dto;

import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileDTO {
    private Long fileId;
    private FileCategory fileCategory;
    private String fileName;
    private String fileUrl;


    public static FileDTO from(File file) {
        return FileDTO.builder()
                .fileId(file.getFileId())
                .fileCategory(file.getFileCategory())
                .fileName(file.getFileName())
                .fileUrl(file.getFileUrl())
                .build();
    }
}
