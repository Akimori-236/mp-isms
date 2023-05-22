package tfip.akimori.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.JsonObject;
import tfip.akimori.server.services.ProfileService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileController {

    @Autowired
    private ProfileService profileSvc;

    @GetMapping(path = "/")
    public ResponseEntity<String> getProfile(@RequestHeader(name = "Authorization") String token) {
        String jwt = token.substring(7, token.length());
        JsonObject profile = profileSvc.getProfile(jwt);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(profile.toString());
    }
}
