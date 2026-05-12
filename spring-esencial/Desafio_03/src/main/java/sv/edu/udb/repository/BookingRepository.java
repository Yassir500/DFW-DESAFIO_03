package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import sv.edu.udb.entity.*;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUser(User user);

    @Query("SELECT SUM(b.quantity) FROM Booking b WHERE b.event.idEvent = :eventId AND b.status = 'CONFIRMED'")
    Integer sumBookings(@Param("eventId") Integer eventId);
}