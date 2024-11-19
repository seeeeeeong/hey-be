package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.service.ArtistQueryService;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.follow.enums.FollowType;
import hey.io.heybackend.domain.follow.service.FollowQueryService;
import hey.io.heybackend.domain.member.dto.MemberDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto.PerformanceArtistDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto.PerformanceGenreDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto.PerformancePriceDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto.PerformanceTicketingDto;
import hey.io.heybackend.domain.performance.dto.PerformanceListReqDto;
import hey.io.heybackend.domain.performance.dto.PerformanceListResDto;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.repository.PerformanceGenresRepository;
import hey.io.heybackend.domain.performance.repository.PerformancePriceRepository;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import hey.io.heybackend.domain.performance.repository.PerformanceTicketingRepository;
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
public class PerformanceService {

    private final PerformanceQueryService performanceQueryService;
    private final FollowQueryService followQueryService;
    private final FileService fileService;
    private final ArtistQueryService artistQueryService;


    /**
     * <p>공연 목록 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request 공연 목록 필터
     * @param pageable 페이지
     * @return 공연 목록
     */
    public SliceResponse<PerformanceListResDto> getPerformanceList(MemberDto memberDto, PerformanceListReqDto request, Pageable pageable) {

        // 1. 공연 목록 조회
        Slice<PerformanceListResDto> performanceSliceList = performanceQueryService.getPerformancesByCondition(request, pageable);
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

    /**
     * <p>공연 상세 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param performanceId 공연 ID
     * @return 공연 상세
     */
    public PerformanceDetailResDto getPerformanceDetail(MemberDto memberDto, Long performanceId) {

        // 1. 공연 상세 조회
        PerformanceDetailResDto performanceDetailResDto = performanceQueryService.getPerformanceDetailByPerformanceId(performanceId);

        // 2. 팔로우 여부 확인
        if (memberDto != null) {
            Boolean isFollowed = followQueryService.existsFollow(memberDto.getMemberId(), FollowType.PERFORMANCE, performanceId);
            performanceDetailResDto.setIsFollowed(isFollowed);
        } else {
            performanceDetailResDto.setIsFollowed(false);
        }

        // 3. 파일 목록 조회
        List<FileDto> performanceFiles = fileService.getFilesById(EntityType.PERFORMANCE, performanceId, FileCategory.DETAIL);
        performanceDetailResDto.setFiles(performanceFiles);

        Performance performance = performanceQueryService.getPerformance(performanceId);

        // 4. 장르 목록 조회
        List<PerformanceGenreDto> genres = performanceQueryService.getPerformanceGenresByPerformance(performance);
        performanceDetailResDto.setGenres(genres);

        // 5. 가격 목록 조회
        List<PerformancePriceDto> prices = performanceQueryService.getPerformancePricesByPerformance(performance);
        performanceDetailResDto.setPrices(prices);

        // 6. 티켓 목록 조회
        List<PerformanceTicketingDto> ticketings = performanceQueryService.getPerformanceTicketingsByPerformance(performance);
        performanceDetailResDto.setTicketings(ticketings);

        // 6. 공연 아티스트 목록 조회
        List<PerformanceArtistDto> performanceArtists = artistQueryService.getPerformanceArtistsByPerformanceId(performance.getPerformanceId());

        // 7. 공연 아티스트 ID 목록 추출
        List<Long> artistIdList = performanceArtists.stream().map(PerformanceArtistDto::getArtistId).collect(Collectors.toList());

        // 8. 공연 아티스트 파일 목록 조회
        Map<Long, List<FileDto>> fileListByIdList = fileService.getFilesByIds(EntityType.ARTIST, artistIdList, FileCategory.THUMBNAIL);

        // 9. 공연 아티스트 파일 매핑
        performanceArtists.forEach(artist -> {
            List<FileDto> artistFiles = fileListByIdList.getOrDefault(artist.getArtistId(), List.of());
            artist.setFiles(artistFiles);
        });

        performanceDetailResDto.setArtists(performanceArtists);

        return performanceDetailResDto;
    }

}