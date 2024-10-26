package hey.io.heybackend.domain.artist.service;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.ArtistPerformanceResponse;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.repository.ArtistRepository;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.repository.FileRepository;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.repository.FollowRepository;
import hey.io.heybackend.domain.performance.entity.Performance;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final FileRepository fileRepository;
    private final FollowRepository followRepository;

    /**
     * <p>특정 아티스트의 상세 정보를 조회합니다.</p>
     *
     * <p>이 메서드는 주어진 아티스트 ID에 해당하는 아티스트 정보를 데이터베이스에서 조회하고,
     * 아티스트의 파일 목록 및 장르 정보를 포함한 상세 응답을 반환합니다.</p>
     *
     * @param artistId 아티스트 ID
     * @param jwtTokenInfo JWT 토큰 정보 (인증용)
     * @return 아티스트 상세 정보 응답
     * @throws EntityNotFoundException 아티스트를 찾지 못한 경우 {@link ErrorCode#ARTIST_NOT_FOUND} 예외 발생
     */
    public ArtistDetailResponse getArtistDetail(Long artistId, JwtTokenInfo jwtTokenInfo) {
        // 1. 아티스트 조회
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ARTIST_NOT_FOUND));

        // 2. 로그인 한 경우, 팔로우 여부 조회
        boolean isFollowed = false;
        if (jwtTokenInfo != null && jwtTokenInfo.getMemberId() != null) {
            isFollowed = checkIfFollowed(artistId, jwtTokenInfo.getMemberId());
        }

        // 3. 아티스트 파일 목록 로드
        List<FileDTO> files = loadArtistFiles(artistId, artist);

        // 4. 장르 목록 로드
        List<String> genres = loadGenres(artist);

        return ArtistDetailResponse.builder()
                .artistId(artist.getArtistId())
                .name(artist.getName())
                .engName(artist.getEngName())
                .artistType(artist.getArtistType())
                .artistUrl(artist.getArtistUrl())
                .popularity(artist.getPopularity())
                .genres(genres)
                .isFollow(isFollowed)
                .files(files)
                .build();
    }

    /**
     * <p>특정 아티스트의 공연 목록을 조회합니다.</p>
     *
     * <p>이 메서드는 주어진 아티스트 ID에 해당하는 공연 목록을 조회하고,
     * 각 공연에 대한 파일 정보를 포함한 응답을 반환합니다.</p>
     *
     * @param artistId 아티스트 ID
     * @param exceptClosed 종료된 공연을 제외할지 여부
     * @param pageable 페이징 정보
     * @return 아티스트 공연 목록 응답
     */
    public SliceResponse<ArtistPerformanceResponse> getArtistPerformanceList(Long artistId, String exceptClosed, Pageable pageable) {
        // 1. 아티스트 공연 목록 조회
        Slice<Performance> performanceSlice = artistRepository.getArtistPerformanceList(artistId, exceptClosed, pageable);

        // 2. 공연 ID 목록 생성
        List<Long> performanceIds = performanceSlice.stream()
                .map(Performance::getPerformanceId)
                .collect(Collectors.toList());

        // 3. 공연 파일 목록 로드
        Map<Long, List<File>> filesByPerformanceId = loadFilesByPerformanceId(performanceIds);

        // 4. 공연 목록 응답 생성
        List<ArtistPerformanceResponse> artistPerformanceResponses = performanceSlice.stream()
                .map(performance -> createArtistPerformanceResponse(performance, filesByPerformanceId))
                .collect(Collectors.toList());

        return new SliceResponse<>(artistPerformanceResponses, pageable, performanceSlice.hasNext());
    }

    /**
     * <p>특정 아티스트의 ID에 대해 사용자가 팔로우하고 있는지 확인합니다.</p>
     *
     * @param artistId 아티스트 ID
     * @param memberId 사용자 ID
     * @return 공연을 팔로우하고 있으면 true, 아니면 false
     */
    private boolean checkIfFollowed(Long artistId, Long memberId) {
        return followRepository.existsFollow(FollowType.ARTIST, artistId, memberId);
    }


    /**
     * <p>주어진 아티스트 ID에 해당하는 파일 목록을 로드합니다.</p>
     *
     * <p>이 메서드는 아티스트의 파일을 데이터베이스에서 조회하고,
     * 해당 파일들을 {@link FileDTO} 객체로 변환하여 리스트로 반환합니다.</p>
     *
     * @param artistId 아티스트 ID
     * @param artist 아티스트 엔티티 (데이터베이스에서 조회된 아티스트)
     * @return 아티스트의 파일 목록을 포함한 {@link List<FileDTO>}
     */
    private List<FileDTO> loadArtistFiles(Long artistId, Artist artist) {
        List<File> artistFiles = fileRepository.findByEntityTypeAndEntityId(EntityType.ARTIST, artistId);
        artist.getFiles().addAll(artistFiles);

        return artist.getFiles().stream()
                .map(FileDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 아티스트의 장르 목록을 로드합니다.</p>
     *
     * <p>이 메서드는 아티스트의 장르 정보를 수집하여
     * 해당 장르들을 문자열 리스트로 변환하여 반환합니다.</p>
     *
     * @param artist 아티스트 엔티티 (데이터베이스에서 조회된 아티스트)
     * @return 아티스트의 장르 목록을 포함한 {@link List<String>}
     */
    private List<String> loadGenres(Artist artist) {
        return artist.getGenres().stream()
                .map(genre -> genre.getArtistGenre().name())
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 공연 ID 목록에 해당하는 파일 목록을 로드합니다.</p>
     *
     * <p>이 메서드는 공연 ID를 기준으로 파일을 데이터베이스에서 조회하고,
     * 조회된 파일들을 공연 ID별로 그룹화하여 반환합니다.</p>
     *
     * @param performanceIds 공연 ID 목록
     * @return 공연 ID별로 그룹화된 파일 목록을 포함하는 {@link Map<Long, List<File>>}
     */
    private Map<Long, List<File>> loadFilesByPerformanceId(List<Long> performanceIds) {
        return fileRepository
                .findByEntityTypeAndEntityIdInAndFileCategory(EntityType.PERFORMANCE, performanceIds, FileCategory.THUMBNAIL)
                .stream()
                .collect(Collectors.groupingBy(File::getEntityId));
    }

    /**
     * <p>주어진 공연 정보를 기반으로 {@link ArtistPerformanceResponse} 객체를 생성합니다.</p>
     *
     * <p>이 메서드는 공연 객체와 해당 공연에 대한 파일 목록을 바탕으로
     * 응답 객체를 생성하여 반환합니다.</p>
     *
     * @param performance 공연 엔티티 (데이터베이스에서 조회된 공연)
     * @param filesByPerformanceId 공연 ID별 파일 목록을 포함한 맵
     * @return {@link ArtistPerformanceResponse} 객체
     */
    private ArtistPerformanceResponse createArtistPerformanceResponse(Performance performance, Map<Long, List<File>> filesByPerformanceId) {
        List<FileDTO> files = filesByPerformanceId.getOrDefault(performance.getPerformanceId(), List.of())
                .stream()
                .map(FileDTO::from)
                .collect(Collectors.toList());

        return ArtistPerformanceResponse.builder()
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
}
