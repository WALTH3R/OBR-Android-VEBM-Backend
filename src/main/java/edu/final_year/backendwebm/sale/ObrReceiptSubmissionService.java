package edu.final_year.backendwebm.sale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Placeholder for submitting a sale's receipt to OBR as a fiscal invoice. {@link edu.final_year.backendwebm.obr.ObrClient}
 * only implements {@code /login/} and {@code /checkTIN/} so far; the EBMS invoice-submission
 * endpoint (path and payload) isn't documented in this codebase yet. Until that spec is available,
 * this marks the sale as submitted locally only, so the rest of the receipt flow can be wired up now.
 * Replace the body with a real {@code ObrClient} call once the endpoint is known.
 */
@Service
public class ObrReceiptSubmissionService {

    private static final Logger log = LoggerFactory.getLogger(ObrReceiptSubmissionService.class);

    public void submit(Sale sale) {
        log.warn("OBR invoice submission endpoint is not implemented yet; marking sale {} as submitted locally only", sale.getId());
        sale.setObrSubmitted(true);
        sale.setObrSubmittedAt(Instant.now());
    }
}
