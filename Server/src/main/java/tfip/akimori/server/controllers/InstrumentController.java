package tfip.akimori.server.controllers;

import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import tfip.akimori.server.services.InstrumentService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/instrument", produces = MediaType.APPLICATION_JSON_VALUE)
public class InstrumentController {

    @Autowired
    private InstrumentService instruSvc;

    @GetMapping(path = "/borrowed")
    public ResponseEntity<String> getBorrowedByJWT(@RequestHeader(name = "Authorization") String token) {
        System.out.println("GETTING BORROWED");
        String jwt = token.substring(7, token.length());

        List<JsonObject> jList = instruSvc.getBorrowedByJWT(jwt);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jList.toString());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<String> getInstrument(@PathVariable String id) {
        JsonObject jObj = instruSvc.getInstrumentByID(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jObj.toString());
    }

    @PostMapping(path = "/add/{storeID}")
    public ResponseEntity<String> addInstrument(@RequestHeader(name = "Authorization") String token,
            @PathVariable String storeID,
            @RequestBody String dataString) {
        System.out.println(dataString);
        String jwt = token.substring(7, token.length());
        System.out.println("ADDING INSTRUMENT TO STORE: " + storeID);
        JsonReader jr = Json.createReader(new StringReader(dataString));
        JsonObject body = jr.readObject().get("body").asJsonObject();

        boolean isInserted = instruSvc.addInstrument(jwt, storeID, body);
        if (isInserted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateInstrument() {
        // check if owns instrument
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("");
    }

}
