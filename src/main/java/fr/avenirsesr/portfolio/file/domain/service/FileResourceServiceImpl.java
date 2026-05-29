package fr.avenirsesr.portfolio.file.domain.service;

import fr.avenirsesr.portfolio.activity.domain.exception.ActivityDraftNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.*;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileCategory;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.trace.domain.exception.InvalidTraceTypeException;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class FileResourceServiceImpl implements FileResourceService {
  private final FileStorageService fileStorageService;
  private final FileRepository fileRepository;
  private final TraceRepository traceRepository;
  private final StaffRepository staffRepository;
  private final StudentRepository studentRepository;
  private final ActivityDraftRepository activityDraftRepository;
  private final LoggedInUserService loggedInUserService;
  private final TraceService traceService;
  private static final List<EFileType> ALLOWED_IMAGE_FILE_TYPES =
      List.of(EFileType.PNG, EFileType.JPEG, EFileType.GIF, EFileType.WEBP, EFileType.PJPEG);
  private static final Map<EFileCategory, List<EFileType>> ALLOWED_FILE_TYPES_BY_CATEGORY =
      Map.of(
          EFileCategory.TRACE_ATTACHEMENT,
          Arrays.stream(EFileType.values()).toList(),
          EFileCategory.STUDENT_PROFILE_PICTURE,
          ALLOWED_IMAGE_FILE_TYPES,
          EFileCategory.STAFF_PROFILE_PICTURE,
          ALLOWED_IMAGE_FILE_TYPES,
          EFileCategory.STUDENT_COVER_PICTURE,
          ALLOWED_IMAGE_FILE_TYPES,
          EFileCategory.STAFF_COVER_PICTURE,
          ALLOWED_IMAGE_FILE_TYPES,
          EFileCategory.ACTIVITY_BANNER,
          ALLOWED_IMAGE_FILE_TYPES);

  @Override
  public File upload(
      UUID elementId,
      EFileCategory fileCategory,
      String fileName,
      String mimeType,
      long size,
      byte[] content) {
    var loggedInUser = loggedInUserService.getLoggedInUser();

    chekUploadRG(loggedInUser, elementId, fileCategory);

    var fileResource =
        new FileResource(
            UUID.randomUUID(), fileName, EFileType.fromMimeType(mimeType), size, content);

    if (!ALLOWED_FILE_TYPES_BY_CATEGORY.get(fileCategory).contains(fileResource.fileType())) {
      throw new FileTypeNotSupportedException();
    }
    if (fileResource.fileType().getSizeLimit().isLessThan(size)) {
      throw new FileSizeTooBigException();
    }

    var uri = fileStorageService.upload(fileResource);
    var allFiles = fileRepository.findAllByElement(elementId);
    var version = allFiles.stream().map(File::getVersion).max(Integer::compareTo).orElse(0) + 1;
    var file =
        File.create(
            fileResource.id(),
            elementId,
            fileCategory,
            fileResource.fileType(),
            fileResource.fileName(),
            fileResource.size(),
            version,
            uri,
            loggedInUser);
    var savedFile = fileRepository.save(file);
    saveFileOnElement(elementId, fileCategory, file);
    return savedFile;
  }

  @Override
  public FileResource fetchContent(UUID fileId) {
    var file = fileRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
    byte[] content = fileStorageService.get(file.getUri());
    return new FileResource(
        file.getId(), file.getFileName(), file.getFileType(), file.getSize(), content);
  }

  @Override
  public FileDownload download(UUID fileId) {
    var loggedInUser = loggedInUserService.getLoggedInUser();
    var file = fileRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
    chekDownloadRG(loggedInUser, file.getElementId(), file.getFileCategory());
    return new FileDownload(file.getFileName(), fileStorageService.get(file.getUri()));
  }

  @Override
  public void delete(UUID fileId) {
    var loggedInUser = loggedInUserService.getLoggedInUser();
    var file = fileRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
    chekDeleteRG(loggedInUser, file.getElementId(), file.getFileCategory());
    fileRepository.removeFromDatabase(file);
    fileStorageService.delete(file.getId());
    log.info("File deleted: {}", file);
  }

  private void saveFileOnElement(UUID elementId, EFileCategory fileCategory, File file) {
    switch (fileCategory) {
      case TRACE_ATTACHEMENT -> {
        var trace = traceService.getTraceById(elementId);
        trace.setAttachment(file);
        traceRepository.save(trace);
      }
      case STUDENT_COVER_PICTURE -> {
        var student = studentRepository.findById(elementId).orElseThrow(UserNotFoundException::new);
        student.setCoverPicture(file);
        studentRepository.save(student);
      }
      case STUDENT_PROFILE_PICTURE -> {
        var student = studentRepository.findById(elementId).orElseThrow(UserNotFoundException::new);
        student.setProfilePicture(file);
        studentRepository.save(student);
      }
      case STAFF_COVER_PICTURE -> {
        var staff = staffRepository.findById(elementId).orElseThrow(UserNotFoundException::new);
        staff.setCoverPicture(file);
        staffRepository.save(staff);
      }
      case STAFF_PROFILE_PICTURE -> {
        var staff = staffRepository.findById(elementId).orElseThrow(UserNotFoundException::new);
        staff.setProfilePicture(file);
        staffRepository.save(staff);
      }
      case ACTIVITY_BANNER -> {
        var draft =
            activityDraftRepository
                .findById(elementId)
                .orElseThrow(ActivityDraftNotFoundException::new);
        draft.setBanner(file);
        activityDraftRepository.save(draft);
      }
    }
  }

  private void chekUploadRG(User loggedInUser, UUID elementId, EFileCategory fileCategory) {
    switch (fileCategory) {
      case TRACE_ATTACHEMENT -> {
        var trace = traceService.getTraceById(elementId);
        if (!trace.getUser().equals(loggedInUser)) throw new UserNotAuthorizedException();
        if (trace.getLink().isPresent()) throw new InvalidTraceTypeException();
      }
      case STUDENT_COVER_PICTURE, STUDENT_PROFILE_PICTURE -> {
        var student = studentRepository.findById(elementId).orElseThrow(UserNotFoundException::new);
        if (!student.getUser().equals(loggedInUser)) throw new UserNotAuthorizedException();
      }
      case STAFF_COVER_PICTURE, STAFF_PROFILE_PICTURE -> {
        var staff = staffRepository.findById(elementId).orElseThrow(UserNotFoundException::new);
        if (!staff.getUser().equals(loggedInUser)) throw new UserNotAuthorizedException();
      }
      case ACTIVITY_BANNER -> {
        var draft =
            activityDraftRepository
                .findById(elementId)
                .orElseThrow(ActivityDraftNotFoundException::new);
        if (!draft.getAuthor().getUser().equals(loggedInUser))
          throw new UserNotAuthorizedException();
      }
    }
  }

  private void chekDeleteRG(User loggedInUser, UUID elementId, EFileCategory fileCategory) {
    switch (fileCategory) {
      case TRACE_ATTACHEMENT -> throw new UserNotAuthorizedException();
      case STUDENT_COVER_PICTURE, STUDENT_PROFILE_PICTURE -> {
        var student = studentRepository.findById(elementId).orElseThrow(UserNotFoundException::new);
        if (!student.getUser().equals(loggedInUser)) throw new UserNotAuthorizedException();
      }
      case STAFF_COVER_PICTURE, STAFF_PROFILE_PICTURE -> {
        var staff = staffRepository.findById(elementId).orElseThrow(UserNotFoundException::new);
        if (!staff.getUser().equals(loggedInUser)) throw new UserNotAuthorizedException();
      }
    }
  }

  private void chekDownloadRG(User loggedInUser, UUID elementId, EFileCategory fileCategory) {
    switch (fileCategory) {
      case TRACE_ATTACHEMENT -> {
        var trace = traceService.getTraceById(elementId);
        if (!trace.getUser().equals(loggedInUser)) throw new UserNotAuthorizedException();
        if (trace.getLink().isPresent()) throw new InvalidTraceTypeException();
        if (trace.getAttachment().isEmpty()) throw new InvalidTraceTypeException();
      }
      case STUDENT_PROFILE_PICTURE,
          STUDENT_COVER_PICTURE,
          STAFF_PROFILE_PICTURE,
          STAFF_COVER_PICTURE,
          ACTIVITY_BANNER ->
          throw new UserNotAuthorizedException();
    }
  }
}
