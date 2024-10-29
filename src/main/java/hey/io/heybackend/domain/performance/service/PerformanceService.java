package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.domain.artist.entity.Artist;
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
    public SliceResponse<PerformanceListResponse> getPerformanceList(JwtTokenInfo jwtTokenInfo, PerformanceFilterRequest request, Pageable pageable) {
        // 1. 공연 목록 조회
        Slice<Performance> performanceSliceList = performanceRepository.getPerformanceList(request, pageable);

        // 2. 공연 파일 ID 매핑
        Map<Long, List<File>> filesByPerformanceId = getFilesByPerformanceId(performanceSliceList);

        // 3. 각 공연에 대한 응답을 생성
        List<PerformanceListResponse> performanceListResponse = performanceSliceList.stream()
                .map(performance -> {

                    List<FileDTO> fileList = createFileDtoList(filesByPerformanceId, performance.getPerformanceId());

                    boolean isFollow = checkExistFollow(jwtTokenInfo, performance.getPerformanceId());

                    return PerformanceListResponse.of(performance, fileList, isFollow);
                })
                .collect(Collectors.toList());

        return new SliceResponse<>(performanceListResponse, pageable, performanceSliceList.hasNext());
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
        Performance performance = performanceRepository.findByIdAndNotInit(performanceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFORMANCE_NOT_FOUND));

        // 2. 팔로우 여부 조회
        boolean isFollow = checkExistFollow(jwtTokenInfo, performanceId);

        // 3. 관련 파일, 가격, 티켓 정보 및 아티스트 목록 로드
        List<FileDTO> fileList = getFileList(performance);
        List<PerformancePriceDTO> priceList = getPriceList(performance);
        List<PerformanceTicketingDTO> ticketingList = getTicketingList(performance);
        List<ArtistDTO> artistList = getArtistList(performance);

        return PerformanceDetailResponse.of(performance, isFollow, fileList, priceList, ticketingList, artistList);
    }

    /**
     * <p>공연 ID 리스트를 기반으로 파일을 로드합니다.</p>
     *
     * @param performanceSliceList 공연 목록의 슬라이스
     * @return 공연 ID와 관련된 파일 목록을 맵으로 반환
     */
    private Map<Long, List<File>> getFilesByPerformanceId(Slice<Performance> performanceSliceList) {
        List<Long> performanceIds = performanceSliceList.stream()
                .map(Performance::getPerformanceId)
                .collect(Collectors.toList());

        return fileRepository
                .findByEntityTypeAndEntityIdInAndFileCategory(EntityType.PERFORMANCE, performanceIds, FileCategory.THUMBNAIL)
                .stream()
                .collect(Collectors.groupingBy(File::getEntityId));
    }

    /**
     * <p>주어진 JWT 토큰 정보로 사용자가 공연을 팔로우하고 있는지 확인합니다.</p>
     *
     * @param jwtTokenInfo JWT 토큰 정보
     * @param performanceId 공연 ID
     * @return 팔로우하고 있으면 true, 아니면 false
     */
    private boolean checkExistFollow(JwtTokenInfo jwtTokenInfo, Long performanceId) {

        if (jwtTokenInfo == null || jwtTokenInfo.getMemberId() == null) {
            return false;
        }

        return followRepository.existsFollow(FollowType.PERFORMANCE, performanceId, jwtTokenInfo.getMemberId());
    }


    /**
     * <p>주어진 공연의 파일을 DTO로 변환합니다.</p>
     *
     * @param filesByPerformanceId 공연 ID와 관련된 파일 목록
     * @param performanceId 공연 ID
     * @return {@link List}<{@link FileDTO}> 공연 관련 파일 DTO 목록
     */
    private List<FileDTO> createFileDtoList(Map<Long, List<File>> filesByPerformanceId, Long performanceId) {
        return filesByPerformanceId.getOrDefault(performanceId, List.of())
                .stream()
                .map(FileDTO::of)
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 공연의 파일을 로드합니다.</p>
     *
     * @param performance 공연 객체
     * @return {@link List}<{@link FileDTO}> 공연 관련 파일 DTO 목록
     */
    private List<FileDTO> getFileList(Performance performance) {
        List<File> performanceFiles = fileRepository.findByEntityTypeAndEntityIdAndFileCategory(EntityType.PERFORMANCE, performance.getPerformanceId(), FileCategory.DETAIL);
        performance.getFiles().addAll(performanceFiles);

        return performance.getFiles().stream()
                .map(FileDTO::of)
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 공연의 가격 정보를 로드합니다.</p>
     *
     * @param performance 공연 객체
     * @return {@link List}<{@link PerformancePriceDTO}> 가격 DTO 목록
     */
    private List<PerformancePriceDTO> getPriceList(Performance performance) {
        return performance.getPrices().stream()
                .map(PerformancePriceDTO::of)
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 공연의 티켓 정보를 로드합니다.</p>
     *
     * @param performance 공연 객체
     * @return {@link List}<{@link PerformanceTicketingDTO}> 티켓 DTO 목록
     */
    private List<PerformanceTicketingDTO> getTicketingList(Performance performance) {
        return performance.getTicketings().stream()
                .map(PerformanceTicketingDTO::of)
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 공연에 출연하는 아티스트 목록을 로드합니다.</p>
     *
     * @param performance 공연 객체
     * @return {@link List}<{@link ArtistDTO}> 아티스트 DTO 목록
     */
    private List<ArtistDTO> getArtistList(Performance performance) {
        List<Artist> artists = performance.getPerformanceArtists().stream()
                .map(PerformanceArtist::getArtist)
                .filter(artist -> !artist.getArtistStatus().getCode().equals("INIT"))
                .sorted(Comparator.comparing(Artist::getName))
                .collect(Collectors.toList());

        artists.forEach(artist -> {
            List<File> artistsFiles = fileRepository.findByEntityTypeAndEntityIdAndFileCategory(EntityType.ARTIST, artist.getArtistId(), FileCategory.THUMBNAIL);
            artist.getFiles().addAll(artistsFiles);
        });

        return artists.stream()
                .map(ArtistDTO::of)
                .collect(Collectors.toList());
    }
}
