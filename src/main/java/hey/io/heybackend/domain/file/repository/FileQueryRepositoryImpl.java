package hey.io.heybackend.domain.file.repository;

import com.querydsl.core.types.Projections;
import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;

import java.util.List;

import static hey.io.heybackend.domain.file.entity.QFile.file;

public class FileQueryRepositoryImpl extends Querydsl5RepositorySupport implements FileQueryRepository{


    public FileQueryRepositoryImpl() {
        super(File.class);
    }


    /**
     * <p>여러 엔티티의 파일 목록 조회</p>
     *
     * @param entityType  엔티티 유형
     * @param entityId ID
     * @param fileCategory 파일 카테고리
     * @return 각 ID에 해당하는 파일 목록
     */
    @Override
    public List<FileDto> findFilesByEntityId(EntityType entityType, Long entityId, FileCategory fileCategory) {
        return select(Projections.fields(
                FileDto.class,
                file.fileId,
                file.fileCategory,
                file.fileName,
                file.fileUrl,
                file.width,
                file.height
                ))
                .from(file)
                .where(file.entityId.eq(entityId), file.entityType.eq(entityType), file.fileCategory.eq(fileCategory))
                .orderBy(file.fileOrder.asc())
                .fetch();


    }

    /**
     * <p>여러 엔티티의 파일 목록 조회</p>
     *
     * @param entityType  엔티티 유형
     * @param entityIds ID 목록
     * @param fileCategory 파일 카테고리
     * @return 각 ID에 해당하는 파일 목록
     */
    @Override
    public List<FileDto> findFilesByEntityIds(EntityType entityType, List<Long> entityIds, FileCategory fileCategory) {
        return select(Projections.fields(
                FileDto.class,
                file.fileId,
                file.fileCategory,
                file.fileName,
                file.fileUrl,
                file.width,
                file.height
                ))
                .from(file)
                .where(file.entityId.in(entityIds), file.entityType.eq(entityType), file.fileCategory.eq(fileCategory))
                .orderBy(file.fileOrder.asc())
                .fetch();
    }
}
