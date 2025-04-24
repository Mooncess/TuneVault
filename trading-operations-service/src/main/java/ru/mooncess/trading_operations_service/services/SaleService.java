package ru.mooncess.trading_operations_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mooncess.trading_operations_service.client.MediaCatalogServClient;
import ru.mooncess.trading_operations_service.domain.MusicResourceSaleInfo;
import ru.mooncess.trading_operations_service.domain.SaleStatus;
import ru.mooncess.trading_operations_service.entities.Sale;
import ru.mooncess.trading_operations_service.repositories.SaleRepository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final MediaCatalogServClient mediaCatalogServClient;
    private final PaymentServiceGrpcImpl paymentServiceGrpc;
    private final SaleRepository saleRepository;
    private final MessageSender messageSender;
    private final ProducerBalanceService producerBalanceService;

    @Value("${payment.redirect.url}")
    private String redirectUrl;
    @Value("${download.path}")
    private String downloadPath;
    @Value("${msg.subject}")
    private String msgSubject;
    @Value("${mcs.api.key}")
    private String secretApiKey;

    public String createSale(Long id, String email) throws RuntimeException {
        try {
            BigDecimal price = new BigDecimal(mediaCatalogServClient.getPriceOfMusicResource(id, secretApiKey).getBody().toString());

            Sale sale = new Sale();
            sale.setSaleDate(LocalDateTime.now());
            sale.setBuyerEmail(email);
            sale.setAmountIncome(price);
            sale.setStatus(SaleStatus.CREATED);
            sale.setMusicResourceId(id);

            saleRepository.save(sale);

            return paymentServiceGrpc.createPaymentForm(sale.getId(), sale.getAmountIncome(), redirectUrl);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Music Resource Not Found");
        }
    }

    public void confirm(Long id) {
        Optional<Sale> sale = saleRepository.findById(id);

        if (sale.isPresent()) {
            sale.get().setStatus(SaleStatus.PAID_FOR);
            saleRepository.save(sale.get());
            MusicResourceSaleInfo musicResourceSaleInfo = mediaCatalogServClient
                    .getMusicResourceSaleInfo(sale.get().getMusicResourceId(), secretApiKey)
                    .getBody();
            producerBalanceService.increaseAuthorsBalance(sale.get().getAmountIncome(), musicResourceSaleInfo.getAuthorInfoList(), sale.get());

            String message = "Download link: " +
                    downloadPath +
                    musicResourceSaleInfo.getSourceURI() +
                    "\nThank you for your purchase!";

            messageSender.sendEmailMessage(sale.get().getBuyerEmail(), msgSubject, message);
        }
    }

    public List<Sale> findAllByMusicResource(Long id) {
        return saleRepository.findAllByMusicResourceId(id);
    }
}


