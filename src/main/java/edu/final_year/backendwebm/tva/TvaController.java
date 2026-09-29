package edu.final_year.backendwebm.tva;

import edu.final_year.backendwebm.tva.dto.TvaResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tva")
public class TvaController {

    private final TvaRepository tvaRepository;

    public TvaController(TvaRepository tvaRepository) {
        this.tvaRepository = tvaRepository;
    }

    @GetMapping
    public List<TvaResponse> list() {
        return tvaRepository.findAll().stream().map(TvaResponse::from).toList();
    }
}
