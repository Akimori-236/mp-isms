package tfip.akimori.server.services;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class MessagingService {
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";

    @Value("${fcm.server.key}")
    private static String SERVER_KEY;

    public ResponseEntity<JsonObject> sendNotification(String toToken, String title, String message) {
        RestTemplate template = new RestTemplate();
        // SET Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "key=%s".formatted(SERVER_KEY));
        headers.setContentType(MediaType.APPLICATION_JSON);
        // POST request creation
        String body = Json.createObjectBuilder()
                .add("to", toToken)
                .add("notification", Json.createObjectBuilder()
                        .add("title", title)
                        .add("body", message))
                .build()
                .toString();
        RequestEntity<String> requestEntity = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(FCM_URL));
        // SEND GET REQUEST
        ResponseEntity<JsonObject> response = template.exchange(requestEntity, JsonObject.class);
        return response;
    }

}
