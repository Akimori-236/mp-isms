package tfip.akimori.server.models;

import lombok.Data;

@Data
public class EmailSchedule {
    private String userEmail;
    private Integer scheduledHour;
}
