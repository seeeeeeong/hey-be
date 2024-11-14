package hey.io.heybackend.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberPush is a Querydsl query type for MemberPush
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberPush extends EntityPathBase<MemberPush> {

    private static final long serialVersionUID = -936102923L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberPush memberPush = new QMemberPush("memberPush");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final BooleanPath pushEnabled = createBoolean("pushEnabled");

    public final EnumPath<hey.io.heybackend.domain.member.enums.PushType> pushType = createEnum("pushType", hey.io.heybackend.domain.member.enums.PushType.class);

    public QMemberPush(String variable) {
        this(MemberPush.class, forVariable(variable), INITS);
    }

    public QMemberPush(Path<? extends MemberPush> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberPush(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberPush(PathMetadata metadata, PathInits inits) {
        this(MemberPush.class, metadata, inits);
    }

    public QMemberPush(Class<? extends MemberPush> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

