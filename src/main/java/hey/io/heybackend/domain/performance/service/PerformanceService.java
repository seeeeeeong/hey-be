package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.repository.FileRepository;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.repository.FollowRepository;
import hey.io.heybackend.domain.performance.dto.*;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse.ArtistDTO;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse.PerformancePriceDTO;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse.PerformanceTicketingDTO;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse.PlaceDTO;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformanceArtist;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
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
    private final FileRepository fileRepository;
    private final FollowRepository followRepository;

    /**
     * <p>공연 목록을 조회합니다.</p>
     *
     * @param request 공연 목록 필터
     * @param pageable 페이지 정보
     * @return {@link SliceResponse} 객체, 공연 목록과 페이지 정보를 포함
     */
    public SliceResponse<PerformanceListResponse> getPerformanceList(PerformanceFilterRequest request, Pageable pageable) {
        // 1. 공연 목록 조회
        Slice<Performance> performanceSlice = performanceRepository.getPerformanceList(request, pageable);

        // 2. 공연 파일 ID 매핑
        Map<Long, List<File>> filesByPerformanceId = loadFilesByPerformanceId(performanceSlice);

        // 3. 각 공연에 대한 응답을 생성
        List<PerformanceListResponse> performanceListResponse = performanceSlice.stream()
                .map(performance -> createPerformanceListResponse(performance, filesByPerformanceId))
                .collect(Collectors.toList());

        return new SliceResponse<>(performanceListResponse, pageable, performanceSlice.hasNext());
    }

    /**
     * <p>특정 공연의 상세 정보를 조회합니다.</p>
     *
     * @param performanceId 공연의 ID
     * @param jwtTokenInfo JWT 토큰 정보 (인증용)
     * @return {@link PerformanceDetailResponse} 객체, 공연 상세 정보를 포함
     * @throws EntityNotFoundException 공연을 찾을 수 없는 경우 {@link ErrorCode#PERFORMANCE_NOT_FOUND} 예외 발생
     */
    public PerformanceDetailResponse getPerformanceDetail(Long performanceId, JwtTokenInfo jwtTokenInfo) {
        // 1. 공연 정보 조회
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFORMANCE_NOT_FOUND));

        // 2. 로그인 한 경우, 팔로우 여부 조회
        boolean isFollowed = false;
        if (jwtTokenInfo != null && jwtTokenInfo.getMemberId() != null) {
            isFollowed = checkIfFollowed(performanceId, jwtTokenInfo.getMemberId());
        }

        // 3. 관련 파일, 장르, 가격, 티켓 정보 및 아티스트 목록 로드
        List<FileDTO> files = loadPerformanceFiles(performance);
        List<String> genres = loadGenres(performance);
        List<PerformancePriceDTO> prices = loadPrices(performance);
        List<PerformanceTicketingDTO> ticketings = loadTicketings(performance);
        List<ArtistDTO> artistList = loadArtists(performance);

        return PerformanceDetailResponse.builder()
                .performanceId(performance.getPerformanceId())
                .performanceType(performance.getPerformanceType())
                .genres(genres)
                .performanceStatus(performance.getPerformanceStatus())
                .name(performance.getName())
                .engName(performance.getEngName())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .runningTime(performance.getRunningTime())
                .viewingAge(performance.getViewingAge())
                .isFollow(isFollowed)
                .files(files)
                .place(PlaceDTO.from(performance.getPlace()))
                .prices(prices)
                .ticketings(ticketings)
                .artists(artistList)
                .build();
    }

    /**
     * <p>공연 ID 리스트를 기반으로 파일을 로드합니다.</p>
     *
     * @param performanceSlice 공연 목록의 슬라이스
     * @return 공연 ID와 관련된 파일 목록을 맵으로 반환
     */
    private Map<Long, List<File>> loadFilesByPerformanceId(Slice<Performance> performanceSlice) {
        List<Long> performanceIds = performanceSlice.stream()
                .map(Performance::getPerformanceId)
                .collect(Collectors.toList());

        return fileRepository
                .findByEntityTypeAndEntityIdInAndFileCategory(EntityType.PERFORMANCE, performanceIds, FileCategory.THUMBNAIL)
                .stream()
                .collect(Collectors.groupingBy(File::getEntityId));
    }

    /**
     * <p>주어진 공연 정보를 바탕으로 공연 목록 응답을 생성합니다.</p>
     *
     * @param performance 공연 객체
     * @param filesByPerformanceId 공연 ID와 관련된 파일 목록을 담고 있는 맵
     * @return {@link PerformanceListResponse} 객체
     */
    private PerformanceListResponse createPerformanceListResponse(Performance performance, Map<Long, List<File>> filesByPerformanceId) {
        List<FileDTO> files = filesByPerformanceId.getOrDefault(performance.getPerformanceId(), List.of())
                .stream()
                .map(FileDTO::from)
                .collect(Collectors.toList());

        return PerformanceListResponse.builder()
                .performanceId(performance.getPerformanceId())
                .performanceName(performance.getName())
                .openDateTime(performance.getTicketings().getFirst().getOpenDatetime())
                .ticketStatus(performance.getTicketStatus())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .placeName(performance.getPlace().getName())
                .files(files)
                .build();
    }


    /**
     * <p>특정 공연 ID에 대해 사용자가 팔로우하고 있는지 확인합니다.</p>
     *
     * @param performanceId 공연 ID
     * @param memberId 사용자 ID
     * @return 공연을 팔로우하고 있으면 true, 아니면 false
     */
    private boolean checkIfFollowed(Long performanceId, Long memberId) {
        return followRepository.existsFollow(FollowType.PERFORMANCE, performanceId, memberId);
    }


    /**
     * <p>주어진 공연의 파일을 로드합니다.</p>
     *
     * @param performance 공연 객체
     * @return {@link List}<{@link FileDTO}> 공연 관련 파일 DTO 목록
     */
    private List<FileDTO> loadPerformanceFiles(Performance performance) {
        List<File> performanceFiles = fileRepository.findByEntityTypeAndEntityId(EntityType.PERFORMANCE, performance.getPerformanceId());
        performance.getFiles().addAll(performanceFiles);

        return performance.getFiles().stream()
                .map(FileDTO::from)
                .collect(Collectors.toList());
    }


    /**
     * <p>주어진 공연의 장르를 로드합니다.</p>
     *
     * @param performance 공연 객체
     * @return {@link List} 장르 이름 목록
     */
    private List<String> loadGenres(Performance performance) {
        return performance.getGenres().stream()
                .map(genre -> genre.getPerformanceGenre().name())
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 공연의 가격 정보를 로드합니다.</p>
     *
     * @param performance 공연 객체
     * @return {@link List}<{@link PerformancePriceDTO}> 가격 DTO 목록
     */
    private List<PerformancePriceDTO> loadPrices(Performance performance) {
        return performance.getPrices().stream()
                .map(PerformancePriceDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 공연의 티켓 정보를 로드합니다.</p>
     *
     * @param performance 공연 객체
     * @return {@link List}<{@link PerformanceTicketingDTO}> 티켓 DTO 목록
     */
    private List<PerformanceTicketingDTO> loadTicketings(Performance performance) {
        return performance.getTicketings().stream()
                .map(PerformanceTicketingDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 공연에 출연하는 아티스트 목록을 로드합니다.</p>
     *
     * @param performance 공연 객체
     * @return {@link List}<{@link ArtistDTO}> 아티스트 DTO 목록
     */
    private List<ArtistDTO> loadArtists(Performance performance) {
        List<Artist> artists = performance.getPerformanceArtists().stream()
                .map(PerformanceArtist::getArtist)
                .sorted(Comparator.comparing(Artist::getName))
                .collect(Collectors.toList());

        artists.forEach(artist -> {
            List<File> artistsFiles = fileRepository.findByEntityTypeAndEntityId(EntityType.ARTIST, artist.getArtistId());
            artist.getFiles().addAll(artistsFiles);
        });

        return artists.stream()
                .map(ArtistDTO::from)
                .collect(Collectors.toList());
    }
}
