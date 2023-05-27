package tfip.akimori.server.services;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import tfip.akimori.server.repositories.MongoRepository;
import tfip.akimori.server.repositories.MongoVariables;

@Service
public class MongoService implements MongoVariables {
    private static Long QR_DURATION_MINUTES = 10L;

    @Autowired
    private MongoRepository mongoRepo;
    @Autowired
    private JwtService jwtSvc;

    public void logUserActivity(String activity, String email) {
        Document doc = new Document();
        doc.put(FIELD_EMAIL, email);
        doc.put(FIELD_ACTIVITY, activity);
        mongoRepo.insertUserActivity(doc);
    }

    public void logInstrumentActivity(
            String store_id,
            String activity,
            String email,
            String instrument_id,
            String message) {
        Document doc = new Document();
        doc.put(FIELD_STORE_ID, store_id);
        doc.put(FIELD_ACTIVITY, activity);
        doc.put(FIELD_EMAIL, email);
        doc.put(FIELD_INSTRUMENT_ID, instrument_id);
        doc.put(FIELD_MESSAGE, message);
        mongoRepo.insertInstrumentActivity(doc);
    }

    public JsonArray getStoreLogs(String storeID) {
        System.out.println("GETTING LOGS FOR STORE: " + storeID);
        List<Document> docList = mongoRepo.getLogsByStoreID(storeID);
        System.out.println(docList);
        JsonArrayBuilder jar = Json.createArrayBuilder();
        for (Document d : docList) {
            jar.add(LogToJOB(d));
        }
        return jar.build();
    }

    private JsonObjectBuilder LogToJOB(Document log) {
        return Json.createObjectBuilder()
                .add(FIELD_STORE_ID, log.getString(FIELD_STORE_ID))
                .add(FIELD_ACTIVITY, log.getString(FIELD_ACTIVITY))
                .add(FIELD_EMAIL, log.getString(FIELD_EMAIL))
                .add(FIELD_INSTRUMENT_ID, log.getString(FIELD_INSTRUMENT_ID))
                .add(FIELD_MESSAGE, log.getString(FIELD_MESSAGE));
    }

    // db.loanapprovals.createIndex( { "expireAt": 1 }, { expireAfterSeconds: 0 } )
    public void approveLoan(String instrument_id, String email) {
        LocalDateTime expireAt = LocalDateTime.now().plusMinutes(QR_DURATION_MINUTES);
        Document approval = new Document();
        approval.put(FIELD_INSTRUMENT_ID, instrument_id);
        approval.put(FIELD_APPROVER, email);
        approval.put(FIELD_EXPIREAT, expireAt);
        mongoRepo.approveLoan(approval);
    }

    public String getApprover(String instrument_id) {
        return mongoRepo.checkApproval(instrument_id)
                .getString(FIELD_APPROVER);
    }

    public String getFCMToken(String approverEmail) {
        Document doc = mongoRepo.getFCMToken(approverEmail);
        if (doc == null) {
            return null;
        }
        return doc.getString(FIELD_FCM_TOKEN);
    }

    public void upsertFCMToken(String jwt, String token) {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        // documentation reccomend to keep no longer than 2 months
        LocalDateTime expireAt = LocalDateTime.now().plusMonths(2);
        Document doc = new Document();
        doc.put(FIELD_EMAIL, email);
        doc.put(FIELD_FCM_TOKEN, token);
        doc.put(FIELD_EXPIREAT, expireAt);
        mongoRepo.upsertFCMToken(doc);
    }

}
