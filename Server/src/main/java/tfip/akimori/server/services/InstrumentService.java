package tfip.akimori.server.services;

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
import tfip.akimori.server.utils.MyUtils;

@Service
public class InstrumentService {
    private static final int ID_LENGTH = 8;

    @Autowired
    private InstrumentRepository instruRepo;
    @Autowired
    private JwtService jwtSvc;
    @Autowired
    private MongoLoggingService logSvc;

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
        logSvc.logInstrumentActivity(i.getStore_id(), "insert", email, i.getInstrument_id(), i.getInstrument_type(),
                i.getSerial_number(), i.isRepairing(), i.getRemarks());
        return instruRepo.addInstrument(i);
    }

    // TODO: how to loan out to myself?

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

    public boolean updateInstrument(String jwt, Instrument i) {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        // TODO:
        logSvc.logInstrumentActivity("update", i.getStore_id(), email, i.getInstrument_id(), i.getInstrument_type(),
                i.getSerial_number(), i.isRepairing(), i.getRemarks());
        return false;
    }

    public Boolean borrow(String jwt, String instrument_id) {
        // when generate qr, post token to mongo (with expiry)
        // then here check if there is token
        // if not qr can be reused
        return false;
    }
}
