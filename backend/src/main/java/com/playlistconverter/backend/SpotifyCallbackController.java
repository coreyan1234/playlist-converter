package com.playlistconverter.backend;

import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Base64;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;

@Controller
public class SpotifyCallbackController {
    private final String clientId = "0b30f0dcdb2e40dd9e173167b8c9f7f1"; // Replace with your client ID
    private final String clientSecret = "eabc406afaa14a37a623f4cda1dc4944"; // Replace with your client secret
    private final String redirectUri = "http://localhost:8080/callback";
    private final String tokenUrl = "https://accounts.spotify.com/api/token";

    @GetMapping("/callback")
    public RedirectView callback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            HttpSession session) {

        // Preventing CSRF attack
        String originalState = (String) session.getAttribute("originalState");
        if (state == null || !state.equals(originalState)) {
            return new RedirectView("http://localhost:3000/?error=state_mismatch");
        }
        
        
        if (code != null) {
            // Prepare to exchange the authorization code for an access token
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("code", code);
            body.add("redirect_uri", redirectUri);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                String accessToken = (String) responseBody.get("access_token");
                String refreshToken = (String) responseBody.get("refresh_token");
                session.setAttribute("accessToken", accessToken);
                session.setAttribute("refreshToken", refreshToken);
                return new RedirectView("http://localhost:3000/?success=true");
            }
        }

        return new RedirectView("http://localhost:3000/?error=access_denied");
    }
}