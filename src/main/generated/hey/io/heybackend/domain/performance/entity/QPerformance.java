package hey.io.heybackend.domain.performance.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPerformance is a Querydsl query type for Performance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPerformance extends EntityPathBase<Performance> {

    private static final long serialVersionUID = 710565869L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPerformance performance = new QPerformance("performance");

    public final hey.io.heybackend.common.entity.QBaseTimeEntity _super = new hey.io.heybackend.common.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final StringPath engName = createString("engName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final NumberPath<Long> performanceId = createNumber("performanceId", Long.class);

    public final EnumPath<hey.io.heybackend.domain.performance.enums.PerformanceStatus> performanceStatus = createEnum("performanceStatus", hey.io.heybackend.domain.performance.enums.PerformanceStatus.class);

    public final EnumPath<hey.io.heybackend.domain.performance.enums.PerformanceType> performanceType = createEnum("performanceType", hey.io.heybackend.domain.performance.enums.PerformanceType.class);

    public final StringPath performanceUid = createString("performanceUid");

    public final QPlace place;

    public final StringPath runningTime = createString("runningTime");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final EnumPath<hey.io.heybackend.domain.performance.enums.TicketStatus> ticketStatus = createEnum("ticketStatus", hey.io.heybackend.domain.performance.enums.TicketStatus.class);

    public final StringPath viewingAge = createString("viewingAge");

    public QPerformance(String variable) {
        this(Performance.class, forVariable(variable), INITS);
    }

    public QPerformance(Path<? extends Performance> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPerformance(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPerformance(PathMetadata metadata, PathInits inits) {
        this(Performance.class, metadata, inits);
    }

    public QPerformance(Class<? extends Performance> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.place = inits.isInitialized("place") ? new QPlace(forProperty("place")) : null;
    }

}

