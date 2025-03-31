package ru.mooncess.media_catalog_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Setter
@Service
public class MessageSender {
    @Value("${queue.name}")
    private String queueName;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendEmailMessage(String emailTo, String subject, String message) {
        EmailMessage emailMessage = new EmailMessage(emailTo, subject, message);
        try {
            String json = objectMapper.writeValueAsString(emailMessage);
            amqpTemplate.convertAndSend(queueName, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert email message to JSON", e);
        }
    }

    public void sendWithdrawEmailMessage(String emailTo, String subject, String message) {
        EmailMessage emailMessage = new EmailMessage(emailTo, subject, message);
        try {
            String json = objectMapper.writeValueAsString(emailMessage);
            amqpTemplate.convertAndSend(queueName, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert email message to JSON", e);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class EmailMessage {
        private String emailTo;
        private String subject;
        private String message;
    }
}
