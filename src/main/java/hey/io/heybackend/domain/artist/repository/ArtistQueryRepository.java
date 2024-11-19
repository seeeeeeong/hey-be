package hey.io.heybackend.domain.artist.repository;

import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto;
import hey.io.heybackend.domain.artist.dto.ArtistListResDto;
import hey.io.heybackend.domain.main.dto.HomeResDto.TopRatedArtistDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto.PerformanceArtistDto;
import hey.io.heybackend.domain.search.dto.SearchReqDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface ArtistQueryRepository {


    /**
     * <p>아티스트 상세 조회</p>
     *
     * @param artistId 아티스트 ID
     * @return 아티스트 상세
     */
    Optional<ArtistDetailResDto> findArtistByArtistId(Long artistId);

    /**
     * <p>공연 아티스트 정보</p>
     *
     * @param performanceId 공연 ID
     * @return 공연 아티스트 정보
     */
    List<PerformanceArtistDto> findPerformanceArtistsByPerformanceId(Long performanceId);


    /**
     * <p>팔로우 아티스트 목록 조회</p>
     *
     * @param memberId 회원 ID
     * @param pageable
     * @return 팔로우 아티스트 목록
     */
    Slice<ArtistListResDto> findFollowedArtistsByMemberId(Long memberId, Pageable pageable);

    /**
     * <p>TopRatedArtist 목록 조회</p>
     *
     * @return TopRatedArtist 목록
     */
    List<TopRatedArtistDto> findTopRatedArtists();

    Slice<ArtistListResDto> findArtistsByKeyword(SearchReqDto request, Pageable pageable);


}