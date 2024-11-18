package hey.io.heybackend.domain.follow.service;

import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.dto.ArtistListResDto;
import hey.io.heybackend.domain.artist.service.ArtistQueryService;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.follow.dto.FollowReqDto;
import hey.io.heybackend.domain.member.dto.MemberDto;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.follow.enums.FollowType;
import hey.io.heybackend.domain.member.service.MemberQueryService;
import hey.io.heybackend.domain.performance.dto.PerformanceListResDto;
import hey.io.heybackend.domain.performance.service.PerformanceQueryService;
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
public class FollowService {


    private final PerformanceQueryService performanceQueryService;
    private final ArtistQueryService artistQueryService;
    private final MemberQueryService memberQueryService;
    private final FollowCommandService followCommandService;
    private final FileService fileService;

    /**
     * <p>팔로우</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 생성된 팔로우 수
     */
    @Transactional
    public Integer createFollow(MemberDto memberDto, FollowReqDto request) {
        // 1. 회원 조회
        Member member = memberQueryService.getMemberByMemberId(memberDto.getMemberId());

        int followCount = 0;
        Map<FollowType, List<Long>> followMap = new HashMap<>();

        // 2.1 공연 ID 목록
        if (request.getPerformanceIds() != null) {
            followMap.put(FollowType.PERFORMANCE, request.getPerformanceIds());
        }

        // 2.2 아티스트 ID 목록
        if (request.getArtistIds() != null) {
            followMap.put(FollowType.ARTIST, request.getArtistIds());
        }

        // 3. 목록에 해당하는 대상 팔로우
        for (Map.Entry<FollowType, List<Long>> entry : followMap.entrySet()) {
            FollowType followType = entry.getKey();
            List<Long> entityIds = entry.getValue();
            followCount += followCommandService.insertFollow(member, followType, entityIds);
        }

        return followCount;
    }

    /**
     * <p>팔로우 취소</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 취소된 팔로우 수
     */
    @Transactional
    public Integer removeFollow(MemberDto memberDto, FollowReqDto request) {
        // 1. 회원 조회
        Member member = memberQueryService.getMemberByMemberId(memberDto.getMemberId());

        int unFollowCount = 0;
        Map<FollowType, List<Long>> followMap = new HashMap<>();

        // 2.1 공연 ID 목록
        if (request.getPerformanceIds() != null) {
            followMap.put(FollowType.PERFORMANCE, request.getPerformanceIds());
        }

        // 2.2 아티스트 ID 목록
        if (request.getArtistIds() != null) {
            followMap.put(FollowType.ARTIST, request.getArtistIds());
        }

        // 3. 목록에 해당하는 대상 팔로우 취소
        for (Map.Entry<FollowType, List<Long>> entry : followMap.entrySet()) {
            FollowType followType = entry.getKey();
            List<Long> entityIds = entry.getValue();
            unFollowCount += followCommandService.deleteFollow(member, followType, entityIds);
        }

        return unFollowCount;
    }

    /**
     * <p>팔로우 공연 목록 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param pageable
     * @return 팔로우 공연 목록
     */
    public SliceResponse<PerformanceListResDto> getFollowedPerformances(MemberDto memberDto, Pageable pageable) {
        // 1. 팔로우 공연 목록 조회
        Slice<PerformanceListResDto> performanceSliceList = performanceQueryService.getFollowedPerformancesByMemberId(memberDto.getMemberId(), pageable);
        List<PerformanceListResDto> performanceListResDto = performanceSliceList.getContent();

        // 2. 공연 ID 목록 추출
        List<Long> performanceIds = performanceListResDto.stream().map(PerformanceListResDto::getPerformanceId).collect(Collectors.toList());

        // 3. 파일 목록 조회
        Map<Long, List<FileDto>> filesByIds = fileService.getFilesByIds(EntityType.PERFORMANCE, performanceIds, FileCategory.THUMBNAIL);

        // 4. 팔로우 및 파일 매핑
        performanceListResDto.forEach(performance -> {
            performance.setIsFollowed(true);

            List<FileDto> files = filesByIds.getOrDefault(performance.getPerformanceId(), List.of());
            performance.setFiles(files);
        });

        return new SliceResponse<>(performanceListResDto, pageable, performanceSliceList.hasNext());
    }

    /**
     * <p>팔로우 아티스트 목록 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param pageable
     * @return 팔로우 아티스트 목록
     */
    public SliceResponse<ArtistListResDto> getFollowedArtists(MemberDto memberDto, Pageable pageable) {
        // 1. 팔로우 아티스트 목록 조회
        Slice<ArtistListResDto> artistSliceList = artistQueryService.getFollowedArtistsByMemberId(memberDto.getMemberId(), pageable);
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
