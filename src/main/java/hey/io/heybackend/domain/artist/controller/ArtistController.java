package hey.io.heybackend.domain.artist.controller;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.response.PageRequest;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.swagger.ApiErrorCode;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistListResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistSearchCondition;
import hey.io.heybackend.domain.artist.service.ArtistService;
import hey.io.heybackend.domain.member.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
     * <p>아티스트 목록</p>
     *
     * @param searchCondition 조회 조건
     * @param memberDto       회원 정보
     * @param pageRequest     페이징 정보
     * @return 아티스트 목록
     */
    @GetMapping
    @Operation(summary = "아티스트 목록", description = "아티스트 목록을 조회합니다.")
    public ApiResponse<SliceResponse<ArtistListResponse>> searchArtistList(
        @ParameterObject ArtistSearchCondition searchCondition, @Parameter(hidden = true) MemberDto memberDto,
        @Valid @ParameterObject PageRequest pageRequest) {
        return ApiResponse.success(artistService.searchArtistSliceList(searchCondition, memberDto, pageRequest));
    }

    /**
     * <p>아티스트 상세</p>
     *
     * @param artistId  아티스트 ID
     * @param memberDto 회원 정보
     * @return 아티스트 상세 정보
     */
    @Operation(summary = "아티스트 상세", description = "아티스트 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    @ApiErrorCode(ErrorCode.ARTIST_NOT_FOUND)
    public ApiResponse<ArtistDetailResponse> getArtistDetail(@PathVariable("id") Long artistId,
        @Parameter(hidden = true) MemberDto memberDto) {
        return ApiResponse.success(artistService.getArtistDetail(artistId, memberDto));
    }
}
