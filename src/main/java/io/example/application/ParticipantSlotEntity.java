package io.example.application;

import akka.Done;
import akka.javasdk.annotations.ComponentId;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import io.example.domain.Participant.ParticipantType;

@ComponentId("participant-slot")
public class ParticipantSlotEntity
        extends EventSourcedEntity<ParticipantSlotEntity.State, ParticipantSlotEntity.Event> {

    public Effect<Done> unmarkAvailable(ParticipantSlotEntity.Commands.UnmarkAvailable unmark) {
        // Supply your own implementation
        return effects().reply(Done.done());
    }

    public Effect<Done> markAvailable(ParticipantSlotEntity.Commands.MarkAvailable mark) {
        // Supply your own implementation
        return effects().reply(Done.done());
    }

    public Effect<Done> book(ParticipantSlotEntity.Commands.Book book) {
        // Supply your own implementation
        return effects().reply(Done.done());
    }

    public Effect<Done> cancel(ParticipantSlotEntity.Commands.Cancel cancel) {
        // Supply your own implementation
        return effects().reply(Done.done());
    }

    record State(
            String slotId, String participantId, ParticipantType participantType, String status) {
    }

    public sealed interface Commands {
        record MarkAvailable(String slotId, String participantId, ParticipantType participantType)
                implements Commands {
        }

        record UnmarkAvailable(String slotId, String participantId, ParticipantType participantType)
                implements Commands {
        }

        record Book(
                String slotId, String participantId, ParticipantType participantType, String bookingId)
                implements Commands {
        }

        record Cancel(
                String slotId, String participantId, ParticipantType participantType, String bookingId)
                implements Commands {
        }
    }

    public sealed interface Event {
        @TypeName("marked-available")
        record MarkedAvailable(String slotId, String participantId, ParticipantType participantType)
                implements Event {
        }

        @TypeName("unmarked-available")
        record UnmarkedAvailable(String slotId, String participantId, ParticipantType participantType)
                implements Event {
        }

        @TypeName("participant-booked")
        record Booked(
                String slotId, String participantId, ParticipantType participantType, String bookingId)
                implements Event {
        }

        @TypeName("participant-canceled")
        record Canceled(
                String slotId, String participantId, ParticipantType participantType, String bookingId)
                implements Event {
        }
    }

    @Override
    public ParticipantSlotEntity.State applyEvent(ParticipantSlotEntity.Event event) {
        // Supply your own implementation
        return null;
    }
}
