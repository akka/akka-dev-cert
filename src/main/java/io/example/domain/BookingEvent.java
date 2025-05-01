package io.example.domain;

import akka.javasdk.annotations.TypeName;
import io.example.domain.Participant.ParticipantType;

// The list of all events emitted by the BookingSlotEntity
public sealed interface BookingEvent {

  @TypeName("slot-reserved")
  record ParticipantMarkedAvailable(
      String slotId, String participantId, ParticipantType participantType)
      implements BookingEvent {}

  @TypeName("slot-unreserved")
  record ParticipantUnmarkedAvailable(
      String slotId, String participantId, ParticipantType participantType)
      implements BookingEvent {}

  @TypeName("reservation-booked")
  record ParticipantBooked(
      String slotId, String participantId, ParticipantType participantType, String bookingId)
      implements BookingEvent {}

  @TypeName("booking-participant-canceled")
  record ParticipantCanceled(
      String slotId, String participantId, ParticipantType participantType, String bookingId)
      implements BookingEvent {}
}
