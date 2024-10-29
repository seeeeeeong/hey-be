package hey.io.heybackend.domain.auth.client;

import hey.io.heybackend.common.util.OAuth2Util;
import hey.io.heybackend.domain.auth.properties.GoogleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthProviderClient {

    private final GoogleProperties googleProperties;

    private final OAuth2Util oAuth2Util;

    public String getGoogleAccessToken(String code) {
        String decode = URLDecoder.decode(code, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        Map<String, String> body = new HashMap<>();
        body.put("code", decode);
        body.put("client_id", googleProperties.getClientId());
        body.put("client_secret", googleProperties.getClientSecret());
        body.put("redirect_uri", googleProperties.getRedirectUri());
        body.put("grant_type", "authorization_code");

        return oAuth2Util.getAccessToken(googleProperties.getTokenUrl(), headers, body);
    }

    public Map getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(googleProperties.getUserInfoUrl(), HttpMethod.GET, request, Map.class);
        return response.getBody();
    }

}
