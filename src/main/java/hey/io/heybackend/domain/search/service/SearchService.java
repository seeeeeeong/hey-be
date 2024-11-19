package hey.io.heybackend.domain.search.service;

import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.dto.ArtistListResDto;
import hey.io.heybackend.domain.artist.service.ArtistQueryService;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.follow.enums.FollowType;
import hey.io.heybackend.domain.follow.service.FollowQueryService;
import hey.io.heybackend.domain.member.dto.MemberDto;
import hey.io.heybackend.domain.performance.dto.PerformanceListResDto;
import hey.io.heybackend.domain.performance.service.PerformanceQueryService;
import hey.io.heybackend.domain.search.dto.SearchReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final PerformanceQueryService performanceQueryService;
    private final FollowQueryService followQueryService;
    private final FileService fileService;
    private final ArtistQueryService artistQueryService;

    public SliceResponse<PerformanceListResDto> searchPerformanceList(MemberDto memberDto, SearchReqDto request, Pageable pageable) {

        Slice<PerformanceListResDto> performanceSliceList = performanceQueryService.searchPerformancesByKeyword(request, pageable);
        List<PerformanceListResDto> performanceListResDto = performanceSliceList.getContent();

        // 2. 공연 ID 목록 추출
        List<Long> performanceIds = performanceListResDto.stream().map(PerformanceListResDto::getPerformanceId).collect(Collectors.toList());

        // 3. 팔로우 여부 확인
        Map<Long, Boolean> isFollowdMap = new HashMap<>();

        if (memberDto != null) {
            List<Long> followedPerformanceIds = followQueryService.getFollowedTargetIds(memberDto.getMemberId(), FollowType.PERFORMANCE, performanceIds);
            followedPerformanceIds.forEach(performanceId -> isFollowdMap.put(performanceId, true));
        } else {
            performanceSliceList.forEach(performance -> performance.setIsFollowed(false));
        }

        // 4. 파일 목록 조회
        Map<Long, List<FileDto>> filesByIds = fileService.getFilesByIds(EntityType.PERFORMANCE, performanceIds, FileCategory.THUMBNAIL);

        // 5. 팔로우 및 파일 매핑
        performanceListResDto.forEach(performance -> {
            Boolean isFollowed = isFollowdMap.getOrDefault(performance.getPerformanceId(), false);
            performance.setIsFollowed(isFollowed);

            List<FileDto> files = filesByIds.getOrDefault(performance.getPerformanceId(), List.of());
            performance.setFiles(files);
        });
        return new SliceResponse<>(performanceListResDto, pageable, performanceSliceList.hasNext());
    }

    public SliceResponse<ArtistListResDto> searchArtistList(MemberDto memberDto, SearchReqDto request, Pageable pageable) {

        Slice<ArtistListResDto> artistSliceList = artistQueryService.searchArtistsByKeyword(request, pageable);
        List<ArtistListResDto> artistListResDto = artistSliceList.getContent();

        // 2. 아티스트 ID 목록 추출
        List<Long> artistIds = artistListResDto.stream().map(artist -> artist.getArtistId()).collect(Collectors.toList());

        // 3. 파일 목록 조회
        Map<Long, List<FileDto>> fileListByIds = fileService.getFilesByIds(EntityType.ARTIST, artistIds, FileCategory.THUMBNAIL);

        artistListResDto.forEach(artist -> {
            List<FileDto> fileList = fileListByIds.getOrDefault(artist.getArtistId(), List.of());
            artist.setFiles(fileList);
        });

        return new SliceResponse<>(artistListResDto, pageable, artistSliceList.hasNext());
    }

}
