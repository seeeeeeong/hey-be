package hey.io.heybackend.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.enums.InterestCategory;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MemberDto {

    @Getter
    @NoArgsConstructor
    public static class MemberTermsRequest {
        @NotNull
        private Boolean basicTermsAgreed;
    }

    @Getter
    @NoArgsConstructor
    public static class MemberInterestRequest {

        private List<PerformanceType> type;
        private List<PerformanceGenre> genre;

        public List<MemberInterest> toMemberInterests(Member member) {
            List<MemberInterest> memberInterests = new ArrayList<>();
            if (type != null) {
                for (PerformanceType type : type) {
                    memberInterests.add(MemberInterest.of(member, InterestCategory.TYPE, type.getCode()));
                }
            }
            if (genre != null) {
                for (PerformanceGenre genre : genre) {
                    memberInterests.add(MemberInterest.of(member, InterestCategory.GENRE, genre.getCode()));
                }
            }
            return memberInterests;
        }
    }


    @Getter
    @NoArgsConstructor
    public static class ModifyMemberRequest {
        @NotNull
        private String nickname;
        private List<PerformanceType> type;
        private List<PerformanceGenre> genre;

        public List<MemberInterest> toMemberInterests(Member member) {
            List<MemberInterest> memberInterests = new ArrayList<>();
            if (type != null) {
                for (PerformanceType type : type) {
                    memberInterests.add(MemberInterest.of(member, InterestCategory.TYPE, type.getCode()));
                }
            }
            if (genre != null) {
                for (PerformanceGenre genre : genre) {
                    memberInterests.add(MemberInterest.of(member, InterestCategory.GENRE, genre.getCode()));
                }
            }
            return memberInterests;
        }
    }


    @Getter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDetailResponse {

        private Long memberId;
        private String nickname;
        private String accessedAt;
        private MemberInterestDto interests;

        @Getter
        @Builder
        public static class MemberInterestDto {
            private List<String> type;
            private List<String> genre;

            public static MemberInterestDto of(List<String> typeList, List<String> genreList) {
                return MemberInterestDto.builder()
                        .type(typeList)
                        .genre(genreList)
                        .build();
            }

        }

        @QueryProjection
        public MemberDetailResponse(Long memberId, String nickname, LocalDateTime accessedAt) {
            this.memberId = memberId;
            this.nickname = nickname;
            this.accessedAt = accessedAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        }

        public static MemberDetailResponse of(MemberDetailResponse memberDetail, MemberDetailResponse.MemberInterestDto interests) {
            return memberDetail.toBuilder()
                    .interests(interests)
                    .build();
        }
    }
}
