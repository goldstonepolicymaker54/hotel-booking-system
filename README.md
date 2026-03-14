# 🏨 Hotel Booking System

A full-featured hotel reservation system built in Java, demonstrating **MVC Architecture**, **SOLID Principles**, and **Creational Design Patterns**.

---

## 🏗️ Architecture Overview

### Model Layer
```
model/
  ├── entity/     # Pure data classes (7 classes)
  └── service/    # Business logic (2 classes)
```

---

## 📁 Project Structure

```
HotelBookingSystem/
├── src/
│   ├── model/
│   │   ├── entity/
│   │   │   ├── User.java               # Guest entity (SRP)
│   │   │   ├── Hotel.java              # Hotel entity with Cloneable
│   │   │   ├── Room.java               # Abstract room (OCP)
│   │   │   ├── StandardRoom.java       # Standard implementation (LSP)
│   │   │   ├── DeluxeRoom.java         # Deluxe implementation (LSP)
│   │   │   ├── SuiteRoom.java          # Suite implementation (LSP)
│   │   │   └── Booking.java            # Booking entity
│   │   └── service/
│   │       ├── BookingBuilder.java     # Builder Pattern
│   │       └── PriceCalculator.java    # GST + discount logic
│   ├── view/
│   │   ├── IView.java                  # View interface (ISP, DIP)
│   │   └── ConsoleView.java            # Console implementation
│   ├── controller/
│   │   ├── UserController.java         # User operations (SRP)
│   │   ├── HotelController.java        # Hotel operations (SRP)
│   │   └── BookingController.java      # Booking operations (SRP)
│   ├── factory/
│   │   ├── RoomFactory.java            # Factory Method Pattern
│   │   └── HotelPrototype.java         # Prototype Pattern
│   ├── util/
│   │   └── SystemConfig.java           # Singleton Pattern
│   └── Main.java
└── README.md
```

---

## 🎯 SOLID Principles

### 1. Single Responsibility Principle (SRP)
- `User` — holds guest data only
- `UserController` — handles auth only
- `HotelController` — handles hotel search only
- `BookingController` — handles reservations only
- `PriceCalculator` — handles pricing logic only

### 2. Open/Closed Principle (OCP)
- `Room` is abstract — open for extension (new room types), closed for modification
- New room types like `VillaRoom` or `PentHouseRoom` can be added without touching existing code

### 3. Liskov Substitution Principle (LSP)
- `StandardRoom`, `DeluxeRoom`, `SuiteRoom` are all substitutable wherever `Room` is expected
- `ConsoleView` can be replaced by any `IView` implementation

### 4. Interface Segregation Principle (ISP)
- `IView` contains only essential view methods — no bloat

### 5. Dependency Inversion Principle (DIP)
- Controllers depend on `IView` abstraction, not `ConsoleView` directly
- `RoomFactory` returns `Room` abstraction, not concrete types

---

## 🎨 Creational Design Patterns

### 1. Factory Method — `RoomFactory.java`
Creates the right room object based on type selection:
```java
Room room = RoomFactory.createRoom(
    RoomFactory.RoomType.DELUXE,
    roomId, hotelId, guestName, roomNumber, basePrice
);
```

### 2. Builder Pattern — `BookingBuilder.java`
Constructs a complex `Booking` step by step:
```java
Booking booking = new BookingBuilder(bookingId, userId)
    .hotel(hotel)
    .rooms(rooms)
    .nights(3)
    .checkIn("2025-06-01")
    .checkOut("2025-06-04")
    .contactEmail(email)
    .paymentStatus("CONFIRMED")
    .build();
```

### 3. Singleton Pattern — `SystemConfig.java`
Single instance of system-wide configuration:
```java
SystemConfig config = SystemConfig.getInstance();
double basePrice = config.getBasePricePerNight();
```

### 4. Prototype Pattern — `HotelPrototype.java`
Clones hotel templates with updated availability:
```java
HotelPrototype prototype = new HotelPrototype();
Hotel hotel = prototype.getHotelTemplate("TAJ", 45);
```

---

## 🚀 How to Run

### Compile
```bash
cd HotelBookingSystem

javac -d bin \
  src/model/entity/*.java \
  src/model/service/*.java \
  src/util/*.java \
  src/factory/*.java \
  src/view/*.java \
  src/controller/*.java \
  src/Main.java
```

### Run
```bash
java -cp bin Main
```

---

## 💡 Features

- **Guest Registration & Login** — with email/password validation
- **Hotel Search** — search by city, view availability
- **Room Booking** — choose Standard / Deluxe / Suite with auto pricing
- **Price Calculation** — GST (18%), weekend surcharge, loyalty discounts via `PriceCalculator`
- **Booking Management** — view and cancel reservations
- **System Config** — runtime settings via Singleton

---

## 📊 Class Relationships

```
Main
 ├── UserController
 ├── HotelController  ──uses──> HotelPrototype (Prototype)
 └── BookingController
      ├── RoomFactory    (Factory Method)
      ├── BookingBuilder (Builder)
      ├── PriceCalculator
      └── SystemConfig   (Singleton)

Room (abstract)
 ├── StandardRoom
 ├── DeluxeRoom
 └── SuiteRoom
```

---

## 🏆 SOLID Compliance Summary

| Principle | Where Applied |
|-----------|--------------|
| SRP | All controllers, PriceCalculator |
| OCP | Abstract Room class |
| LSP | Room subclasses, IView implementations |
| ISP | IView interface |
| DIP | Controllers → IView, Factory → Room |

---

## ✨ Extra Features vs Standard Implementations

| Feature | Description |
|---------|-------------|
| `PriceCalculator` | GST + weekend surcharge + loyalty discount logic |
| Indian hotel data | Real hotel names, cities, ₹ pricing |
| Thread-safe Singleton | `synchronized` getInstance |
| Room pricing multipliers | Template method pattern inside Room hierarchy |

---
 
**Language:** Java 8+  
**Architecture:** MVC + SOLID + Creational Design Patterns
## Author
Kishan Patel P 
