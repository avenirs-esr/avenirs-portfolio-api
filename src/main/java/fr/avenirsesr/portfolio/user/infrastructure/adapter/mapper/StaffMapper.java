package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;

public class StaffMapper implements Mapper<StaffEntity, Staff> {
  public static final StaffMapper INSTANCE = new StaffMapper();

  @Override
  public StaffEntity fromDomain(Staff staff) {
    return StaffEntity.of(
        UserMapper.INSTANCE.fromDomain(staff.getUser()),
        staff.getInstitutionEmail(),
        staff.getInstitutionId(),
        staff.getGroupId(),
        staff.getBio(),
        staff.isHasUnseenNotification(),
        staff.getCoverPicture().map(FileMapper.INSTANCE::fromDomain).orElse(null),
        staff.getProfilePicture().map(FileMapper.INSTANCE::fromDomain).orElse(null),
        staff.getCreatedAt(),
        staff.getUpdatedAt());
  }

  @Override
  public Staff toDomain(StaffEntity staffEntity) {
    return Staff.toDomain(
        UserMapper.INSTANCE.toDomain(staffEntity.getUser()),
        staffEntity.getInstitutionEmail(),
        staffEntity.getInstitutionId(),
        staffEntity.getGroupId(),
        staffEntity.getBio(),
        staffEntity.isHasUnseenNotification(),
        staffEntity.getCoverPicture() == null
            ? null
            : FileMapper.INSTANCE.toDomain(staffEntity.getCoverPicture()),
        staffEntity.getProfilePicture() == null
            ? null
            : FileMapper.INSTANCE.toDomain(staffEntity.getProfilePicture()),
        staffEntity.getCreatedAt(),
        staffEntity.getUpdatedAt());
  }
}
