package hey.io.heybackend.domain.performance.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.performance.dto.*;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformanceArtist;
import hey.io.heybackend.domain.performance.entity.PerformanceGenres;
import hey.io.heybackend.domain.performance.entity.PerformanceTicketing;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static hey.io.heybackend.domain.artist.entity.QArtist.artist;
import static hey.io.heybackend.domain.file.entity.QFile.file;
import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;
import static hey.io.heybackend.domain.performance.entity.QPerformanceGenres.performanceGenres;
import static hey.io.heybackend.domain.performance.entity.QPerformancePrice.performancePrice;
import static hey.io.heybackend.domain.performance.entity.QPerformanceTicketing.performanceTicketing;
import static hey.io.heybackend.domain.performance.entity.QPlace.place;


@RequiredArgsConstructor
public class PerformanceQueryRepositoryImpl implements PerformanceQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Performance> getPerformanceList(PerformanceFilterRequest request, Pageable pageable) {
        int pageSize = pageable.getPageSize();

        List<Performance> performanceList = queryFactory.selectFrom(performance)
                .where(
                        performance.performanceStatus.ne(PerformanceStatus.INIT),
                        inType(request.getType()),
                        inGenres(request.getGenres()),
                        inStatuses(request.getStatuses()),
                        inTickets(request.getTickets())
                )
                .orderBy(performance.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = false;
        if (performanceList.size() > pageSize) {
            performanceList.remove(pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(performanceList, pageable, hasNext);
    }

    @Override
    public Performance getPerformanceDetail(Long performanceId) {

        Performance performanceDetail = queryFactory.selectFrom(performance)
                .leftJoin(performance.performanceArtists, performanceArtist)
                .fetchJoin()
                .where(performance.performanceId.eq(performanceId),
                        performance.performanceStatus.ne(PerformanceStatus.INIT),
                        performanceArtist.artist.artistStatus.ne(ArtistStatus.INIT)
                )
                .orderBy(performanceArtist.artist.name.asc())
                .fetchOne();

        return performanceDetail;
    }


    private BooleanExpression inType(PerformanceType type) {
        return ObjectUtils.isEmpty(type) ? null : performance.performanceType.in(type);
    }

    private BooleanExpression inGenres(List<PerformanceGenre> genres) {
        return ObjectUtils.isEmpty(genres) ? null : performanceGenres.performanceGenre.in(genres);
    }

    private BooleanExpression inStatuses(List<PerformanceStatus> statuses) {
        return ObjectUtils.isEmpty(statuses) ? null : performance.performanceStatus.in(statuses);
    }

    private BooleanExpression inTickets(List<TicketStatus> tickets) {
        return ObjectUtils.isEmpty(tickets) ? null : performance.ticketStatus.in(tickets);
    }

}
