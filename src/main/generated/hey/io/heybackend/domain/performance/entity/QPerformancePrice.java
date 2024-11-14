package hey.io.heybackend.domain.performance.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPerformancePrice is a Querydsl query type for PerformancePrice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPerformancePrice extends EntityPathBase<PerformancePrice> {

    private static final long serialVersionUID = -212710052L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPerformancePrice performancePrice = new QPerformancePrice("performancePrice");

    public final hey.io.heybackend.common.entity.QBaseTimeEntity _super = new hey.io.heybackend.common.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QPerformance performance;

    public final NumberPath<Integer> priceAmount = createNumber("priceAmount", Integer.class);

    public final NumberPath<Long> priceId = createNumber("priceId", Long.class);

    public final StringPath priceInfo = createString("priceInfo");

    public QPerformancePrice(String variable) {
        this(PerformancePrice.class, forVariable(variable), INITS);
    }

    public QPerformancePrice(Path<? extends PerformancePrice> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPerformancePrice(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPerformancePrice(PathMetadata metadata, PathInits inits) {
        this(PerformancePrice.class, metadata, inits);
    }

    public QPerformancePrice(Class<? extends PerformancePrice> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.performance = inits.isInitialized("performance") ? new QPerformance(forProperty("performance"), inits.get("performance")) : null;
    }

}

