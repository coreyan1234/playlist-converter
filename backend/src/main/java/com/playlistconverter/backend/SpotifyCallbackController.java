package com.playlistconverter.backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletResponse;
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
            // return new RedirectView("/?error=state_mismatch");
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
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            Map<String, String> body = new HashMap<>();
            body.put("code", code);
            body.put("grant_type", "authorization_code");
            body.put("redirect_uri", redirectUri);
            body.put("client_id", clientId);
            body.put("client_secret", clientSecret);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

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
                System.out.println("Success!");
                System.out.println(accessToken);
                System.out.println(refreshToken);
                // return new RedirectView("/?success");
                return new RedirectView("http://localhost:3000/?success=true");
            }
        }

        // return new RedirectView("/?error=access_denied");
        return new RedirectView("http://localhost:3000/?error=access_denied");
    }
}


// @Controller
// public class SpotifyCallbackController {
//     private final String clientId = "0b30f0dcdb2e40dd9e173167b8c9f7f1"; // Replace with your client ID
//     private final String clientSecret = "eabc406afaa14a37a623f4cda1dc4944"; // Replace with your client secret
//     private final String redirectUri = "http://localhost:8080/callback";

//     @GetMapping("/callback")
//     public void callback(
//             @RequestParam(value = "code", required = false) String code,
//             @RequestParam(value = "state", required = false) String state,
//             HttpSession session,
//             HttpServletResponse response) throws IOException {

//         // Preventing CSRF attack
//         String originalState = (String) session.getAttribute("originalState");
//         if (state == null || !state.equals(originalState)) {
//             response.sendRedirect("http://localhost:3000/?error=state_mismatch");
//             return;
//         }
        
//         if (code != null) {
//             // Prepare to exchange the authorization code for an access token
//             RestTemplate restTemplate = new RestTemplate();
//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//             String auth = clientId + ":" + clientSecret;
//             String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
//             headers.set("Authorization", "Basic " + encodedAuth);

//             Map<String, String> body = new HashMap<>();
//             body.put("grant_type", "authorization_code");
//             body.put("code", code);
//             body.put("redirect_uri", redirectUri);

//             HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

//             ResponseEntity<Map> responseEntity = restTemplate.exchange(
//                     "https://accounts.spotify.com/api/token",
//                     HttpMethod.POST,
//                     request,
//                     Map.class
//             );

//             if (responseEntity.getStatusCode() == HttpStatus.OK) {
//                 Map<String, Object> responseBody = responseEntity.getBody();
//                 String accessToken = (String) responseBody.get("access_token");
//                 String refreshToken = (String) responseBody.get("refresh_token");
//                 System.out.println("Success!");
//                 System.out.println(accessToken);
//                 System.out.println(refreshToken);
//                 response.sendRedirect("http://localhost:3000/?success");
//             } else {
//                 response.sendRedirect("http://localhost:3000/?error=token_exchange_failed");
//             }
//         }

//         response.sendRedirect("http://localhost:3000/?error=access_denied");
//     }
// }