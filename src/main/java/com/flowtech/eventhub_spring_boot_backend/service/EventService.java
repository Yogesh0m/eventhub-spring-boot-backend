package com.flowtech.eventhub_spring_boot_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.flowtech.eventhub_spring_boot_backend.entity.Booking;
import com.flowtech.eventhub_spring_boot_backend.entity.Event;
import com.flowtech.eventhub_spring_boot_backend.entity.User;
import com.flowtech.eventhub_spring_boot_backend.enums.EventVerification;
import com.flowtech.eventhub_spring_boot_backend.exception.TicketNotAvailableException;
import com.flowtech.eventhub_spring_boot_backend.mail.EventHubEmailService;
import com.flowtech.eventhub_spring_boot_backend.repository.BookingRepository;
import com.flowtech.eventhub_spring_boot_backend.repository.EventRepository;
import com.flowtech.eventhub_spring_boot_backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository repository;
    private final BookingRepository bookingRepository;
    private final EventHubEmailService emailService;

    public ResponseEntity<?> registerEvent(Event event, Authentication authentication) {
        System.out.println("Event received in service: " + event);

        String email = authentication.getName();
        System.out.println("Authenticated user: " + email);

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse(null);

        if ("ROLE_ORGANISER".equals(role)) {
            repository.findByEmail(email).ifPresent(event::setUser);
        }

        Event dbEvent = eventRepository.save(event);
        return ResponseEntity.ok(dbEvent);
    }

    @Transactional
    public Booking bookEvent(Integer eventId, int ticketQuantity, Authentication authentication) {
        String customerEmail = authentication.getName();

        User user = repository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId).orElse(null);

        if (event != null && event.getStatus() == EventVerification.APPROVED) {
            int availableTickets = event.getAvailableTickets();

            if (ticketQuantity <= availableTickets && ticketQuantity > 0) {
                Booking booking = new Booking();
                booking.setEvent(event);
                booking.setUser(user);
                booking.setQuantity(ticketQuantity);
                booking.setTotalPrice(ticketQuantity * event.getTicketPrice());
                booking.setPaymentStatus("PAID");
                booking.setPaymentDateTime(LocalDateTime.now());
                booking.setEventDateTime(event.getEventDateTime());
                booking.setBookingDateTime(LocalDateTime.now());

                bookingRepository.save(booking);

                event.setAvailableTickets(availableTickets - ticketQuantity);
                eventRepository.save(event);

                try {
                    emailService.sendBookingSuccessEmail(
                            customerEmail,
                            user.getName(),
                            event.getName(),
                            booking.getBookingId(),
                            booking.getEventDateTime(),
                            event.getLocation());
                } catch (MailException exception) {
                    log.error("Booking {} was saved, but its confirmation email failed",
                            booking.getBookingId(), exception);
                }

                return booking;
            }

            throw new TicketNotAvailableException("Ticket quantity is invalid or tickets are sold out.");
        }

        return null;
    }

    public List<Event> findEventByStatusPending() {
        return eventRepository.findByStatus(EventVerification.PENDING);
    }

    public Event find(Integer eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }

    public List<Event> getAllApprovedEvents() {
        return eventRepository.findByStatus(EventVerification.APPROVED);
    }

    public List<Event> getAllRejectedEvents() {
        return eventRepository.findByStatus(EventVerification.REJECTED);
    }
}
