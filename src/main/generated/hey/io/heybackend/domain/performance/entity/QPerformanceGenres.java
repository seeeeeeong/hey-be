package hey.io.heybackend.domain.performance.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPerformanceGenres is a Querydsl query type for PerformanceGenres
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPerformanceGenres extends EntityPathBase<PerformanceGenres> {

    private static final long serialVersionUID = 1726418333L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPerformanceGenres performanceGenres = new QPerformanceGenres("performanceGenres");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPerformance performance;

    public final EnumPath<hey.io.heybackend.domain.performance.enums.PerformanceGenre> performanceGenre = createEnum("performanceGenre", hey.io.heybackend.domain.performance.enums.PerformanceGenre.class);

    public QPerformanceGenres(String variable) {
        this(PerformanceGenres.class, forVariable(variable), INITS);
    }

    public QPerformanceGenres(Path<? extends PerformanceGenres> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPerformanceGenres(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPerformanceGenres(PathMetadata metadata, PathInits inits) {
        this(PerformanceGenres.class, metadata, inits);
    }

    public QPerformanceGenres(Class<? extends PerformanceGenres> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.performance = inits.isInitialized("performance") ? new QPerformance(forProperty("performance"), inits.get("performance")) : null;
    }

}

