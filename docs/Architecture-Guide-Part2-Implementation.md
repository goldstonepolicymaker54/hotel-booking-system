# Architecture Guide — Part 2: Implementation

## Step 1 — Set Up Package Structure

Create the following folder hierarchy under `src/`:

```
src/
├── model/entity/
├── model/service/
├── view/
├── controller/
├── factory/
└── util/
```

Every `.java` file starts with its package declaration matching this structure.

---

## Step 2 — Build the Entity Layer (model/entity)

Start from the bottom up — pure data classes with no dependencies on other layers.

### User.java
```java
package model.entity;

public class User {
    private String userId, name, email, password, phone;
    // constructor + getters
}
```
**SRP applied:** User only holds guest data. Authentication logic lives in `UserController`.

### Hotel.java
```java
package model.entity;

public class Hotel implements Cloneable {
    private String hotelId, name, location, city;
    private int totalRooms, availableRooms;
    private double starRating;
    // constructor + getters + clone()
}
```
**Cloneable** implemented here to support the Prototype Pattern in `HotelPrototype`.

### Room.java (Abstract)
```java
package model.entity;

public abstract class Room {
    protected abstract double getPriceMultiplier();
    public abstract String getRoomType();
    public double calculateFinalPrice() { return basePrice * getPriceMultiplier(); }
}
```
**OCP applied:** New room types extend `Room` without touching existing code.

### Room Subclasses
Each subclass only overrides `getPriceMultiplier()` and `getRoomType()`:
- `StandardRoom` → multiplier `1.0`
- `DeluxeRoom` → multiplier `2.5`
- `SuiteRoom` → multiplier `5.0`

**LSP applied:** All three are safely substitutable wherever `Room` is used.

### Booking.java
Uses an inner `Builder` class — making it impossible to construct an invalid Booking:
```java
Booking booking = new Booking.Builder(bookingId, userId)
    .hotel(hotel).rooms(rooms).nights(3).build();
```

---

## Step 3 — Build the Service Layer (model/service)

### BookingBuilder.java
A fluent wrapper around `Booking.Builder` — exposes a clean API to `BookingController`:
```java
new BookingBuilder(bookingId, userId)
    .hotel(hotel).rooms(rooms).nights(nights)
    .contactEmail(email).paymentStatus("CONFIRMED")
    .build();
```

### PriceCalculator.java
Handles all financial logic in one place (SRP):
```java
public double calculateFinalBill(List<Room> rooms, int nights,
                                  boolean isWeekend, boolean isLoyaltyMember)
```
- Base subtotal = sum of room prices × nights
- Weekend surcharge: +10%
- Loyalty discount: -5%
- GST: +18%

---

## Step 4 — Build the View Layer (view/)

### IView.java
Define the interface first — this is the contract all views must honour:
```java
public interface IView {
    void showMessage(String message);
    void showHotels(List<Hotel> hotels);
    void showBookings(List<Booking> bookings);
    String getInput(String prompt);
    int getIntInput(String prompt);
    // ... etc
}
```

### ConsoleView.java
Implements `IView` using `System.out` and `Scanner`. Controllers never import this class directly — they only know `IView`.

---

## Step 5 — Build the Factory Layer (factory/)

### RoomFactory.java
```java
public static Room createRoom(RoomType type, String roomId, String hotelId,
                               String guestName, int roomNumber, double basePrice)
```
Uses a `switch` on `RoomType` enum to instantiate the right subclass. Returns `Room` abstraction.

### HotelPrototype.java
Pre-populates a registry of real Indian hotels:
```java
hotelRegistry.put("TAJ", new Hotel("H001", "Taj Palace", "Mansingh Road", "Delhi", 200, 200, 5.0));
```
`getHotelTemplate(key, availableRooms)` clones the prototype and sets current availability.

---

## Step 6 — Build the Util Layer (util/)

### SystemConfig.java
Thread-safe Singleton:
```java
public static synchronized SystemConfig getInstance() {
    if (instance == null) instance = new SystemConfig();
    return instance;
}
```
Stores: `basePricePerNight`, `maxBookingsPerUser`, `gstRate`, `maintenanceMode`.

---

## Step 7 — Build the Controller Layer (controller/)

Controllers are the "glue" — they depend on abstractions (`IView`) and coordinate between layers.

### UserController.java
- Maintains a `List<User>` in memory
- `register()` — validates email uniqueness, creates User
- `login()` — matches credentials, sets `loggedInUser`

### HotelController.java
- Loads hotels via `HotelPrototype` on startup
- `searchByCity()` — filters by city + availability
- `decrementAvailability()` / `incrementAvailability()` — called by BookingController on book/cancel

### BookingController.java
- `createBooking()` — uses `RoomFactory` + `BookingBuilder` + `PriceCalculator`
- `cancelBooking()` — removes booking, restores availability
- Checks `SystemConfig` for maintenance mode and booking limits

---

## Step 8 — Wire Everything in Main.java

```java
IView view = new ConsoleView();                          // DIP: program to abstraction
UserController userCtrl = new UserController(view);
HotelController hotelCtrl = new HotelController(view);
BookingController bookingCtrl = new BookingController(view, hotelCtrl);
```

The main loop handles menu input and delegates to the right controller.

---

## Step 9 — Compile and Run

```bash
javac -d bin \
  src/model/entity/*.java \
  src/model/service/*.java \
  src/util/*.java \
  src/factory/*.java \
  src/view/*.java \
  src/controller/*.java \
  src/Main.java

java -cp bin Main
```

---

## Common Mistakes to Avoid

| Mistake | Correct Approach |
|---------|-----------------|
| Putting business logic in entity classes | Keep entities as pure data; logic goes in service/controller |
| Controllers importing `ConsoleView` directly | Always inject `IView` via constructor |
| Creating rooms with `new DeluxeRoom()` in controllers | Always use `RoomFactory.createRoom()` |
| Single god-class controller | One controller per domain area (SRP) |
