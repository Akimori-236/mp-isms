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
public class MongoLoggingRepository implements MongoVariables {

    @Autowired
    private MongoTemplate template;

    // logging of user access
    public void insertUserActivity(Document activity) {
        activity.put(FIELD_TIME,
                LocalDateTime.now(ZoneId.of(ZONE_SG)).format(getDTF(TIME_FORMAT)).toString()
                        + " SGT");
        template.insert(activity, COLLECTION_USER_ACTIVITY);
    }

    // tracking of instrument movement
    public void insertInstrumentActivity(Document activity) {
        activity.put(FIELD_TIME,
                LocalDateTime.now(ZoneId.of(ZONE_SG)).format(getDTF(TIME_FORMAT)).toString()
                        + " SGT");
        template.insert(activity, COLLECTION_STORE_ACTIVITY);
    }

    // db.storeactivity.find({"store_id": "asdf232"})
    public List<Document> getLogsByStoreID(String StoreID) {
        Criteria criteria = Criteria.where(FIELD_STORE_ID).is(StoreID);
        Query query = new Query(criteria);
        return template.find(query, Document.class, COLLECTION_STORE_ACTIVITY);
    }

    private DateTimeFormatter getDTF(String format) {
        return DateTimeFormatter.ofPattern(format);
    }

    public void approveLoan(Document doc) {
        template.insert(doc, COLLECTION_LOAN_APPROVALS);
    }

    // db.loanapprovals.find({"instrument_id": "367377a1"}).count()
    public Document checkApproval(String instrument_id) {
        Criteria criteria = Criteria.where(FIELD_INSTRUMENT_ID).is(instrument_id);
        Query query = new Query(criteria);
        return template.findOne(query, Document.class, COLLECTION_LOAN_APPROVALS);
    }
}