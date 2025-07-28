package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum EFileType {
  // Images
  PNG(FileSize.of(5, FileSize.Unit.Mo), "image/png"),
  JPEG(FileSize.of(5, FileSize.Unit.Mo), "image/jpeg"),
  PJPEG(FileSize.of(5, FileSize.Unit.Mo), "image/pjpeg"),
  GIF(FileSize.of(5, FileSize.Unit.Mo), "image/gif"),
  WEBP(FileSize.of(5, FileSize.Unit.Mo), "image/webp"),

  // Audio
  OGG_APP(FileSize.of(10, FileSize.Unit.Mo), "application/ogg"),
  OGG(FileSize.of(10, FileSize.Unit.Mo), "audio/ogg"),
  WEBM_AUDIO(FileSize.of(10, FileSize.Unit.Mo), "audio/webm"),
  _3GPP_AUDIO(FileSize.of(10, FileSize.Unit.Mo), "audio/3gpp"),
  MP4_AUDIO(FileSize.of(10, FileSize.Unit.Mo), "audio/mp4"),
  MP3(FileSize.of(10, FileSize.Unit.Mo), "audio/mpeg"),
  MPEG4_GENERIC(FileSize.of(5, FileSize.Unit.Mo), "audio/mpeg4-generic"),
  MPEG3(FileSize.of(10, FileSize.Unit.Mo), "audio/mpeg3"),
  X_MPEG3(FileSize.of(10, FileSize.Unit.Mo), "audio/x-mpeg-3"),
  VORBIS(FileSize.of(10, FileSize.Unit.Mo), "audio/vorbis"),

  // Videos
  AVI(FileSize.of(50, FileSize.Unit.Mo), "video/avi"),
  OGG_VIDEO(FileSize.of(50, FileSize.Unit.Mo), "video/ogg"),
  MSVIDEO(FileSize.of(50, FileSize.Unit.Mo), "video/msvideo"),
  X_MSVIDEO(FileSize.of(50, FileSize.Unit.Mo), "video/x-msvideo"),
  MP4(FileSize.of(50, FileSize.Unit.Mo), "video/mp4"),
  MPEG(FileSize.of(50, FileSize.Unit.Mo), "video/mpeg"),
  MPEG3_VIDEO(FileSize.of(50, FileSize.Unit.Mo), "video/mpeg3"),
  X_MPEG_VIDEO(FileSize.of(50, FileSize.Unit.Mo), "video/x-mpeg"),
  WEBM_VIDEO(FileSize.of(50, FileSize.Unit.Mo), "video/webm"),
  _3GPP_VIDEO(FileSize.of(50, FileSize.Unit.Mo), "video/3gpp"),
  _3GPP2_VIDEO(FileSize.of(50, FileSize.Unit.Mo), "video/3gpp2"),

  // Text
  TXT(FileSize.of(5, FileSize.Unit.Mo), "text/plain"),
  CSV(FileSize.of(10, FileSize.Unit.Mo), "text/csv"),
  ICS(FileSize.of(10, FileSize.Unit.Mo), "text/calendar"),
  RICHTEXT(FileSize.of(10, FileSize.Unit.Mo), "text/richtext"),

  // Documents
  PDF(FileSize.of(10, FileSize.Unit.Mo), "application/pdf"),
  POSTSCRIPT(FileSize.of(10, FileSize.Unit.Mo), "application/postscript"),
  DOC(FileSize.of(10, FileSize.Unit.Mo), "application/msword"),
  MSPPT(FileSize.of(10, FileSize.Unit.Mo), "application/mspowerpoint"),
  PPT(FileSize.of(10, FileSize.Unit.Mo), "application/powerpoint"),
  X_PPT(FileSize.of(10, FileSize.Unit.Mo), "application/x-mspowerpoint"),
  XLS(FileSize.of(10, FileSize.Unit.Mo), "application/excel"),
  X_XLS(FileSize.of(10, FileSize.Unit.Mo), "application/x-excel"),
  X_MSEXCEL(FileSize.of(10, FileSize.Unit.Mo), "application/x-msexcel"),
  XLS_VND(FileSize.of(10, FileSize.Unit.Mo), "application/vnd.ms-excel"),
  PPT_VND(FileSize.of(10, FileSize.Unit.Mo), "application/vnd.ms-powerpoint"),
  ODT(FileSize.of(510, FileSize.Unit.Mo), "application/vnd.oasis.opendocument.text"),
  VSD(FileSize.of(10, FileSize.Unit.Mo), "application/vnd.visio"),
  RTF(FileSize.of(10, FileSize.Unit.Mo), "application/rtf"),
  X_RTF(FileSize.of(10, FileSize.Unit.Mo), "application/x-rtf"),
  PPTX_SLIDESHOW(
      FileSize.of(10, FileSize.Unit.Mo),
      "application/vnd.openxmlformats-officedocument.presentationml.slideshow"),
  PPTX(
      FileSize.of(10, FileSize.Unit.Mo),
      "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
  ODP(FileSize.of(10, FileSize.Unit.Mo), "application/vnd.oasis.opendocument.presentation"),
  XLSX(
      FileSize.of(10, FileSize.Unit.Mo),
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
  ODS(FileSize.of(10, FileSize.Unit.Mo), "application/vnd.oasis.opendocument.spreadsheet"),
  DOCX(
      FileSize.of(10, FileSize.Unit.Mo),
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
  ;

  private final FileSize sizeLimit;
  private final String mimeType;

  EFileType(FileSize sizeLimit, String mimeType) {
    this.mimeType = mimeType;
    this.sizeLimit = sizeLimit;
  }

  public static EFileType fromMimeType(String mimeType) throws FileTypeNotSupportedException {
    return Arrays.stream(values())
        .filter(ct -> ct.mimeType.equalsIgnoreCase(mimeType))
        .findFirst()
        .orElseThrow(() -> new FileTypeNotSupportedException("Unknown mime type: " + mimeType));
  }
}
