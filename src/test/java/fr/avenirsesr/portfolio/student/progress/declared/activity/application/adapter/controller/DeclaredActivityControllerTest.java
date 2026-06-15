package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.association.application.adapter.mapper.AssociationSearchResultDTOMapper;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.CreationResponse;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.SubscribeDeclaredActivityRequest;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityAssociationsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityDetailsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DeclaredActivityControllerTest {

  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private DeclaredActivityViewDTOMapper declaredActivityViewDTOMapper;
  @Mock private DeclaredActivityDetailsDTOMapper declaredActivityDetailsDTOMapper;
  @Mock private DeclaredActivityAssociationsDTOMapper declaredActivityAssociationsDTOMapper;
  @Mock private AssociationSearchResultDTOMapper associationSearchResultDTOMapper;

  @InjectMocks private DeclaredActivityController controller;

  private Principal principal;

  @BeforeEach
  void setUp() {
    principal = () -> "0a8700ab-90b6-4a38-8338-acbdd4fbcd3d";
  }

  @Test
  void getDeclaredActivitiesView_should_return_200_with_mapped_declared_activities() {
    BddLogger.given("A logged-in student with declared activities");

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    DeclaredActivityViewDTO expectedDto = mock(DeclaredActivityViewDTO.class);

    PagedResult<DeclaredActivity> pagedResult =
        new PagedResult<>(List.of(declaredActivity), new PageInfo(0, 8, 1));

    when(declaredActivityService.getDeclaredActivities(any())).thenReturn(pagedResult);
    when(declaredActivityService.getDeclaredActivityStatus(List.of(declaredActivity)))
        .thenReturn(Map.of(declaredActivity, EDeclaredActivityStatus.SUBSCRIBED));
    when(declaredActivityViewDTOMapper.toDTO(declaredActivity, EDeclaredActivityStatus.SUBSCRIBED))
        .thenReturn(expectedDto);

    BddLogger.when("getDeclaredActivitiesView is called");
    ResponseEntity<PagedResponse<DeclaredActivityViewDTO>> response =
        controller.getDeclaredActivitiesView(principal, 0, 8);

    BddLogger.then("200 OK is returned with mapped declared activities");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().data()).containsExactly(expectedDto);
    assertThat(response.getBody().page().page()).isEqualTo(0);
    assertThat(response.getBody().page().pageSize()).isEqualTo(8);
    assertThat(response.getBody().page().totalElements()).isEqualTo(1);

    verify(declaredActivityService).getDeclaredActivities(any());
    verify(declaredActivityService).getDeclaredActivityStatus(List.of(declaredActivity));
    verify(declaredActivityViewDTOMapper)
        .toDTO(declaredActivity, EDeclaredActivityStatus.SUBSCRIBED);
  }

  @Test
  void
      subscribeActivity_should_return_201_with_creation_response_containing_declared_activity_id() {
    BddLogger.given("A valid activity ID and no period");

    UUID activityId = UUID.randomUUID();
    UUID expectedId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(declaredActivity.getId()).thenReturn(expectedId);
    when(declaredActivityService.subscribe(eq(activityId), isNull(), isNull()))
        .thenReturn(declaredActivity);

    BddLogger.when("subscribeActivity is called without period");
    ResponseEntity<CreationResponse> response =
        controller.subscribeActivity(activityId, new SubscribeDeclaredActivityRequest(null));

    BddLogger.then("201 CREATED is returned with the new declared activity id");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().createdItemId()).isEqualTo(expectedId);

    verify(declaredActivityService).subscribe(activityId, null, null);
    verifyNoInteractions(declaredActivityDetailsDTOMapper);
  }

  @Test
  void finish_should_return_204_no_content() {
    BddLogger.given("A valid declared activity ID");

    UUID declaredActivityId = UUID.randomUUID();
    doNothing().when(declaredActivityService).finish(declaredActivityId);

    BddLogger.when("finish is called");
    ResponseEntity<Void> response = controller.finish(principal, declaredActivityId);

    BddLogger.then("204 No Content is returned and the mapper is never called");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();

    verify(declaredActivityService).finish(declaredActivityId);
    verifyNoInteractions(declaredActivityDetailsDTOMapper);
  }

  @Test
  void
      getDeclaredActivityDetails_should_delegate_to_service_and_mapper_with_details_data_and_status() {
    BddLogger.given("A declared activity details data with feedbacks returned by the service");

    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    DeclaredActivityDetailsData detailsData = mock(DeclaredActivityDetailsData.class);
    DeclaredActivityDetailsDTO expectedDto = mock(DeclaredActivityDetailsDTO.class);

    when(detailsData.declaredActivity()).thenReturn(declaredActivity);
    when(declaredActivityService.getDeclaredActivityDetails(declaredActivityId))
        .thenReturn(detailsData);
    when(declaredActivityService.getDeclaredActivityStatus(declaredActivity))
        .thenReturn(EDeclaredActivityStatus.SUBMITTED);
    when(declaredActivityDetailsDTOMapper.toDTO(detailsData, EDeclaredActivityStatus.SUBMITTED))
        .thenReturn(expectedDto);

    BddLogger.when("getDeclaredActivityDetails is called");
    ResponseEntity<DeclaredActivityDetailsDTO> response =
        controller.getDeclaredActivityDetails(principal, declaredActivityId);

    BddLogger.then("200 OK is returned with the mapped DTO");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isSameAs(expectedDto);

    verify(declaredActivityService).getDeclaredActivityDetails(declaredActivityId);
    verify(declaredActivityService).getDeclaredActivityStatus(declaredActivity);
    verify(declaredActivityDetailsDTOMapper).toDTO(detailsData, EDeclaredActivityStatus.SUBMITTED);
  }
}
