package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.file.domain.mapper.FileDataMapper;
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
        staff.isHasUnseenNotification(),
        FileDataMapper.mapFileData(
            staff.getCoverPicture().orElse(null), FileStorageConstants.DEFAULT_COVER_FILE_URL),
        FileDataMapper.mapFileData(
            staff.getProfilePicture().orElse(null), FileStorageConstants.DEFAULT_PROFILE_FILE_URL));
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
}
