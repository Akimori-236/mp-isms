package tfip.akimori.server.repositories;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MongoLoggingRepository {
    public static final String COLLECTION_STORES = "storeactivity";
    public static final String COLLECTION_USER_ACTIVITY = "useractivity";

    @Autowired
    private MongoTemplate template;

    // logging of user access
    public void insertUserActivity(Document activity) {
        activity.put("time",
                LocalDateTime.now(ZoneId.of("Asia/Singapore")).format(getDTF("yyyy-LLL-dd HH:mm:ss")).toString()
                        + " SGT");
        template.insert(activity, COLLECTION_USER_ACTIVITY);
    }

    // tracking of instrument movement
    public void insertInstrumentActivity(Document activity) {
        activity.put("time",
                LocalDateTime.now(ZoneId.of("Asia/Singapore")).format(getDTF("yyyy-LLL-dd HH:mm:ss")).toString()
                        + " SGT");
        template.insert(activity, COLLECTION_STORES);
    }

    public List<Document> getLogsByStoreID(String StoreID) {
        // template TODO:
        return null;
    }

    private DateTimeFormatter getDTF(String format) {
        return DateTimeFormatter.ofPattern(format);
    }
}