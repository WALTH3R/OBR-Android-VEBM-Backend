package edu.final_year.backendwebm.category.dto;

import edu.final_year.backendwebm.category.Category;

import java.util.UUID;

public record CategoryResponse(UUID id, String name) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
