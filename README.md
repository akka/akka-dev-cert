# How to Get Certified

The Flight Training Scheduler project serves as the certification test for Akka developers. This certification process evaluates your ability to implement a real-world application using Akka SDK components given a set of requirements, scaffolding, and some starter classes.

## Getting Started

### Prerequisites

* Java 21, Eclipse Adoptium recommend
* Apache Maven version 3.9 or later
* `curl` command-line tool

Clone this template repository, which contains:

* Project structure and configuration
* Documentation and requirements
* Test suite
* All non-Akka components

### Certification Requirements

Your task is to implement the following Akka SDK components:

* Flights [endpoint](https://doc.akka.io/java/http-endpoints.html)
* BookingSlot [entity](https://doc.akka.io/java/event-sourced-entities.html)
* Timeslot [view](https://doc.akka.io/java/views.html)

### Implementation Guidelines

* Adhere to the design specifications for each component
* Ensure all components work together as described
* Pass all provided test cases
* Maintain proper event flow and state management
* Handle all required operations correctly

### Submission Process

1. Complete your implementation
2. Test thoroughly using the provided test suite
3. Upload your completed project to a public repository (e.g., GitHub)
4. Email [certification@akka.io](mailto:certification@akka.io) with:
   * Your contact information
   * Link to your public repository

### Evaluation

The certification team will review your implementation for:

* Correct functionality
* Proper use of Akka SDK components
* Code quality and organization
* Adherence to specified requirements
* Successful test completion

## Flight Training Scheduler App Design

Flight schools provide training to students looking to become pilots. While some of that training is in a classroom, most training takes place in a real plane. Scheduling this training can be a complex process and so your assignment is to create the backend for a flight training scheduler.

The core concept in this flight scheduler is that of a `Timeslot`. A timeslot is identified by a unique identifier, but the backend makes no actual calendar demands of a timeslot. This lets the application UI decide how it will deal with timeslots such as their start and end times. 

Participants will indicate their availability for a given timeslot. Once enough participants are available for a given slot, the student can then book that slot, confirming it. The following are the three types of participants that can mark availability and confirm timeslots:

* Students - One of two types of end uers of the application
* Instructor
* Aircraft

A booking requires the availability of all three participant types. An important design decision to remember is that for a given timeslot, multiple aircraft, instructors, and students can all be available. The student then must indicate which aircraft and instructor they're reserving when they make a booking.

The ID of a timeslot is an opaque string and no requirements are imposed on it. An application might choose a naming convention that indicates the date and start time of a slot, e.g. `2025-08-08-09`, which would be a slot for August 8th, 2025 at 9am local time.

All interactions with the training flight booking system are done through an HTTP endpoint with the following API:

| Method | URL | Description |
|:-:|---|---|
| `POST` | `/flight/availability/{slotId}` | Adds an availability indication for a participant in a given slot | 
| `DELETE` | `/flight/availability/{slotId}` | Removes an availability indication for a participant in a given slot |
| `GET` | `/flight/availability/{slotId}` | Retrieves the availability status of a given slot |
| `POST` | `/flight/bookings/{slotId}` | Book a slot. Requires availability of the three indicated participants | 
| `DELETE` | `/flight/bookings/{slotId}/{bookingId}` | Cancels a booking for a given slot |
| `GET` | `/flight/slots/{participantId}/{status}` | Retrieves timeslot status for the given `participantId` with a status of `status` |


## Flight Training Scheduler Core Functions

The provided template repository contains all the business logic defined in domain objects. Do not modify the provided domain objects, your objective is to implement the necessary Akka SDK components that interact with the domain objects, processing requests, commands, and events.

### Availability Management

The application allows all participants to indicate their available time slots in a calendar system. Each participant can mark when they are free for training sessions, creating a pool of available time slots for each participant type.

### Booking System

Students can browse available time slots and create bookings. The system ensures that a valid reservation can only be created when all three required participants (student, instructor, and aircraft) have marked availability for the same time slot. Bookings are always for future time slots.

## Flight Training Scheduler Business Rules

### Scheduling Logic

* All reservations require exactly three participants: one each of student, instructor, and aircraft
* Participants can have multiple reservations
* Consecutive time slots are allowed
* No approval workflow is required
* No qualification matching is needed between participants

### Booking Management

* Bookings can only be created for future time slots
* Existing bookings can be canceled but not modified
* Cancellations can occur for any reason
* There are no restrictions on how far in advance slots can be booked

The system maintains consistency through Akka's concurrency management, ensuring double bookings cannot occur, and all participants remain correctly scheduled.

## Flight Training Scheduler Components to Implement
The following is a list of the components that need to be implemented in order for this solution to be considered complete. Scaffolding and appropriate placeholders will be there so you can supply the implementation without worrying about ceremony.

### Booking Slot Entity
The `BookingSlotEntity` component serves as the authority for a single instance of a time slot. A timeslot manages the list of participants that have been marked as `available` (ready to book) as well as those that have been converted to `booked` via the HTTP endpoint.

This entity maintains these two internal lists so that it can reject bad commands as well as commands that might violate system integrity or business rules.

### Participant Slot Entity
For view purposes we want to be able to query the list of timeslots for a given participant. For example, as a student I want to see the slots that I've marked as `available` as well as those that are actively booked.

Since the `BookingSlotEntity` is keyed to a single slot, we have the `ParticipantSlotEntity` which is keyed to a specific _slot-participant_ and it maintains an attribute of `status`. This entity is automatically maintained and doesn't have any endpoint interaction.

### Participant Slots View
The `ParticipantSlotsView` is a view that allows the endpoint to query data managed by events specific to the `ParticipantSlotEntity`. Each row in this view is keyed by `slotId-participantId` and has fields for the participant type and the slot status (`booked`, `available`).

### Slot-to-Participant Consumer
This consumer is responsible for taking events emitted by the `BookingSlotEntity` and invoking corresponding commands on the `ParticipantSlotEntity`, effectively normalizing the data so it can be queried and filtered by attributes smaller than the timeslot ID.

### Flight HTTP Endpoint
The public, RESTful API that provides consumers with access to the flight service.

## Booking Flight Training Reservations

To book a training flight:

* The `student` participant must be marked `available` for a given slot
* The `aircraft` participant must be marked `available` for the same slot
* The `instructor` participant must be marked `available` for that same slot
* A booking request is then made of the timeslot, containing the student, aircraft, and instructor IDs.

### Cancel a Booking
If a timeslot has a given booking then that booking can be canceled. The call to the HTTP endpoint's "create boooking" route requires the client to pass the booking ID so it will be able to use it for future calls such as `cancel`.

## Testing with Curl
The easiest way to make sure your flight service is performing as designed is to use some canned `curl` statements that we know produce predictable results.

Start by marking availability in the slot `bestslot` for 3 participants: `alice`, `superplane`, and `superteacher` for the `student`, `aircraft`, and `instructor` respectively.

```
curl -v -H "Content-Type: application/json" -X POST -d '{"participantId": "alice", "participantType": "student"}' localhost:9000/flight/availability/bestslot

curl -v -H "Content-Type: application/json" -X POST -d '{"participantId": "superplane", "participantType": "aircraft"}' localhost:9000/flight/availability/bestslot

curl -v -H "Content-Type: application/json" -X POST -d '{"participantId": "superteacher", "participantType": "instructor"}' localhost:9000/flight/availability/bestslot
```

Query the slot's internal state:
```
curl -H "Content-Type: application/json" localhost:9000/flight/availability/bestslot
```

```json
{
  "bookings": [],
  "available": [
    {
      "id": "alice",
      "participantType": "STUDENT"
    },
    {
      "id": "superteacher",
      "participantType": "INSTRUCTOR"
    },
    {
      "id": "superplane",
      "participantType": "AIRCRAFT"
    }
  ]
}
```

Now you can query for all of Alice's availability slots:
```
curl -v localhost:9000/flight/slots/alice/available
```

And the `superplane`:
```
curl -v localhost:9000/flight/slots/superplane/available
```

Now book the slot:
```
curl -v -H "Content-Type: application/json" localhost:9000/flight/bookings/bestslot -d '{"bookingId": "booking4", "aircraftId": "superplane", "instructorId": "superteacher", "studentId": "alice"}'
```

Check alice's booked timeslots:
```
curl -v localhost:9000/flight/slots/alice/booked
```

The JSON output:
```json
{
  "slots": [
    {
      "slotId": "bestslot",
      "participantId": "alice",
      "participantType": "STUDENT",
      "bookingId": "booking4",
      "status": "booked"
    }
  ]
}
```
Note that there's enough information in the output of this timeslot query to cancel a booking. We got both the `slotId` and the `bookingId`.

Cancel the booking, which should result in all 3 participants having a canceled event:

```
curl -v -X DELETE -H "Content-Type: application/json" localhost:9000/flight/bookings/bestslot/booking4 
```

You'll see something like this in the service's log:

```

12:49:38.595 INFO  i.e.a.SlotToParticipantConsumer - Canceling booking booking4 for participant superteacher
12:49:38.609 INFO  i.e.a.SlotToParticipantConsumer - Canceling booking booking4 for participant superplane
12:49:38.614 INFO  i.e.a.SlotToParticipantConsumer - Canceling booking booking4 for participant alice
```

The timeslot entity should now be empty (no availability, no bookings):
```
curl -H "Content-Type: application/json" localhost:9000/flight/availability/bestslot
```

```json

{
  "bookings": [],
  "available": []
}
```
