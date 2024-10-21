package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.performance.dto.GetPerformanceArtistListResponse;
import hey.io.heybackend.domain.performance.dto.GetPerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.GetPerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformanceArtist;
import hey.io.heybackend.domain.performance.entity.Place;
import hey.io.heybackend.domain.performance.repository.PerformanceGenresRepository;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    public SliceResponse<GetPerformanceListResponse> getPerformanceList(PerformanceFilterRequest request, int size, int page, Sort.Direction direction) {

        Slice<GetPerformanceListResponse> performanceList = performanceRepository.getPerformanceList(request, Pageable.ofSize(size).withPage(page), direction);
        return new SliceResponse<>(performanceList);

    }

    public GetPerformanceDetailResponse getPerformanceDetail(Long performanceId) {
        Performance performance = performanceRepository.findWithPlaceByPerformanceId(performanceId)
                .orElseThrow(() -> new IllegalArgumentException());

        Place place = performance.getPlace();
        if (!ObjectUtils.isEmpty(place)) {
            GetPerformanceDetailResponse.builder()
                    .placeName(place.getName())
                    .address(place.getAddress())
                    .latitude(place.getLatitude())
                    .longitude(place.getLongitude())
                    .build();
        }

        return GetPerformanceDetailResponse.from(performance);
    }

    public SliceResponse<GetPerformanceArtistListResponse> getPerformanceArtistList(Long performanceId, int size, int page, Sort.Direction direction) {

        Slice<GetPerformanceArtistListResponse> performanceArtistListResponse = performanceRepository.getPerformanceArtistList(performanceId, Pageable.ofSize(size).withPage(page), direction);
        return new SliceResponse<>(performanceArtistListResponse);

    }


}
