package tfip.akimori.server.repositories;

import java.sql.PreparedStatement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import tfip.akimori.server.exceptions.DuplicateEmailException;

@Repository
public class GoogleRepository implements SQLQueries {

    @Autowired
    private JdbcTemplate template;

    public void insertUser(Payload payload) throws DuplicateEmailException {
        try {
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_INSERT_GOOGLEUSER);
                ps.setString(1, payload.getSubject());
                ps.setString(2, payload.getEmail());
                ps.setBoolean(3, Boolean.valueOf(payload.getEmailVerified()));
                ps.setString(4, (String) payload.get("name"));
                ps.setString(5, (String) payload.get("picture"));
                ps.setString(6, (String) payload.getOrDefault("family_name", ""));
                ps.setString(7, (String) payload.getOrDefault("given_name", ""));
                return ps;
            });
        } catch (DataIntegrityViolationException e) {
            System.err.println(e);
            throw new DuplicateEmailException("Google user with email " + payload.getEmail() + " already exists", e);
        }
    }
}
