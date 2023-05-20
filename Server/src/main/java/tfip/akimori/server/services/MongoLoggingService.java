package tfip.akimori.server.services;

import java.time.LocalTime;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import tfip.akimori.server.repositories.InstrumentRepository;
import tfip.akimori.server.repositories.MongoLoggingRepository;
import tfip.akimori.server.repositories.MongoVariables;

@Service
public class MongoLoggingService implements MongoVariables {
    private static Long QR_DURATION_MINUTES = 15L;

    @Autowired
    private MongoLoggingRepository mongoRepo;
    @Autowired
    private InstrumentRepository instruRepo;

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
            String instrument_type,
            String serial_number,
            Boolean isRepairing,
            String remarks) {
        Document doc = new Document();
        doc.put(FIELD_STORE_ID, store_id);
        doc.put(FIELD_ACTIVITY, activity);
        doc.put(FIELD_EMAIL, email);
        doc.put(FIELD_INSTRUMENT_ID, instrument_id);
        doc.put(FIELD_INSTRUMENT_TYPE, instrument_type);
        doc.put(FIELD_SERIAL_NUMBER, serial_number);
        doc.put(FIELD_ISREPAIRING, isRepairing);
        doc.put(FIELD_REMARKS, remarks);
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
                .add(FIELD_STORE_ID, log.getString(FIELD_STORE_ID))
                .add(FIELD_ACTIVITY, log.getString(FIELD_ACTIVITY))
                .add(FIELD_EMAIL, log.getString(FIELD_EMAIL))
                .add(FIELD_INSTRUMENT_ID, log.getString(FIELD_INSTRUMENT_ID))
                .add(FIELD_INSTRUMENT_TYPE, log.getString(FIELD_INSTRUMENT_TYPE))
                .add(FIELD_SERIAL_NUMBER, log.getString(FIELD_SERIAL_NUMBER))
                .add(FIELD_ISREPAIRING, log.getBoolean(FIELD_ISREPAIRING))
                .add(FIELD_TIME, log.getString(FIELD_TIME))
                .add(FIELD_REMARKS, log.getString(FIELD_REMARKS));
    }

    // db.loanapprovals.createIndex( { "expireAt": 1 }, { expireAfterSeconds: 0 } )
    public void approveLoan(String instrument_id, String email) {
        LocalTime expireAt = LocalTime.now().plusMinutes(QR_DURATION_MINUTES);
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

    public void logInstrumentLoaned(String borrowerEmail, String instrument_id, String approverEmail) {
        String store_id = instruRepo.getInstrumentById(instrument_id).getStore_id();
        Document doc = new Document();
        doc.put(FIELD_APPROVER, approverEmail);
        doc.put(FIELD_INSTRUMENT_ID, instrument_id);
        doc.put(FIELD_BORROWER, borrowerEmail);
        doc.put(FIELD_STORE_ID, store_id);
        mongoRepo.insertInstrumentActivity(doc);
    }
}
