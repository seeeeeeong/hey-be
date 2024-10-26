package hey.io.heybackend.domain.file.dto;

import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.FileCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileDTO {

    @Schema(description = "파일 ID", example = "1")
    private Long fileId; // 파일의 고유 ID

    @Schema(description = "파일 카테고리", example = "THUMBNAIL",
            allowableValues = {"THUMBNAIL", "DETAIL"})
    private FileCategory fileCategory; // 파일의 카테고리 (썸네일, 상세 등)

    @Schema(description = "파일 이름", example = "example.png")
    private String fileName; // 파일의 이름

    @Schema(description = "파일 URL", example = "http://example.com/image.png")
    private String fileUrl; // 파일에 접근할 수 있는 URL

    public static FileDTO from(File file) {
        return FileDTO.builder()
                .fileId(file.getFileId())
                .fileCategory(file.getFileCategory())
                .fileName(file.getFileName())
                .fileUrl(file.getFileUrl())
                .build();
    }
}
