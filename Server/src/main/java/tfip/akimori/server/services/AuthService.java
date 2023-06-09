package tfip.akimori.server.services;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.mail.MessagingException;
import tfip.akimori.server.exceptions.DuplicateEmailException;
import tfip.akimori.server.models.Role;
import tfip.akimori.server.models.User;
import tfip.akimori.server.repositories.GoogleRepository;
import tfip.akimori.server.repositories.UserRepository;

@Service
public class AuthService {

    private final static int RANDOM_PW_LENGTH = 15;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JwtService jwtSvc;
    @Autowired
    private PasswordEncoder pwEncoder;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private MongoService logSvc;
    @Autowired
    private GoogleRepository googleRepo;
    @Autowired
    private EmailSenderService emailSvc;

    public JsonObject register(JsonObject request) throws DuplicateEmailException {
        System.out.println("REGISTERING: " + request.getString("email"));
        User newUser = User.builder()
                .givenname(request.getString("givenname"))
                .familyname(request.getString("familyname"))
                .email(request.getString("email"))
                .password(pwEncoder.encode(request.getString("password")))
                .role(Role.USER)
                .isGoogleLogin(false)
                .build();
        // System.out.println(newUser);
        userRepo.insertUser(newUser);
        // LOGGING
        logSvc.logUserActivity("register", newUser.getEmail());
        // send email
        try {
            emailSvc.sendWelcomeEmail(newUser.getEmail(), newUser.getGivenname());
        } catch (MessagingException e) {
            System.err.println("EMAIL ERROR >>> " + e);
        }
        // give new user JWT
        return jwtSvc.generateJWT(newUser);
    }

    public JsonObject login(JsonObject request) throws NoSuchElementException, EmptyResultDataAccessException {
        System.out.println("LOGGING IN: " + request.getString("email"));
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getString("email"),
                        request.getString("password")));
        // authenticated
        User user = userRepo.getUserByEmail(request.getString("email"));
        // LOGGING
        logSvc.logUserActivity("login", request.getString("email"));
        return jwtSvc.generateJWT(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public JsonObject registerGoogleUser(Payload payload) throws Exception {
        System.out.println("REGISTERING GOOGLE USER: " + payload.getEmail());
        JsonObject googleUser = Json.createObjectBuilder()
                .add("googleUserId", payload.getSubject())
                .add("email", payload.getEmail())
                .add("emailVerified", Boolean.valueOf(payload.getEmailVerified()))
                .add("name", (String) payload.get("name"))
                .add("picture", (String) payload.get("picture"))
                .add("familyname", (String) payload.getOrDefault("family_name", ""))
                .add("givenname", (String) payload.getOrDefault("given_name", ""))
                .add("password", generateRandomPassword(RANDOM_PW_LENGTH))
                .add("isGoogleLogin", true)
                .build();
        System.out.println(googleUser);
        // LOGGING
        logSvc.logUserActivity("google register", googleUser.getString("email"));
        JsonObject jwt = this.register(googleUser);
        googleRepo.insertUser(payload);
        emailSvc.sendWelcomeEmail(payload.getEmail(),
                googleUser.getOrDefault("givenname", googleUser.getJsonString("familyname")).toString());
        return jwt;
    }

    public JsonObject loginGoogleUser(Payload payload) throws NoSuchElementException, EmptyResultDataAccessException {
        System.out.println("LOGGING IN GOOGLE USER: " + payload.getEmail());
        // authenticated by google already
        userRepo.updateGoogleUser(payload);
        User user = userRepo.getUserByEmail(payload.getEmail());
        // LOGGING
        logSvc.logUserActivity("google login", payload.getEmail());
        return jwtSvc.generateJWT(user);
    }

    private static String generateRandomPassword(int length) {
        String randPw = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .replace("_", "")
                .substring(0, length);
        System.out.println("Random password generated: " + randPw);
        return randPw;
    }
}