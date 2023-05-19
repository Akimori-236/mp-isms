package tfip.akimori.server.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Instrument {
    private String instrument_id;
    private String instrument_type;
    private String brand;
    private String model;
    private String serial_number;
    private String store_id;
    private String store_name;
    private boolean isRepairing;
    private String email;
    private String givenname;
    private String familyname;
    private String remarks;
}
