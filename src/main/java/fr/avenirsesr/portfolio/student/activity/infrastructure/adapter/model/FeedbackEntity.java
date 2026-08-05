package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "feedback",
    indexes = {
      @Index(name = "idx_feedback_status", columnList = "status"),
      @Index(name = "idx_feedback_declared_activity", columnList = "declared_activity")
    })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeedbackEntity extends AvenirsBaseEntity {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "declared_activity")
  private DeclaredActivityEntity declaredActivity;

  @Size(max = RICH_TEXT_LENGTH, message = "reflection can not exceed {max} characters")
  @Column(columnDefinition = "TEXT")
  private String reflection;

  @Size(max = RICH_TEXT_LENGTH, message = "feedback can not exceed {max} characters")
  @Column(columnDefinition = "TEXT")
  private String feedback;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EFeedbackStatus status;

  @Column(nullable = false)
  private int iteration;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false)
  private AssociationsJson associations;
}
