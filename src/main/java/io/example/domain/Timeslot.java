package io.example.domain;

import io.example.domain.Participant.ParticipantType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// The Timeslot is a core domain object. It maintain two internal sets:
// the list of bookings and the list of participants available for booking.
// As bookings and availability are added and removed, the contents of those
// sets are shifted from one to the other.
public record Timeslot(Set<Booking> bookings, Set<Participant> available) {

  public Timeslot reserve(BookingEvent.ParticipantMarkedAvailable reserved) {
    available.add(new Participant(reserved.participantId(), reserved.participantType()));

    return new Timeslot(bookings, available);
  }

  public Timeslot unreserve(BookingEvent.ParticipantUnmarkedAvailable unreserved) {
    available.remove(new Participant(unreserved.participantId(), unreserved.participantType()));

    return new Timeslot(bookings, available);
  }

  public Timeslot book(BookingEvent.ParticipantBooked booked) {
    Participant p = new Participant(booked.participantId(), booked.participantType());
    available.remove(p);
    bookings.add(new Booking(p, booked.bookingId()));

    return new Timeslot(bookings, available);
  }

  // Checks to see if the given participant is among those marked as available
  public boolean isWaiting(String participantId, ParticipantType participantType) {
    return available.contains(new Participant(participantId, participantType));
  }

  public boolean isBookable(String studentId, String aircraftId, String instructorId) {
    return isWaiting(studentId, ParticipantType.STUDENT)
        && isWaiting(aircraftId, ParticipantType.AIRCRAFT)
        && isWaiting(instructorId, ParticipantType.INSTRUCTOR);
  }

  // Retrieves all booking entries for a given booking ID. Note that there will
  // be 3 participants for a single booking, so this will usually return no items
  // or 3 items.
  public List<Booking> findBooking(String bookingId) {
    return bookings.stream().filter(b -> b.bookingId().equals(bookingId)).toList();
  }

  // Removes all three participants of a booking from the booking list. It does
  // not automatically mark them as available for that slot.
  public Timeslot cancelBooking(String bookingId) {
    Set<Booking> books =
        bookings.stream().filter(b -> !b.bookingId().equals(bookingId)).collect(Collectors.toSet());
    return new Timeslot(books, available);
  }

  public record Booking(Participant participant, String bookingId) {}
}
