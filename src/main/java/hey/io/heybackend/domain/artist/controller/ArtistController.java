package hey.io.heybackend.domain.artist.controller;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.common.resolver.AuthUser;
import hey.io.heybackend.common.swagger.ApiErrorCode;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.ArtistPerformanceResponse;
import hey.io.heybackend.domain.artist.service.ArtistService;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;

    /**
     * <p>아티스트 상세 조회</p>
     *
     * @param artistId 아티스트 ID
     * @return {@link ApiResponse} 아티스트 상세 정보
     */
    @GetMapping("/{id}")
    @ApiErrorCode(ErrorCode.ARTIST_NOT_FOUND)
    public ApiResponse<ArtistDetailResponse> getArtistDetail(@PathVariable("id") Long artistId) {
        return ApiResponse.success(artistService.getArtistDetail(artistId));
    }

    /**
     * <p>아티스트 공연 목록</p>
     *
     * @param artistId 아티스트 ID
     * @param statuses 공연 상태 필터 (예: ONGOING, READY 등)
     * @param jwtTokenInfo 인증된 사용자의 JWT 토큰 정보
     * @param pageable 페이징 정보
     * @return {@link ApiResponse} 아티스트 공연 목록
     */
    @GetMapping("/{id}/performances")
    public ApiResponse<SliceResponse<ArtistPerformanceResponse>> getArtistPerformanceList(@PathVariable("id") Long artistId,
                                                                                          @RequestParam(value = "status", required = false) List<PerformanceStatus> statuses,
                                                                                          @AuthUser JwtTokenInfo jwtTokenInfo,
                                                                                          Pageable pageable) {
        return ApiResponse.success(artistService.getArtistPerformanceList(artistId, statuses, jwtTokenInfo, pageable));
    }

}
