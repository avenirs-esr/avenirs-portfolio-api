package fr.avenirsesr.portfolio.file.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.ActivityBanner;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.ActivityBannerRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ActivityResourceServiceImplTest {

  @Mock private FileStorageService fileStorageService;

  @Mock private ActivityBannerRepository activityBannerRepository;

  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private ActivityResourceServiceImpl service;

  private Activity activity;
  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    activity = ActivityFixture.create().toModel();
    user = UserFixture.create().toModel();
    when(loggedInUserService.getLoggedInUser()).thenReturn(user);
  }

  @Test
  void uploadBannerFor_shouldSaveNewBannerAndReturnIt() throws IOException {
    var fileName = "banner.png";
    var mimeType = "image/png";
    long size = 1234L;
    var content = "fake-image-data".getBytes();

    var expectedUri = "uri/to/banner.png";
    when(fileStorageService.upload(any(FileResource.class))).thenReturn(expectedUri);
    when(activityBannerRepository.findAllByActivity(activity)).thenReturn(List.of());

    ActivityBanner banner = service.uploadBannerFor(activity, fileName, mimeType, size, content);

    assertNotNull(banner);
    assertEquals(fileName, banner.getFileName());
    assertEquals(EFileType.PNG, banner.getFileType());
    assertEquals(size, banner.getSize());
    assertEquals(1, banner.getVersion());
    assertEquals(expectedUri, banner.getUri());
    assertEquals(user, banner.getUploadedBy());
    assertEquals(activity, banner.getActivity());

    ArgumentCaptor<ActivityBanner> captor = ArgumentCaptor.forClass(ActivityBanner.class);
    verify(activityBannerRepository).save(captor.capture());
    assertEquals(banner, captor.getValue());
  }

  @Test
  void uploadBannerFor_shouldThrowFileTypeNotSupportedException() {
    var fileName = "banner.tiff";
    var mimeType = "image/tiff";
    long size = 1234L;
    var content = new byte[] {};

    assertThrows(
        FileTypeNotSupportedException.class,
        () -> service.uploadBannerFor(activity, fileName, mimeType, size, content));
  }

  @Test
  void uploadBannerFor_shouldThrowFileSizeTooBigException() {
    var fileName = "banner.png";
    var mimeType = "image/png";
    long size = EFileType.PNG.getSizeLimit().bytes() + 1;
    var content = new byte[(int) size];

    assertThrows(
        FileSizeTooBigException.class,
        () -> service.uploadBannerFor(activity, fileName, mimeType, size, content));
  }

  @Test
  void uploadBannerFor_shouldIncrementVersionAndDeactivateOldBanners() throws IOException {
    var oldBanner =
        ActivityBanner.create(
            UUID.randomUUID(), "old.png", EFileType.PNG, 123, 1, "old-uri", user, activity);
    oldBanner.setActiveVersion(true);

    when(activityBannerRepository.findAllByActivity(activity)).thenReturn(List.of(oldBanner));
    when(fileStorageService.upload(any(FileResource.class))).thenReturn("new-uri");

    ActivityBanner newBanner =
        service.uploadBannerFor(activity, "new.png", "image/png", 123, new byte[] {1, 2, 3});

    // Old banner should be deactivated
    assertFalse(oldBanner.isActiveVersion());
    // New banner version should be incremented
    assertEquals(2, newBanner.getVersion());
    assertTrue(newBanner.isActiveVersion());
  }
}
