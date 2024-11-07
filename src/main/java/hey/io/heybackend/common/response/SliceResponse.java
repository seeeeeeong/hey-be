package hey.io.heybackend.common.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class SliceResponse<T> {

    @Schema(description = "응답 데이터")
    private final List<T> content;

    @Schema(description = "현재 페이지 번호", example = "0")
    private final int currentPage;

    @Schema(description = "페이지 크기", example = "20")
    private final int size;

    @Schema(description = "첫 번째 페이지 여부", example = "true")
    private final boolean first;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private final boolean last;

    public SliceResponse(Slice<T> sliceContent) {
        this.content = sliceContent.getContent();
        this.currentPage = sliceContent.getNumber();
        this.size = sliceContent.getSize();
        this.first = sliceContent.isFirst();
        this.last = sliceContent.isLast();
    }

    @JsonCreator
    public SliceResponse(
            @JsonProperty("content") List<T> content,
            @JsonProperty("currentPage") int currentPage,
            @JsonProperty("size") int size,
            @JsonProperty("first") boolean first,
            @JsonProperty("last") boolean last) {
        this.content = content;
        this.currentPage = currentPage;
        this.size = size;
        this.first = first;
        this.last = last;
    }

    public SliceResponse(List<T> content, Pageable pageable, boolean hasNext) {
        this.content = content;
        this.currentPage = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.first = pageable.getPageNumber() == 0;
        this.last = !hasNext;
    }

}
