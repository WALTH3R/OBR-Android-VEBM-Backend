package edu.final_year.backendwebm.tva;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds OBR's published VAT rates (Law n°1/12 of 29 July 2013) on first boot.
 * These are set by law, not something a store owner defines, so there's no create endpoint.
 */
@Component
public class TvaSeeder implements ApplicationRunner {

    private final TvaRepository tvaRepository;

    public TvaSeeder(TvaRepository tvaRepository) {
        this.tvaRepository = tvaRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (tvaRepository.count() > 0) {
            return;
        }

        List<Tva> defaults = List.of(
                newTva("Taux normal", new BigDecimal("18.00")),
                newTva("Taux zéro (export)", BigDecimal.ZERO),
                newTva("Exonéré", BigDecimal.ZERO)
        );
        tvaRepository.saveAll(defaults);
    }

    private static Tva newTva(String name, BigDecimal rate) {
        Tva tva = new Tva();
        tva.setName(name);
        tva.setRate(rate);
        return tva;
    }
}
