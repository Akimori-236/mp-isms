package tfip.akimori.server.repositories;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MongoLoggingRepository {


    public static final String COLLECTION_STORE_ACTIVITY = "storeactivity";
    public static final String COLLECTION_USER_ACTIVITY = "useractivity";
    public static final String COLLECTION_LOAN_APPROVALS = "loanapprovals";

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
        template.insert(activity, COLLECTION_STORE_ACTIVITY);
    }

    // db.storeactivity.find({"email": "aki@gmail.com"})
    public List<Document> getLogsByStoreID(String StoreID) {
        Criteria criteria = Criteria.where("Store_id").is(StoreID);
        Query query = new Query(criteria);
        return template.find(query, Document.class, COLLECTION_STORE_ACTIVITY);
    }

    private DateTimeFormatter getDTF(String format) {
        return DateTimeFormatter.ofPattern(format);
    }

    public void approveLoan(Document doc) {
        template.insert(doc, COLLECTION_LOAN_APPROVALS);
    }
}