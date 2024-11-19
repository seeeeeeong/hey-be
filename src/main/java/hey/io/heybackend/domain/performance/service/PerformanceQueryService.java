package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto.ArtistPerformanceDto;
import hey.io.heybackend.domain.main.dto.HomeResDto.NewPerformanceDto;
import hey.io.heybackend.domain.main.dto.HomeResDto.TopRatedPerformanceDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto;
import hey.io.heybackend.domain.performance.dto.PerformanceListReqDto;
import hey.io.heybackend.domain.performance.dto.PerformanceListResDto;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.repository.PerformanceGenresRepository;
import hey.io.heybackend.domain.performance.repository.PerformancePriceRepository;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import hey.io.heybackend.domain.performance.repository.PerformanceTicketingRepository;
import hey.io.heybackend.domain.search.dto.SearchReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceQueryService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceGenresRepository performanceGenresRepository;
    private final PerformancePriceRepository performancePriceRepository;
    private final PerformanceTicketingRepository performanceTicketingRepository;

    public Performance getPerformance(Long performanceId) {
        return performanceRepository.findById(performanceId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFORMANCE_NOT_FOUND));
    }

    public Slice<PerformanceListResDto> getPerformancesByCondition(PerformanceListReqDto request, Pageable pageable) {
        return performanceRepository.findPerformancesByCondition(request, pageable);
    }

    public PerformanceDetailResDto getPerformanceDetailByPerformanceId(Long performanceId) {
        return performanceRepository.findPerformanceDetailByPerformanceId(performanceId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFORMANCE_NOT_FOUND));
    }

    public Slice<PerformanceListResDto> searchPerformancesByKeyword(SearchReqDto request, Pageable pageable) {
        return performanceRepository.findPerformancesByKeyword(request, pageable);
    }

    public List<ArtistPerformanceDto> getArtistPerformancesByArtistId(Long artistId) {
        return performanceRepository.findArtistPerformancesByArtistId(artistId);
    }

    public Slice<PerformanceListResDto> getFollowedPerformancesByMemberId(Long memberId, Pageable pageable) {
        return performanceRepository.findFollowedPerformancesByMemberId(memberId, pageable);
    }

    public List<TopRatedPerformanceDto> getTopRatedPerformances() {
        return performanceRepository.findTopRatedPerformances();
    }

    public List<NewPerformanceDto> getNewPerformances() {
        return performanceRepository.findNewPerformances();
    }

    public List<PerformanceDetailResDto.PerformanceGenreDto> getPerformanceGenresByPerformance(Performance performance) {
        return performanceGenresRepository.findPerformanceGenresByPerformance(performance);
    }

    public List<PerformanceDetailResDto.PerformancePriceDto> getPerformancePricesByPerformance(Performance performance) {
        return performancePriceRepository.findPerformancePriceByPerformance(performance);
    }

    public List<PerformanceDetailResDto.PerformanceTicketingDto> getPerformanceTicketingsByPerformance(Performance performance) {
        return performanceTicketingRepository.findPerformanceTicketingByPerformance(performance);
    }

}
