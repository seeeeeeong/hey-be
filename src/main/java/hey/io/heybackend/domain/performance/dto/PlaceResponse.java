package hey.io.heybackend.domain.performance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import hey.io.heybackend.domain.performance.entity.Place;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceResponse {

    private Long placeId; // 장소 ID
    private String name; // 장소명
    private String address; // 주소
    private double latitude; // 위도
    private double longitude; // 경도

    public static PlaceResponse from(Place place) {
        return PlaceResponse.builder()
                .placeId(place.getPlaceId())
                .name(place.getName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .build();
    }
}
