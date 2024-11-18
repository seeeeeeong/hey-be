package hey.io.heybackend.domain.member.dto;

import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyMemberReqDto {

    @NotNull
    private String nickname;
    private List<PerformanceType> type;
    private List<PerformanceGenre> genre;

}
