package hey.io.heybackend.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class SliceResponse<T> {

    @Schema(description = "응답 데이터")
    private final List<T> content = new ArrayList<>();

    @Schema(description = "현재 페이지 번호", example = "0")
    private final int number;

    @Schema(description = "페이지 크기", example = "20")
    private final int size;

    @Schema(description = "첫 번째 페이지 여부", example = "true")
    private final boolean first;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private final boolean last;

    public SliceResponse(List<T> content, Pageable pageable, boolean hasNext) {
        this.content.addAll(content);
        this.number = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.first = getNumber() == 0;
        this.last = !hasNext;
    }
}
