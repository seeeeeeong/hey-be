package hey.io.heybackend.domain.artist.repository;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static hey.io.heybackend.domain.artist.entity.QArtist.artist;
import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;

public class ArtistQueryRepositoryImpl extends Querydsl5RepositorySupport implements ArtistQueryRepository {

    public ArtistQueryRepositoryImpl(){
        super(Artist.class);
    }


    /**
     * <p>아티스트 상세 정보</p>
     *
     * Artist의 Performance 데이터를 함께 조회
     * 공연 상태에 따른 정렬 조건 적용하여 조회
     *
     * @return Artist
     */
    @Override
    public Optional<Artist> getArtistDetail(Long artistId) {

        Artist artistDetail = selectFrom(artist)
                .leftJoin(artist.performanceArtists, performanceArtist).fetchJoin()
                .leftJoin(performanceArtist.performance, performance).fetchJoin()
                .where(artist.artistId.eq(artistId),
                        artist.artistStatus.eq(ArtistStatus.ENABLE))
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
