package hey.io.heybackend.domain.file.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.enums.FileCategory;
import hey.io.heybackend.domain.file.enums.FileType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

}