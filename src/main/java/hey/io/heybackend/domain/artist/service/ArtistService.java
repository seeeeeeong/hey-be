package hey.io.heybackend.domain.artist.service;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.domain.artist.dto.GetArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.GetArtistPerformanceListResponse;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;

    public GetArtistDetailResponse getArtistDetail(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException());

        return GetArtistDetailResponse.from(artist);
    }

    public SliceResponse<GetArtistPerformanceListResponse> getArtistPerformanceList(Long artistId, String exceptClosed, int size, int page, Sort.Direction direction) {
        Slice<GetArtistPerformanceListResponse> getArtistPerformanceListResponse = artistRepository.getArtistPerformanceList(artistId, exceptClosed, Pageable.ofSize(size).withPage(page), direction);
        return new SliceResponse<>(getArtistPerformanceListResponse);
    }

}
