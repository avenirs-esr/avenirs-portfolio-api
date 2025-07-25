package fr.avenirsesr.portfolio.shared.domain.model;

public class FileSize {
  private final long bytes;

  private FileSize(long bytes) {
    this.bytes = bytes;
  }

  private FileSize(double value, Unit unit) {
    this.bytes = (long) (value * unit.toBytes());
  }

  public static FileSize of(long bytes) {
    return new FileSize(bytes);
  }

  public static FileSize of(double value, Unit unit) {
    return new FileSize(value, unit);
  }

  public long bytes() {
    return bytes;
  }

  public double to(Unit unit) {
    return (double) bytes / unit.toBytes();
  }

  public boolean isLessThan(long size) {
    return bytes() < size;
  }

  @Override
  public String toString() {
    if (bytes >= Unit.Go.toBytes()) {
      return String.format("%.2f Go", to(Unit.Go));
    } else if (bytes >= Unit.Mo.toBytes()) {
      return String.format("%.2f Mo", to(Unit.Mo));
    } else if (bytes >= Unit.Ko.toBytes()) {
      return String.format("%.2f Ko", to(Unit.Ko));
    } else {
      return bytes + " o";
    }
  }

  public enum Unit {
    BYTE(1L),
    Ko(1024L),
    Mo(1024L * 1024L),
    Go(1024L * 1024L * 1024L);

    private final long bytes;

    Unit(long bytes) {
      this.bytes = bytes;
    }

    public long toBytes() {
      return bytes;
    }
  }
}
