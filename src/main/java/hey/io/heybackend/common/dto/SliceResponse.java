package hey.io.heybackend.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class SliceResponse<T> {

    private final List<T> content;
    private final int currentPage;
    private final int size;
    private final boolean first;
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

}
