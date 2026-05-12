package sv.edu.udb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Útil para crear objetos rápidamente en tus pruebas unitarias
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_event")
    @JsonProperty("id_event") // Asegura que el frontend reciba "id_event" y no "idEvent"
    private Integer idEvent;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // Permite descripciones más largas en la BD
    private String description;

    @Column(name = "event_date", nullable = false)
    @JsonProperty("event_date")
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "price_per_ticket", nullable = false, precision = 10, scale = 2)
    @JsonProperty("price_per_ticket")
    private BigDecimal pricePerTicket;
}