package hey.io.heybackend.domain.artist.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArtist is a Querydsl query type for Artist
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArtist extends EntityPathBase<Artist> {

    private static final long serialVersionUID = 850795733L;

    public static final QArtist artist = new QArtist("artist");

    public final hey.io.heybackend.common.entity.QBaseTimeEntity _super = new hey.io.heybackend.common.entity.QBaseTimeEntity(this);

    public final NumberPath<Long> artistId = createNumber("artistId", Long.class);

    public final EnumPath<hey.io.heybackend.domain.artist.enums.ArtistStatus> artistStatus = createEnum("artistStatus", hey.io.heybackend.domain.artist.enums.ArtistStatus.class);

    public final EnumPath<hey.io.heybackend.domain.artist.enums.ArtistType> artistType = createEnum("artistType", hey.io.heybackend.domain.artist.enums.ArtistType.class);

    public final StringPath artistUid = createString("artistUid");

    public final StringPath artistUrl = createString("artistUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath engName = createString("engName");

    public final ListPath<ArtistGenres, QArtistGenres> genres = this.<ArtistGenres, QArtistGenres>createList("genres", ArtistGenres.class, QArtistGenres.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final StringPath orgName = createString("orgName");

    public final ListPath<hey.io.heybackend.domain.performance.entity.PerformanceArtist, hey.io.heybackend.domain.performance.entity.QPerformanceArtist> performanceArtists = this.<hey.io.heybackend.domain.performance.entity.PerformanceArtist, hey.io.heybackend.domain.performance.entity.QPerformanceArtist>createList("performanceArtists", hey.io.heybackend.domain.performance.entity.PerformanceArtist.class, hey.io.heybackend.domain.performance.entity.QPerformanceArtist.class, PathInits.DIRECT2);

    public final NumberPath<Integer> popularity = createNumber("popularity", Integer.class);

    public QArtist(String variable) {
        super(Artist.class, forVariable(variable));
    }

    public QArtist(Path<? extends Artist> path) {
        super(path.getType(), path.getMetadata());
    }

    public QArtist(PathMetadata metadata) {
        super(Artist.class, metadata);
    }

}

