package com.playlistconverter.backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpSession;

@Controller
public class SpotifyCallbackController {
    private final String clientId = "0b30f0dcdb2e40dd9e173167b8c9f7f1"; // Replace with your client ID
    private final String clientSecret = "eabc406afaa14a37a623f4cda1dc4944"; // Replace with your client secret
    private final String redirectUri = "http://localhost:3000/callback";

    @GetMapping("/callback")
    public RedirectView callback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            HttpSession session) {

        String originalState = (String) session.getAttribute("originalState");

        if (state == null || !state.equals(originalState)) {
            return new RedirectView("/?error=state_mismatch");
        }
        else {
            return new RedirectView("/?error=access_denied");
        }
        
        // if (code != null) {
        //     System.out.println("Worked!");
        //     return new RedirectView("/?error=access_denied");
        // }
    }
}
