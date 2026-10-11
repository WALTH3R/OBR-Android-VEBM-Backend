package edu.final_year.backendwebm.province.dto;

import edu.final_year.backendwebm.province.Province;

import java.util.UUID;

public record ProvinceResponse(UUID id, String name) {

    public static ProvinceResponse from(Province province) {
        return new ProvinceResponse(province.getId(), province.getName());
    }
}
