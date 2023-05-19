package tfip.akimori.server.services;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            String serial_number) {
        Document doc = new Document();
        doc.put("store_id", store_id);
        doc.put("activity", activity);
        doc.put("email", email);
        doc.put("instrument_id", instrument_id);
        doc.put("instrument_type", instrument_type);
        doc.put("serial_number", serial_number);
        mongoRepo.insertInstrumentActivity(doc);
    }

}
