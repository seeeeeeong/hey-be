package hey.io.heybackend.domain.artist.service;

import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto.ArtistGenreDto;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto.ArtistPerformanceDto;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.follow.enums.FollowType;
import hey.io.heybackend.domain.follow.service.FollowQueryService;
import hey.io.heybackend.domain.member.dto.MemberDto;
import hey.io.heybackend.domain.performance.service.PerformanceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final PerformanceQueryService performanceQueryService;
    private final ArtistQueryService artistQueryService;
    private final FollowQueryService followQueryService;
    private final FileService fileService;

    /**
     * <p>아티스트 상세</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param artistId 아티스트 ID
     * @return 아티스트 상세 정보
     */
    public ArtistDetailResDto getArtistDetail(MemberDto memberDto, Long artistId) {
        // 1. 아티스트 상세 조회
        ArtistDetailResDto artistDetailResDto = artistQueryService.getArtistDetailByArtistId(artistId);

        // 2. 팔로우 여부 확인
        if (memberDto != null) {
            Boolean isFollowedPerformance = followQueryService.existsFollow(memberDto.getMemberId(), FollowType.ARTIST, artistId);
            artistDetailResDto.setIsFollowed(isFollowedPerformance);
        } else {
            artistDetailResDto.setIsFollowed(false);
        }
        // 4. 파일 목록 조회
        List<FileDto> artistFiles = fileService.getFilesById(EntityType.ARTIST, artistId, FileCategory.DETAIL);
        artistDetailResDto.setFiles(artistFiles);

        // 5. 아티스트 조회
        Artist artist = artistQueryService.getArtistByArtistId(artistId);

        // 6. 장르 목록 조회
        List<ArtistGenreDto> genres = artistQueryService.getArtistGenresByArtist(artist);
        artistDetailResDto.setGenres(genres);

        // 7. 아티스트 공연 조회
        List<ArtistPerformanceDto> artistPerformances = performanceQueryService.getArtistPerformancesByArtistId(artist.getArtistId());

        // 8. 아티스트 공연 ID 목록 추출
        List<Long> performanceIds = artistPerformances.stream().map(ArtistPerformanceDto::getPerformanceId).collect(Collectors.toList());

        // 9. 아티스트 공연 팔로우 여부 확인
        Map<Long, Boolean> isFollowdMap = new HashMap<>();

        if (memberDto != null) {
            List<Long> followedPerformanceIds = followQueryService.getFollowedTargetIds(memberDto.getMemberId(), FollowType.PERFORMANCE, performanceIds);
            followedPerformanceIds.forEach(performanceId -> isFollowdMap.put(performanceId, true));
        }

        // 10. 파일 목록 조회
        Map<Long, List<FileDto>> filesByIds = fileService.getFilesByIds(EntityType.PERFORMANCE, performanceIds, FileCategory.THUMBNAIL);

        // 11. 팔로우 및 파일 매핑
        artistPerformances.forEach(performance -> {
            Boolean isFollowedArtist = isFollowdMap.getOrDefault(performance.getPerformanceId(), false);
            performance.setIsFollowed(isFollowedArtist);

            List<FileDto> files = filesByIds.getOrDefault(performance.getPerformanceId(), List.of());
            performance.setFiles(files);
        });

        return artistDetailResDto;
    }

}
