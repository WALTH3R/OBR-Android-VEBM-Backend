package edu.final_year.backendwebm.staff.dto;

import edu.final_year.backendwebm.staff.Staff;
import edu.final_year.backendwebm.staff.StaffRole;

import java.time.Instant;
import java.util.UUID;

public record StaffResponse(UUID id, String fullName, StaffRole role, Instant createdAt) {

    public static StaffResponse from(Staff staff) {
        return new StaffResponse(staff.getId(), staff.getFullName(), staff.getRole(), staff.getCreatedAt());
    }
}
