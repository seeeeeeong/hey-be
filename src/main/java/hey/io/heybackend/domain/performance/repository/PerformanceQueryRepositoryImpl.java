package hey.io.heybackend.domain.performance.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

import static hey.io.heybackend.domain.artist.entity.QArtist.artist;
import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;
import static hey.io.heybackend.domain.performance.entity.QPerformanceGenres.performanceGenres;
import static hey.io.heybackend.domain.performance.entity.QPlace.place;


public class PerformanceQueryRepositoryImpl extends Querydsl5RepositorySupport implements PerformanceQueryRepository {

    public PerformanceQueryRepositoryImpl() {
        super(Performance.class);
    }

    @Override
    public Slice<Performance> getPerformanceList(PerformanceFilterRequest request, Pageable pageable) {
        return applySlicePagination(pageable, queryFactory ->
                queryFactory.selectFrom(performance)
                        .where(
                                performance.performanceStatus.ne(PerformanceStatus.INIT),
                                inType(request.getType()),
                                inGenres(request.getGenres()),
                                inStatuses(request.getStatuses()),
                                inTickets(request.getTickets())
                        )
                        .orderBy(performance.createdAt.desc())
        );
    }

    @Override
    public Optional<Performance> getPerformanceDetail(Long performanceId) {

        Performance performanceDetail = selectFrom(performance)
                .join(performance.place).fetchJoin()
                .leftJoin(performance.performanceArtists, performanceArtist).fetchJoin()
                .leftJoin(performanceArtist.artist, artist).fetchJoin()
                .where(performance.performanceId.eq(performanceId),
                        performance.performanceStatus.ne(PerformanceStatus.INIT))
                .orderBy(artist.name.asc())
                .fetchOne();

        return Optional.ofNullable(performanceDetail);
    }

    private BooleanExpression inType(PerformanceType type) {
        return ObjectUtils.isEmpty(type) ? null : performance.performanceType.in(type);
    }

    private BooleanExpression inGenres(List<PerformanceGenre> genres) {
        return ObjectUtils.isEmpty(genres) ? null : performance.genres.any().performanceGenre.in(genres);
    }

    private BooleanExpression inStatuses(List<PerformanceStatus> statuses) {
        return ObjectUtils.isEmpty(statuses) ? null : performance.performanceStatus.in(statuses);
    }

    private BooleanExpression inTickets(List<TicketStatus> tickets) {
        return ObjectUtils.isEmpty(tickets) ? null : performance.ticketStatus.in(tickets);
    }

}
