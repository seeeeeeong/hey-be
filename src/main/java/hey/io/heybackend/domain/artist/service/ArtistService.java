package hey.io.heybackend.domain.artist.service;

import hey.io.heybackend.common.response.SliceResponse;
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
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 아티스트 서비스 클래스입니다.
 * 아티스트의 상세 정보와 공연 목록을 조회하고, 필요한 파일 및 팔로우 정보를 포함하여
 * 응답 객체로 반환하는 비즈니스 로직을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository; // 아티스트 정보를 저장하고 조회하는 저장소
    private final FileRepository fileRepository; // 파일 정보를 저장하고 조회하는 저장소
    private final FollowRepository followRepository; // 팔로우 정보 조회를 위한 저장소

    /**
     * <p>아티스트 상세 조회</p>
     *
     * <p>아티스트 ID에 해당하는 아티스트 정보를 조회하고,
     * 아티스트의 파일 목록 및 장르 정보를 포함하여 상세 반환
     * 사용자 인증 정보가 제공된 경우 팔로우 상태도 함께 반환</p>
     *
     * @param artistId 아티스트 ID
     * @return 아티스트 상세 정보
     * @throws EntityNotFoundException 아티스트를 찾을 수 없는 경우 {@link ErrorCode#ARTIST_NOT_FOUND} 예외 발생
     */
    public ArtistDetailResponse getArtistDetail(Long artistId) {
        // 1. 아티스트 조회, 존재하지 않으면 예외 발생
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ARTIST_NOT_FOUND));

        // 2. 아티스트와 연관된 파일 목록을 FileDTO 형태로 변환하여 로드
        List<FileDTO> fileList = getFileList(artist);

        // 3. 아티스트와 연관된 장르 목록을 문자열 리스트 형태로 로드
        List<String> genreList = getGenreList(artist);

        // 4. ArtistDetailResponse 반환
        return ArtistDetailResponse.of(artist, genreList, fileList);
    }

    /**
     * <p>아티스트 공연 목록 조회</p>
     *
     * <p>아티스트 ID와 공연 상태에 해당하는 공연 목록 조회,
     * 각 공연의 파일 정보 및 팔로우 여부를 포함하여 응답</p>
     *
     * @param artistId 아티스트 ID
     * @param statuses 공연 상태 필터 (예: ONGOING, READY 등)
     * @param jwtTokenInfo 사용자의 JWT 토큰 정보, 팔로우 여부 확인에 사용
     * @param pageable 페이징 정보
     * @return 아티스트 공연 목록
     */
    public SliceResponse<ArtistPerformanceResponse> getArtistPerformanceList(Long artistId, List<PerformanceStatus> statuses, JwtTokenInfo jwtTokenInfo, Pageable pageable) {
        // 1. 아티스트 ID와 공연 상태를 기준으로 공연 목록 조회
        Slice<Performance> performanceSliceList = artistRepository.getArtistPerformanceList(artistId, statuses, pageable);

        // 2. 조회된 공연 목록에서 각 공연 ID에 해당하는 파일 정보 매핑
        Map<Long, List<File>> filesByPerformanceId = getFilesByPerformanceId(performanceSliceList);

        // 3. 공연 목록 응답 생성
        List<ArtistPerformanceResponse> artistPerformanceResponses = performanceSliceList.stream()
                .map(performance -> {
                    // 3.1. 공연 ID를 이용해 해당 공연의 파일 목록을 FileDTO 형태로 변환
                    List<FileDTO> fileList = createFileDtoList(filesByPerformanceId, performance.getPerformanceId());

                    // 3.2. 사용자가 해당 공연을 팔로우하고 있는지 여부 확인
                    boolean isFollow = checkExistFollow(jwtTokenInfo, performance.getPerformanceId());

                    // 3.3. ArtistPerformanceResponse 반환
                    return ArtistPerformanceResponse.of(performance, isFollow, fileList);
                })
                .collect(Collectors.toList());

        // 4. 페이징 정보를 포함한 SliceResponse 반환
        return new SliceResponse<>(artistPerformanceResponses, pageable, performanceSliceList.hasNext());
    }

    /**
     * <p>특정 아티스트를 사용자가 팔로우하고 있는지 확인합니다.</p>
     *
     * @param jwtTokenInfo 사용자의 JWT 토큰 정보
     * @param artistId 아티스트 ID
     * @return 아티스트를 팔로우하고 있으면 true, 아니면 false
     */
    private boolean checkExistFollow(JwtTokenInfo jwtTokenInfo, Long artistId) {
        // 1. 인증 정보가 없거나, 사용자 ID가 없는 경우 팔로우 상태를 false로 반환
        if (jwtTokenInfo == null || jwtTokenInfo.getMemberId() == null) {
            return false;
        }
        // 2. 팔로우 정보를 조회하여 존재하는 경우 true 반환
        return followRepository.existsFollow(FollowType.ARTIST, artistId, jwtTokenInfo.getMemberId());
    }

    /**
     * <p>주어진 아티스트의 파일 목록을 조회하고 FileDTO 형태로 변환하여 반환합니다.</p>
     *
     * @param artist 아티스트 엔티티
     * @return 아티스트의 파일 목록을 포함한 {@link List<FileDTO>}
     */
    private List<FileDTO> getFileList(Artist artist) {
        // 1. 아티스트 ID로 파일 목록을 조회, 아티스트 파일 리스트에 추가
        List<File> artistFiles = fileRepository.findByEntityTypeAndEntityIdAndFileCategory(EntityType.ARTIST, artist.getArtistId(), FileCategory.DETAIL);
        artist.getFiles().addAll(artistFiles);

        // 2. 파일 리스트를 FileDTO 형태로 변환하여 반환
        return artist.getFiles().stream()
                .map(FileDTO::of)
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 아티스트의 장르 목록을 문자열 리스트로 변환하여 반환합니다.</p>
     *
     * @param artist 아티스트 엔티티
     * @return 아티스트의 장르 목록을 포함한 {@link List<String>}
     */
    private List<String> getGenreList(Artist artist) {
        // 아티스트의 장르 리스트를 문자열로 변환하여 반환
        return artist.getGenres().stream()
                .map(genre -> genre.getArtistGenre().getDescription())
                .collect(Collectors.toList());
    }

    /**
     * <p>주어진 공연 목록에서 각 공연의 파일을 공연 ID별로 그룹화하여 반환합니다.</p>
     *
     * @param performanceSliceList 페이징 처리된 공연 목록
     * @return 공연 ID별 파일 목록을 매핑한 {@link Map<Long, List<File>>}
     */
    private Map<Long, List<File>> getFilesByPerformanceId(Slice<Performance> performanceSliceList) {
        // 1. 공연 목록에서 공연 ID 리스트를 추출
        List<Long> performanceIds = performanceSliceList.stream()
                .map(Performance::getPerformanceId)
                .collect(Collectors.toList());

        // 2. 공연 ID 리스트에 해당하는 파일을 조회하여 공연 ID별로 그룹화하여 반환
        return fileRepository
                .findByEntityTypeAndEntityIdInAndFileCategory(EntityType.PERFORMANCE, performanceIds, FileCategory.THUMBNAIL)
                .stream()
                .collect(Collectors.groupingBy(File::getEntityId));
    }

    /**
     * <p>공연 ID에 해당하는 파일 목록을 FileDTO 형태로 변환하여 반환합니다.</p>
     *
     * @param filesByPerformanceId 공연 ID별 파일 목록 맵
     * @param performanceId 공연 ID
     * @return 공연에 해당하는 파일 목록의 {@link List<FileDTO>}
     */
    private List<FileDTO> createFileDtoList(Map<Long, List<File>> filesByPerformanceId, Long performanceId) {
        // 공연 ID에 해당하는 파일 목록을 FileDTO 리스트로 변환하여 반환
        return filesByPerformanceId.getOrDefault(performanceId, List.of())
                .stream()
                .map(FileDTO::of)
                .collect(Collectors.toList());
    }
}
