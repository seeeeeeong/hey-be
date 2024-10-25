package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.service.MemberFollowService;
import hey.io.heybackend.domain.member.service.MemberService;
import hey.io.heybackend.domain.performance.dto.PerformanceArtistResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final FileService fileService;
    private final MemberFollowService memberFollowService;

    public SliceResponse<PerformanceListResponse> getPerformanceList(PerformanceFilterRequest request, int size, int page, Sort.Direction direction) {

        Slice<PerformanceListResponse> performanceList = performanceRepository.getPerformanceList(request, Pageable.ofSize(size).withPage(page), direction);

        List<Long> performanceIds = performanceList.stream()
                .map(PerformanceListResponse::getPerformanceId)
                .collect(Collectors.toList());

        List<File> files = fileService.findThumbnailFiles(EntityType.PERFORMANCE, performanceIds);

        Map<Long, List<FileDTO>> filesByPerformanceId = files.stream()
                .collect(Collectors.groupingBy(
                        File::getEntityId,
                        Collectors.mapping(FileDTO::from, Collectors.toList())
                ));

        performanceList.forEach(performance ->
                performance.setFiles(filesByPerformanceId.getOrDefault(performance.getPerformanceId(), Collections.emptyList()))
        );

        return new SliceResponse<>(performanceList);

    }

    public PerformanceDetailResponse getPerformanceDetail(Long performanceId, JwtTokenInfo jwtTokenInfo) {
        PerformanceDetailResponse performanceDetailResponse = performanceRepository.getPerformanceDetail(performanceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERFORMANCE_NOT_FOUND));

        if (jwtTokenInfo.getMemberId() != null) {
            performanceDetailResponse.setIsFollow(memberFollowService.isFollowed(jwtTokenInfo.getMemberId(), FollowType.PERFORMANCE, performanceDetailResponse.getPerformance().getPerformanceId()));
        }

        return performanceDetailResponse;
    }


    public SliceResponse<PerformanceArtistResponse> getPerformanceArtistList(Long performanceId, int size, int page, Sort.Direction direction) {

        Slice<PerformanceArtistResponse> performanceArtistResponse = performanceRepository.getPerformanceArtistList(performanceId, Pageable.ofSize(size).withPage(page), direction);

        List<Long> performanceArtistIds = performanceArtistResponse.stream()
                .map(PerformanceArtistResponse::getArtistId)
                .collect(Collectors.toList());

        List<File> files = fileService.findThumbnailFiles(EntityType.ARTIST, performanceArtistIds);

        Map<Long, List<FileDTO>> filesByPerformanceArtistId = files.stream()
                .collect(Collectors.groupingBy(
                        File::getEntityId,
                        Collectors.mapping(FileDTO::from, Collectors.toList())
                ));

        performanceArtistResponse.forEach(performanceArtist ->
                performanceArtist.setFiles(filesByPerformanceArtistId.getOrDefault(performanceArtist.getArtistId(), Collections.emptyList())));

        return new SliceResponse<>(performanceArtistResponse);

    }

}
