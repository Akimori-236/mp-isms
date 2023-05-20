package tfip.akimori.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tfip.akimori.server.services.MongoService;

@RestController
@RequestMapping(path = "/api/fcm")
public class FCMController {

    @Autowired
    private MongoService mongoSvc;

    @PostMapping(path = "/keep", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void receiveFCMToken(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody String fcmToken) {
        String jwt = token.substring(7, token.length());
        // save the fcm token
        mongoSvc.upsertFCMToken(jwt, fcmToken);
    }
}
