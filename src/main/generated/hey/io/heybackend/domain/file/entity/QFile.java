package hey.io.heybackend.domain.file.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFile is a Querydsl query type for File
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFile extends EntityPathBase<File> {

    private static final long serialVersionUID = -1940774977L;

    public static final QFile file = new QFile("file");

    public final hey.io.heybackend.common.entity.QBaseTimeEntity _super = new hey.io.heybackend.common.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> entityId = createNumber("entityId", Long.class);

    public final EnumPath<hey.io.heybackend.domain.file.enums.EntityType> entityType = createEnum("entityType", hey.io.heybackend.domain.file.enums.EntityType.class);

    public final EnumPath<hey.io.heybackend.domain.file.enums.FileCategory> fileCategory = createEnum("fileCategory", hey.io.heybackend.domain.file.enums.FileCategory.class);

    public final NumberPath<Long> fileId = createNumber("fileId", Long.class);

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Integer> fileOrder = createNumber("fileOrder", Integer.class);

    public final EnumPath<hey.io.heybackend.domain.file.enums.FileType> fileType = createEnum("fileType", hey.io.heybackend.domain.file.enums.FileType.class);

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Integer> height = createNumber("height", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Integer> width = createNumber("width", Integer.class);

    public QFile(String variable) {
        super(File.class, forVariable(variable));
    }

    public QFile(Path<? extends File> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFile(PathMetadata metadata) {
        super(File.class, metadata);
    }

}

