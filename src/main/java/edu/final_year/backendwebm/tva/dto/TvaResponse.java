package edu.final_year.backendwebm.tva.dto;

import edu.final_year.backendwebm.tva.Tva;

import java.math.BigDecimal;

public record TvaResponse(Long id, String name, BigDecimal rate) {

    public static TvaResponse from(Tva tva) {
        return new TvaResponse(tva.getId(), tva.getName(), tva.getRate());
    }
}
