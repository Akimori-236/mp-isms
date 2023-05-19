package tfip.akimori.server.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import tfip.akimori.server.exceptions.DuplicateEmailException;
import tfip.akimori.server.models.EmailSchedule;
import tfip.akimori.server.models.User;

@Repository
public class UserRepository implements SQLQueries {

    @Autowired
    private JdbcTemplate template;

    public Integer insertUser(User user) throws DuplicateEmailException {
        // System.out.println("PASSWORD>>>>>>>>" + user.getPassword());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getGivenname());
                ps.setString(2, user.getFamilyname());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPassword());
                ps.setString(5, user.getRole().name());
                ps.setBoolean(6, user.getIsGoogleLogin());
                return ps;
            }, keyHolder);
        } catch (DataIntegrityViolationException e) {
            System.err.println(e);
            throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists", e);
        }
        Number key = keyHolder.getKey();
        return (key == null) ? null : key.intValue();
    }

    public User getUserByEmail(String email) throws EmptyResultDataAccessException {
        return template.queryForObject(SQL_GETUSERBYEMAIL, BeanPropertyRowMapper.newInstance(User.class),
                email);
    }

    public List<EmailSchedule> getAllSchedules() {
        return template.query(SQL_GETALLSCHEDULES, new BeanPropertyRowMapper<>(EmailSchedule.class));
    }
}
