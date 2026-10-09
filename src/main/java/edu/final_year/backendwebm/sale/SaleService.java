package edu.final_year.backendwebm.sale;

import edu.final_year.backendwebm.client.Client;
import edu.final_year.backendwebm.client.ClientRepository;
import edu.final_year.backendwebm.exception.ClientNotFoundException;
import edu.final_year.backendwebm.exception.InvalidPaymentException;
import edu.final_year.backendwebm.exception.ProductNotFoundException;
import edu.final_year.backendwebm.exception.SaleNotFoundException;
import edu.final_year.backendwebm.product.Product;
import edu.final_year.backendwebm.product.ProductRepository;
import edu.final_year.backendwebm.sale.dto.CreateSaleRequest;
import edu.final_year.backendwebm.sale.dto.PaymentRequest;
import edu.final_year.backendwebm.sale.dto.SaleItemRequest;
import edu.final_year.backendwebm.sale.dto.SaleResponse;
import edu.final_year.backendwebm.store.Store;
import edu.final_year.backendwebm.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final StoreService storeService;
    private final ReceiptPdfGenerator receiptPdfGenerator;
    private final ObrReceiptSubmissionService obrReceiptSubmissionService;

    @Transactional
    public SaleResponse create(UUID storeId, String userEmail, CreateSaleRequest request) {
        Store store = storeService.findOwnedStore(storeId, userEmail);

        Sale sale = new Sale();
        sale.setStore(store);

        if (request.clientId() != null) {
            Client client = clientRepository.findByIdAndStoreId(request.clientId(), storeId)
                    .orElseThrow(ClientNotFoundException::new);
            sale.setClient(client);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal tvaAmount = BigDecimal.ZERO;

        for (SaleItemRequest itemRequest : request.items()) {
            Product product = productRepository.findByIdAndStoreId(itemRequest.productId(), storeId)
                    .orElseThrow(ProductNotFoundException::new);

            BigDecimal quantity = BigDecimal.valueOf(itemRequest.quantity());
            BigDecimal lineSubtotal = product.getSellPricePerUnit().multiply(quantity).setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineTva = lineSubtotal.multiply(product.getTva().getRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = lineSubtotal.add(lineTva);

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setUnitOfMeasurement(product.getUnitOfMeasurement());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(product.getSellPricePerUnit());
            item.setTvaRate(product.getTva().getRate());
            item.setLineSubtotal(lineSubtotal);
            item.setLineTvaAmount(lineTva);
            item.setLineTotal(lineTotal);
            sale.getItems().add(item);

            subtotal = subtotal.add(lineSubtotal);
            tvaAmount = tvaAmount.add(lineTva);
        }

        BigDecimal totalAmount = subtotal.add(tvaAmount);
        applyPayment(sale, request.payment(), totalAmount);

        sale.setSubtotal(subtotal);
        sale.setTvaAmount(tvaAmount);
        sale.setTotalAmount(totalAmount);

        return SaleResponse.from(saleRepository.save(sale));
    }

    public List<SaleResponse> list(UUID storeId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);

        return saleRepository.findAllByStoreId(storeId).stream()
                .map(SaleResponse::from)
                .toList();
    }

    public SaleResponse get(UUID storeId, UUID saleId, String userEmail) {
        return SaleResponse.from(findOwnedSale(storeId, saleId, userEmail));
    }

    public byte[] generateReceipt(UUID storeId, UUID saleId, String userEmail) {
        Sale sale = findOwnedSale(storeId, saleId, userEmail);
        return receiptPdfGenerator.generate(sale);
    }

    @Transactional
    public SaleResponse submitToObr(UUID storeId, UUID saleId, String userEmail) {
        Sale sale = findOwnedSale(storeId, saleId, userEmail);
        obrReceiptSubmissionService.submit(sale);
        return SaleResponse.from(saleRepository.save(sale));
    }

    private Sale findOwnedSale(UUID storeId, UUID saleId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);
        return saleRepository.findByIdAndStoreId(saleId, storeId).orElseThrow(SaleNotFoundException::new);
    }

    private void applyPayment(Sale sale, PaymentRequest payment, BigDecimal totalAmount) {
        if (payment.amount().compareTo(totalAmount) < 0) {
            throw InvalidPaymentException.insufficientAmount();
        }
        if (payment.method() != PaymentMethod.CASH) {
            if (payment.reference() == null || payment.reference().isBlank()) {
                throw InvalidPaymentException.referenceRequired();
            }
            if (payment.amount().compareTo(totalAmount) != 0) {
                throw InvalidPaymentException.exactAmountRequired();
            }
        }

        sale.setPaymentMethod(payment.method());
        sale.setPaymentAmount(payment.amount());
        sale.setPaymentReference(payment.reference() == null || payment.reference().isBlank()
                ? null : payment.reference().trim());
    }
}
