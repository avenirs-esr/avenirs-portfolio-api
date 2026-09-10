package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityFeedbacksPreviewDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityFeedbacksPreviewMapper {
  ActivityFeedbacksPreviewDTO toDTO(Activity activity);
}
