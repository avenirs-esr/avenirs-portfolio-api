package fr.avenirsesr.portfolio.staff.activity.domain.data;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;

public record InactiveStudentData(Student student, Instant enrolledAt, Instant lastViewedAt) {}
