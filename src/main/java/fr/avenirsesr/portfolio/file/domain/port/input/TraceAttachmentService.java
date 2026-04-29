package fr.avenirsesr.portfolio.file.domain.port.input;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachmentDownload;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.util.List;
import java.util.UUID;

public interface TraceAttachmentService {
  TraceAttachment uploadTraceAttachment(
      UUID traceId, String fileName, String mimeType, long size, byte[] content);

  TraceAttachmentDownload downloadTraceAttachment(UUID attachmentId);

  List<TraceAttachment> findByTrace(Trace trace);
}
