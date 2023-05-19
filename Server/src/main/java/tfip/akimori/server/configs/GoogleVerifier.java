package tfip.akimori.server.configs;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

public class GoogleVerifier {

    public static final String CLIENT_ID = "869245493728-jcr4ussoue4u3eu7e020s37gvee8kp05.apps.googleusercontent.com";

    public static GoogleIdTokenVerifier getVerifier() throws GeneralSecurityException, IOException {
        return new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections
                        .singletonList(CLIENT_ID))
                .build();
    }
}
