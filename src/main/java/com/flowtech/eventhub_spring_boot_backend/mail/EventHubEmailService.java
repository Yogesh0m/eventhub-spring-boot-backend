package com.flowtech.eventhub_spring_boot_backend.mail;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventHubEmailService {

    
    private final JavaMailSender mailSender;

    public void sendBookingSuccessEmail(
            String toEmail,
            String customerName,
            String eventName,
            int bookingId,
            LocalDateTime eventDate,
            String venue) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Booking Confirmed - EventHub");

        String emailBody = """
                Dear %s,

                Congratulations! Your event booking has been confirmed successfully.

                Booking Details:
                -------------------------
                Booking ID : %s
                Event Name : %s
                Event Date : %s
                Venue      : %s
                -------------------------

                Thank you for choosing EventHub!

                We look forward to seeing you at the event.

                Best Regards,
                Team EventHub
                """.formatted(
                customerName,
                bookingId,
                eventName,
                eventDate,
                venue
        );

        message.setText(emailBody);

        mailSender.send(message);
    }
}
