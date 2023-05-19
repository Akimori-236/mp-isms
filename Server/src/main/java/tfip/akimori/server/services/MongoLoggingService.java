package tfip.akimori.server.services;

import java.time.LocalTime;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import tfip.akimori.server.repositories.MongoLoggingRepository;

@Service
public class MongoLoggingService {
    private static Long QR_DURATION_MINUTES = 15L;

    @Autowired
    private MongoLoggingRepository mongoRepo;

    public void logUserActivity(String activity, String email) {
        Document doc = new Document();
        doc.put("email", email);
        doc.put("activity", activity);
        mongoRepo.insertUserActivity(doc);
    }

    public void logInstrumentActivity(
            String store_id,
            String activity,
            String email,
            String instrument_id,
            String instrument_type,
            String serial_number,
            Boolean isRepairing,
            String remarks) {
        Document doc = new Document();
        doc.put("store_id", store_id);
        doc.put("activity", activity);
        doc.put("email", email);
        doc.put("instrument_id", instrument_id);
        doc.put("instrument_type", instrument_type);
        doc.put("serial_number", serial_number);
        doc.put("isRepairing", isRepairing);
        doc.put("remarks", remarks);
        mongoRepo.insertInstrumentActivity(doc);
    }

    public JsonArray getStoreLogs(String storeID) {
        System.out.println("GETTING LOGS FOR STORE: " + storeID);
        List<Document> docList = mongoRepo.getLogsByStoreID(storeID);
        JsonArrayBuilder jar = Json.createArrayBuilder();
        for (Document d : docList) {
            jar.add(LogToJOB(d));
        }
        return jar.build();
    }

    private JsonObjectBuilder LogToJOB(Document log) {
        return Json.createObjectBuilder()
                .add("store_id", log.getString("store_id"))
                .add("activity", log.getString("activity"))
                .add("email", log.getString("email"))
                .add("instrument_id", log.getString("instrument_id"))
                .add("instrument_type", log.getString("instrument_type"))
                .add("serial_number", log.getString("serial_number"))
                .add("isRepairing", log.getBoolean("isRepairing"))
                .add("time", log.getString("time"))
                .add("remarks", log.getString("remarks"));
    }

    public void approveLoan(String instrumentID, String email) {
        // db.loanapprovals.createIndex( { "expireAt": 1 }, { expireAfterSeconds: 0 } )
        LocalTime expireAt = LocalTime.now().plusMinutes(QR_DURATION_MINUTES);
        Document approval = new Document();
        approval.put("instrumentID", instrumentID);
        approval.put("approver", email);
        approval.put("expireAt", expireAt);
        mongoRepo.approveLoan(approval);
    }
}
