package hey.io.heybackend.domain.performance.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.domain.performance.dto.*;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Supplier;

import static hey.io.heybackend.domain.artist.entity.QArtist.artist;
import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;
import static hey.io.heybackend.domain.performance.entity.QPerformanceGenres.performanceGenres;
import static hey.io.heybackend.domain.performance.entity.QPerformanceTicketing.performanceTicketing;
import static hey.io.heybackend.domain.performance.entity.QPlace.place;

@RequiredArgsConstructor
public class PerformanceQueryRepositoryImpl implements PerformanceQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<GetPerformanceListResponse> getPerformanceList(PerformanceFilterRequest request, Pageable pageable, Sort.Direction direction) {
        BooleanBuilder builder = new BooleanBuilder();
        int pageSize = pageable.getPageSize();

        if (!StringUtils.isEmpty(request.getPerformanceType())) {
            builder.and(performance.performanceType.in(request.getPerformanceType()));
        }

        List<GetPerformanceListResponse> content = queryFactory.select(
                        new QGetPerformanceListResponse(
                                performance.performanceId,
                                performance.name,
                                performanceTicketing.openDatetime.min(),
                                performance.ticketStatus,
                                performance.startDate,
                                performance.endDate,
                                place.name
                        )
                )
                .distinct()
                .from(performance)
                .leftJoin(performanceTicketing)
                .on(performanceTicketing.performance.eq(performance))
                .leftJoin(place)
                .on(performance.place.placeId.eq(place.placeId))
                .where(builder.and(eqFilter(request)))
                .groupBy(performance.performanceId, performance.name, performance.ticketStatus, performance.startDate, performance.endDate, place.name)
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

    @Override
    public Slice<GetPerformanceArtistListResponse> getPerformanceArtistList(Long performanceId, Pageable pageable, Sort.Direction direction) {

        int pageSize = pageable.getPageSize();

        List<GetPerformanceArtistListResponse> content = queryFactory.select(
                new QGetPerformanceArtistListResponse(artist.artistId, artist.name))
                .from(performanceArtist)
                .join(performanceArtist.artist, artist)
                .where(performanceArtist.performance.performanceId.eq(performanceId))
                .orderBy(artist.name.asc())
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

    private BooleanBuilder eqPerformanceType(PerformanceType performanceType) {
        return nullSafeBooleanBuilder(() ->
                ObjectUtils.isEmpty(performanceType) ? null : performance.performanceType.in(performanceType)
        );
    }

    private BooleanBuilder eqPerformanceGenre(List<PerformanceGenre> performanceGenre) {
        return nullSafeBooleanBuilder(() ->
                ObjectUtils.isEmpty(performanceGenre) ? null : performanceGenres.performanceGenre.in(performanceGenre)
        );
    }

    private BooleanBuilder eqPerformanceStatus(List<PerformanceStatus> performanceStatus) {
        return nullSafeBooleanBuilder(() ->
                ObjectUtils.isEmpty(performanceStatus) ? null : performance.performanceStatus.in(performanceStatus)
        );
    }

    private BooleanBuilder eqTicketStatus(List<TicketStatus> ticketStatus) {
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
