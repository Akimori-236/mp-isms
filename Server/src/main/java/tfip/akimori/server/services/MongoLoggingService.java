package tfip.akimori.server.services;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import tfip.akimori.server.repositories.MongoLoggingRepository;

@Service
public class MongoLoggingService {
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
                .add("remarks", log.getString("remarks"));
    }
}
