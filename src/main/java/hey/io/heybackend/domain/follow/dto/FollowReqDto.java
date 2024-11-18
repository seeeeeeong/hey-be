package hey.io.heybackend.domain.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "팔로우/언팔로우 요청")
public class FollowReqDto {

    @Schema(description = "팔로우/언팔로우 공연 ID 목록", example = "[1, 2, 3]", nullable = true)
    private List<Long> performanceIds;

    @Schema(description = "팔로우/언팔로우 아티스트 ID 목록", example = "[101, 102]", nullable = true)
    private List<Long> artistIds;

    @AssertTrue
    public boolean isValid() {
        return (performanceIds != null && !performanceIds.isEmpty()) ||
                (artistIds != null && !artistIds.isEmpty());
    }

}
