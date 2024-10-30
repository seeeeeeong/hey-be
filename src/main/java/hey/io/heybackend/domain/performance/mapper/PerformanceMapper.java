package hey.io.heybackend.domain.performance.mapper;

import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.service.FollowService;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse.ArtistDTO;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformanceArtist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PerformanceMapper {

    private final FileService fileService;
    private final FollowService followService;

    public List<PerformanceListResponse> createPerformanceListResponse(List<Performance> performanceList, JwtTokenInfo jwtTokenInfo) {
        List<Long> performanceIds = performanceList.stream()
                .map(Performance::getPerformanceId)
                .collect(Collectors.toList());

        Map<Long, List<FileDTO>> filesByPerformanceIds = getFileDtoList(EntityType.PERFORMANCE, performanceIds, FileCategory.THUMBNAIL);

        return performanceList.stream()
                .map(performance -> PerformanceListResponse.of(
                        performance,
                        followService.checkExistFollow(jwtTokenInfo, performance.getPerformanceId(), FollowType.PERFORMANCE),
                        filesByPerformanceIds.getOrDefault(performance.getPerformanceId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    private Map<Long, List<FileDTO>> getFileDtoList(EntityType entityType, List<Long> entityIds, FileCategory fileCategory) {
        return fileService.getFileDtosByEntityType(entityIds, entityType, fileCategory);
    }
}
