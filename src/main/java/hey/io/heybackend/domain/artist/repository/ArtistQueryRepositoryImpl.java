package hey.io.heybackend.domain.artist.repository;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static hey.io.heybackend.domain.artist.entity.QArtist.artist;
import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;

@RequiredArgsConstructor
public class ArtistQueryRepositoryImpl implements ArtistQueryRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<Artist> getArtistDetail(Long artistId) {

        Artist artistDetail = queryFactory.selectFrom(artist)
                .leftJoin(artist.performanceArtists, performanceArtist)
                .fetchJoin()
                .where(artist.artistId.eq(artistId),
                        artist.artistStatus.ne(ArtistStatus.INIT),
                        performanceArtist.performance.performanceStatus.ne(PerformanceStatus.INIT))
                .orderBy(
                        new CaseBuilder()
                                .when(performance.performanceStatus.eq(PerformanceStatus.ONGOING)).then(1)
                                .when(performance.performanceStatus.eq(PerformanceStatus.READY)).then(2)
                                .otherwise(3).asc(),
                        performance.createdAt.desc()
                )
                .fetchOne();

        return Optional.ofNullable(artistDetail);
    }


}
