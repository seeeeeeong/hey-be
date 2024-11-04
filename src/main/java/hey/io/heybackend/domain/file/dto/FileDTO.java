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
    private Long fileId;

    @Schema(description = "파일 카테고리", example = "썸네일",
            allowableValues = {"썸네일", "상세"})
    private FileCategory fileCategory;

    @Schema(description = "파일명", example = "example.png")
    private String fileName;

    @Schema(description = "파일 URL", example = "http://example.com/image.png")
    private String fileUrl;

    @Schema(description = "파일 너비", example = "640")
    private Integer width;

    @Schema(description = "파일 높이", example = "640")
    private Integer height;

    public static FileDTO of(File file) {
        return FileDTO.builder()
                .fileId(file.getFileId())
                .fileCategory(file.getFileCategory())
                .fileName(file.getFileName())
                .fileUrl(file.getFileUrl())
                .width(file.getWidth())
                .height(file.getHeight())
                .build();
    }
}
