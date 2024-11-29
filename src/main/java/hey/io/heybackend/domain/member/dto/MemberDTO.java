package hey.io.heybackend.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.enums.InterestCategory;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MemberDto {

    @Getter
    @NoArgsConstructor
    @Schema(description = "필수 약관 동의 여부 등록")
    public static class MemberTermsRequest {
        @NotNull
        @Schema(description = "필수 약관 동의 여부", example = "true")
        private Boolean basicTermsAgreed;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "관심 정보 수정")
    public static class MemberInterestRequest {

        @Parameter(description = "관심 유형", array = @ArraySchema(schema = @Schema(implementation = PerformanceType.class)))
        private List<PerformanceType> type;

        @Parameter(description = "관심 장르", array = @ArraySchema(schema = @Schema(implementation = PerformanceGenre.class)))
        private List<PerformanceGenre> genre;

        // MemberInterest 목록 변환
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
    @Schema(description = "회원 정보 수정")
    public static class ModifyMemberRequest {
        @NotNull
        @Schema(description = "닉네임", example = "페스티벌 러버_54321")
        private String nickname;
        @Parameter(description = "관심 유형", array = @ArraySchema(schema = @Schema(implementation = PerformanceType.class)))
        private List<PerformanceType> type;
        @Parameter(description = "관심 장르", array = @ArraySchema(schema = @Schema(implementation = PerformanceGenre.class)))
        private List<PerformanceGenre> genre;

        // MemberInterest 목록 변환
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
    @Schema(description = "회원 상세")
    public static class MemberDetailResponse {

        @Schema(description = "회원 ID", example = "1")
        private Long memberId;

        @Schema(description = "닉네임", example = "페스티벌 러버_54321")
        private String nickname;

        @Schema(description = "최종 접속 일시", example = "2024.11.29 12:35")
        private String accessedAt;

        @Schema(description = "관심 정보", implementation = MemberInterestDto.class)
        private MemberInterestDto interests;

        @Getter
        @Builder
        @Schema(description = "관심 정보")
        public static class MemberInterestDto {

            @Schema(description = "관심 유형", example = "[\"FESTIVAL_EX\"]")
            private List<String> type;

            @Schema(description = "관심 장르", example = "[\"BALLAD\", \"HIPHOP\"]")
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
