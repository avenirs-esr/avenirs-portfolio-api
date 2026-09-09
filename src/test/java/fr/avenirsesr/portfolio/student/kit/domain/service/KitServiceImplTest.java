package fr.avenirsesr.portfolio.student.kit.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.student.kit.domain.exception.KitDownloadMediaFailed;
import fr.avenirsesr.portfolio.student.kit.domain.model.KitMediaEntry;
import fr.avenirsesr.portfolio.student.kit.domain.port.output.KitDownloadPort;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class KitServiceImplTest {
  @Mock private FileStorageService fileStorageService;
  @Mock private KitDownloadPort firstPort;
  @Mock private KitDownloadPort secondPort;

  private KitServiceImpl kitService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class GivenAKitServiceImpl {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a KitServiceImpl backed by one or more KitDownloadPorts");
    }

    @Nested
    class WhenDownloadingMedia {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("downloading kit media");
      }

      @Test
      void thenItShouldAggregateMediaFromAllPorts() throws Exception {
        BddLogger.then("the zip should contain one entry per port");

        File fileA = aFile("a.txt", "uri-a");
        File fileB = aFile("b.txt", "uri-b");
        when(firstPort.getMedia())
            .thenReturn(List.of(new KitMediaEntry(fileA, List.of("traces", "Trace A"))));
        when(secondPort.getMedia())
            .thenReturn(List.of(new KitMediaEntry(fileB, List.of("traces", "Trace B"))));
        when(fileStorageService.get("uri-a")).thenReturn("contenu A".getBytes());
        when(fileStorageService.get("uri-b")).thenReturn("contenu B".getBytes());

        kitService = new KitServiceImpl(fileStorageService, List.of(firstPort, secondPort));

        Map<String, byte[]> entries = downloadAndReadZip();

        assertTrue(entries.containsKey("traces/Trace A/a.txt"));
        assertTrue(entries.containsKey("traces/Trace B/b.txt"));
        assertArrayEquals("contenu A".getBytes(), entries.get("traces/Trace A/a.txt"));
        assertArrayEquals("contenu B".getBytes(), entries.get("traces/Trace B/b.txt"));
      }

      @Test
      void thenItShouldReplaceSlashesInFolderSegments() throws Exception {
        BddLogger.then("slashes in folder segments should be sanitized");

        File file = aFile("rapport.pdf", "uri-c");
        when(firstPort.getMedia())
            .thenReturn(List.of(new KitMediaEntry(file, List.of("traces", "Bilan pro/perso"))));
        when(fileStorageService.get("uri-c")).thenReturn("contenu".getBytes());

        kitService = new KitServiceImpl(fileStorageService, List.of(firstPort));

        Map<String, byte[]> entries = downloadAndReadZip();

        assertTrue(entries.containsKey("traces/Bilan pro-perso/rapport.pdf"));
      }

      @Test
      void thenItShouldNumberTheFolderWhenTwoEntriesShareTheSameFolderPath() throws Exception {
        BddLogger.then("the second entry sharing a folder path should get a numbered folder");

        File file1 = aFile("rapport.pdf", "uri-1");
        File file2 = aFile("annexe.pdf", "uri-2");
        when(firstPort.getMedia())
            .thenReturn(
                List.of(
                    new KitMediaEntry(file1, List.of("traces", "Voyage à Berlin")),
                    new KitMediaEntry(file2, List.of("traces", "Voyage à Berlin"))));
        when(fileStorageService.get("uri-1")).thenReturn("contenu 1".getBytes());
        when(fileStorageService.get("uri-2")).thenReturn("contenu 2".getBytes());

        kitService = new KitServiceImpl(fileStorageService, List.of(firstPort));

        Map<String, byte[]> entries = downloadAndReadZip();

        assertTrue(entries.containsKey("traces/Voyage à Berlin/rapport.pdf"));
        assertTrue(entries.containsKey("traces/Voyage à Berlin (1)/annexe.pdf"));
      }

      @Test
      void thenItShouldNotNumberTheSharedParentSegment() throws Exception {
        BddLogger.then("the shared parent segment 'traces' should never be numbered itself");

        File file1 = aFile("a.txt", "uri-a");
        File file2 = aFile("b.txt", "uri-b");
        when(firstPort.getMedia())
            .thenReturn(
                List.of(
                    new KitMediaEntry(file1, List.of("traces", "Alpha")),
                    new KitMediaEntry(file2, List.of("traces", "Beta"))));
        when(fileStorageService.get("uri-a")).thenReturn("A".getBytes());
        when(fileStorageService.get("uri-b")).thenReturn("B".getBytes());

        kitService = new KitServiceImpl(fileStorageService, List.of(firstPort));

        Map<String, byte[]> entries = downloadAndReadZip();

        assertTrue(entries.keySet().stream().allMatch(name -> name.startsWith("traces/")));
        assertTrue(entries.containsKey("traces/Alpha/a.txt"));
        assertTrue(entries.containsKey("traces/Beta/b.txt"));
      }

      @Test
      void thenItShouldWriteAnEmptyValidZipWhenNoPortsReturnMedia() throws Exception {
        BddLogger.then("it should produce a valid, empty zip");

        when(firstPort.getMedia()).thenReturn(List.of());

        kitService = new KitServiceImpl(fileStorageService, List.of(firstPort));

        Map<String, byte[]> entries = downloadAndReadZip();

        assertTrue(entries.isEmpty());
        verifyNoInteractions(fileStorageService);
      }

      @Test
      void thenItShouldWrapIOExceptionIntoKitDownloadMediaFailed() {
        BddLogger.then("an IOException while writing should be wrapped as KitDownloadMediaFailed");

        File file = aFile("a.txt", "uri-a");
        when(firstPort.getMedia())
            .thenReturn(List.of(new KitMediaEntry(file, List.of("traces", "Trace A"))));
        when(fileStorageService.get("uri-a")).thenReturn("contenu".getBytes());

        kitService = new KitServiceImpl(fileStorageService, List.of(firstPort));

        OutputStream brokenStream =
            new OutputStream() {
              @Override
              public void write(int b) throws IOException {
                throw new IOException("disk full");
              }
            };

        assertThrows(KitDownloadMediaFailed.class, () -> kitService.downloadMedia(brokenStream));
      }
    }
  }

  private Map<String, byte[]> downloadAndReadZip() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    kitService.downloadMedia(out);

    Map<String, byte[]> entries = new LinkedHashMap<>();
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(out.toByteArray()))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        entries.put(entry.getName(), zis.readAllBytes());
        zis.closeEntry();
      }
    }
    return entries;
  }

  private File aFile(String fileName, String uri) {
    File file = mock(File.class);
    when(file.getFileName()).thenReturn(fileName);
    when(file.getUri()).thenReturn(uri);
    return file;
  }
}
