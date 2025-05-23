package fr.avenirsesr.portfolio.api.domain.exception;

public class ProgramProgressNotFoundException extends RuntimeException {
  public ProgramProgressNotFoundException(String message) {
    super("Program progress not found: " + message);
  }
}
