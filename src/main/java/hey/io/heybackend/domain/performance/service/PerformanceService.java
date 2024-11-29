package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistListResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistSearchCondition;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.auth.dto.AuthenticatedMember;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceDetailResponse.PriceDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceDetailResponse.TicketingDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceSearchCondition;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceArtistService performanceArtistService;
    private final FileService fileService;

    /**
     * <p>공연 목록 (Slice)</p>
     *
     * @param searchCondition 조회 조건
     * @param authenticatedMember 인증 회원 정보
     * @param pageable        페이징 정보
     * @return 공연 목록
     */
    public SliceResponse<PerformanceListResponse> searchPerformanceSliceList(PerformanceSearchCondition searchCondition,
                                                                             AuthenticatedMember authenticatedMember, Pageable pageable) {
        // 1. 공연 목록 조회
        SliceResponse<PerformanceListResponse> performanceList = performanceRepository.selectPerformanceSliceList(
            searchCondition, authenticatedMember.getMemberId(), pageable);

        List<Long> performanceIds = performanceList.getContent().stream()
            .map(PerformanceListResponse::getPerformanceId)
            .toList();

        // 2. 공연별 티켓 오픈 시간 조회
        List<PerformanceListResponse> ticketingList = performanceRepository.selectPerformanceOpenDatetimeList(performanceIds);

        // 3. 공연 파일 목록 조회
        List<FileDto> fileList = fileService.getThumbnailFileList(EntityType.PERFORMANCE, performanceIds);

        // 4. 목록 데이터 생성
        return PerformanceListResponse.sliceOf(performanceList, ticketingList, fileList);
    }

    /**
     * <p>공연 상세</p>
     *
     * @param performanceId 공연 ID
     * @param authenticatedMember 인증 회원 정보
     * @return 공연 상세 정보
     */
    public PerformanceDetailResponse getPerformanceDetail(Long performanceId, AuthenticatedMember authenticatedMember) {
        // 1. 공연 상세 정보 조회
        PerformanceDetailResponse performanceDetail = performanceRepository.selectPerformanceDetail(performanceId, authenticatedMember.getMemberId());
        if (performanceDetail == null) {
            throw new EntityNotFoundException(ErrorCode.PERFORMANCE_NOT_FOUND);
        }

        // 2. 공연 장르 조회
        List<PerformanceGenre> genreList = performanceRepository.selectPerformanceGenreList(performanceId);

        // 3. 공연 가격 조회
        List<PriceDto> priceList = performanceRepository.selectPerformancePriceList(performanceId);

        // 4. 공연 예매 목록 조회
        List<TicketingDto> ticketingList = performanceRepository.selectPerformanceTicketList(performanceId);

        // 5. 공연 파일 목록 조회
        List<FileDto> fileList = fileService.getDetailFileList(EntityType.PERFORMANCE, performanceId);

        // 6. 공연 아티스트 목록 조회
        List<ArtistListResponse> artistList = performanceArtistService.searchArtistList(ArtistSearchCondition.of(performanceId), authenticatedMember);

        // 7. 목록 데이터 생성
        return PerformanceDetailResponse.of(performanceDetail, genreList, priceList, ticketingList, fileList, artistList);
    }
}