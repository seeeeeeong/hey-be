package hey.io.heybackend.domain.artist.service;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.domain.artist.dto.ArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.ArtistPerformanceResponse;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.repository.ArtistRepository;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.file.enums.EntityType;
import hey.io.heybackend.domain.file.service.FileService;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.service.MemberFollowService;
import hey.io.heybackend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final MemberFollowService memberFollowService;
    private final FileService fileService;

    public ArtistDetailResponse getArtistDetail(Long artistId, JwtTokenInfo jwtTokenInfo) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERFORMANCE_NOT_FOUND));

        List<File> files = fileService.findFiles(EntityType.ARTIST, artistId);
        artist.getFiles().addAll(files);

        ArtistDetailResponse artistDetailResponse = ArtistDetailResponse.from(artist);

        if (jwtTokenInfo.getMemberId() != null) {
            artistDetailResponse.setIsFollow(memberFollowService.isFollowed(jwtTokenInfo.getMemberId(), FollowType.PERFORMANCE, artist.getArtistId()));
        }

        return ArtistDetailResponse.from(artist);
    }

    public SliceResponse<ArtistPerformanceResponse> getArtistPerformanceList(Long artistId, String exceptClosed, int size, int page, Sort.Direction direction) {
        Slice<ArtistPerformanceResponse> artistPerformanceResponse = artistRepository.getArtistPerformanceList(artistId, exceptClosed, Pageable.ofSize(size).withPage(page), direction);

        List<Long> artistPerformanceIds = artistPerformanceResponse.stream()
                .map(ArtistPerformanceResponse::getPerformanceId)
                .collect(Collectors.toList());

        List<File> files = fileService.findThumbnailFiles(EntityType.PERFORMANCE, artistPerformanceIds);

        Map<Long, List<FileDTO>> filesByArtistPerformanceId = files.stream()
                .collect(Collectors.groupingBy(
                        File::getEntityId,
                        Collectors.mapping(FileDTO::from, Collectors.toList())
                ));

        artistPerformanceResponse.forEach(artistPerformance ->
                artistPerformance.setFiles(filesByArtistPerformanceId.getOrDefault(artistPerformance.getPerformanceId(), Collections.emptyList())));

        return new SliceResponse<>(artistPerformanceResponse);
    }

}
