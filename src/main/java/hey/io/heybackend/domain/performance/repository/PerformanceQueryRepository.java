package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceDetailResponse.PriceDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceDetailResponse.TicketingDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceSearchCondition;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface PerformanceQueryRepository {

    /**
     * <p>공연 목록 (Slice)</p>
     *
     * @param searchCondition 조회 조건
     * @param memberId        회원 ID
     * @param pageable        페이징 정보
     * @return Slice 공연 목록
     */
    SliceResponse<PerformanceListResponse> selectPerformanceSliceList(PerformanceSearchCondition searchCondition,
        Long memberId, Pageable pageable);

    /**
     * <p>공연 목록</p>
     *
     * @param searchCondition 조회 조건
     * @param memberId        회원 ID
     * @return 공연 목록
     */
    List<PerformanceListResponse> selectPerformanceList(PerformanceSearchCondition searchCondition, Long memberId);

    /**
     * <p>공연별 티켓 오픈 목록</p>
     *
     * @param performanceIds 공연 ID 목록
     * @return 가장 빠른 예매 시간과 공연 매핑 목록
     */
    List<PerformanceListResponse> selectPerformanceOpenDatetimeList(List<Long> performanceIds);

    /**
     * <p>공연 상세</p>
     *
     * @param performanceId 공연 ID
     * @param memberId      회원 ID
     * @return 공연 상세 정보 + 장소 정보 + 팔로우 정보
     */
    PerformanceDetailResponse selectPerformanceDetail(Long performanceId, Long memberId);

    /**
     * <p>공연 장르 목록</p>
     *
     * @param performanceId 공연 ID
     * @return 공연 장르 목록
     */
    List<PerformanceGenre> selectPerformanceGenreList(Long performanceId);

    /**
     * <p>공연 가격 목록</p>
     *
     * @param performanceId 공연 ID
     * @return 공연 가격 목록
     */
    List<PriceDto> selectPerformancePriceList(Long performanceId);

    /**
     * <p>공연 예매 목록</p>
     *
     * @param performanceId 공연 ID
     * @return 공연 예매 목록
     */
    List<TicketingDto> selectPerformanceTicketList(Long performanceId);
}