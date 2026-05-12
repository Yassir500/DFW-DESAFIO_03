package sv.edu.udb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_booking")
    private Integer idBooking;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonProperty("event_id") //  vincula el JSON con el objeto Event
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonProperty("user_id") //  vincula el JSON con el objeto User
    private User user;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_amount", nullable = false)
    @JsonProperty("total_amount") // Permite devolver el total calculado al HTML
    private BigDecimal totalAmount;

    @Column(name = "booking_date")
    @JsonProperty("booking_date")
    private LocalDateTime bookingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // CONFIRMED o CANCELLED
}