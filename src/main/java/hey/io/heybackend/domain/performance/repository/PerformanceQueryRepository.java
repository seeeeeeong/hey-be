package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto.ArtistPerformanceDto;
import hey.io.heybackend.domain.main.dto.HomeResDto.NewPerformanceDto;
import hey.io.heybackend.domain.main.dto.HomeResDto.TopRatedPerformanceDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto;
import hey.io.heybackend.domain.performance.dto.PerformanceListReqDto;
import hey.io.heybackend.domain.performance.dto.PerformanceListResDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface PerformanceQueryRepository {


    /**
     * <p>공연 목록 조회</p>
     *
     * @param request
     * @param pageable
     * @return 공연 목록
     */
    Slice<PerformanceListResDto> findPerformancesByCondition(PerformanceListReqDto request, Pageable pageable);


    /**
     * <p>공연 상세</p>
     *
     * @param performanceId 공연 ID
     * @return 공연 상세
     */
    Optional<PerformanceDetailResDto> findPerformanceDetailByPerformanceId(Long performanceId);

    /**
     * <p>팔로우 공연 목록 조회</p>
     *
     * @param memberId 회원 ID
     * @param pageable
     * @return 팔로우 공연 목록
     */
    Slice<PerformanceListResDto> findFollowedPerformancesByMemberId(Long memberId, Pageable pageable);

    /**
     * <p>아티스트 공연 목록 조회</p>
     *
     * @param artistId 아티스트 ID
     * @return List<PerformanceListResponse>
     */
    List<ArtistPerformanceDto> findArtistPerformancesByArtistId(Long artistId);

    /**
     * <p>HOT 5 공연 목록 조회</p>
     *
     * @return HOT 5 공연 목록
     */
    List<TopRatedPerformanceDto> findTopRatedPerformances();

    /**
     * <p>NEW 공연 목록 조회</p>
     *
     * @return NEW 공연 목록
     */
    List<NewPerformanceDto> findNewPerformances();
}