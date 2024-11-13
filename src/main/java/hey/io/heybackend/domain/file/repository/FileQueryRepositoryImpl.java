package hey.io.heybackend.domain.file.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static hey.io.heybackend.domain.file.entity.QFile.file;

public class FileQueryRepositoryImpl extends Querydsl5RepositorySupport implements FileQueryRepository{


    public FileQueryRepositoryImpl() {
        super(File.class);
    }


    /**
     * <p>특정 엔티티의 파일 목록 조회</p>
     *
     * @return List<File>
     */
    @Override
    public List<File> findFilesByEntityAndId(Long entityId, EntityType entityType, FileCategory fileCategory) {
        return selectFrom(file)
                .where(file.entityId.eq(entityId)
                        .and(file.entityType.eq(entityType))
                        .and(file.fileCategory.eq(fileCategory)))
                .orderBy(file.fileOrder.asc())
                .fetch();
    }

    /**
     * <p>여러 엔티티의 파일 목록 조회</p>
     *
     * @return List<File>
     */
    @Override
    public List<File> findFilesByEntityAndIds(List<Long> entityIds, EntityType entityType, FileCategory fileCategory) {
        return selectFrom(file)
                .where(file.entityId.in(entityIds)
                        .and(file.entityType.eq(entityType))
                        .and(file.fileCategory.eq(fileCategory)))
                .orderBy(file.fileOrder.asc())
                .fetch();
    }
}
