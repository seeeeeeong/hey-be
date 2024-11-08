package hey.io.heybackend.domain.artist.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.jwt.dto.JwtTokenInfo;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResponse;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.repository.ArtistRepository;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformanceArtist;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.mapper.PerformanceMapper; // ResponseBuilder import 추가
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final FileService fileService;
    private final PerformanceMapper performanceMapper;

    /**
     * <p>아티스트 상세 조회</p>
     *
     * @param artistId 아티스트 ID
     * @param jwtTokenInfo JWT 토큰 정보
     * @return 아티스트 상세 정보
     */
    public ArtistDetailResponse getArtistDetail(Long artistId, JwtTokenInfo jwtTokenInfo) {
        // 1. 아티스트 조회
        Artist artist = artistRepository.getArtistDetail(artistId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ARTIST_NOT_FOUND));

        // 2. 아티스트 파일 조회
        List<FileDTO> fileList = fileService.getFileDtosByEntity(artistId, EntityType.ARTIST, FileCategory.DETAIL);

        // 4. 아티스트 공연 목록 조회
        List<Performance> performanceList = artist.getPerformanceArtists().stream()
                .map(PerformanceArtist::getPerformance)
                .filter(performance -> !performance.getPerformanceStatus().equals(PerformanceStatus.INIT))
                .collect(Collectors.toList());

        // 5. 아티스트 공연 목록 응답 생성
        List<PerformanceListResponse> performanceListResponse = performanceMapper.createPerformanceListResponse(performanceList, jwtTokenInfo);

        return ArtistDetailResponse.of(artist, fileList, performanceListResponse);
    }


}
