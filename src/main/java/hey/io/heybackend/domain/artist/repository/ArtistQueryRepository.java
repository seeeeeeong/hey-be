package hey.io.heybackend.domain.artist.repository;

import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistDetailResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistListResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistSearchCondition;
import hey.io.heybackend.domain.artist.enums.ArtistGenre;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ArtistQueryRepository {

    /**
     * <p>아티스트 목록 (Slice)</p>
     *
     * @param searchCondition 조회 조건
     * @param memberId        회원 ID
     * @param pageable        페이징 정보
     * @return Slice 아티스트 목록
     */
    SliceResponse<ArtistListResponse> selectArtistSliceList(ArtistSearchCondition searchCondition, Long memberId, Pageable pageable);

    /**
     * <p>아티스트 목록</p>
     *
     * @param searchCondition 조회 조건
     * @param memberId        회원 ID
     * @return 아티스트 목록
     */
    List<ArtistListResponse> selectArtistList(ArtistSearchCondition searchCondition, Long memberId);

    /**
     * <p>아티스트 상세</p>
     *
     * @param artistId 아티스트 ID
     * @param memberId 회원 ID
     * @return 아티스트 상세 정보 + 팔로우 정보
     */
    ArtistDetailResponse selectArtistDetail(Long artistId, Long memberId);

    /**
     * <p>아티스트 장르 목록</p>
     *
     * @param artistId 아티스트 ID
     * @return 아티스트 장르 목록
     */
    List<ArtistGenre> selectArtistGenreList(Long artistId);
}
