package fr.avenirsesr.portfolio.file.domain.port.input;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.io.IOException;
import java.util.UUID;

public interface TraceAttachmentService {
  TraceAttachment uploadTraceAttachment(
      Student student, UUID traceId, String fileName, String mimeType, long size, byte[] content)
      throws IOException;
}
