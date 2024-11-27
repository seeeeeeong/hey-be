package hey.io.heybackend.domain.file.repository;

import static hey.io.heybackend.domain.file.entity.QFile.file;

import com.querydsl.core.types.Projections;
import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.file.dto.QFileDto;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.enums.FileType;
import java.util.List;

public class FileQueryRepositoryImpl extends Querydsl5RepositorySupport implements FileQueryRepository {

    public FileQueryRepositoryImpl() {
        super(File.class);
    }

    /**
     * <p>엔티티별 썸네일 목록</p>
     *
     * @param entityType 엔티티 유형
     * @param entityIds  엔티티 ID 목록
     * @return 썸네일 목록
     */
    @Override
    public List<FileDto> selectThumbnailFileList(EntityType entityType, List<Long> entityIds) {
        return select(Projections.fields(FileDto.class,
            file.entityId,
            file.fileName.min().as("fileName"),
            file.fileUrl.min().as("fileUrl")))
            .from(file)
            .where(
                file.entityType.eq(entityType),
                file.entityId.in(entityIds),
                file.fileType.eq(FileType.IMAGE),
                file.fileCategory.eq(FileCategory.THUMBNAIL),
                file.fileOrder.eq(1)
            ).groupBy(file.entityType, file.entityId)
            .fetch();
    }

    /**
     * <p>특정 엔티티 상세 파일 목록</p>
     *
     * @param entityType 엔티티 유형
     * @param entityId   엔티티 ID
     * @return 상세 파일 목록
     */
    @Override
    public List<FileDto> selectDetailFileList(EntityType entityType, Long entityId) {
        return select(new QFileDto(
            file.fileId, file.fileCategory, file.fileName,
            file.fileUrl, file.width, file.height))
            .from(file)
            .where(
                file.entityType.eq(entityType),
                file.entityId.eq(entityId),
                file.fileType.eq(FileType.IMAGE),
                file.fileCategory.eq(FileCategory.DETAIL)
            )
            .fetch();
    }
}
