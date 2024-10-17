package hey.io.heybackend.domain.performance.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.QPerformanceListResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceGenres.performanceGenres;
import static hey.io.heybackend.domain.performance.entity.QPerformanceTicketing.performanceTicketing;
import static hey.io.heybackend.domain.performance.entity.QPlace.place;

@RequiredArgsConstructor
public class PerformanceQueryRepositoryImpl implements PerformanceQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PerformanceListResponse> getPerformances(PerformanceFilterRequest filter) {
        BooleanBuilder builder = new BooleanBuilder();

        if (filter.getPerformanceGenre() != null && !filter.getPerformanceGenre().isEmpty()) {
            builder.and(performanceGenres.performanceGenre.in(filter.getPerformanceGenre()));
        }

        if (filter.getPerformanceStatus() != null && !filter.getPerformanceStatus().isEmpty()) {
            builder.and(performance.performanceStatus.in(filter.getPerformanceStatus()));
        }

        if (filter.getTicketStatus() != null && !filter.getTicketStatus().isEmpty()) {
            builder.and(performance.ticketStatus.in(filter.getTicketStatus()));
        }

        List<PerformanceListResponse> performanceList = queryFactory.select(
                new QPerformanceListResponse(performance.performanceId, performance.name, performanceTicketing.openDatetime, performance.ticketStatus, performance.startDate, performance.endDate, place.name))
                .distinct()
                .from(performance)
                .leftJoin(performanceTicketing).on(performance.performanceId.eq(performanceTicketing.performanceId))
                .leftJoin(place).on(performance.placeId.eq(place.placeId))
                .where(builder)
                .fetch();

        return performanceList;
    }

}
