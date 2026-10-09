package edu.final_year.backendwebm.staff;

import edu.final_year.backendwebm.staff.dto.StaffRequest;
import edu.final_year.backendwebm.staff.dto.StaffResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores/{storeId}/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    public ResponseEntity<StaffResponse> create(@PathVariable UUID storeId,
                                                  @Valid @RequestBody StaffRequest request,
                                                  Authentication authentication) {
        StaffResponse response = staffService.create(storeId, authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<StaffResponse> list(@PathVariable UUID storeId, Authentication authentication) {
        return staffService.list(storeId, authentication.getName());
    }

    @PutMapping("/{staffId}")
    public StaffResponse update(@PathVariable UUID storeId,
                                 @PathVariable UUID staffId,
                                 @Valid @RequestBody StaffRequest request,
                                 Authentication authentication) {
        return staffService.update(storeId, staffId, authentication.getName(), request);
    }

    @DeleteMapping("/{staffId}")
    public ResponseEntity<Void> delete(@PathVariable UUID storeId,
                                        @PathVariable UUID staffId,
                                        Authentication authentication) {
        staffService.delete(storeId, staffId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
