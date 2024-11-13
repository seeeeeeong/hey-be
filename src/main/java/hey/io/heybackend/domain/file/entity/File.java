package hey.io.heybackend.domain.file.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.enums.FileType;
import hey.io.heybackend.domain.performance.entity.Performance;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "system")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId; // 파일 ID

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityType entityType; // 관련 엔티티 유형

    @Column(nullable = false)
    private Long entityId; // 관련 엔티티 ID

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType fileType; // 파일 유형

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileCategory fileCategory; // 파일 분류

    @Column(nullable = false)
    private String fileName; // 파일명

    @Column(nullable = false)
    private String fileUrl; // 파일 URL

    private Integer width; // 파일 너비

    private Integer height; // 파일 높이

    @Column(nullable = false)
    private Integer fileOrder; // 정렬 분류

    @Builder
    public File(EntityType entityType, Long entityId, FileType fileType, FileCategory fileCategory, String fileUrl,
        Integer width, Integer height, Integer fileOrder) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.fileType = fileType;
        this.fileCategory = fileCategory;
        this.fileName = extractFileName(fileUrl);
        this.fileUrl = fileUrl;
        this.width = width;
        this.height = height;
        this.fileOrder = fileOrder;
    }

    private String extractFileName(String url) {
        // URL에서 마지막 '/' 이후의 파일명 추출
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex == -1 || lastSlashIndex == url.length() - 1) {
            return null; // '/'가 없거나 파일명이 없는 경우
        }

        return url.substring(lastSlashIndex + 1);
    }

}