package hey.io.heybackend.domain.artist.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto;
import hey.io.heybackend.domain.artist.dto.ArtistListResDto;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.follow.enums.FollowType;
import hey.io.heybackend.domain.main.dto.HomeResDto.TopRatedArtistDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto.PerformanceArtistDto;
import hey.io.heybackend.domain.search.dto.SearchReqDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static hey.io.heybackend.domain.artist.entity.QArtist.artist;
import static hey.io.heybackend.domain.follow.entity.QFollow.follow;
import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;
import static hey.io.heybackend.domain.performance.entity.QPerformanceTicketing.performanceTicketing;

public class ArtistQueryRepositoryImpl extends Querydsl5RepositorySupport implements ArtistQueryRepository {

    public ArtistQueryRepositoryImpl(){
        super(Artist.class);
    }


    /**
     * <p>아티스트 상세 조회</p>
     *
     * @param artistId 아티스트 ID
     * @return 아티스트 상세
     */
    @Override
    public Optional<ArtistDetailResDto> findArtistByArtistId(Long artistId) {
        return Optional.ofNullable(
                select(Projections.fields(
                        ArtistDetailResDto.class,
                        artist.artistId,
                        artist.name,
                        artist.engName,
                        artist.artistType,
                        artist.artistUrl,
                        artist.popularity
                ))
                        .from(artist)
                        .where(
                                artist.artistId.eq(artistId),
                                artist.artistStatus.eq(ArtistStatus.ENABLE)
                        )
                        .fetchOne()
        );
    }

    /**
     * <p>공연 아티스트 정보</p>
     *
     * @param performanceId 공연 ID
     * @return 공연 아티스트 정보
     */
    @Override
    public List<PerformanceArtistDto> findPerformanceArtistsByPerformanceId(Long performanceId) {
        return select(Projections.fields(
                PerformanceArtistDto.class,
                artist.artistId,
                artist.name,
                artist.engName,
                artist.artistType
                ))
                .from(artist)
                .leftJoin(artist.performanceArtists, performanceArtist)
                .leftJoin(performanceArtist.performance, performance)
                .where(performance.performanceId.eq(performanceId), artist.artistStatus.eq(ArtistStatus.ENABLE))
                .orderBy(artist.name.asc())
                .fetch();
    }

    /**
     * <p>팔로우 아티스트 목록 조회</p>
     *
     * @param memberId 회원 ID
     * @param pageable
     * @return 팔로우 아티스트 목록
     */
    @Override
    public Slice<ArtistListResDto> findFollowedArtistsByMemberId(Long memberId, Pageable pageable) {
        return applySlicePagination(pageable, queryFactory ->
                queryFactory.select(Projections.fields(
                                ArtistListResDto.class,
                                artist.artistId,
                                artist.name,
                                artist.engName,
                                artist.artistType
                                ))
                        .from(artist)
                        .leftJoin(follow).on(follow.followTargetId.eq(artist.artistId).and(follow.followType.eq(FollowType.PERFORMANCE)))
                        .where(follow.member.memberId.eq(memberId))
                        .where(artist.artistStatus.eq(ArtistStatus.ENABLE))
                        .orderBy(artist.name.asc())
        );
    }

    /**
     * <p>TopRatedArtist 목록 조회</p>
     *
     * @return TopRatedArtist 목록
     */
    @Override
    public List<TopRatedArtistDto> findTopRatedArtists() {
        return select(Projections.fields(
                TopRatedArtistDto.class,
                artist.artistId,
                artist.name.as("artistName")
                ))
                .from(artist)
                .where(artist.artistStatus.eq(ArtistStatus.ENABLE), artist.popularity.isNotNull())
                .orderBy(artist.popularity.desc())
                .limit(5)
                .fetch();
    }

    @Override
    public Slice<ArtistListResDto> findArtistsByKeyword(SearchReqDto request, Pageable pageable) {
        return applySlicePagination(pageable, queryFactory ->
                queryFactory.select(Projections.fields(
                                ArtistListResDto.class,
                                artist.artistId,
                                artist.name,
                                artist.engName,
                                artist.artistType
                        ))
                        .from(artist)
                        .leftJoin(artist.performanceArtists, performanceArtist)
                        .leftJoin(performanceArtist.performance, performance)
                        .leftJoin(performanceTicketing).on(performanceTicketing.performance.eq(performance))
                        .where(
                                artist.artistStatus.eq(ArtistStatus.ENABLE),
                                artist.name.startsWith(request.getKeyword())
                        )
                        .groupBy(
                                artist.artistId,
                                artist.name,
                                artist.engName,
                                artist.artistType
                        )
                        .orderBy(
                                new CaseBuilder()
                                        .when(performanceTicketing.openDatetime.min().isNotNull()
                                                .and(performanceTicketing.openDatetime.min().after(LocalDateTime.now())))
                                        .then(1)
                                        .when(performanceTicketing.openDatetime.min().isNotNull()
                                                .and(performanceTicketing.openDatetime.min().before(LocalDateTime.now())))
                                        .then(2)
                                        .otherwise(3)
                                        .asc(),
                                performanceTicketing.openDatetime.min().asc().nullsLast(),
                                artist.name.asc()
                        )
        );
    }

}
