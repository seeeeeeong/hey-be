package hey.io.heybackend.domain.performance.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto;
import hey.io.heybackend.domain.follow.enums.FollowType;
import hey.io.heybackend.domain.main.dto.HomeResDto.NewPerformanceDto;
import hey.io.heybackend.domain.main.dto.HomeResDto.TopRatedPerformanceDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto;
import hey.io.heybackend.domain.performance.dto.PerformanceListReqDto;
import hey.io.heybackend.domain.performance.dto.PerformanceListResDto;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import hey.io.heybackend.domain.search.dto.SearchReqDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static hey.io.heybackend.domain.artist.entity.QArtist.artist;
import static hey.io.heybackend.domain.follow.entity.QFollow.follow;
import static hey.io.heybackend.domain.performance.entity.QPerformance.performance;
import static hey.io.heybackend.domain.performance.entity.QPerformanceArtist.performanceArtist;
import static hey.io.heybackend.domain.performance.entity.QPerformanceTicketing.performanceTicketing;


public class PerformanceQueryRepositoryImpl extends Querydsl5RepositorySupport implements PerformanceQueryRepository {

    public PerformanceQueryRepositoryImpl() {
        super(Performance.class);
    }

    /**
     * <p>공연 목록 조회</p>
     *
     * @param request
     * @param pageable
     * @return 공연 목록
     */
    @Override
    public Slice<PerformanceListResDto> findPerformancesByCondition(PerformanceListReqDto request, Pageable pageable) {
        return applySlicePagination(pageable, queryFactory ->
                queryFactory.select(Projections.fields(
                                PerformanceListResDto.class,
                                performance.performanceId.as("performanceId"),
                                performance.name.as("performanceName"),
                                performanceTicketing.openDatetime.min().as("openDatetime"),
                                performance.ticketStatus.as("ticketStatus"),
                                performance.startDate.as("startDate"),
                                performance.endDate.as("endDate"),
                                performance.place.name.as("placeName")
                        ))
                        .from(performance)
                        .leftJoin(performanceTicketing).on(performanceTicketing.performance.eq(performance))
                        .fetchJoin()
                        .where(
                                performance.performanceStatus.ne(PerformanceStatus.INIT),
                                inType(request.getType()),
                                inGenres(request.getGenres()),
                                inStatuses(request.getStatuses()),
                                inTickets(request.getTickets())
                        )
                        .groupBy(performance.performanceId,
                                performance.name,
                                performance.ticketStatus,
                                performance.startDate,
                                performance.endDate,
                                performance.place.name)
                        .orderBy(performance.createdAt.desc())
        );
    }

    /**
     * <p>공연 상세 조회</p>
     *
     * @param performanceId 공연 ID
     * @return 공연 상세
     */
    @Override
    public Optional<PerformanceDetailResDto> findPerformanceDetailByPerformanceId(Long performanceId) {
        return Optional.ofNullable(select(Projections.fields(
                PerformanceDetailResDto.class,
                performance.performanceId,
                performance.performanceType,
                performance.performanceStatus,
                performance.name.as("performanceName"),
                performance.engName,
                performance.startDate,
                performance.endDate,
                performance.runningTime,
                performance.viewingAge,
                performance.place.name.as("placeName"),
                performance.place.address,
                performance.place.latitude,
                performance.place.longitude
                ))
                .from(performance)
                .where(performance.performanceId.eq(performanceId), performance.performanceStatus.ne(PerformanceStatus.INIT))
                .fetchOne());
    }

    /**
     * <p>팔로우 공연 목록 조회</p>
     *
     * @param memberId
     * @param pageable
     * @return Slice<PerformanceListResponse>
     */
    @Override
    public Slice<PerformanceListResDto> findFollowedPerformancesByMemberId(Long memberId, Pageable pageable) {
        return applySlicePagination(pageable, queryFactory ->
                queryFactory.select(Projections.fields(
                                PerformanceListResDto.class,
                                performance.performanceId.as("performanceId"),
                                performance.name.as("performanceName"),
                                performanceTicketing.openDatetime.min().as("openDatetime"),
                                performance.ticketStatus.as("ticketStatus"),
                                performance.startDate.as("startDate"),
                                performance.endDate.as("endDate"),
                                performance.place.name.as("placeName")
                        ))
                        .from(performance)
                        .leftJoin(performanceTicketing).on(performanceTicketing.performance.eq(performance))
                        .leftJoin(follow).on(follow.followTargetId.eq(performance.performanceId).and(follow.followType.eq(FollowType.PERFORMANCE)))
                        .where(follow.member.memberId.eq(memberId))
                        .where(performance.performanceStatus.ne(PerformanceStatus.INIT))
                        .groupBy(performance.performanceId,
                                performance.name,
                                performance.ticketStatus,
                                performance.startDate,
                                performance.endDate,
                                performance.place.name)
                        .orderBy(performance.createdAt.desc())
        );
    }

