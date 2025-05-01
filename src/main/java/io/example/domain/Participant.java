package io.example.domain;

// A tuple-style class that holds a participant ID and the corresponding
// type: student, instructor, or aircraft.
public record Participant(String id, ParticipantType participantType) {
  public enum ParticipantType {
    STUDENT,
    INSTRUCTOR,
    AIRCRAFT
  }
}
