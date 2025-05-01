package io.example.application;

import akka.Done;
import akka.javasdk.annotations.ComponentId;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import io.example.domain.BookingEvent;
import io.example.domain.Participant;
import io.example.domain.Timeslot;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ComponentId("booking-slot")
public class BookingSlotEntity extends EventSourcedEntity<Timeslot, BookingEvent> {

    private final String entityId;
    private static final Logger logger = LoggerFactory.getLogger(BookingSlotEntity.class);

    public BookingSlotEntity(EventSourcedEntityContext context) {
        this.entityId = context.entityId();
    }

    public Effect<Done> markSlotAvailable(Command.MarkSlotAvailable cmd) {
        return effects().error("not yet implemented");
    }

    public Effect<Done> unmarkSlotAvailable(Command.UnmarkSlotAvailable cmd) {
        return effects().error("not yet implemented");
    }

    // NOTE: booking a slot should produce 3
    // `ParticipantBooked` events
    public Effect<Done> bookSlot(Command.BookReservation cmd) {
        return effects().error("not yet implemented");
    }

    // NOTE: canceling a booking should produce 3
    // `ParticipantCanceled` events
    public Effect<Done> cancelBooking(String bookingId) {
        return effects().error("not yet implemented");

    }

    public ReadOnlyEffect<Timeslot> getSlot() {
        return effects().error("not yet implemented");
    }

    @Override
    public Timeslot emptyState() {
        return new Timeslot(
                // NOTE: these are just estimates for capacity based on it being a sample
                HashSet.newHashSet(10), HashSet.newHashSet(10));
    }

    @Override
    public Timeslot applyEvent(BookingEvent event) {
        // Supply your own implementation to update state based
        // on the event
        return currentState();
    }

    public sealed interface Command {
        record MarkSlotAvailable(Participant participant) implements Command {
        }

        record UnmarkSlotAvailable(Participant participant) implements Command {
        }

        record BookReservation(
                String studentId, String aircraftId, String instructorId, String bookingId)
                implements Command {
        }
    }
}
