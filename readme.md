# Design8or (Designator)
Assigns a lead from a list of candidates to handle a task (anything that can be done in rotation) using a Round Robin algorithm.
Candidates participate in a designation pool and everyone has to be designated before the pool is considered closed.

Please find the use cases below

- The system automatically designates candidates.
  - A candidate is designated randomly from the list of candidates that have not yet participated in the current pool
  - The designated candidate will receive an email with details about the designation
- Candidates can accept or decline the designation
  - When a candidate accepts the designation, a new assignment will be created
  - When a candidate declines the designation, a request will be broadcast to all candidates that have not yet participated in the current pool
  - The first candidate to accept a broadcast designation request will get the assignment
- Candidates can be created, updated & deleted
- Some parameters are stored in the database and can be updated from the application.
- Browser notifications can be sent to candidates who have subscribed

## Tech Stack

- Spring & Spring Boot
- H2 Database & Spring Data for the persistence layer
- Quartz to schedule designation jobs
- Local email server to test designation emails
- Docker & Docker Compose to package & run the app
- React & Fluent UI for the presentation layer

## Build & Run