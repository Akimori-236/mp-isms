package tfip.akimori.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tfip.akimori.server.models.EmailRequest;
import tfip.akimori.server.services.EmailSenderService;

@RestController
@RequestMapping(path = "/api/email")
public class EmailController {

    @Autowired
    private EmailSenderService emailSvc;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
        emailSvc.sendEmail(emailRequest.getToEmail(), emailRequest.getSubject(), emailRequest.getBody());
        return ResponseEntity.ok("Email sent successfully.");
    }

    

}
