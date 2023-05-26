package tfip.akimori.server.services;

import java.sql.SQLWarning;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import tfip.akimori.server.models.Instrument;
import tfip.akimori.server.repositories.InstrumentRepository;
import tfip.akimori.server.repositories.StoreRepository;
import tfip.akimori.server.utils.MyUtils;

@Service
public class InstrumentService {
    private static final int ID_LENGTH = 8;

    @Autowired
    private InstrumentRepository instruRepo;
    @Autowired
    private JwtService jwtSvc;
    @Autowired
    private MongoService logSvc;
    @Autowired
    private MessagingService msgSvc;
    @Autowired
    private StoreRepository storeRepo;

    public List<JsonObject> getBorrowedByJWT(String jwt) {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        // System.out.println(email);

        List<Instrument> instrumentList = instruRepo.getBorrowedByEmail(email);
        List<JsonObject> jList = new LinkedList<>();
        for (Instrument i : instrumentList) {
            jList.add(MyUtils.instrumentToJOB(i).build());
        }
        return jList;
    }

    public boolean addInstrument(String jwt, String storeID, JsonObject jObj) {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);

        Instrument i = Instrument.builder()
                .instrument_id(generateID(ID_LENGTH))
                .instrument_type(jObj.getString("instrument_type"))
                .brand(jObj.getString("brand"))
                .model(jObj.getString("model"))
                .serial_number(jObj.getString("serial_number"))
                .store_id(storeID)
                .isRepairing(false)
                .email(email)
                .build();
        System.out.println(i);
        Boolean isInserted = instruRepo.addInstrument(i);
        String logMsg = "%s Inserted %s (S/N: %s) ".formatted(email, i.getInstrument_type(), i.getSerial_number());
        logSvc.logInstrumentActivity(i.getStore_id(), "insert", email, i.getInstrument_id(), logMsg);
        return isInserted;
    }

    private static String generateID(int length) {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .replace("_", "")
                .substring(0, length);
    }

    public JsonObject getInstrumentByID(String id) {
        Instrument i = instruRepo.getInstrumentById(id);
        JsonObjectBuilder job = Json.createObjectBuilder()
                .add("instrument_id", i.getInstrument_id())
                .add("instrument_type", i.getInstrument_type())
                .add("brand", i.getBrand())
                .add("model", i.getModel())
                .add("serial_number", i.getSerial_number())
                .add("store_id", i.getBrand())
                .add("store_name", i.getStore_name())
                .add("isRepairing", i.getBrand());
        if (i.getRemarks() == null) {
            job.add("remarks", "");
        } else {
            job.add("remarks", i.getRemarks());
        }
        return job.build();
    }

    public boolean updateInstrument(String jwt, JsonObject jObj) {
        Instrument i = jsonToInstrument(jObj);
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        if (!storeRepo.isManagerOfStore(email, i.getStore_id())) {
            return false;
        } else {
            Boolean isUpdated = instruRepo.updateInstrument(i);
            if (isUpdated) {
                String logMsg = "%s Updated %s (S/N: %s) ".formatted(email, i.getInstrument_type(),
                        i.getSerial_number());
                logSvc.logInstrumentActivity(i.getStore_id(), "update", email, i.getInstrument_id(), logMsg);
            }
            return isUpdated;
        }
    }

    public Boolean borrowInstrument(String jwt, String instrument_id) {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        // check for approval if not qr can be reused
        String approverEmail = logSvc.getApprover(instrument_id);
        if (approverEmail.isBlank()) {
            return false;
        } else {
            // send update to SQL
            Boolean isUpdated = instruRepo.borrow(email, instrument_id);
            if (!isUpdated) {
                return false;
            } else {
                Instrument i = instruRepo.getInstrumentById(instrument_id);
                String logMsg = "%s Loaned out %s (S/N: %s) to %s".formatted(
                        approverEmail,
                        i.getInstrument_type(),
                        i.getSerial_number(),
                        email);
                logSvc.logInstrumentActivity(i.getStore_id(), "loaned out", approverEmail, i.getInstrument_id(),
                        logMsg);
                // trigger notification
                msgSvc.borrowNotification(email, instrument_id, approverEmail);
                return true;
            }
        }
    }

    public Boolean returnInstrument(String jwt, String storeID, String instrument_id) {
        // get email from JWT
        String receiverEmail = jwtSvc.extractUsername(jwt);
        if (!storeRepo.isManagerOfStore(receiverEmail, storeID)) {
            return false;
        } else {
            // send update to SQL
            String returnerEmail;
            try {
                returnerEmail = instruRepo.returnInstrument(instrument_id);
            } catch (SQLWarning e) {
                System.err.println(e);
                return false;
            }
            Instrument i = instruRepo.getInstrumentById(instrument_id);
            String logMsg = "%s Returned %s (S/N: %s) to %s".formatted(
                    returnerEmail,
                    i.getInstrument_type(),
                    i.getSerial_number(),
                    receiverEmail);
            logSvc.logInstrumentActivity(i.getStore_id(), "returned", receiverEmail, instrument_id, logMsg);
            // trigger notification
            msgSvc.returnedNotification(returnerEmail, instrument_id, receiverEmail);
            return true;
        }
    }

    private Instrument jsonToInstrument(JsonObject jObj) {
        return Instrument.builder()
                .instrument_id(jObj.getString("instrument_id"))
                .instrument_type(jObj.getString("instrument_type"))
                .brand(jObj.getString("brand"))
                .model(jObj.getString("model"))
                .serial_number(jObj.getString("serial_number"))
                .store_id(jObj.getString("store_id"))
                .store_name(jObj.getString("store_name"))
                .isRepairing(jObj.getBoolean("isRepairing"))
                .remarks(jObj.getString("remarks"))
                .build();
    }
}
