package tfip.akimori.server.services;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import tfip.akimori.server.models.Instrument;
import tfip.akimori.server.models.Store;
import tfip.akimori.server.models.User;
import tfip.akimori.server.repositories.StoreRepository;
import tfip.akimori.server.utils.MyUtils;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepo;
    @Autowired
    private JwtService jwtSvc;

    @Transactional(rollbackFor = SQLException.class)
    public boolean createStore(String jwt, String store_name) throws SQLException {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        // Generate storeID
        String storeID = UUID.randomUUID().toString().substring(0, 8);
        // rollback these >
        storeRepo.createStore(storeID, store_name, email);
        boolean isInserted = storeRepo.insertStoreManager(email, storeID);
        return isInserted;
    }

    public List<JsonObject> getManagedStores(String jwt) {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        List<Store> storeList = storeRepo.getManagedStores(email);
        List<JsonObject> outputList = new LinkedList<>();
        for (Store s : storeList) {
            outputList.add(MyUtils.storeToJson(s));
        }
        return outputList;
    }

    public JsonObject getStoreDetails(String jwt, String storeID) {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        // verify store manager
        if (storeRepo.isManagerOfStore(email, storeID)) {
            // get all store managers
            List<User> managerList = this.storeRepo.getStoreManagers(storeID);
            // get all instruments
            List<Instrument> instrumentList = this.storeRepo.getStoreInstruments(storeID);
            // build json
            return Json.createObjectBuilder()
                    .add("managers", MyUtils.userListToJAB(managerList))
                    .add("instruments", MyUtils.instrumentListToJAB(instrumentList))
                    .build();
        } else {
            // not a manager of the store
            return null;
        }
    }

    public void addManager(String jwt, String storeID, String inviteEmail) throws DataIntegrityViolationException {
        // get email from JWT
        String email = jwtSvc.extractUsername(jwt);
        if (storeRepo.isManagerOfStore(email, storeID)) {
            storeRepo.insertStoreManager(inviteEmail, storeID);
        }
    }

    // ==============================

    // private static List<JsonObject> sortManager(List<Store> storeList) {
    // // sorting
    // Map<Integer, List<User>> storeMap = new HashMap<>();
    // for (Store s : storeList) {
    // if (!storeMap.containsKey(s.getStore_id())) {
    // storeMap.put(s.getStore_id(), new LinkedList<>());
    // storeMap.get(s.getStore_id()).add(s.getUser());
    // } else {
    // storeMap.get(s.getStore_id()).add(s.getUser());
    // }
    // }
    // List<JsonObject> jList = new LinkedList<>();
    // for (Integer storeid : storeMap.keySet()) {
    // JsonObjectBuilder job = Json.createObjectBuilder();
    // JsonArrayBuilder jab = Json.createArrayBuilder();
    // for (User u : storeMap.get(storeid)) {
    // jab.add(Json.createObjectBuilder()
    // .add("email", u.getEmail())
    // .add("givenname", u.getGivenname())
    // .add("familyname", u.getFamilyname()));
    // }
    // job.add("store_name", storeid);
    // job.add("managers", jab);
    // jList.add(job.build());
    // }
    // System.out.println(storeMap);
    // return jList;
    // }
}
