package edu.final_year.backendwebm.province;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Seeds Burundi's 5 provinces and their communes, per "Loi Organique N° 1/05 du 16 Mars 2023"
 * (the 2023 reform, in force since the 2025 elections). Gitega is seeded with 8 of its 9 communes:
 * every available source (the law's own news coverage, government sites) states "9 communes" for
 * Gitega but only ever names 8 — the law's original text is a non-OCRable scanned PDF. Add the
 * missing commune here once confirmed.
 */
@Component
public class ProvinceCommuneSeeder implements ApplicationRunner {

    private static final Map<String, List<String>> PROVINCES_AND_COMMUNES = Map.of(
            "Buhumuza", List.of("Butaganzwa", "Butihinda", "Cankuzo", "Gisagara", "Gisuru", "Muyinga", "Ruyigi"),
            "Bujumbura", List.of("Bubanza", "Bukinanyana", "Cibitoke", "Isare", "Mpanda", "Mugere", "Mugina",
                    "Muhuta", "Mukaza", "Ntahangwa", "Rwibaga"),
            "Burunga", List.of("Bururi", "Makamba", "Matana", "Musongati", "Nyanza", "Rumonge", "Rutana"),
            "Butanyerera", List.of("Busoni", "Kayanza", "Kiremba", "Kirundo", "Matongo", "Muhanga", "Ngozi",
                    "Tangara"),
            "Gitega", List.of("Bugendana", "Gishubi", "Gitega", "Kiganda", "Muramvya", "Mwaro", "Nyabihanga",
                    "Shombo")
    );

    private final ProvinceRepository provinceRepository;
    private final CommuneRepository communeRepository;

    public ProvinceCommuneSeeder(ProvinceRepository provinceRepository, CommuneRepository communeRepository) {
        this.provinceRepository = provinceRepository;
        this.communeRepository = communeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (provinceRepository.count() > 0) {
            return;
        }

        PROVINCES_AND_COMMUNES.forEach((provinceName, communeNames) -> {
            Province province = new Province();
            province.setName(provinceName);
            provinceRepository.save(province);

            communeNames.forEach(communeName -> {
                Commune commune = new Commune();
                commune.setName(communeName);
                commune.setProvince(province);
                communeRepository.save(commune);
            });
        });
    }
}
