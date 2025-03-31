package ru.mooncess.payment_service.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentProviderService {
    public String formPaymentPage(long id, String amount, String redirectUrl) {
        /*
        Here you need a code to send a quotation request to the payment system.
         */
        return "/confirm/" + id;
    }

    public boolean confirmWithdraw(long id, String amount, String destination) {
        /*
        Here you need a code to send a quotation request to the payment system.
         */
        return true;
    }
}

