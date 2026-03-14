# Architecture Guide — Part 3: Design Patterns

## Overview

This project implements four Creational Design Patterns. Each pattern solves a specific object-creation problem — understanding *why* each is used is more important than just knowing it exists.

---

## 1. Factory Method Pattern

### The Problem
`BookingController` needs to create rooms, but the type of room isn't known until runtime (user chooses Standard / Deluxe / Suite). Without a factory:
```java
// BAD — controller decides construction details
if (type.equals("DELUXE")) {
    room = new DeluxeRoom(roomId, hotelId, guestName, roomNumber, basePrice);
} else if ...
```
This violates OCP — adding a new room type means editing the controller.

### The Solution
```java
// GOOD — factory handles it
Room room = RoomFactory.createRoom(RoomFactory.RoomType.DELUXE, roomId, hotelId, guestName, roomNumber, basePrice);
```

### Structure
```
RoomFactory
  └── createRoom(RoomType, ...) → Room
        ├── STANDARD → new StandardRoom(...)
        ├── DELUXE   → new DeluxeRoom(...)
        └── SUITE    → new SuiteRoom(...)
```

### When to use Factory Method
- When the exact class to instantiate is decided at runtime
- When you want to centralise and encapsulate object creation
- When adding new types shouldn't require changing calling code

### Extension Example
To add a `PentHouseRoom` later:
1. Create `PentHouseRoom extends Room`
2. Add `PENTHOUSE` to `RoomFactory.RoomType` enum
3. Add one `case` in `RoomFactory.createRoom()`
4. **Zero changes to controllers**

---

## 2. Builder Pattern

### The Problem
`Booking` has many fields: bookingId, userId, hotel, rooms, checkIn, checkOut, nights, contactEmail, contactPhone, paymentStatus. A constructor for all of these is unreadable:
```java
// BAD
new Booking("BK001", "U001", hotel, rooms, "2025-06-01", "2025-06-04", 3, "a@b.com", "9876543210", "CONFIRMED");
```

### The Solution
```java
// GOOD — readable, step by step
Booking booking = new BookingBuilder(bookingId, userId)
    .hotel(hotel)
    .rooms(rooms)
    .nights(3)
    .checkIn("2025-06-01")
    .checkOut("2025-06-04")
    .contactEmail("a@b.com")
    .paymentStatus("CONFIRMED")
    .build();
```

### Structure
```
BookingBuilder
  ├── hotel(Hotel)        → returns this
  ├── rooms(List<Room>)   → returns this
  ├── nights(int)         → returns this
  ├── checkIn(String)     → returns this
  ├── checkOut(String)    → returns this
  ├── contactEmail(String)→ returns this
  ├── paymentStatus(String)→ returns this
  └── build()             → returns Booking
```

### When to use Builder
- When an object has 4+ optional/configurable fields
- When you want to avoid telescoping constructors
- When object construction should be readable and self-documenting

---

## 3. Singleton Pattern

### The Problem
`SystemConfig` stores settings used across the entire application (base price, GST rate, maintenance mode). If multiple instances exist, settings could be inconsistent.

### The Solution
```java
public class SystemConfig {
    private static SystemConfig instance;

    private SystemConfig() { /* private constructor */ }

    public static synchronized SystemConfig getInstance() {
        if (instance == null) {
            instance = new SystemConfig();
        }
        return instance;
    }
}
```

**`synchronized`** ensures thread safety — two threads can't simultaneously create two instances.

### Usage anywhere in the app
```java
SystemConfig config = SystemConfig.getInstance();
double price = config.getBasePricePerNight();  // always the same instance
```

### When to use Singleton
- Shared configuration / settings
- Logger instances
- Database connection pools
- Cache managers

### Trade-offs
| Pro | Con |
|-----|-----|
| Guarantees single instance | Makes unit testing harder |
| Global access point | Can become a "god object" if overused |
| Lazy initialisation possible | Hidden dependencies |

---

## 4. Prototype Pattern

### The Problem
Several hotels share the same structure (same fields, same defaults) but differ in current availability and possibly minor details. Creating each from scratch with full constructors is repetitive.

### The Solution
Pre-register hotel *templates* and clone them when needed:
```java
// Register once
hotelRegistry.put("TAJ", new Hotel("H001", "Taj Palace", "Mansingh Road", "Delhi", 200, 200, 5.0));

// Clone and customise
Hotel todaysTaj = prototype.getHotelTemplate("TAJ", 45); // 45 rooms available today
```

`Hotel` implements `Cloneable`:
```java
@Override
public Hotel clone() {
    try {
        return (Hotel) super.clone();
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException("Clone failed", e);
    }
}
```

### Structure
```
HotelPrototype
  ├── registry: Map<String, Hotel>   (pre-loaded templates)
  └── getHotelTemplate(key, rooms)
        ├── find prototype in registry
        ├── clone() it
        └── set availability → return clone
```

### When to use Prototype
- When object creation is expensive and objects share structure
- When you need many similar objects with small variations
- When avoiding subclass explosion for configuration differences

---

## Pattern Interaction Map

```
BookingController
      │
      ├── RoomFactory.createRoom()        ← Factory Method
      │         └── returns Room subclass
      │
      ├── new BookingBuilder().build()    ← Builder
      │         └── returns Booking
      │
      ├── SystemConfig.getInstance()      ← Singleton
      │         └── returns config
      │
HotelController
      │
      └── HotelPrototype.getHotelTemplate() ← Prototype
                └── returns cloned Hotel
```

---

## Pattern Selection Chessboard

| Scenario | Use this pattern |
|----------|-----------------|
| Type decided at runtime | Factory Method |
| Object has many optional fields | Builder |
| Need one shared global instance | Singleton |
| Many similar objects with small differences | Prototype |
| Complex object families | Abstract Factory |
| Separating construction from representation | Builder |
