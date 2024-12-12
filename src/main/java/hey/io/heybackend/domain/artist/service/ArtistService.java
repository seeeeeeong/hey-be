package hey.io.heybackend.domain.artist.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistListResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistSearchCondition;
import hey.io.heybackend.domain.artist.enums.ArtistGenre;
import hey.io.heybackend.domain.artist.repository.ArtistRepository;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.member.dto.AuthenticatedMember;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceSearchCondition;
import hey.io.heybackend.domain.performance.service.PerformanceArtistService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final PerformanceArtistService performanceArtistService;
    private final FileService fileService;

    /**
     * <p>아티스트 목록 (Slice)</p>
     *
     * @param searchCondition 조회 조건
     * @param authenticatedMember 회원 정보
     * @param pageable        페이징 정보
     * @return 아티스트 목록
     */
    public SliceResponse<ArtistListResponse> searchArtistSliceList(ArtistSearchCondition searchCondition,
        AuthenticatedMember authenticatedMember, Pageable pageable) {
        // 1. 아티스트 목록 조회
        SliceResponse<ArtistListResponse> artistList = artistRepository.selectArtistSliceList(searchCondition,
            authenticatedMember.getMemberId(), pageable);

        List<Long> artistIds = artistList.getContent().stream()
            .map(ArtistListResponse::getArtistId)
            .toList();

        // 2. 아티스트 파일 목록 조회
        List<FileDto> fileList = fileService.getThumbnailFileList(EntityType.ARTIST, artistIds);

        // 3. 목록 데이터 생성
        return ArtistListResponse.sliceOf(artistList, fileList);
    }

    /**
     * <p>아티스트 상세</p>
     *
     * @param artistId 공연 ID
     * @param authenticatedMember 회원 정보
     * @return 아티스트 상세 정보
     */
    public ArtistDetailResponse getArtistDetail(Long artistId, AuthenticatedMember authenticatedMember) {
        // 1. 아티스트 상세 정보 조회
        ArtistDetailResponse artistDetail = artistRepository.selectArtistDetail(artistId, authenticatedMember.getMemberId());
        if (artistDetail == null) {
            throw new EntityNotFoundException(ErrorCode.ARTIST_NOT_FOUND);
        }

        // 2. 아티스트 장르 조회
        List<ArtistGenre> genreList = artistRepository.selectArtistGenreList(artistId);

        // 3. 아티스트 파일 목록 조회
        List<FileDto> fileList = fileService.getDetailFileList(EntityType.ARTIST, artistId);

        // 4. 아티스트 공연 목록 조회
        List<PerformanceListResponse> performanceList = performanceArtistService.searchPerformanceList(
            PerformanceSearchCondition.of(artistId), authenticatedMember);

        // 5. 목록 데이터 생성
        return ArtistDetailResponse.of(artistDetail, genreList, fileList, performanceList);
    }
}
