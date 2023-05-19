package tfip.akimori.server.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tfip.akimori.server.models.Instrument;
import tfip.akimori.server.models.Store;
import tfip.akimori.server.models.User;

@Repository
public class StoreRepository implements SQLQueries {

    @Autowired
    private JdbcTemplate template;

    // CREATE

    public boolean createStore(String store_id, String store_name, String creator_email) throws SQLException {
        // KeyHolder keyHolder = new GeneratedKeyHolder();
        System.out.println("NEW STORE ID: " + store_id);
        int rowsInserted = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_STORE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, store_id);
            ps.setString(2, store_name);
            ps.setString(3, creator_email);
            return ps;
        }
        // , keyHolder
        );
        // Number key = keyHolder.getKey();
        if (rowsInserted != 1) {
            throw new SQLException("Error inserting into stores table");
        }
        return rowsInserted == 1;
    }

    public boolean insertStoreManager(String email, String store_id) throws SQLException {
        int rowsInserted = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_MANAGER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, store_id);
            return ps;
        });
        if (rowsInserted != 1) {
            throw new SQLException("Error inserting into managers table");
        }
        return rowsInserted == 1;
    }

    // READ

    public List<Store> getManagedStores(String email) {
        return template.query(SQL_GETMANAGEDSTORES,
                BeanPropertyRowMapper.newInstance(Store.class),
                email);
    }

    public Boolean isManagerOfStore(String email, String storeID) {
        return template.queryForObject(SQL_CHECK_ISMANAGEROFSTORE, Boolean.class, storeID, email);
    }

    public List<Instrument> getStoreInstruments(String storeID) {
        return template.query(SQL_GETSTOREINSTRUMENTS,
                BeanPropertyRowMapper.newInstance(Instrument.class),
                storeID);
    }

    public List<User> getStoreManagers(String storeID) {
        return template.query(SQL_GETSTOREMANAGERS,
                BeanPropertyRowMapper.newInstance(User.class),
                storeID);
    }

}
