package edu.final_year.backendwebm.province;

import edu.final_year.backendwebm.exception.ProvinceNotFoundException;
import edu.final_year.backendwebm.province.dto.CommuneResponse;
import edu.final_year.backendwebm.province.dto.ProvinceResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/provinces")
public class ProvinceController {

    private final ProvinceRepository provinceRepository;
    private final CommuneRepository communeRepository;

    public ProvinceController(ProvinceRepository provinceRepository, CommuneRepository communeRepository) {
        this.provinceRepository = provinceRepository;
        this.communeRepository = communeRepository;
    }

    @GetMapping
    public List<ProvinceResponse> list() {
        return provinceRepository.findAll().stream().map(ProvinceResponse::from).toList();
    }

    @GetMapping("/{provinceId}/communes")
    public List<CommuneResponse> listCommunes(@PathVariable UUID provinceId) {
        if (!provinceRepository.existsById(provinceId)) {
            throw new ProvinceNotFoundException();
        }
        return communeRepository.findAllByProvinceId(provinceId).stream().map(CommuneResponse::from).toList();
    }
}
