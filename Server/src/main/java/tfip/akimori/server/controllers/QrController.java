package tfip.akimori.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tfip.akimori.server.exceptions.UnauthorizedException;
import tfip.akimori.server.services.QrService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/qr")
public class QrController {
    @Autowired
    private QrService qrSvc;

    @GetMapping(path = "/{storeID}/loanout/{instrumentID}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getLoanQR(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String storeID,
            @PathVariable String instrumentID) {
        System.out.println("GETTING QR TO LOAN OUT : " + instrumentID);
        String jwt = token.substring(7, token.length());

        byte[] qr;
        try {
            qr = qrSvc.getLoanQR(instrumentID, storeID, jwt);
        } catch (UnauthorizedException e) {
            return null;
        }
        final ByteArrayResource is = new ByteArrayResource(qr);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(is);
    }

}
