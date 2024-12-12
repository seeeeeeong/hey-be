package hey.io.heybackend.domain.performance.repository;

import static hey.io.heybackend.domain.member.entity.QFollow.follow;
import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;
import static hey.io.heybackend.domain.performance.entity.QPerformanceGenres.performanceGenres;
import static hey.io.heybackend.domain.performance.entity.QPerformancePrice.performancePrice;
import static hey.io.heybackend.domain.performance.entity.QPerformanceTicketing.performanceTicketing;
import static hey.io.heybackend.domain.performance.entity.QPlace.place;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceDetailResponse.PriceDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceDetailResponse.TicketingDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceSearchCondition;
import hey.io.heybackend.domain.performance.dto.QPerformanceDto_PerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.QPerformanceDto_PerformanceDetailResponse_TicketingDto;
import hey.io.heybackend.domain.performance.dto.QPerformanceDto_PerformanceListResponse;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceSortType;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

public class PerformanceQueryRepositoryImpl extends Querydsl5RepositorySupport implements PerformanceQueryRepository {

    public PerformanceQueryRepositoryImpl() {
        super(Performance.class);
    }

    /**
     * <p>공연 목록 (Slice)</p>
     *
     * @param searchCondition 조회 조건
     * @param memberId        회원 ID
     * @param pageable        페이징 정보
     * @return Slice 공연 목록
     */
    @Override
    public SliceResponse<PerformanceListResponse> selectPerformanceSliceList(PerformanceSearchCondition searchCondition,
        Long memberId, Pageable pageable) {
        return applySlicePagination(pageable,
            queryFactory -> getPerformanceListQuery(searchCondition, memberId, queryFactory));
    }

    /**
     * <p>공연 목록</p>
     *
     * @param searchCondition 조회 조건
     * @param memberId        회원 ID
     * @return 공연 목록
     */
    @Override
    public List<PerformanceListResponse> selectPerformanceList(PerformanceSearchCondition searchCondition, Long memberId) {
        return getPerformanceListQuery(searchCondition, memberId, getQueryFactory()).fetch();
    }

    /**
     * <p>공연 목록 쿼리</p>
     *
     * @param searchCondition 조회 조건
     * @param memberId        회원 ID
     * @param queryFactory    쿼리 Factory
     * @return 생성된 쿼리문
     */
    private JPAQuery<PerformanceListResponse> getPerformanceListQuery(PerformanceSearchCondition searchCondition,
        Long memberId, JPAQueryFactory queryFactory) {
        return queryFactory
            .select(new QPerformanceDto_PerformanceListResponse(
                performance.performanceId,
                performance.name.as("performanceName"),
                performance.ticketStatus,
                performance.startDate,
                performance.endDate,
                place.name.as("placeName"),
                follow.followTargetId.isNotNull().as("isFollow")))
            .from(performance)
            .leftJoin(performance.place, place)
            .leftJoin(follow)
            .on(
                follow.followType.eq(FollowType.PERFORMANCE),
                performance.performanceId.eq(follow.followTargetId),
                memberIdEq(memberId)
            ).where(
                performance.performanceStatus.ne(PerformanceStatus.INIT),
                performanceTypeIn(searchCondition.getPerformanceType()),
                performanceGenreIn(searchCondition.getPerformanceGenre()),
                ticketStatusIn(searchCondition.getTicketStatus()),
                performanceStatusIn(searchCondition.getPerformanceStatus()),
                performanceNameLike(searchCondition.getPerformanceName()),
                performanceArtistEq(searchCondition.getArtistId())
            )
            .orderBy(performanceSortTypeAs(searchCondition.getSortType()));
    }

    /**
     * <p>공연별 티켓 오픈 목록</p>
     *
     * @param performanceIds 공연 ID 목록
     * @return 가장 빠른 예매 시간과 공연 매핑 목록
     */
    @Override
    public List<PerformanceListResponse> selectPerformanceOpenDatetimeList(List<Long> performanceIds) {
        return select(new QPerformanceDto_PerformanceListResponse(
            performanceTicketing.performance.performanceId,
            performanceTicketing.openDatetime.min().as("openDatetime")))
            .from(performanceTicketing)
            .where(
                performanceTicketing.openDatetime.isNotNull(),
                performanceTicketing.performance.performanceId.in(performanceIds)
            ).groupBy(performanceTicketing.performance.performanceId)
            .fetch();
    }

    /**
     * <p>공연 목록 조회 조건 : 공연 유형</p>
     *
     * @param performanceTypes 공연 유형
     * @return BooleanExpression
     */
    private BooleanExpression performanceTypeIn(List<PerformanceType> performanceTypes) {
        return !ObjectUtils.isEmpty(performanceTypes) ? performance.performanceType.in(performanceTypes) : null;
    }

    /**
     * <p>공연 목록 조회 조건 : 공연 장르</p>
     *
     * @param genres 공연 장르
     * @return BooleanExpression
     */
    private BooleanExpression performanceGenreIn(List<PerformanceGenre> genres) {
        return !ObjectUtils.isEmpty(genres) ?
            performance.performanceId.in(
                JPAExpressions.select(performanceGenres.performance.performanceId)
                    .from(performanceGenres)
                    .where(performanceGenres.performanceGenre.in(genres))
                    .groupBy(performanceGenres.performance.performanceId)
            ) : null;
    }

