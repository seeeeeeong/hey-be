package hey.io.heybackend.domain.search.dto;

import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchReqDto {

    @NotBlank
    private String keyword;

    @Parameter(
            description = "공연 상태 (INIT, READY, ONGOING, CLOSED, CANCEL)",
            array = @ArraySchema(schema = @Schema(implementation = PerformanceStatus.class))
    )
    private List<PerformanceStatus> statuses = new ArrayList<>();

}
