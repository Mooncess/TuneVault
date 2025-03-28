package ru.mooncess.media_catalog_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mooncess.media_catalog_service.domain.enums.SaleStatus;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Sale;
import ru.mooncess.media_catalog_service.repositories.SaleRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final MusicResourceService musicResourceService;
    private final PaymentServiceGrpcImpl paymentServiceGrpc;
    private final SaleRepository saleRepository;
    private final AuthorService authorService;
    private final MessageSender messageSender;

    @Value("${payment.redirect.url}")
    private String redirectUrl;
    @Value("${download.path}")
    private String downloadPath;
    @Value("${msg.subject}")
    private String msgSubject;

    public String createSale(Long id, String email) throws RuntimeException {
        Optional<MusicResource> mr = musicResourceService.findById(id);
        if (mr.isPresent()) {
            Sale sale = new Sale();
            sale.setSaleDate(LocalDateTime.now());
            sale.setBuyerEmail(email);
            sale.setAmountIncome(mr.get().getPrice());
            sale.setStatus(SaleStatus.CREATED);
            sale.setMusicResource(mr.get());

            saleRepository.save(sale);
            return paymentServiceGrpc.createPaymentForm(sale.getId(), sale.getAmountIncome(), redirectUrl);
        } else {
            throw new RuntimeException("Music Resource Not Found");
        }
    }

    public void confirm(Long id) {
        Optional<Sale> sale = saleRepository.findById(id);

        if (sale.isPresent()) {
            sale.get().setStatus(SaleStatus.PAID_FOR);
            saleRepository.save(sale.get());
            authorService.increaseAuthorsBalance(sale.get().getAmountIncome(), sale.get().getMusicResource());

            String message = "Download link: " +
                    downloadPath +
                    sale.get().getMusicResource().getSourceURI() +
                    "\nThank you for your purchase!";

            messageSender.sendEmailMessage(sale.get().getBuyerEmail(), msgSubject, message);
        }
    }
}

