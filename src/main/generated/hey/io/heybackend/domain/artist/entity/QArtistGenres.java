package hey.io.heybackend.domain.artist.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArtistGenres is a Querydsl query type for ArtistGenres
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArtistGenres extends EntityPathBase<ArtistGenres> {

    private static final long serialVersionUID = -2094302075L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArtistGenres artistGenres = new QArtistGenres("artistGenres");

    public final QArtist artist;

    public final EnumPath<hey.io.heybackend.domain.artist.enums.ArtistGenre> artistGenre = createEnum("artistGenre", hey.io.heybackend.domain.artist.enums.ArtistGenre.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QArtistGenres(String variable) {
        this(ArtistGenres.class, forVariable(variable), INITS);
    }

    public QArtistGenres(Path<? extends ArtistGenres> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArtistGenres(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArtistGenres(PathMetadata metadata, PathInits inits) {
        this(ArtistGenres.class, metadata, inits);
    }

    public QArtistGenres(Class<? extends ArtistGenres> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.artist = inits.isInitialized("artist") ? new QArtist(forProperty("artist")) : null;
    }

}

