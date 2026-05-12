package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.entity.Event;
import sv.edu.udb.repository.EventRepository;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EventController {

    private final EventRepository eventRepository;

    @GetMapping
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public Event getById(@PathVariable Integer id) {
        return eventRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public Event create(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @PutMapping("/{id}")
    public Event update(@PathVariable Integer id, @RequestBody Event event) {
        event.setIdEvent(id);
        return eventRepository.save(event);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        eventRepository.deleteById(id);
    }
}