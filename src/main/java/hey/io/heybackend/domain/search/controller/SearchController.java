package hey.io.heybackend.domain.search.controller;


import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.dto.ArtistListResDto;
import hey.io.heybackend.domain.member.dto.MemberDto;
import hey.io.heybackend.domain.performance.dto.PerformanceListResDto;
import hey.io.heybackend.domain.search.dto.SearchReqDto;
import hey.io.heybackend.domain.search.service.SearchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Tag(name = "6. Search", description = "통합검색 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/performances")
    public ApiResponse<SliceResponse<PerformanceListResDto>> searchPerformanceList(@AuthenticationPrincipal MemberDto memberDto,
                                                                                   @Valid SearchReqDto request,
                                                                                   @ParameterObject Pageable pageable) {
        return ApiResponse.success(searchService.searchPerformanceList(memberDto, request, pageable));
    }

    @GetMapping("/artists")
    public ApiResponse<SliceResponse<ArtistListResDto>> searchArtistList(@AuthenticationPrincipal MemberDto memberDto,
                                                                         @Valid SearchReqDto request,
                                                                         @ParameterObject Pageable pageable) {
        return ApiResponse.success(searchService.searchArtistList(memberDto, request, pageable));
    }

}
