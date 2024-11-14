package hey.io.heybackend.domain.system.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAuth is a Querydsl query type for UserAuth
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAuth extends EntityPathBase<UserAuth> {

    private static final long serialVersionUID = 1586902281L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAuth userAuth = new QUserAuth("userAuth");

    public final QAuth auth;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath userId = createString("userId");

    public QUserAuth(String variable) {
        this(UserAuth.class, forVariable(variable), INITS);
    }

    public QUserAuth(Path<? extends UserAuth> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAuth(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAuth(PathMetadata metadata, PathInits inits) {
        this(UserAuth.class, metadata, inits);
    }

    public QUserAuth(Class<? extends UserAuth> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auth = inits.isInitialized("auth") ? new QAuth(forProperty("auth"), inits.get("auth")) : null;
    }

}

