package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.dto.BookingRequest;
import sv.edu.udb.entity.Booking;
import sv.edu.udb.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody BookingRequest request, Authentication auth) {
        // Esto aparecerá en la consola de IntelliJ
        System.out.println("DEBUG: Intentando reservar Evento ID: " + request.getEventId());

        Booking newBooking = bookingService.createBooking(
                request.getEventId(),
                request.getQuantity(),
                auth.getName()
        );
        return ResponseEntity.ok(newBooking);
    }
    // Obtener las reservas del usuario logueado (Requerimiento 3.C)
    @GetMapping("/my")
    public List<Booking> myBookings(Authentication auth) {
        return bookingService.getMyBookings(auth.getName());
    }

    // Cancelar reserva (Cambio de estado a CANCELLED)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
}