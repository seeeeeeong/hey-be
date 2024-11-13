package hey.io.heybackend.domain.performance.mapper;

import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.service.FollowService;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.system.dto.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PerformanceMapper {

    private final FileService fileService;
    private final FollowService followService;


    /**
     * <p>공연 목록 응답 생성</p>
     *
     * @param performanceList
     * @param tokenDTO
     * @return PerformanceListResponse
     */
    public List<PerformanceListResponse> createPerformanceListResponse(List<Performance> performanceList, TokenDTO tokenDTO) {
        // 공연 목록의 ID 리스트 생성
        List<Long> performanceIds = performanceList.stream()
                .map(Performance::getPerformanceId)
                .collect(Collectors.toList());

        // 공연 ID를 기준으로 List<FileDTO> 조회
        Map<Long, List<FileDTO>> filesByPerformanceIds = getFileDtoList(EntityType.PERFORMANCE, performanceIds, FileCategory.THUMBNAIL);

        return performanceList.stream()
                .map(performance -> PerformanceListResponse.of(
                        performance,
                        followService.checkExistFollow(tokenDTO, performance.getPerformanceId(), FollowType.PERFORMANCE),
                        filesByPerformanceIds.getOrDefault(performance.getPerformanceId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    private Map<Long, List<FileDTO>> getFileDtoList(EntityType entityType, List<Long> entityIds, FileCategory fileCategory) {
        return fileService.getFileDtosByEntityType(entityIds, entityType, fileCategory);
    }
}
