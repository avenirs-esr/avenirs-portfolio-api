package fr.avenirsesr.portfolio.file.domain.port.input;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachmentDownload;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TraceAttachmentService {
  TraceAttachment uploadTraceAttachment(
      UUID traceId, String fileName, String mimeType, long size, byte[] content) throws IOException;

  TraceAttachmentDownload downloadTraceAttachment(UUID attachmentId) throws IOException;

  List<TraceAttachment> findByTrace(Trace trace);
}
