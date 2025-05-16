package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class StudentEntity {

    @Column
    private String bio;

    @Column
    private String profilePicture;

    @Column
    private String coverPicture;

    public static StudentEntity fromDomain(Student student) {
        return new StudentEntity(student.getBio(), student.getProfilePicture(), student.getCoverPicture());
    }
}
