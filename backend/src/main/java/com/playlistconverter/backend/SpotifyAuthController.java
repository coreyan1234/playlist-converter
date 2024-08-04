package com.playlistconverter.backend;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class SpotifyAuthController {
    private final String clientId = "0b30f0dcdb2e40dd9e173167b8c9f7f1";
    private final String redirectUri = "http://localhost:8080/callback";
    private final String state = generateRandomString(16); // TODO: Supposed to be a random string with 16 characters
    private final String scope = "user-read-private user-read-email";

    @GetMapping("/login")
    public RedirectView login() {
        String authorizationUrl = UriComponentsBuilder.fromHttpUrl("https://accounts.spotify.com/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("scope", scope)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .toUriString();
        return new RedirectView(authorizationUrl);
    }

    // Generate random string of given length
    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
