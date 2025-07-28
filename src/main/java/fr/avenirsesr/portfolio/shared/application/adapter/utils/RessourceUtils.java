package fr.avenirsesr.portfolio.shared.application.adapter.utils;

import fr.avenirsesr.portfolio.file.domain.exception.BadImageTypeException;
import org.springframework.http.MediaType;

public class RessourceUtils {

  public static String getFileExtension(String filename) {
    if (filename == null) {
      return null;
    }
    int dotIndex = filename.lastIndexOf(".");
    if (dotIndex >= 0) {
      return filename.substring(dotIndex + 1);
    }
    return "";
  }

  public static MediaType getImageExtensionMediaType(String filename) {
    String extention = getFileExtension(filename);

    if (extention.equalsIgnoreCase("jpg") || extention.equalsIgnoreCase("jpeg")) {
      return MediaType.IMAGE_JPEG;
    } else if (extention.equalsIgnoreCase("png")) {
      return MediaType.IMAGE_PNG;
    }

    throw new BadImageTypeException();
  }
}
