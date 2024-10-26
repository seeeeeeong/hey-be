package hey.io.heybackend.domain.artist.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static hey.io.heybackend.domain.artist.entity.QArtist.artist;
import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;
import static hey.io.heybackend.domain.performance.entity.QPerformanceTicketing.performanceTicketing;
import static hey.io.heybackend.domain.performance.entity.QPlace.place;

@RequiredArgsConstructor
public class ArtistQueryRepositoryImpl implements ArtistQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Performance> getArtistPerformanceList(Long artistId, String exceptClosed, Pageable pageable) {
        int pageSize = pageable.getPageSize();

        List<Performance> performanceList = queryFactory.selectFrom(performance)
                .where(
                        performance.performanceArtists.any().artist.artistId.eq(artistId),
//                        performance.performanceStatus.ne(PerformanceStatus.INIT),
//                        performance.performanceArtists.any().artist.artistStatus.ne(ArtistStatus.INIT),
                        inExcept(exceptClosed)
                )
                .leftJoin(performance.performanceArtists, performanceArtist)
                .leftJoin(performanceArtist.artist, artist)
                .leftJoin(performanceTicketing)
                .on(performanceTicketing.performance.eq(performance))
                .orderBy(
                        new CaseBuilder()
                                .when(performance.performanceStatus.eq(PerformanceStatus.ONGOING)).then(1)
                                .when(performance.performanceStatus.eq(PerformanceStatus.READY)).then(2)
                                .otherwise(3)
                                .asc()
                )
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


    private BooleanExpression inExcept(String exceptClosed) {
        return ObjectUtils.isEmpty(exceptClosed) ? null : performance.performanceStatus.in(PerformanceStatus.READY, PerformanceStatus.ONGOING);
    }

}