    /**
     * <p>아티스트 공연 목록 조회</p>
     *
     * @param artistId 아티스트 ID
     * @return List<PerformanceListResponse>
     */
    @Override
    public List<ArtistDetailResDto.ArtistPerformanceDto> findArtistPerformancesByArtistId(Long artistId) {
        return select(Projections.fields(
                ArtistDetailResDto.ArtistPerformanceDto.class,
                performance.performanceId.as("performanceId"),
                performance.name.as("performanceName"),
                performanceTicketing.openDatetime.min().as("openDatetime"),
                performance.ticketStatus.as("ticketStatus"),
                performance.startDate.as("startDate"),
                performance.endDate.as("endDate"),
                performance.place.name.as("placeName")
                ))
                .from(performance)
                .leftJoin(performanceTicketing).on(performanceTicketing.performance.eq(performance))
                .leftJoin(performance.performanceArtists, performanceArtist)
                .leftJoin(performanceArtist.artist, artist)
                .where(artist.artistId.eq(artistId), performance.performanceStatus.ne(PerformanceStatus.INIT))
                .groupBy(performance.performanceId,
                        performance.name,
                        performance.ticketStatus,
                        performance.startDate,
                        performance.endDate,
                        performance.place.name)
                .orderBy(performance.createdAt.desc())
                .fetch();
    }

    /**
     * <p>HOT 5 공연 목록 조회</p>
     *
     * @return HOT 5 공연 목록
     */
    @Override
    public List<TopRatedPerformanceDto> findTopRatedPerformances() {
        return select(Projections.fields(
                TopRatedPerformanceDto.class,
                performance.performanceId,
                performance.name.as("performanceName"),
                performance.startDate,
                performance.endDate
                ))
                .from(performance)
                .leftJoin(performanceTicketing).on(performanceTicketing.performance.eq(performance))
                .where(performance.startDate.goe(LocalDate.now().minusMonths(3)), performance.performanceStatus.ne(PerformanceStatus.INIT))
                .orderBy(
                        performance.startDate.desc(),
                        performanceTicketing.openDatetime.asc()
                )
                .limit(5)
                .fetch();
    }

    /**
     * <p>NEW 공연 목록 조회</p>
     *
     * @return NEW 공연 목록
     */
    @Override
    public List<NewPerformanceDto> findNewPerformances() {
        return select(Projections.fields(
                NewPerformanceDto.class,
                performance.performanceId,
                performance.name.as("performanceName"),
                performance.startDate,
                performance.endDate
                ))
                .from(performance)
                .where(performance.performanceStatus.ne(PerformanceStatus.INIT))
                .orderBy(performance.createdAt.desc())
                .limit(5)
                .fetch();

    }

    @Override
    public Slice<PerformanceListResDto> findPerformancesByKeyword(SearchReqDto request, Pageable pageable) {
        return applySlicePagination(pageable, queryFactory ->
                queryFactory.select(Projections.fields(
                                PerformanceListResDto.class,
                                performance.performanceId.as("performanceId"),
                                performance.name.as("performanceName"),
                                performanceTicketing.openDatetime.min().as("openDatetime"),
                                performance.ticketStatus.as("ticketStatus"),
                                performance.startDate.as("startDate"),
                                performance.endDate.as("endDate"),
                                performance.place.name.as("placeName")
                        ))
                        .from(performance)
                        .leftJoin(performanceTicketing).on(performanceTicketing.performance.eq(performance))
                        .fetchJoin()
                        .where(
                                performance.performanceStatus.ne(PerformanceStatus.INIT),
                                inKeyword(request.getKeyword()),
                                inStatuses(request.getStatuses())
                        )
                        .groupBy(performance.performanceId,
                                performance.name,
                                performance.ticketStatus,
                                performance.startDate,
                                performance.endDate,
                                performance.place.name)
                        .orderBy(performance.createdAt.desc())
        );
    }

    private BooleanExpression inKeyword(String keyword) {
        return ObjectUtils.isEmpty(keyword) ? null : performance.name.contains(keyword);
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