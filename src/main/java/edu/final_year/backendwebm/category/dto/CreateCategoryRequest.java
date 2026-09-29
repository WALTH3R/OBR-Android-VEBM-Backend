package edu.final_year.backendwebm.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 150, message = "Category name must be at most 150 characters")
        String name
) {
}
