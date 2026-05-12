package sv.edu.udb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BookingRequest {
    @JsonProperty("event_id") // Esto mapea el JSON del app.js a esta variable
    private Integer eventId;

    private Integer quantity;
}