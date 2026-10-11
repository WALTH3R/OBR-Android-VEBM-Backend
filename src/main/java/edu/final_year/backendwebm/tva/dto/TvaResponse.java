package edu.final_year.backendwebm.tva.dto;

import edu.final_year.backendwebm.tva.Tva;

import java.math.BigDecimal;
import java.util.UUID;

public record TvaResponse(UUID id, String name, BigDecimal rate) {

    public static TvaResponse from(Tva tva) {
        return new TvaResponse(tva.getId(), tva.getName(), tva.getRate());
    }
}
