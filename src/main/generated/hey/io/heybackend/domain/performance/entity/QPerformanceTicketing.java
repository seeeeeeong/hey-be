package hey.io.heybackend.domain.performance.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPerformanceTicketing is a Querydsl query type for PerformanceTicketing
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPerformanceTicketing extends EntityPathBase<PerformanceTicketing> {

    private static final long serialVersionUID = -1223484439L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPerformanceTicketing performanceTicketing = new QPerformanceTicketing("performanceTicketing");

    public final hey.io.heybackend.common.entity.QBaseTimeEntity _super = new hey.io.heybackend.common.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final DateTimePath<java.time.LocalDateTime> openDatetime = createDateTime("openDatetime", java.time.LocalDateTime.class);

    public final QPerformance performance;

    public final StringPath ticketingBooth = createString("ticketingBooth");

    public final NumberPath<Long> ticketingId = createNumber("ticketingId", Long.class);

    public final StringPath ticketingPremium = createString("ticketingPremium");

    public final StringPath ticketingUrl = createString("ticketingUrl");

    public QPerformanceTicketing(String variable) {
        this(PerformanceTicketing.class, forVariable(variable), INITS);
    }

    public QPerformanceTicketing(Path<? extends PerformanceTicketing> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPerformanceTicketing(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPerformanceTicketing(PathMetadata metadata, PathInits inits) {
        this(PerformanceTicketing.class, metadata, inits);
    }

    public QPerformanceTicketing(Class<? extends PerformanceTicketing> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.performance = inits.isInitialized("performance") ? new QPerformance(forProperty("performance"), inits.get("performance")) : null;
    }

}

