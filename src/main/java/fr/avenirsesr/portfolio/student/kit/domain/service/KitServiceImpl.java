package fr.avenirsesr.portfolio.student.kit.domain.service;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.student.kit.domain.exception.KitDownloadMediaFailed;
import fr.avenirsesr.portfolio.student.kit.domain.model.KitMediaEntry;
import fr.avenirsesr.portfolio.student.kit.domain.port.input.KitService;
import fr.avenirsesr.portfolio.student.kit.domain.port.output.KitDownloadPort;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class KitServiceImpl implements KitService {
  private final FileStorageService fileStorageService;
  private final List<KitDownloadPort> kitDownloadPorts;

  @Override
  public void downloadMedia(OutputStream out) throws BusinessException {
    List<KitMediaEntry> valorizedMedia =
        kitDownloadPorts.stream().flatMap(port -> port.getMedia().stream()).toList();

    Map<String, Set<String>> usedFolderNames = new HashMap<>();
    Map<String, Set<String>> usedFileNames = new HashMap<>();

    // TODO: Create a utility class to simplify ZIP creation or file streaming
    try (ZipOutputStream zos = new ZipOutputStream(out)) {
      for (KitMediaEntry entry : valorizedMedia) {
        String folder = resolveFolder(entry.folderPath(), usedFolderNames);
        Set<String> filesHere = usedFileNames.computeIfAbsent(folder, k -> new HashSet<>());
        String sanitizedFileName = sanitize(entry.file().getFileName());
        String uniqueFileName = uniqueFileName(sanitizedFileName, filesHere);
        String entryPath = folder.isEmpty() ? uniqueFileName : folder + "/" + uniqueFileName;

        zos.putNextEntry(new ZipEntry(entryPath));
        zos.write(fileStorageService.get(entry.file().getUri()));
        zos.closeEntry();
      }
    } catch (IOException e) {
      log.error("Failed to write kit zip", e);
      throw new KitDownloadMediaFailed();
    }
  }

  private String resolveFolder(List<String> path, Map<String, Set<String>> usedNames) {
    if (path == null || path.isEmpty()) {
      return "";
    }

    List<String> parents = path.subList(0, path.size() - 1);
    String parentPrefix = parents.stream().map(this::sanitize).collect(Collectors.joining("/"));

    String leaf = sanitize(path.get(path.size() - 1));
    Set<String> siblings = usedNames.computeIfAbsent(parentPrefix, k -> new HashSet<>());
    String uniqueLeaf = uniqueFolderName(leaf, siblings);

    return parentPrefix.isEmpty() ? uniqueLeaf : parentPrefix + "/" + uniqueLeaf;
  }

  private String uniqueFolderName(String name, Set<String> usedNames) {
    if (usedNames.add(name)) {
      return name;
    }

    String candidate;
    int i = 1;
    do {
      candidate = "%s (%d)".formatted(name, i++);
    } while (!usedNames.add(candidate));

    return candidate;
  }

  private String uniqueFileName(String name, Set<String> usedNames) {
    if (usedNames.add(name)) {
      return name;
    }

    String base = name;
    String ext = "";
    int dot = name.lastIndexOf('.');

    if (dot > 0) {
      base = name.substring(0, dot);
      ext = name.substring(dot);
    }

    String candidate;
    int i = 1;
    do {
      candidate = "%s (%d)%s".formatted(base, i++, ext);
    } while (!usedNames.add(candidate));

    return candidate;
  }

  private String sanitize(String segment) {
    return segment.replaceAll("[/\\\\]", "-").trim();
  }
}
