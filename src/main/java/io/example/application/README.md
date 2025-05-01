# Akka Components (application)
In this folder you will need to implement 4 akka components:

* `BookingSlotEntity` - The main entity of the application. It manages a timeslot by maintaining lists of bookings and participants available for booking.
* `ParticipantSlotEntity` - A derived entity that stores the status of a participant within a given slot (e.g. `available` or `booked`).
* `ParticipantSlotsView` - A view allowing queries of all slots for a given participant and slot
* `SlotToParticipantConsumer` - A consumer that pulls events from the `BookingSlotEntity` and in turn sends commands to `ParticipantSlotEntity` to derive the participant-slot status.
