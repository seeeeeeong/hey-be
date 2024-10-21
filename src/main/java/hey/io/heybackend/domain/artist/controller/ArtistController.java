package hey.io.heybackend.domain.artist.controller;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.domain.artist.dto.GetArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.GetArtistPerformanceListResponse;
import hey.io.heybackend.domain.artist.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping("/{id}")
    public ResponseEntity<GetArtistDetailResponse> getArtistDetail(@PathVariable("id") Long artistId) {
        GetArtistDetailResponse getArtistDetailResponse = artistService.getArtistDetail(artistId);
        return ResponseEntity.status(HttpStatus.OK).body(getArtistDetailResponse);
    }

    @GetMapping("/{id}/performances")
    public ResponseEntity<SliceResponse<GetArtistPerformanceListResponse>> getArtistPerformanceList(@PathVariable("id") Long artistId,
                                                                                                @RequestParam(value = "except_closed", required = false) String exceptClosed,
                                                                                                @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                                                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                                @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        SliceResponse<GetArtistPerformanceListResponse> getArtistPerformanceListResponse
                = artistService.getArtistPerformanceList(artistId, exceptClosed, size, page, direction);

        return ResponseEntity.status(HttpStatus.OK).body(getArtistPerformanceListResponse);
    }

}
