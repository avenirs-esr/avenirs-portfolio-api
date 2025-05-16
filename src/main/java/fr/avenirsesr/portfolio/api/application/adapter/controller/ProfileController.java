package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProfileDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.ProfileMapper;
import fr.avenirsesr.portfolio.api.application.adapter.request.ProfileUpdateRequest;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.input.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/me")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile() {
        UUID id = UUID.randomUUID();
        User user = profileService.getProfile(id);
        return ResponseEntity.ok(ProfileMapper.userDomainToDto(user));
    }

    @PutMapping("/update")
    public ResponseEntity<ProfileDTO> updateProfile(@RequestBody ProfileUpdateRequest request) {
        UUID id = UUID.randomUUID();
        User user = profileService.updateProfile(id, request.getFirstname(), request.getLastname(), request.getEmail(), request.getBio());
        return ResponseEntity.ok(ProfileMapper.userDomainToDto(user));
    }

    @PutMapping("/update/photo")
    public ResponseEntity<ProfileDTO> updateProfilePhoto(@RequestParam("file") MultipartFile photoFile) throws IOException {
        UUID id = UUID.randomUUID();
        User user = profileService.updateProfilePhoto(id, photoFile);
        return ResponseEntity.ok(ProfileMapper.userDomainToDto(user));
    }

    @PutMapping("/update/cover")
    public ResponseEntity<ProfileDTO> updateProfileCover(@RequestParam("file") MultipartFile coverFile) throws IOException {
        UUID id = UUID.randomUUID();
        User user = profileService.updateProfileCover(id, coverFile);
        return ResponseEntity.ok(ProfileMapper.userDomainToDto(user));
    }

}
