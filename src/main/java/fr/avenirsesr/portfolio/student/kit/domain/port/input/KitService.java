package fr.avenirsesr.portfolio.student.kit.domain.port.input;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import java.io.OutputStream;

public interface KitService {
  void downloadMedia(OutputStream out) throws BusinessException;
}
