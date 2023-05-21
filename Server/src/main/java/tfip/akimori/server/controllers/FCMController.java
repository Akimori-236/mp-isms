package tfip.akimori.server.controllers;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import tfip.akimori.server.services.MongoService;

@RestController
@RequestMapping(path = "/api/fcm")
public class FCMController {

    @Autowired
    private MongoService mongoSvc;

    @PostMapping(path = "/keep", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void receiveFCMToken(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody String body) {
        String jwt = token.substring(7, token.length());
        JsonReader jr = Json.createReader(new StringReader(body));
        String fcmToken = jr.readObject().getString("body");
        System.out.println("RECEIVED FCM TOKEN: " + fcmToken);
        // save the fcm token
        mongoSvc.upsertFCMToken(jwt, fcmToken);
    }
}
