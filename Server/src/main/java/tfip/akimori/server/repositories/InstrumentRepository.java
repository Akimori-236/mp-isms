package tfip.akimori.server.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tfip.akimori.server.models.Instrument;

@Repository
public class InstrumentRepository implements SQLQueries {

    @Autowired
    private JdbcTemplate template;

    public List<Instrument> getBorrowedByEmail(String email) {
        return template.query(SQL_GETBORROWEDBYEMAIL,
                BeanPropertyRowMapper.newInstance(Instrument.class),
                email);
    }

    public List<Instrument> getManagedInstrumentsByEmail(String email) {
        return template.query(SQL_GETMANAGEDINSTRUMENTSBYEMAIL,
                BeanPropertyRowMapper.newInstance(Instrument.class),
                email);
    }

    public boolean insertInstrument(Instrument instr, int store_id) {
        int rowsInserted = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_INSTRUMENT,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, instr.getInstrument_type());
            ps.setString(2, instr.getBrand());
            ps.setString(3, instr.getModel());
            ps.setString(4, instr.getSerial_number());
            ps.setInt(5, store_id);
            return ps;
        });
        return rowsInserted > 0;
    }

    public boolean addInstrument(Instrument i) {
        int rowsInserted = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_INSTRUMENT);
            ps.setString(1, i.getInstrument_id());
            ps.setString(2, i.getInstrument_type());
            ps.setString(3, i.getBrand());
            ps.setString(4, i.getModel());
            ps.setString(5, i.getSerial_number());
            ps.setString(6, i.getRemarks());
            ps.setString(7, i.getStore_id());
            ps.setBoolean(8, i.isRepairing());
            return ps;
        });
        return rowsInserted > 0;
        // NO REPLY?!?!
    }

    public Instrument getInstrumentById(String id) {
        return template.queryForObject(SQL_GETINSTRUMENTBYID,
                BeanPropertyRowMapper.newInstance(Instrument.class),
                id);
    }

    public Boolean borrow(String email, String id) {
        int rowsUpdated = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_LOANOUT_INSTRUMENT);
            ps.setString(1, email);
            ps.setString(2, id);
            return ps;
        });
        return rowsUpdated > 0;
    }
}