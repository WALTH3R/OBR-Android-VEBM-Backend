package edu.final_year.backendwebm.province.dto;

import edu.final_year.backendwebm.province.Commune;

import java.util.UUID;

public record CommuneResponse(UUID id, String name) {

    public static CommuneResponse from(Commune commune) {
        return new CommuneResponse(commune.getId(), commune.getName());
    }
}
