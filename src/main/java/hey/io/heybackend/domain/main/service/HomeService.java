package hey.io.heybackend.domain.main.service;

import hey.io.heybackend.domain.artist.service.ArtistQueryService;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.main.dto.HomeResDto;
import hey.io.heybackend.domain.main.dto.HomeResDto.NewPerformanceDto;
import hey.io.heybackend.domain.main.dto.HomeResDto.TopRatedArtistDto;
import hey.io.heybackend.domain.main.dto.HomeResDto.TopRatedPerformanceDto;
import hey.io.heybackend.domain.performance.service.PerformanceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final PerformanceQueryService performanceQueryService;
    private final ArtistQueryService artistQueryService;
    private final FileService fileService;

    /**
     * <p>메인 홈</p>
     *
     * @return 메인 홈 공연, 아티스트 정보
     */
    public HomeResDto getHomePerformancesAndArtists() {

        // 1. TopRatedPerformance 목록 조회
        List<TopRatedPerformanceDto> topRatedPerformances = performanceQueryService.getTopRatedPerformances();

        // 1.1 TopRatedPerformance ID 목록 추출
        List<Long> topRatedPerformanceIds = topRatedPerformances.stream()
                .map(TopRatedPerformanceDto::getPerformanceId)
                .collect(Collectors.toList());

        // 1.2 TopRatedPerformance 파일 목록 조회
        Map<Long, List<FileDto>> fileListByPerformanceIds = fileService.getFilesByIds(EntityType.PERFORMANCE, topRatedPerformanceIds, FileCategory.THUMBNAIL);
        AtomicInteger rankCounter = new AtomicInteger(1);
        topRatedPerformances.forEach(performance -> {
            List<FileDto> files = fileListByPerformanceIds.getOrDefault(performance.getPerformanceId(), List.of());
            performance.setFiles(files);
            performance.setRank(rankCounter.getAndIncrement());
        });


        // 2. TopRatedArtistDto 목록 조회
        List<TopRatedArtistDto> topRatedArtists = artistQueryService.getTopRatedArtists();

        // 2.2 TopRatedArtistDto ID 목록 추출
        List<Long> topRatedArtistIds = topRatedArtists.stream()
                .map(TopRatedArtistDto::getArtistId)
                .collect(Collectors.toList());

        // 2.3 TopRatedArtistDto 파일 목록 조회
        Map<Long, List<FileDto>>  filesByArtistIds = fileService.getFilesByIds(EntityType.ARTIST, topRatedArtistIds, FileCategory.THUMBNAIL);
        topRatedArtists.forEach(artist -> {
            List<FileDto> files = filesByArtistIds.getOrDefault(artist.getArtistId(), List.of());
            artist.setFiles(files);
        });

        // 3. NewPerformanceDto 목록 조회
        List<NewPerformanceDto> newPerformances = performanceQueryService.getNewPerformances();

        // 3.2 NewPerformanceDto ID 목록 추출
        List<Long> newPerformanceIds = newPerformances.stream()
                .map(NewPerformanceDto::getPerformanceId)
                .collect(Collectors.toList());

        // 3.3 NewPerformanceDto 파일 목록 조회
        Map<Long, List<FileDto>>  filesByNewPerformanceIds = fileService.getFilesByIds(EntityType.PERFORMANCE, newPerformanceIds, FileCategory.THUMBNAIL);
        newPerformances.forEach(performance -> {
            List<FileDto> files = filesByNewPerformanceIds.getOrDefault(performance.getPerformanceId(), List.of());
            performance.setFiles(files);
        });

        return HomeResDto.builder()
                .topRatedPerformanceList(topRatedPerformances)
                .topRatedArtistList(topRatedArtists)
                .newPerformanceList(newPerformances)
                .build();

    }
}
