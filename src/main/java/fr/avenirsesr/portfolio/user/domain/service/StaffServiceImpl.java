package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
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

  @Override
  public String getBio(User user) {
    var staff = staffRepository.findById(user.getId()).orElseThrow(UserIsNotStaffException::new);
    return staff.getBio();
  }

  @Override
  public void updateProfile(User user, String bio) {
    var staff = staffRepository.findById(user.getId()).orElseThrow(UserIsNotStaffException::new);

    if (bio != null) staff.setBio(bio);

    staffRepository.save(staff);
  }

  @Override
  public Staff createStaff(UUID userId, String bio) {
    var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var staff = Staff.create(user, bio);
    staffRepository.save(staff);
    return staff;
  }
}
