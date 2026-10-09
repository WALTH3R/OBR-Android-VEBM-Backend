package edu.final_year.backendwebm.sale;

import edu.final_year.backendwebm.sale.dto.CreateSaleRequest;
import edu.final_year.backendwebm.sale.dto.SaleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores/{storeId}/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<SaleResponse> create(@PathVariable UUID storeId,
                                                @Valid @RequestBody CreateSaleRequest request,
                                                Authentication authentication) {
        SaleResponse response = saleService.create(storeId, authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<SaleResponse> list(@PathVariable UUID storeId, Authentication authentication) {
        return saleService.list(storeId, authentication.getName());
    }

    @GetMapping("/{saleId}")
    public SaleResponse get(@PathVariable UUID storeId, @PathVariable UUID saleId, Authentication authentication) {
        return saleService.get(storeId, saleId, authentication.getName());
    }

    @GetMapping("/{saleId}/receipt")
    public ResponseEntity<byte[]> receipt(@PathVariable UUID storeId,
                                           @PathVariable UUID saleId,
                                           Authentication authentication) {
        byte[] pdf = saleService.generateReceipt(storeId, saleId, authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"receipt-" + saleId + ".pdf\"")
                .body(pdf);
    }

    @PostMapping("/{saleId}/submit-to-obr")
    public SaleResponse submitToObr(@PathVariable UUID storeId,
                                     @PathVariable UUID saleId,
                                     Authentication authentication) {
        return saleService.submitToObr(storeId, saleId, authentication.getName());
    }
}
