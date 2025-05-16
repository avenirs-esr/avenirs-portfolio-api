package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProfileDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.ProfileMapper;
import fr.avenirsesr.portfolio.api.application.adapter.request.ProfileUpdateRequest;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.api.domain.port.input.UserService;
import fr.avenirsesr.portfolio.api.domain.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}/{profile}/overview")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable UUID userId, @PathVariable String profile) {
        EUserCategory userCategory = UserUtils.getUserCategory(profile);
        User user = userService.getProfile(userId);

        if (userCategory == EUserCategory.STUDENT) {
            return ResponseEntity.ok(ProfileMapper.userStudentDomainToDto(user));
        } else {
            return ResponseEntity.ok(ProfileMapper.userTeacherDomainToDto(user));
        }
    }

    @PutMapping("/{userId}/{profile}/update")
    public ResponseEntity<String> updateProfile(@PathVariable UUID userId, @PathVariable String profile, @RequestBody ProfileUpdateRequest request) {
        userService.updateProfile(userId, request.getFirstname(), request.getLastname(), request.getEmail(), request.getBio());
        return ResponseEntity.ok("Mise à jour faite.");
    }

    @PutMapping("/{userId}/{profile}/update/photo")
    public ResponseEntity<String> updateProfilePhoto(@PathVariable UUID userId, @PathVariable String profile, @RequestParam("file") MultipartFile photoFile) throws IOException {
        userService.updateProfilePicture(userId, photoFile);
        return ResponseEntity.ok("Mise à jour faite.");
    }

    @PutMapping("/{userId}/{profile}/update/cover")
    public ResponseEntity<String> updateProfileCover(@PathVariable UUID userId, @PathVariable String profile, @RequestParam("file") MultipartFile coverFile) throws IOException {
        userService.updateCoverPicture(userId, coverFile);
        return ResponseEntity.ok("Mise à jour faite.");
    }
}
