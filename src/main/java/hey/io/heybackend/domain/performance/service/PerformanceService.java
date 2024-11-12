package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.service.FollowService;
import hey.io.heybackend.domain.performance.dto.*;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse.ArtistDTO;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse.PerformancePriceDTO;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse.PerformanceTicketingDTO;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformanceArtist;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import hey.io.heybackend.domain.performance.mapper.PerformanceMapper; // ResponseBuilder import 추가
import hey.io.heybackend.domain.system.dto.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final FileService fileService;
    private final FollowService followService;
    private final PerformanceMapper performanceMapper;

    /**
     * <p>공연 목록 조회</p>
     *
     * @param tokenDTO JWT 토큰 정보
     * @param request 공연 목록 필터
     * @param pageable 페이지 정보
     * @return 공연 목록
     */
    public SliceResponse<PerformanceListResponse> getPerformanceList(TokenDTO tokenDTO, PerformanceFilterRequest request, Pageable pageable) {
        // 1. 공연 목록 조회
        Slice<Performance> performanceSliceList = performanceRepository.getPerformanceList(request, pageable);

        // 2. 공연 목록 응답 생성
        List<PerformanceListResponse> performanceListResponse = performanceMapper.createPerformanceListResponse(performanceSliceList.getContent(), tokenDTO);

        return new SliceResponse<>(performanceListResponse, pageable, performanceSliceList.hasNext());
    }

    /**
     * <p>공연 상세 조회</p>
     *
     * @param performanceId 공연 ID
     * @param tokenDTO JWT 토큰 정보
     * @return 공연 상세 정보
     */
    public PerformanceDetailResponse getPerformanceDetail(Long performanceId, TokenDTO tokenDTO) {
        // 1. 공연 조회
        Performance performance = performanceRepository.getPerformanceDetail(performanceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFORMANCE_NOT_FOUND));

        // 2. 팔로우 여부 조회
        boolean isFollow = followService.checkExistFollow(tokenDTO, performanceId, FollowType.PERFORMANCE);

        // 3. 공연 파일, 가격, 티켓 조회
        List<FileDTO> fileList = fileService.getFileDtosByEntity(performanceId, EntityType.PERFORMANCE, FileCategory.DETAIL);
        List<PerformancePriceDTO> priceList = getPriceList(performance);
        List<PerformanceTicketingDTO> ticketingList = getTicketingList(performance);

        // 4. 공연 아티스트 조회
        List<ArtistDTO> artistList = getArtistList(performance.getPerformanceArtists());

        return PerformanceDetailResponse.of(performance, isFollow, fileList, priceList, ticketingList, artistList);
    }

    /**
     * <p>공연 가격 정보 조회</p>
     *
     * @param performance 공연 엔티티
     * @return 공연 가격 정보
     */
    private List<PerformancePriceDTO> getPriceList(Performance performance) {
        return performance.getPrices().stream()
                .map(PerformancePriceDTO::of)
                .collect(Collectors.toList()).reversed();
    }

    /**
     * <p>공연 티켓 조회</p>
     *
     * @param performance 공연 엔티티
     * @return 공연 티켓 정보
     */
    private List<PerformanceTicketingDTO> getTicketingList(Performance performance) {
        return performance.getTicketings().stream()
                .map(PerformanceTicketingDTO::of)
                .collect(Collectors.toList());
    }

    /**
     * <p>공연 아티스트 조회</p>
     *
     * @param performanceArtists 공연 아티스트 목록
     * @return 공연 아티스트 목록
     */
    public List<ArtistDTO> getArtistList(List<PerformanceArtist> performanceArtists) {
        // 1. 아티스트 ID 목록
        List<Long> artistIds = performanceArtists.stream()
                .filter(performanceArtist -> performanceArtist.getArtist().getArtistStatus() == ArtistStatus.ENABLE)
                .map(performanceArtist -> performanceArtist.getArtist().getArtistId())
                .collect(Collectors.toList());

        // 2. 아티스트 ID에 해당하는 파일 리스트
        Map<Long, List<FileDTO>> filesByArtistIds = fileService.getFileDtosByEntityType(artistIds, EntityType.ARTIST, FileCategory.THUMBNAIL);

        // 3. ArtistDTO 생성
        return performanceArtists.stream()
                .filter(performanceArtist -> artistIds.contains(performanceArtist.getArtist().getArtistId()))
                .map(performanceArtist -> {
                    Artist artist = performanceArtist.getArtist();
                    List<FileDTO> fileList = filesByArtistIds.getOrDefault(artist.getArtistId(), Collections.emptyList());
                    return ArtistDTO.of(artist, fileList);
                })
                .collect(Collectors.toList());
    }
}
