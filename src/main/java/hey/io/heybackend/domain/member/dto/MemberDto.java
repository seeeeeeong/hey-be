package hey.io.heybackend.domain.member.dto;

import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "필수 약관 동의 여부 등록")
    public static class MemberTermsRequest {
        @NotNull
        @Schema(description = "필수 약관 동의 여부", example = "true")
        private Boolean basicTermsAgreed;
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "관심 정보 수정")
    public static class MemberInterestRequest {

        @Parameter(description = "관심 유형", array = @ArraySchema(schema = @Schema(implementation = PerformanceType.class)))
        private List<PerformanceType> type;

        @Parameter(description = "관심 장르", array = @ArraySchema(schema = @Schema(implementation = PerformanceGenre.class)))
        private List<PerformanceGenre> genre;
    }
}
