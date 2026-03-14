# Architecture Guide — Part 1: Requirements & Planning

## 1. Problem Statement

Build a console-based Hotel Booking System in Java that allows guests to register, search hotels, book rooms, and manage reservations — while demonstrating clean software architecture through MVC, SOLID principles, and Creational Design Patterns.

---

## 2. Requirements Gathering

### Functional Requirements

| ID | Requirement |
|----|-------------|
| FR1 | Guest can register with name, email, password, phone |
| FR2 | Guest can log in and log out |
| FR3 | Guest can search hotels by city |
| FR4 | Guest can view hotel availability |
| FR5 | Guest can book a room (Standard / Deluxe / Suite) |
| FR6 | System calculates price with GST and discounts |
| FR7 | Guest can view all their bookings |
| FR8 | Guest can cancel a booking |
| FR9 | Admin can view system configuration |

### Non-Functional Requirements

| ID | Requirement |
|----|-------------|
| NFR1 | Code must follow SOLID principles |
| NFR2 | Use at least 4 Creational Design Patterns |
| NFR3 | MVC architecture must be strictly followed |
| NFR4 | System must be extensible (new room types without breaking existing code) |
| NFR5 | View layer must be replaceable (Console → GUI → Web) |

---

## 3. Entity Identification

By analysing the requirements, we identify these core entities:

```
User        → A registered guest
Hotel       → A hotel property with availability
Room        → An abstract bookable unit (Standard / Deluxe / Suite)
Booking     → A confirmed reservation linking User + Hotel + Rooms
```

Supporting classes:
```
SystemConfig    → Application-wide settings (Singleton)
PriceCalculator → GST, surcharges, discounts
BookingBuilder  → Step-by-step booking construction
RoomFactory     → Room object creation
HotelPrototype  → Hotel template cloning
```

---

## 4. Use Case Definitions

### UC1 — Register Guest
- **Actor**: New User
- **Flow**: Enter name → email → password → phone → system creates User
- **Exception**: Duplicate email rejected

### UC2 — Login
- **Actor**: Registered User
- **Flow**: Enter email + password → system validates → session started
- **Exception**: Invalid credentials show error

### UC3 — Search Hotels
- **Actor**: Logged-in User
- **Flow**: Enter city → system filters hotels by city + availability → list shown

### UC4 — Book Room
- **Actor**: Logged-in User
- **Flow**: Select hotel → choose room type → enter nights + guest name → system creates Booking
- **Exception**: No availability, maintenance mode, max booking limit

### UC5 — View Bookings
- **Actor**: Logged-in User
- **Flow**: System fetches all bookings for user ID → displays list

### UC6 — Cancel Booking
- **Actor**: Logged-in User
- **Flow**: View bookings → enter booking ID → system removes booking + restores room availability

---

## 5. SOLID Principles Planning

Before writing a single line of code, we plan which SOLID principle applies to which class:

| Principle | Planned Application |
|-----------|---------------------|
| **SRP** | Separate controllers for User, Hotel, Booking operations |
| **OCP** | Abstract `Room` class — new types extend without modifying base |
| **LSP** | `StandardRoom`, `DeluxeRoom`, `SuiteRoom` interchangeable as `Room` |
| **ISP** | `IView` contains only what views need — no unused methods |
| **DIP** | Controllers receive `IView` via constructor — not `ConsoleView` directly |

---

## 6. Design Pattern Selection

| Pattern | Why chosen | Where applied |
|---------|------------|---------------|
| **Factory Method** | Room creation logic varies by type — centralise it | `RoomFactory.java` |
| **Builder** | `Booking` has many optional fields — avoid constructor overload | `BookingBuilder.java` |
| **Singleton** | System config should be one shared instance | `SystemConfig.java` |
| **Prototype** | Hotels have similar structures — clone and customise | `HotelPrototype.java` |

---

## 7. Package Structure Decision

```
src/
├── model/
│   ├── entity/     ← Pure data (no business logic)
│   └── service/    ← Business logic (pricing, building)
├── view/           ← UI abstraction
├── controller/     ← Orchestration layer
├── factory/        ← Object creation patterns
└── util/           ← System-wide utilities
```

**Why group entity and service under model?**
Both represent the "M" in MVC. Separating them within model makes the distinction between raw data and behaviour explicit, while keeping them logically co-located.