    /**
     * <p>공연 목록 조회 조건 : 티켓 상태</p>
     *
     * @param ticketStatuses 티켓 상태
     * @return BooleanExpression
     */
    private BooleanExpression ticketStatusIn(List<TicketStatus> ticketStatuses) {
        return !ObjectUtils.isEmpty(ticketStatuses) ? performance.ticketStatus.in(ticketStatuses) : null;
    }

    /**
     * <p>공연 목록 조회 조건 : 공연 상태</p>
     *
     * @param performanceStatuses 공연 상태
     * @return BooleanExpression
     */
    private BooleanExpression performanceStatusIn(List<PerformanceStatus> performanceStatuses) {
        return !ObjectUtils.isEmpty(performanceStatuses) ? performance.performanceStatus.in(performanceStatuses) : null;
    }

    /**
     * <p>공연 목록 조회 조건 : 공연명</p>
     *
     * @param performanceName 공연명
     * @return BooleanExpression
     */
    private BooleanExpression performanceNameLike(String performanceName) {
        return !ObjectUtils.isEmpty(performanceName) ? performance.name.containsIgnoreCase(performanceName) : null;
    }

    /**
     * <p>공연 목록 조회 조건 : 아티스트 ID</p>
     *
     * @param artistId 아티스트 ID
     * @return BooleanExpression
     */
    private BooleanExpression performanceArtistEq(Long artistId) {
        return !ObjectUtils.isEmpty(artistId) ?
            performance.performanceId.in(
                JPAExpressions.select(performanceArtist.performance.performanceId)
                    .from(performanceArtist)
                    .where(performanceArtist.artist.artistId.eq(artistId))
            ) : null;
    }

    /**
     * <p>
     * <b>공연 목록 정렬</b> <br/>
     * LATEST_CREATED(등록일 기준 최신순) : 등록일 내림차순 <br/>
     * LATEST_START(공연 시작일 기준 최신순) : 공연 시작 일자 내림차순
     * </p>
     *
     * @param sortType 정렬 기준
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> performanceSortTypeAs(PerformanceSortType sortType) {
        return switch (sortType) {
            case LATEST_CREATED -> new OrderSpecifier<>(Order.DESC, performance.createdAt);
            case LATEST_START -> new OrderSpecifier<>(Order.DESC, performance.startDate);
        };
    }

    /**
     * <p>공연별 팔로우 목록 조회 조건 : 회원 ID</p>
     *
     * @param memberId 회원 ID
     * @return BooleanExpression
     */
    private BooleanExpression memberIdEq(Long memberId) {
        return !ObjectUtils.isEmpty(memberId) ? follow.member.memberId.eq(memberId) : follow.member.memberId.isNull();
    }

    /**
     * <p>공연 상세</p>
     *
     * @param performanceId 공연 ID
     * @param memberId      회원 ID
     * @return 공연 상세 정보 + 장소 정보 + 팔로우 정보
     */
    @Override
    public PerformanceDetailResponse selectPerformanceDetail(Long performanceId, Long memberId) {
        return select(new QPerformanceDto_PerformanceDetailResponse(
            performance.performanceId, performance.performanceType,
            performance.name, performance.engName, performance.ticketStatus, performance.startDate,
            performance.endDate, performance.runningTime, performance.viewingAge, performance.performanceStatus,
            follow.followTargetId.isNotNull().as("isFollow"),
            place.placeId, place.name.as("placeName"), place.address, place.latitude, place.longitude))
            .from(performance)
            .leftJoin(performance.place, place)
            .leftJoin(follow)
            .on(
                follow.followType.eq(FollowType.PERFORMANCE),
                performance.performanceId.eq(follow.followTargetId),
                memberIdEq(memberId)
            )
            .where(
                performance.performanceStatus.ne(PerformanceStatus.INIT),
                performance.performanceId.eq(performanceId)
            )
            .fetchOne();
    }

    /**
     * <p>공연 장르 목록</p>
     *
     * @param performanceId 공연 ID
     * @return 공연 장르 목록
     */
    @Override
    public List<PerformanceGenre> selectPerformanceGenreList(Long performanceId) {
        return select(performanceGenres.performanceGenre)
            .from(performanceGenres)
            .where(performanceGenres.performance.performanceId.eq(performanceId))
            .fetch();
    }

    /**
     * <p>공연 가격 목록</p>
     *
     * @param performanceId 공연 ID
     * @return 공연 가격 목록
     */
    @Override
    public List<PriceDto> selectPerformancePriceList(Long performanceId) {
        return select(Projections.fields(PriceDto.class,
            performancePrice.priceId,
            performancePrice.priceInfo,
            performancePrice.priceAmount))
            .from(performancePrice)
            .where(performancePrice.performance.performanceId.eq(performanceId))
            .fetch();
    }

    /**
     * <p>공연 예매 목록</p>
     *
     * @param performanceId 공연 ID
     * @return 공연 예매 목록
     */
    @Override
    public List<TicketingDto> selectPerformanceTicketList(Long performanceId) {
        return select(new QPerformanceDto_PerformanceDetailResponse_TicketingDto(
            performanceTicketing.ticketingId,
            performanceTicketing.ticketingBooth,
            performanceTicketing.ticketingPremium,
            performanceTicketing.openDatetime,
            performanceTicketing.ticketingUrl))
            .from(performanceTicketing)
            .where(performanceTicketing.performance.performanceId.eq(performanceId))
            .orderBy(performanceTicketing.openDatetime.asc())
            .fetch();
    }
}