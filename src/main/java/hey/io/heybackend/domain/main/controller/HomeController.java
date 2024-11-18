package hey.io.heybackend.domain.main.controller;

import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.domain.main.dto.HomeResDto;
import hey.io.heybackend.domain.main.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
@Tag(name = "5. Main", description = "메인 홈 API")
public class HomeController {

    private final HomeService homeService;

    /**
     * <p>메인 홈</p>
     *
     * @return 메인 홈 공연, 아티스트 정보
     */
    @GetMapping
    @Operation(summary = "메인 홈", description = "메인 홈을 조회합니다.")
    public ApiResponse<HomeResDto> getHomePerformancesAndArtists() {
        return ApiResponse.success(homeService.getHomePerformancesAndArtists());
    }

}
