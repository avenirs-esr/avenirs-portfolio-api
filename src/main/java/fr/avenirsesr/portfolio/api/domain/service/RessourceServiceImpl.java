package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.api.domain.port.input.RessourceService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.RessourceRepository;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RessourceServiceImpl implements RessourceService {

  private final RessourceRepository ressourceRepository;

  @Override
  public byte[] getPhoto(EUserCategory userCategory, String filename) throws IOException {
    if (userCategory == EUserCategory.STUDENT) {
      return ressourceRepository.getStudentPhoto(filename);
    } else {
      return ressourceRepository.getTeacherPhoto(filename);
    }
  }

  @Override
  public byte[] getCover(EUserCategory userCategory, String filename) throws IOException {
    if (userCategory == EUserCategory.STUDENT) {
      return ressourceRepository.getStudentCover(filename);
    } else {
      return ressourceRepository.getTeacherCover(filename);
    }
  }
}
