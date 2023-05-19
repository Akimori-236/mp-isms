package tfip.akimori.server.utils;

import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import tfip.akimori.server.models.Instrument;
import tfip.akimori.server.models.Store;
import tfip.akimori.server.models.User;

public class MyUtils {

    // public static Instrument instrumentBuilder(ResultSet rs) {
    // User user;
    // try {
    // user = User.builder()
    // .givenname(rs.getString("givenname"))
    // .familyname(rs.getString("familyname"))
    // .email(rs.getString("email"))
    // .build();
    // } catch (SQLException e) {
    // System.err.println("FAILED BUILDING USER");
    // return null;
    // }
    // Instrument instrument;
    // try {
    // instrument = Instrument.builder()
    // .instrument_id(rs.getString("instrument_id"))
    // .instrument_type(rs.getString("instrument_type"))
    // .brand(rs.getString("brand"))
    // .model(rs.getString("model"))
    // .serial_number(rs.getString("serial_number"))
    // .store_name(rs.getString("store_name"))
    // .user(user)
    // .build();
    // } catch (SQLException e) {
    // System.err.println("FAILED BUILDING INSTRUMENT");
    // return null;
    // }
    // return instrument;
    // }

    // public static JsonObject instrumentToJsonObject(Instrument i) {
    // JsonObject jObj = Json.createObjectBuilder()
    // .add("instrument_id", i.getInstrument_id())
    // .add("instrument_type", i.getInstrument_type())
    // .add("brand", i.getBrand())
    // .add("model", i.getModel())
    // .add("serial_number", i.getSerial_number())
    // .add("store_name", i.getStore_name())
    // .add("givenname", i.getGivenname())
    // .add("familyname", i.getFamilyname())
    // .add("email", i.getEmail())
    // .build();
    // return jObj;
    // }

    // public static List<JsonObject>
    // instrumentListToJsonObjectList(List<Instrument> instrumentList) {
    // List<JsonObject> jList = new LinkedList<>();
    // for (Instrument ins : instrumentList) {
    // jList.add(instrumentToJsonObject(ins));
    // }
    // return jList;
    // }

    public static JsonObject storeToJson(Store s) {
        return Json.createObjectBuilder()
                .add("store_id", s.getStore_id())
                .add("store_name", s.getStore_name())
                .build();
    }

    public static JsonObjectBuilder instrumentToJOB(Instrument i) {
        JsonObjectBuilder job = Json.createObjectBuilder()
                .add("instrument_id", i.getInstrument_id())
                .add("instrument_type", i.getInstrument_type())
                .add("brand", i.getBrand())
                .add("model", i.getModel())
                .add("serial_number", i.getSerial_number())
                .add("store_name", i.getStore_name())
                .add("isRepairing", i.isRepairing());
        if (null != i.getEmail()) {
            // if not loaned out
            job.add("email", i.getEmail())
                    .add("givenname", i.getGivenname())
                    .add("familyname", i.getFamilyname());
        } else {
            job.add("email", JsonValue.NULL)
                    .add("givenname", JsonValue.NULL)
                    .add("familyname", JsonValue.NULL);
        }
        return job;
    }

    public static JsonArrayBuilder instrumentListToJAB(List<Instrument> instrumentList) {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (Instrument i : instrumentList) {
            jab.add(instrumentToJOB(i));
        }
        return jab;
    }

    public static JsonObjectBuilder userToJOB(User u) {
        JsonObjectBuilder job = Json.createObjectBuilder()
                .add("email", u.getEmail())
                .add("givenname", u.getGivenname())
                .add("familyname", u.getFamilyname());
        return job;
    }

    public static JsonArrayBuilder userListToJAB(List<User> userList) {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (User u : userList) {
            jab.add(userToJOB(u));
        }
        return jab;
    }
}