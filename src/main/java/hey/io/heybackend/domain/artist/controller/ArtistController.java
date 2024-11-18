package hey.io.heybackend.domain.artist.controller;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.swagger.ApiErrorCode;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto;
import hey.io.heybackend.domain.artist.service.ArtistService;
import hey.io.heybackend.domain.member.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artists")
@Tag(name = "2. Artist", description = "아티스트 관련 API")
public class ArtistController {

    private final ArtistService artistService;

    /**
     * <p>아티스트 상세</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param artistId 아티스트 ID
     * @return 아티스트 상세 정보
     */
    @GetMapping("/{id}")
    @ApiErrorCode(ErrorCode.ARTIST_NOT_FOUND)
    @Operation(summary = "아티스트 상세 조회", description = "아티스트 상세 정보를 조회합니다.")
    public ApiResponse<ArtistDetailResDto> getArtistDetail(@AuthenticationPrincipal MemberDto memberDto,
                                                           @PathVariable("id") Long artistId) {
        return ApiResponse.success(artistService.getArtistDetail(memberDto, artistId));
    }

}
