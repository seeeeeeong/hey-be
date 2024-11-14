package hey.io.heybackend.domain.system.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuth is a Querydsl query type for Auth
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuth extends EntityPathBase<Auth> {

    private static final long serialVersionUID = -1675911778L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuth auth = new QAuth("auth");

    public final StringPath authId = createString("authId");

    public final NumberPath<Integer> authLevel = createNumber("authLevel", Integer.class);

    public final NumberPath<Integer> authOrder = createNumber("authOrder", Integer.class);

    public final ListPath<Auth, QAuth> auths = this.<Auth, QAuth>createList("auths", Auth.class, QAuth.class, PathInits.DIRECT2);

    public final StringPath description = createString("description");

    public final BooleanPath enabled = createBoolean("enabled");

    public final StringPath name = createString("name");

    public final QAuth upperAuth;

    public QAuth(String variable) {
        this(Auth.class, forVariable(variable), INITS);
    }

    public QAuth(Path<? extends Auth> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuth(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuth(PathMetadata metadata, PathInits inits) {
        this(Auth.class, metadata, inits);
    }

    public QAuth(Class<? extends Auth> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.upperAuth = inits.isInitialized("upperAuth") ? new QAuth(forProperty("upperAuth"), inits.get("upperAuth")) : null;
    }

}

