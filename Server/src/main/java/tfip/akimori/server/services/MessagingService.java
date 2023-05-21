package tfip.akimori.server.services;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
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
import tfip.akimori.server.models.Instrument;
import tfip.akimori.server.repositories.InstrumentRepository;

@Service
public class MessagingService {
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";

    @Value("${fcm.server.key}")
    private String SERVER_KEY;

    @Autowired
    private MongoService mongoSvc;
    @Autowired
    private InstrumentRepository instruRepo;

    private ResponseEntity<JsonObject> sendNotification(String toToken, String title, String message) {
        RestTemplate template = new RestTemplate();
        // SET Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "key=%s".formatted(SERVER_KEY));
        headers.setContentType(MediaType.APPLICATION_JSON);
        System.out.println(headers);
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
        ResponseEntity<JsonObject> response = template.exchange(requestEntity, JsonObject.class); // ERROR 12392
        return response;
    }

    public void borrowNotification(String borrowerEmail, String instrument_id, String approverEmail) {
        // get fcm token with email
        String toToken = mongoSvc.getFCMToken(approverEmail);
        if (null == toToken) {
            System.err.println("No FCM token found for: " + approverEmail);
            return;
        }
        // get instrument details
        Instrument instrument = instruRepo.getInstrumentById(instrument_id);
        String title = "ISMS:";// + store_name;
        String message = "%s (S/N: %s) borrowed by: %s".formatted(instrument.getInstrument_type(),
                instrument.getSerial_number(), borrowerEmail);
        sendNotification(toToken, title, message);
    }

    public void returnNotification(String instrument_id) {
        // TODO:
    }
}
