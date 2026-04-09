package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;

public class StaffMapper implements Mapper<StaffEntity, Staff> {
  public static final StaffMapper INSTANCE = new StaffMapper();

  @Override
  public StaffEntity fromDomain(Staff staff) {
    return StaffEntity.of(
        UserMapper.INSTANCE.fromDomain(staff.getUser()),
        staff.getInstitutionEmail(),
        staff.getBio());
  }

  @Override
  public Staff toDomain(StaffEntity staffEntity) {
    return Staff.toDomain(
        UserMapper.INSTANCE.toDomain(staffEntity.getUser()),
        staffEntity.getBio(),
        staffEntity.getInstitutionEmail(),
        staffEntity.getCreatedAt(),
        staffEntity.getUpdatedAt());
  }
}
