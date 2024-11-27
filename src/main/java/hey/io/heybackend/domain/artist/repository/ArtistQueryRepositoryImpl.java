package hey.io.heybackend.domain.artist.repository;

import static hey.io.heybackend.domain.artist.entity.QArtist.artist;
import static hey.io.heybackend.domain.artist.entity.QArtistGenres.artistGenres;
import static hey.io.heybackend.domain.member.entity.QFollow.follow;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistListResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistSearchCondition;
import hey.io.heybackend.domain.artist.dto.QArtistDto_ArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.QArtistDto_ArtistListResponse;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistGenre;
import hey.io.heybackend.domain.artist.enums.ArtistSortType;
import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.member.enums.FollowType;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

public class ArtistQueryRepositoryImpl extends Querydsl5RepositorySupport implements ArtistQueryRepository {

    public ArtistQueryRepositoryImpl() {
        super(Artist.class);
    }

    /**
     * <p>아티스트 목록 (Slice)</p>
     *
     * @param searchCondition 조회 조건
     * @param memberId        회원 ID
     * @param pageable        페이징 정보
     * @return Slice 아티스트 목록
     */
    @Override
    public SliceResponse<ArtistListResponse> selectArtistSliceList(ArtistSearchCondition searchCondition, Long memberId,
        Pageable pageable) {
        return applySlicePagination(pageable,
            queryFactory -> getArtistListQuery(searchCondition, memberId, queryFactory));
    }

    /**
     * <p>아티스트 목록</p>
     *
     * @param searchCondition 조회 조건
     * @param memberId        회원 ID
     * @return 아티스트 목록
     */
    @Override
    public List<ArtistListResponse> selectArtistList(ArtistSearchCondition searchCondition, Long memberId) {
        return getArtistListQuery(searchCondition, memberId, getQueryFactory()).fetch();
    }

    /**
     * <p>아티스트 목록 쿼리</p>
     *
     * @param searchCondition 조회 조건
     * @param memberId        회원 ID
     * @param queryFactory    쿼리 Factory
     * @return 생성된 쿼리문
     */
    private JPAQuery<ArtistListResponse> getArtistListQuery(ArtistSearchCondition searchCondition, Long memberId,
        JPAQueryFactory queryFactory) {
        return queryFactory
            .select(new QArtistDto_ArtistListResponse(
                artist.artistId,
                artist.name.as("artistName"),
                artist.engName,
                artist.artistType,
                follow.followTargetId.isNotNull().as("isFollow")))
            .from(artist)
            .leftJoin(follow)
            .on(
                follow.followType.eq(FollowType.ARTIST),
                artist.artistId.eq(follow.followTargetId),
                memberIdEq(memberId)
            )
            .where(
                artist.artistStatus.eq(ArtistStatus.ENABLE),
                artistTypeIn(searchCondition.getArtistType()),
                artistGenreIn(searchCondition.getArtistGenre()),
                artistNameLike(searchCondition.getArtistName()),
                artistPerformanceEq(searchCondition.getPerformanceId())
            )
            .orderBy(artistSortTypeAs(searchCondition.getSortType()));
    }

    /**
     * <p>아티스트 목록 조회 조건 : 아티스트 유형</p>
     *
     * @param artistTypes 아티스트 유형
     * @return BooleanExpression
     */
    private BooleanExpression artistTypeIn(List<ArtistType> artistTypes) {
        return !ObjectUtils.isEmpty(artistTypes) ? artist.artistType.in(artistTypes) : null;
    }

    /**
     * <p>아티스트 목록 조회 조건 : 아티스트 장르</p>
     *
     * @param genres 아티스트 장르
     * @return BooleanExpression
     */
    private BooleanExpression artistGenreIn(List<ArtistGenre> genres) {
        return !ObjectUtils.isEmpty(genres) ?
            artist.artistId.in(
                JPAExpressions.select(artistGenres.artist.artistId)
                    .from(artistGenres)
                    .where(artistGenres.artistGenre.in(genres))
                    .groupBy(artistGenres.artist.artistId)
            ) : null;
    }

    /**
     * <p>아티스트 목록 조회 조건 : 아티스트명</p>
     *
     * @param artistName 아티스트명
     * @return BooleanExpression
     */
    private BooleanExpression artistNameLike(String artistName) {
        return !ObjectUtils.isEmpty(artistName) ? artist.name.containsIgnoreCase(artistName) : null;
    }

    /**
     * <p>아티스트 목록 조회 조건 : 공연 ID</p>
     *
     * @param performanceId 공연 ID
     * @return BooleanExpression
     */
    private BooleanExpression artistPerformanceEq(Long performanceId) {
        return !ObjectUtils.isEmpty(performanceId) ?
            artist.artistId.in(
                JPAExpressions.select(performanceArtist.artist.artistId)
                    .from(performanceArtist)
                    .where(performanceArtist.performance.performanceId.eq(performanceId))
            ) : null;
    }

    /**
     * <p>
     * <b>아티스트 목록 정렬</b> <br/>
     * NAME_ASC(아티스트명 가나다순) : 아티스트명 오름차순 <br/>
     * OPEN_CLOSER(티켓 오픈 임박순) : 오픈일 가까운순
     * </p>
     *
     * @param sortType 정렬 기준
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> artistSortTypeAs(ArtistSortType sortType) {
        return switch (sortType) {
            case NAME_ASC -> new OrderSpecifier<>(Order.ASC, artist.name);
            // TODO 티켓 오픈 임박순 : ABS(now()-closerOpenDatetime) ASC, now()-closerOpenDatetime > 0 DESC
//            case OPEN_CLOSER -> new OrderSpecifier<>(Order.ASC, artist.name);
        };
    }

    /**
     * <p>아티스트별 팔로우 목록 조회 조건 : 회원 ID</p>
     *
     * @param memberId 회원 ID
     * @return BooleanExpression
     */
    private BooleanExpression memberIdEq(Long memberId) {
        return !ObjectUtils.isEmpty(memberId) ? follow.member.memberId.eq(memberId) : follow.member.memberId.isNull();
    }

    /**
     * <p>아티스트 상세</p>
     *
     * @param artistId 아티스트 ID
     * @param memberId 회원 ID
     * @return 아티스트 상세 정보 + 팔로우 정보
     */
    @Override
    public ArtistDetailResponse selectArtistDetail(Long artistId, Long memberId) {
        return select(new QArtistDto_ArtistDetailResponse(
            artist.artistId, artist.name, artist.engName, artist.artistType,
            artist.artistUrl, artist.popularity, follow.followTargetId.isNotNull().as("isFollow")))
            .from(artist)
            .leftJoin(follow)
            .on(
                follow.followType.eq(FollowType.ARTIST),
                artist.artistId.eq(follow.followTargetId),
                memberIdEq(memberId)
            )
            .where(
                artist.artistStatus.eq(ArtistStatus.ENABLE),
                artist.artistId.eq(artistId)
            )
            .fetchOne();
    }

    /**
     * <p>아티스트 장르 목록</p>
     *
     * @param artistId 아티스트 ID
     * @return 아티스트 장르 목록
     */
    @Override
    public List<ArtistGenre> selectArtistGenreList(Long artistId) {
        return select(artistGenres.artistGenre)
            .from(artistGenres)
            .where(artistGenres.artist.artistId.eq(artistId))
            .fetch();
    }
}
