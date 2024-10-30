package hey.io.heybackend.domain.file.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static hey.io.heybackend.domain.file.entity.QFile.file;

@RequiredArgsConstructor
public class FileQueryRepositoryImpl implements FileQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<File> findFilesByEntityAndId(Long entityId, EntityType entityType, FileCategory fileCategory) {
        return queryFactory.selectFrom(file)
                .where(file.entityId.eq(entityId)
                        .and(file.entityType.eq(entityType))
                        .and(file.fileCategory.eq(fileCategory)))
                .orderBy(file.fileOrder.asc())
                .fetch();
    }

    @Override
    public List<File> findFilesByEntityAndIds(List<Long> entityIds, EntityType entityType, FileCategory fileCategory) {
        return queryFactory.selectFrom(file)
                .where(file.entityId.in(entityIds)
                        .and(file.entityType.eq(entityType))
                        .and(file.fileCategory.eq(fileCategory)))
                .orderBy(file.fileOrder.asc())
                .fetch();
    }
}
