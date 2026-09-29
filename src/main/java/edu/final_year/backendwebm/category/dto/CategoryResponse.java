package edu.final_year.backendwebm.category.dto;

import edu.final_year.backendwebm.category.Category;

public record CategoryResponse(Long id, String name) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
