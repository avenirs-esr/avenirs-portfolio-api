package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import java.io.IOException;

public interface RessourceService {

  byte[] getPhoto(EUserCategory userCategory, String filename) throws IOException;

  byte[] getCover(EUserCategory userCategory, String filename) throws IOException;
}
