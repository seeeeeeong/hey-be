package hey.io.heybackend.domain.artist.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.domain.artist.dto.ArtistPerformanceResponse;
import hey.io.heybackend.domain.artist.dto.QArtistPerformanceResponse;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import java.util.List;

import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;
import static hey.io.heybackend.domain.performance.entity.QPerformanceTicketing.performanceTicketing;
import static hey.io.heybackend.domain.performance.entity.QPlace.place;

@RequiredArgsConstructor
public class ArtistQueryRepositoryImpl implements ArtistQueryRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Slice<ArtistPerformanceResponse> getArtistPerformanceList(Long artistId, String exceptClosed, Pageable pageable, Sort.Direction direction) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(performanceArtist.artist.artistId.eq(artistId));

        if ("y".equals(exceptClosed)) {
            builder.and(performance.performanceStatus.in(PerformanceStatus.READY, PerformanceStatus.ONGOING));
        }

        int pageSize = pageable.getPageSize();

        List<ArtistPerformanceResponse> content = queryFactory.select(
                new QArtistPerformanceResponse(
                        performance.performanceId,
                        performance.name,
                        performanceTicketing.openDatetime.min(),
                        performance.ticketStatus,
                        performance.startDate,
                        performance.endDate,
                        place.name))
                .from(performanceArtist)
                .join(performanceArtist.performance, performance)
                .leftJoin(performanceTicketing)
                .on(performanceTicketing.performance.eq(performance))
                .leftJoin(place)
                .on(performance.place.placeId.eq(place.placeId))
                .where(builder)
                .orderBy(
                        new CaseBuilder()
                                .when(performance.performanceStatus.eq(PerformanceStatus.ONGOING)).then(1)
                                .when(performance.performanceStatus.eq(PerformanceStatus.READY)).then(2)
                                .otherwise(3)
                                .asc()
                )
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
}
