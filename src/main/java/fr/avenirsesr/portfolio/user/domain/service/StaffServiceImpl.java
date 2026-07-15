package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.mapper.FileDataMapper;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStaffException;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class StaffServiceImpl implements StaffService {
  private final StaffRepository staffRepository;
  private final UserRepository userRepository;
  private final LoggedInUserService loggedInUserService;
  private final FileResourceService fileResourceService;

  @Override
  public Staff getStaffById(UUID id) {
    return staffRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.error("Staff {} not found", id);
              return new UserIsNotStaffException();
            });
  }

  @Override
  public UserProfileOverviewData getStaffProfile() {
    var staff = loggedInUserService.getLoggedInStaff();

    return new UserProfileOverviewData(
        staff.getId(),
        staff.getUser().getFirstName(),
        staff.getUser().getLastName(),
        staff.getInstitutionEmail(),
        staff.getBio(),
        FileDataMapper.mapFileData(
            staff.getCoverPicture(), FileStorageConstants.DEFAULT_COVER_FILE_URL),
        FileDataMapper.mapFileData(
            staff.getProfilePicture(), FileStorageConstants.DEFAULT_PROFILE_FILE_URL));
  }

  @Override
  public void updateProfile(User user, String bio) {
    var staff = staffRepository.findById(user.getId()).orElseThrow(UserIsNotStaffException::new);

    if (bio != null) staff.setBio(bio);

    staffRepository.save(staff);
  }

  @Override
  public Staff createStaff(UUID userId, String institutionEmail, String bio) {
    var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var staff = Staff.create(user, institutionEmail, bio);
    staffRepository.save(staff);
    if (user.getEmail() == null) {
      user.setEmail(institutionEmail);
      userRepository.save(user);
    }
    return staff;
  }

  @Override
  public File uploadProfilePicture(String fileName, String mimeType, long size, byte[] content) {
    var staff = loggedInUserService.getLoggedInStaff();
    requireImage(mimeType);
    var file = fileResourceService.upload(fileName, mimeType, size, content, false);
    staff.setProfilePicture(file);
    staffRepository.save(staff);
    return file;
  }

  @Override
  public void deleteProfilePicture() {
    var staff = loggedInUserService.getLoggedInStaff();
    staff.getProfilePicture().ifPresent(picture -> fileResourceService.delete(picture.getId()));
    staff.setProfilePicture(null);
    staffRepository.save(staff);
  }

  @Override
  public File uploadCoverPicture(String fileName, String mimeType, long size, byte[] content) {
    var staff = loggedInUserService.getLoggedInStaff();
    requireImage(mimeType);
    var file = fileResourceService.upload(fileName, mimeType, size, content, false);
    staff.setCoverPicture(file);
    staffRepository.save(staff);
    return file;
  }

  @Override
  public void deleteCoverPicture() {
    var staff = loggedInUserService.getLoggedInStaff();
    staff.getCoverPicture().ifPresent(picture -> fileResourceService.delete(picture.getId()));
    staff.setCoverPicture(null);
    staffRepository.save(staff);
  }

  private void requireImage(String mimeType) {
    if (!EFileType.fromMimeType(mimeType).isImage()) {
      throw new FileTypeNotSupportedException();
    }
  }
}
