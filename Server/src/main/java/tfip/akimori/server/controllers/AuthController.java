package tfip.akimori.server.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.glassfish.json.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import jakarta.json.JsonObject;
import tfip.akimori.server.configs.GoogleVerifier;
import tfip.akimori.server.exceptions.DuplicateEmailException;
import tfip.akimori.server.services.AuthService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @Autowired
    private AuthService authSvc;

    // REGISTRATION
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody String request) {
        // read JSON
        JsonObject jsonRequest = JsonUtil.toJson(request)
                .asJsonObject();
        // System.out.println("Register Request >>>>> " + jsonRequest);
        JsonObject jwt;
        try {
            // save new user & get jwt
            jwt = authSvc.register(jsonRequest);
        } catch (DuplicateEmailException e) {
            System.err.println(e);
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User already registered, please log in");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jwt.toString());
    }

    // LOG IN
    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody String request) {
        // read JSON
        JsonObject jsonRequest = JsonUtil.toJson(request).asJsonObject();
        JsonObject jwt;
        try {
            jwt = authSvc.login(jsonRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jwt.toString());
    }

    @PostMapping(path = "/googleregister")
    public ResponseEntity<String> googleRegister(@RequestBody String clientToken) throws Exception {
        // System.out.println(clientToken);
        try {
            GoogleIdToken idToken = GoogleVerifier.getVerifier().verify(clientToken);
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                // Generate JWT token and return it
                JsonObject jwt = authSvc.registerGoogleUser(payload);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jwt.toString());
            }
        } catch (DuplicateEmailException dee) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User already registered, please log in");
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the google token");
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid google login");
    }

    @PostMapping(path = "/googlelogin")
    public ResponseEntity<String> googleLogin(@RequestBody String clientToken) {
        // System.out.println(clientToken);
        try {
            GoogleIdToken idToken = GoogleVerifier.getVerifier().verify(clientToken);
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                // Generate JWT token and return it
                JsonObject jwt = authSvc.loginGoogleUser(payload);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jwt.toString());
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the google token");
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid google login");
    }

}
