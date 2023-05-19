package tfip.akimori.server.models;

import lombok.Data;

@Data
public class EmailRequest {
    private String toEmail;
    private String subject;
    private String body;
}
