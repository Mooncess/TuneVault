package ru.mooncess.mail_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RabbitReceiver {

    private final MsgSenderService msgSenderService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = {"#{queue.name}"})
    public void receive(String jsonMessage) {
        try {
            EmailMessage emailMessage = objectMapper.readValue(jsonMessage, EmailMessage.class);
            msgSenderService.send(emailMessage.getEmailTo(),
                    emailMessage.getSubject(),
                    emailMessage.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to process message: " + jsonMessage, e);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class EmailMessage {
        private String emailTo;
        private String subject;
        private String message;
    }
}
