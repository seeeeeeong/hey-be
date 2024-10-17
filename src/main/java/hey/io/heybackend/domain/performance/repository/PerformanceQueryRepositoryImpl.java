package hey.io.heybackend.domain.performance.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.QPerformanceListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Supplier;

import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceGenres.performanceGenres;
import static hey.io.heybackend.domain.performance.entity.QPerformanceTicketing.performanceTicketing;
import static hey.io.heybackend.domain.performance.entity.QPlace.place;

@RequiredArgsConstructor
public class PerformanceQueryRepositoryImpl implements PerformanceQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PerformanceListResponse> getPerformances(PerformanceFilterRequest filter, Pageable pageable, Sort.Direction direction) {
        BooleanBuilder builder = new BooleanBuilder();

        int pageSize = pageable.getPageSize();

        if (!StringUtils.isEmpty(filter.getPerformanceType())) {
            builder.and(performance.performanceType.in(filter.getPerformanceType()));
        }

        List<PerformanceListResponse> content = queryFactory.select(
                        new QPerformanceListResponse(performance.performanceId, performance.name, performanceTicketing.openDatetime, performance.ticketStatus, performance.startDate, performance.endDate, place.name))
                .distinct()
                .from(performance)
                .leftJoin(performanceTicketing)
                .on(performance.performanceId.eq(performanceTicketing.performanceId))
                .leftJoin(place)
                .on(performance.placeId.eq(place.placeId))
                .where(eqFilter(filter))
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageSize) {
            content.remove(pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    public BooleanBuilder eqFilter(PerformanceFilterRequest filter) {
        return eqPerformanceType(filter.getPerformanceType())
                .and(eqPerformanceGenre(filter.getPerformanceGenre()))
                .and(eqPerformanceStatus(filter.getPerformanceStatus()))
                .and(eqTicketStatus(filter.getTicketStatus()));
    }

    private BooleanBuilder eqPerformanceType(String performanceType) {
        return nullSafeBooleanBuilder(() ->
                ObjectUtils.isEmpty(performanceType) ? null : performance.performanceType.in(performanceType)
        );
    }

    private BooleanBuilder eqPerformanceGenre(List<String> performanceGenre) {
        return nullSafeBooleanBuilder(() ->
                ObjectUtils.isEmpty(performanceGenre) ? null : performanceGenres.performanceGenre.in(performanceGenre)
        );
    }

    private BooleanBuilder eqPerformanceStatus(List<String> performanceStatus) {
        return nullSafeBooleanBuilder(() ->
                ObjectUtils.isEmpty(performanceStatus) ? null : performance.performanceStatus.in(performanceStatus)
        );
    }

    private BooleanBuilder eqTicketStatus(List<String> ticketStatus) {
        return nullSafeBooleanBuilder(() ->
                ObjectUtils.isEmpty(ticketStatus) ? null : performance.ticketStatus.in(ticketStatus)
        );
    }

    private BooleanBuilder nullSafeBooleanBuilder(Supplier<BooleanExpression> supplier) {
        try {
            BooleanExpression expression = supplier.get();
            return expression != null ? new BooleanBuilder(expression) : new BooleanBuilder();
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }
}
