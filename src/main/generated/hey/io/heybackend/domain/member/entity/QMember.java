package hey.io.heybackend.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 303146555L;

    public static final QMember member = new QMember("member1");

    public final hey.io.heybackend.common.entity.QBaseTimeEntity _super = new hey.io.heybackend.common.entity.QBaseTimeEntity(this);

    public final DateTimePath<java.time.LocalDateTime> accessedAt = createDateTime("accessedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final EnumPath<hey.io.heybackend.domain.member.enums.MemberStatus> memberStatus = createEnum("memberStatus", hey.io.heybackend.domain.member.enums.MemberStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final BooleanPath optionalTermsAgreed = createBoolean("optionalTermsAgreed");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

