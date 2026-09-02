package fr.avenirsesr.portfolio.file.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FileResourceServiceImplTest {

  private static final String SOURCE_URI = "/storage/source.pdf";
  private static final String COPY_URI = "/storage/copy.pdf";

  @Mock private FileStorageService fileStorageService;
  @Mock private FileRepository fileRepository;
  @Mock private LoggedInUserService loggedInUserService;

  private FileResourceServiceImpl fileResourceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    fileResourceService =
        new FileResourceServiceImpl(fileStorageService, fileRepository, loggedInUserService);
  }

  @Nested
  class GivenAFileResourceService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a file resource service");
    }

    @Nested
    class WhenCopyingAFile {

      UUID sourceId;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("copying a file");
        sourceId = UUID.randomUUID();
      }

      @Nested
      class AndTheSourceFileExists {

        User loggedInUser;
        byte[] sourceContent;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the source file exists");
          loggedInUser = mock(User.class);
          sourceContent = new byte[] {1, 2, 3};

          when(fileRepository.findById(sourceId))
              .thenReturn(Optional.of(aSourceFile(EFileType.PDF, "consigne.pdf", 3L, true)));
          when(fileStorageService.get(SOURCE_URI)).thenReturn(sourceContent);
          when(loggedInUserService.getLoggedInUser()).thenReturn(loggedInUser);
          when(fileStorageService.upload(any())).thenReturn(COPY_URI);
          when(fileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Test
        void thenItShouldStoreTheSourceContentUnderANewId() {
          BddLogger.then("a new file holding the source content should be stored and saved");

          File copy = fileResourceService.copy(sourceId);

          assertNotEquals(sourceId, copy.getId());
          assertEquals(COPY_URI, copy.getUri());
          verify(fileRepository).save(copy);

          ArgumentCaptor<FileResource> captor = ArgumentCaptor.forClass(FileResource.class);
          verify(fileStorageService).upload(captor.capture());
          assertEquals(copy.getId(), captor.getValue().id());
          assertArrayEquals(sourceContent, captor.getValue().content());
        }

        @Test
        void thenItShouldKeepTheSourceFileMetadata() {
          BddLogger.then("name, type, size and restriction should be kept");

          File copy = fileResourceService.copy(sourceId);

          assertEquals("consigne.pdf", copy.getFileName());
          assertEquals(EFileType.PDF, copy.getFileType());
          assertEquals(3L, copy.getSize());
          assertTrue(copy.isRestricted());
        }

        @Test
        void thenItShouldSetTheLoggedInUserAsUploader() {
          BddLogger.then("the copy should be owned by the logged-in user");

          File copy = fileResourceService.copy(sourceId);

          assertEquals(loggedInUser, copy.getUploadedBy());
        }

        @Test
        void thenItShouldLeaveTheSourceFileUntouched() {
          BddLogger.then("the source file should neither be deleted nor overwritten");

          fileResourceService.copy(sourceId);

          verify(fileStorageService, never()).delete(any());
          verify(fileRepository, never()).removeFromDatabase(any());
        }
      }

      @Nested
      class AndTheSourceFileDoesNotExist {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the source file does not exist");
          when(fileRepository.findById(sourceId)).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldThrowFileNotFoundException() {
          BddLogger.then("the service should throw FileNotFoundException");

          assertThrows(FileNotFoundException.class, () -> fileResourceService.copy(sourceId));

          verifyNoInteractions(fileStorageService);
          verify(fileRepository, never()).save(any());
        }
      }

      private File aSourceFile(
          EFileType fileType, String fileName, long size, boolean isRestricted) {
        return File.toDomain(
            sourceId,
            fileType,
            fileName,
            size,
            SOURCE_URI,
            mock(User.class),
            Instant.now(),
            isRestricted,
            Instant.now(),
            Instant.now());
      }
    }
  }
}
