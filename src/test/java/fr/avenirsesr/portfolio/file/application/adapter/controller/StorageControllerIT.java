package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.UserResourceServiceImpl;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.repository.UserPhotoDatabaseRepository;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StorageControllerIT {

  @Autowired private MockMvc mockMvc;

  @Mock private FileStorageService fileStorageService;
  @Autowired private StorageController storageController;
  @Autowired private UserPhotoDatabaseRepository userPhotoDatabaseRepository;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @BeforeAll
  static void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    storageController =
        new StorageController(
            new UserResourceServiceImpl(fileStorageService, userPhotoDatabaseRepository),
            fileStorageService);
    mockMvc = MockMvcBuilders.standaloneSetup(storageController).build();
  }

  @Test
  void shouldGetUserResourceByFileId() throws Exception {
    UUID existingFileId = UUID.fromString("1d90989c-0f99-4a9c-a834-8a97d8b39d23");

    when(fileStorageService.get(anyString()))
        .thenReturn("Contenu du fichier de test".getBytes(StandardCharsets.UTF_8));
    mockMvc
        .perform(
            get("/storage/storage/users/{fileId}", existingFileId)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_PNG));
  }

  @Test
  void shouldGetDefaultUserProfilePhoto() throws Exception {
    when(fileStorageService.get(anyString()))
        .thenReturn("Contenu du fichier de test".getBytes(StandardCharsets.UTF_8));
    mockMvc
        .perform(
            get("/storage/storage/users/default/{photoType}", EUserPhotoType.PROFILE)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_PNG));
  }

  @Test
  void shouldGetDefaultUserCoverPhoto() throws Exception {
    when(fileStorageService.get(anyString()))
        .thenReturn("Contenu du fichier de test".getBytes(StandardCharsets.UTF_8));
    mockMvc
        .perform(
            get("/storage/storage/users/default/{photoType}", EUserPhotoType.COVER)
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_PNG));
  }
}
