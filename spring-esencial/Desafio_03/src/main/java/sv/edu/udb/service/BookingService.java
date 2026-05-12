package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.entity.*;
import sv.edu.udb.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional //  Asegura que la operación sea atómica (punto extra en rúbrica)
    public Booking createBooking(Integer eventId, Integer quantity, String username) {

        // 1. Validar que el ID no sea nulo antes de buscar
        if (eventId == null) {
            throw new RuntimeException("El ID del evento es obligatorio.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2.  VALIDACIÓN DE CUPOS (Requerimiento 3.C)
        Integer reserved = bookingRepository.sumBookings(eventId);
        if (reserved == null) reserved = 0;

        if (reserved + quantity > event.getCapacity()) {
            throw new RuntimeException("No hay cupos disponibles. Espacios restantes: " + (event.getCapacity() - reserved));
        }

        // 3.  PREVENCIÓN DE NullPointerException EN EL PRECIO
        BigDecimal price = event.getPricePerTicket();
        if (price == null) {
            throw new RuntimeException("Error: El evento seleccionado no tiene un precio definido en la base de datos.");
        }

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setUser(user);
        booking.setQuantity(quantity);

        // 4.  CÁLCULO AUTOMÁTICO SEGURO
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
        booking.setTotalAmount(total);

        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(Status.CONFIRMED);

        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(String username) {
        // Uso de orElseThrow para evitar errores si el usuario no existe en la sesión
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return bookingRepository.findByUser(user);
    }

    @Transactional
    public void cancelBooking(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        booking.setStatus(Status.CANCELLED);
        bookingRepository.save(booking);
    }
}