package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.util.UUID;

public interface StaffService {
  Staff getStaffById(UUID id);

  UserProfileOverviewData getStaffProfile();

  void updateProfile(User user, String bio);

  Staff createStaff(UUID userId, String institutionEmail, String bio);
}
