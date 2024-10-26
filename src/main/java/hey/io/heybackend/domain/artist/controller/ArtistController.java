package hey.io.heybackend.domain.artist.controller;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.common.resolver.AuthUser;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.ArtistPerformanceResponse;
import hey.io.heybackend.domain.artist.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;

    /**
     * <p>특정 아티스트의 상세 정보를 조회합니다.</p>
     *
     * @param artistId 아티스트의 ID
     * @param jwtTokenInfo JWT 토큰 정보 (인증용)
     * @return {@link ResponseEntity} 객체, 아티스트 상세 정보를 포함
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtistDetailResponse> getArtistDetail(@PathVariable("id") Long artistId,
                                                                @AuthUser JwtTokenInfo jwtTokenInfo) {
        ArtistDetailResponse artistDetailResponse = artistService.getArtistDetail(artistId, jwtTokenInfo);
        return ResponseEntity.status(HttpStatus.OK).body(artistDetailResponse);
    }

    /**
     * <p>특정 아티스트의 공연 목록을 조회합니다.</p>
     *
     * @param artistId 아티스트의 ID
     * @param exceptClosed 종료된 공연을 제외할지 여부 (optional)
     * @param pageable 페이징 정보
     * @return {@link ResponseEntity} 객체, 아티스트의 공연 목록을 포함
     */
    @GetMapping("/{id}/performances")
    public ResponseEntity<SliceResponse<ArtistPerformanceResponse>> getArtistPerformanceList(@PathVariable("id") Long artistId,
                                                                                             @RequestParam(value = "except_closed", required = false) String exceptClosed,
                                                                                             Pageable pageable) {
        SliceResponse<ArtistPerformanceResponse> getArtistPerformanceListResponse
                = artistService.getArtistPerformanceList(artistId, exceptClosed, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(getArtistPerformanceListResponse);
    }

}
