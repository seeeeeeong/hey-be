package hey.io.heybackend.domain.artist.controller;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.domain.artist.dto.GetArtistDetailResponse;
import hey.io.heybackend.domain.artist.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
