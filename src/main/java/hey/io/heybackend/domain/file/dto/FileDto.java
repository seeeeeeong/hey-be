package hey.io.heybackend.domain.file.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.file.enums.FileCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileDto {

    @Schema(description = "파일 ID", example = "4")
    private Long fileId;

    @Schema(description = "엔티티 ID", hidden = true)
    private Long entityId;

    @Schema(description = "파일 분류", example = "DETAIL")
    private String fileCategory;

    @Schema(description = "파일명", example = "PF_PF250394_241004_0910442.png")
    private String fileName;

    @Schema(description = "파일 URL", example = "https://hey-bucket.s3.amazonaws.com/app/performance/2/03252893-8592-456c-b7ab-fa5a4806775b.png")
    private String fileUrl;

    @Schema(description = "파일 너비", nullable = true, example = "640")
    private Integer width;

    @Schema(description = "파일 높이", nullable = true, example = "640")
    private Integer height;

    @QueryProjection
    public FileDto(Long fileId, FileCategory fileCategory, String fileName, String fileUrl, Integer width,
        Integer height) {
        this.fileId = fileId;
        this.fileCategory = fileCategory.getCode();
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.width = width;
        this.height = height;
    }
}
