package hey.io.heybackend.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Schema(description = "페이징 정보")
public class PageRequest implements Pageable {

    @NotNull
    @Min(value = 0, message = "페이지 번호는 0보다 작을 수 없습니다.")
    @Schema(description = "페이지 번호 (0..N)", defaultValue = "0", type = "integer")
    private final Integer page;

    @NotNull
    @Min(value = 1, message = "페이지 크기는 1보다 작을 수 없습니다.")
    @Max(value = 100, message = "페이지 크기는 10보다 클 수 없습니다.")
    @Schema(description = "페이지 크기 (1~100)", defaultValue = "20", type = "integer")
    private final Integer size;

    @Schema(description = "정렬 (예: 'name,asc|desc')", hidden = true)
    private final Sort sort;

    protected PageRequest(int page, int size, Sort sort) {
        this.page = page;
        this.size = size;
        this.sort = sort != null ? sort : Sort.unsorted();
    }

    public static PageRequest of() {
        return ofSize(20);
    }

    public static PageRequest ofSize(int size) {
        return of(0, size);
    }

    public static PageRequest of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    public static PageRequest of(int page, int size, Sort.Direction direction, String... properties) {
        return of(page, size, Sort.by(direction, properties));
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return new PageRequest(page, size, sort);
    }

    public int getPageNumber() {
        return this.page;
    }

    public int getPageSize() {
        return this.size;
    }

    @Override
    public long getOffset() {
        return (long) this.page * (long) this.size;
    }

    @Override
    @NonNull
    public Pageable next() {
        return of(this.page + 1, this.size, this.sort);
    }

    public Pageable previous() {
        return this.page == 0 ? this : of(this.page - 1, this.size, this.sort);
    }

    @Override
    @NonNull
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    @NonNull
    public Pageable first() {
        return of(0, this.size, this.sort);
    }

    @Override
    @NonNull
    public Pageable withPage(int page) {
        return of(page, this.size, this.sort);
    }

    @Override
    public boolean hasPrevious() {
        return this.page > 0;
    }
}
