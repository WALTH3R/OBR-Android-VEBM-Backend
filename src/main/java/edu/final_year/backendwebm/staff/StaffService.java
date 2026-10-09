package edu.final_year.backendwebm.staff;

import edu.final_year.backendwebm.exception.StaffNotFoundException;
import edu.final_year.backendwebm.staff.dto.StaffRequest;
import edu.final_year.backendwebm.staff.dto.StaffResponse;
import edu.final_year.backendwebm.store.Store;
import edu.final_year.backendwebm.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final StoreService storeService;
    private final PasswordEncoder passwordEncoder;

    public StaffResponse create(UUID storeId, String userEmail, StaffRequest request) {
        Store store = storeService.findOwnedStore(storeId, userEmail);

        Staff staff = new Staff();
        staff.setStore(store);
        applyRequest(staff, request);

        return StaffResponse.from(staffRepository.save(staff));
    }

    public List<StaffResponse> list(UUID storeId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);

        return staffRepository.findAllByStoreId(storeId).stream()
                .map(StaffResponse::from)
                .toList();
    }

    @Transactional
    public StaffResponse update(UUID storeId, UUID staffId, String userEmail, StaffRequest request) {
        storeService.findOwnedStore(storeId, userEmail);
        Staff staff = staffRepository.findByIdAndStoreId(staffId, storeId)
                .orElseThrow(StaffNotFoundException::new);

        applyRequest(staff, request);

        return StaffResponse.from(staffRepository.save(staff));
    }

    @Transactional
    public void delete(UUID storeId, UUID staffId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);
        Staff staff = staffRepository.findByIdAndStoreId(staffId, storeId)
                .orElseThrow(StaffNotFoundException::new);

        staffRepository.delete(staff);
    }

    private void applyRequest(Staff staff, StaffRequest request) {
        staff.setFullName(request.fullName().trim());
        staff.setPinHash(passwordEncoder.encode(request.pin()));
        staff.setRole(request.role());
    }
}
