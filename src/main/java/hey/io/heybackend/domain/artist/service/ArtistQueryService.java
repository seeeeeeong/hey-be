package hey.io.heybackend.domain.artist.service;

import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto.ArtistGenreDto;
import hey.io.heybackend.domain.artist.dto.ArtistListResDto;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.repository.ArtistGenresRepository;
import hey.io.heybackend.domain.artist.repository.ArtistRepository;
import hey.io.heybackend.domain.main.dto.HomeResDto.TopRatedArtistDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto.PerformanceArtistDto;
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
public class ArtistQueryService {

    private final ArtistRepository artistRepository;
    private final ArtistGenresRepository artistGenresRepository;

    public Artist getArtistByArtistId(Long artistId) {
        return artistRepository.findById(artistId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ARTIST_NOT_FOUND));
    }

    public ArtistDetailResDto getArtistDetailByArtistId(Long artistId) {
        return artistRepository.findArtistByArtistId(artistId).orElseThrow(() -> new BusinessException(ErrorCode.ARTIST_NOT_FOUND));
    }

    public List<PerformanceArtistDto> getPerformanceArtistsByPerformanceId(Long performanceId) {
        return artistRepository.findPerformanceArtistsByPerformanceId(performanceId);
    }

    public Slice<ArtistListResDto> getFollowedArtistsByMemberId(Long memberId, Pageable pageable) {
        return artistRepository.findFollowedArtistsByMemberId(memberId, pageable);
    }

    public List<TopRatedArtistDto> getTopRatedArtists() {
        return artistRepository.findTopRatedArtists();
    }

    public List<ArtistGenreDto> getArtistGenresByArtist(Artist artist) {
        return artistGenresRepository.findArtistGenresByArtist(artist);
    }

    public Slice<ArtistListResDto> searchArtistsByKeyword(SearchReqDto request, Pageable pageable) {
        return artistRepository.findArtistsByKeyword(request, pageable);
    }
}
