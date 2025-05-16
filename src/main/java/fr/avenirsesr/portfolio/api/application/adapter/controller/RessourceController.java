package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.api.domain.port.input.RessourceService;
import fr.avenirsesr.portfolio.api.domain.utils.RessourceUtils;
import fr.avenirsesr.portfolio.api.domain.utils.UserUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping
public class RessourceController {

    private final RessourceService ressourceService;

    public RessourceController(RessourceService ressourceService) {
        this.ressourceService = ressourceService;
    }


    @GetMapping("/photo/{profile}/{filename}")
    public ResponseEntity<ByteArrayResource> getPhoto(@PathVariable String profile, @PathVariable String filename) throws IOException {
        EUserCategory userCategory = UserUtils.getUserCategory(profile);
        byte[] photo = ressourceService.getPhoto(userCategory, filename);
        ByteArrayResource resource = new ByteArrayResource(photo);

        return ResponseEntity.ok()
            .contentType(RessourceUtils.getImageExtensionMediaType(filename))
            .body(resource);
    }

    @GetMapping("/cover/{profile}/{filename}")
    public ResponseEntity<ByteArrayResource> getCover(@PathVariable String profile, @PathVariable String filename) throws IOException {
        EUserCategory userCategory = UserUtils.getUserCategory(profile);
        byte[] photo = ressourceService.getCover(userCategory, filename);
        ByteArrayResource resource = new ByteArrayResource(photo);

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(resource);
    }
}
