package hey.io.heybackend.domain.member.repository;


import hey.io.heybackend.domain.auth.enums.AuthId;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.InterestCategory;
import hey.io.heybackend.domain.mypage.dto.MyPageDto.MemberDetailResponse;
import java.util.List;
import java.util.Optional;

public interface MemberQueryRepository {


    /**
     * <p>회원 정보</p>
     *
     * @param providerUid
     * @return 회원 정보
     */
    Optional<Member> selectMemberByProviderUid(String providerUid);


    /**
     * <p>회원 상세</p>
     *
     * @param memberId 회원 ID
     * @return 회원 상세 정보
     */
    MemberDetailResponse selectMemberDetail(Long memberId);

    /**
     * <p>사용자 권한 정보</p>
     *
     * @param memberId 사용자 ID
     * @return 사용자 권한 목록
     */
    List<AuthId> selectUserAuthList(Long memberId);


    /**
     * <p>회원 관심 목록</p>
     *
     * @param category 카테고리
     * @param memberId 회원 ID
     * @return 회원 관심 목록 정보
     */
    List<String> selectMemberInterestList(InterestCategory category, Long memberId);

}
