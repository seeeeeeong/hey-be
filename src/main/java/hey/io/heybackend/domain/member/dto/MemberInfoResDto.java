package hey.io.heybackend.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MemberInfoResDto {

    private Long memberId;
    private String nickname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessedAt;
    private MemberInterestDto interests;

    @Builder
    public MemberInfoResDto(Long memberId, String nickname, LocalDateTime accessedAt) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.accessedAt = accessedAt;
    }


    @Getter
    @NoArgsConstructor
    public static class MemberInterestDto {

        List<PerformanceType> type;
        List<PerformanceGenre> genre;

        @Builder
        public MemberInterestDto(List<PerformanceType> type, List<PerformanceGenre> genre) {
            this.type = type;
            this.genre = genre;
        }
    }
}
