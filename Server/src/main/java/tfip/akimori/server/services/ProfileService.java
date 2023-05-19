package tfip.akimori.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import tfip.akimori.server.models.User;
import tfip.akimori.server.repositories.UserRepository;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JwtService jwtSvc;

    public JsonObject getProfile(String jwt) {
        String email = jwtSvc.extractUsername(jwt);
        System.out.println("GETTING PROFILE OF: " + email);
        User user = userRepo.getUserByEmail(email);
        JsonObject userJson = Json.createObjectBuilder()
                .add("email", user.getEmail())
                .add("givenname", user.getGivenname())
                .add("familyname", user.getFamilyname())
                .build();
        return userJson;
    }
}
