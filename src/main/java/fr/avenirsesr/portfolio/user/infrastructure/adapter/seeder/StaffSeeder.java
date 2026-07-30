package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.StaffCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeStaff;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaffSeeder {
  private static final String PATH_FILE = "seeder/staffs.json";
  private final FileReader fileReader;
  private final StaffService staffService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<StaffEntity> seed(List<UserEntity> savedUsers) {
    ValidationUtils.requireNonEmpty(savedUsers, "users cannot be empty");
    log.info("Seeding Staffs...");

    List<StaffCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<StaffCreationData>>() {});
          case FAKER ->
              IntStream.range(0, SeederConfig.USERS_NB_OF_STAFF)
                  .mapToObj(i -> savedUsers.get(new Random().nextInt(savedUsers.size())))
                  .map(FakeStaff::create)
                  .map(FakeStaff::toEntity)
                  .map(
                      fakeStaff ->
                          new StaffCreationData(
                              fakeStaff.getUser().getId(),
                              fakeStaff.getInstitutionEmail(),
                              fakeStaff.getBio(),
                              fakeStaff.getInstitutionId(),
                              fakeStaff.getGroupId()))
                  .toList();
        };

    List<Staff> staffs = new ArrayList<>();
    creationData.forEach(
        data -> {
          var staff =
              staffService.createStaff(
                  data.userId(),
                  data.institutionEmail(),
                  data.institutionId(),
                  data.groupId(),
                  data.bio());
          staffs.add(staff);
        });

    log.info("✔ {} staffs synced", staffs.size());

    return staffs.stream().map(StaffMapper.INSTANCE::fromDomain).toList();
  }
}
