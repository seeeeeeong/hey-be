package hey.io.heybackend.domain.artist.controller;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.common.resolver.AuthUser;
import hey.io.heybackend.common.swagger.ApiErrorCode;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResponse;
import hey.io.heybackend.domain.artist.service.ArtistService;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artists")
@Tag(name = "2. Artist", description = "아티스트 관련 API")
public class ArtistController {

    private final ArtistService artistService;

    /**
     * <p>아티스트 상세 </p>
     *
     * @param artistId 아티스트 ID
     * @return 아티스트 상세 정보
     */
    @Operation(summary = "아티스트 상세", description = "아티스트 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    @ApiErrorCode(ErrorCode.ARTIST_NOT_FOUND)
    public ApiResponse<ArtistDetailResponse> getArtistDetail(@PathVariable("id") Long artistId,
                                                             @RequestParam(value = "status", required = false) List<PerformanceStatus> statuses,
                                                             @AuthUser JwtTokenInfo jwtTokenInfo) {
        return ApiResponse.success(artistService.getArtistDetail(artistId, statuses, jwtTokenInfo));
    }

}
