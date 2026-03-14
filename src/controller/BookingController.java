package controller;

import factory.RoomFactory;
import model.entity.Booking;
import model.entity.Hotel;
import model.entity.Room;
import model.entity.User;
import model.service.BookingBuilder;
import model.service.PriceCalculator;
import util.SystemConfig;
import view.IView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// SRP: Only handles booking operations
public class BookingController {

    private IView view;
    private HotelController hotelController;
    private List<Booking> bookings = new ArrayList<>();
    private PriceCalculator priceCalculator = new PriceCalculator();

    public BookingController(IView view, HotelController hotelController) {
        this.view = view;
        this.hotelController = hotelController;
    }

    public void createBooking(User user) {
        SystemConfig config = SystemConfig.getInstance();

        if (config.isMaintenanceMode()) {
            view.showError("System is under maintenance. Bookings are temporarily unavailable.");
            return;
        }

        long userBookings = bookings.stream()
                .filter(b -> b.getUserId().equals(user.getUserId()))
                .count();

        if (userBookings >= config.getMaxBookingsPerUser()) {
            view.showError("Maximum booking limit reached (" + config.getMaxBookingsPerUser() + ")");
            return;
        }

        hotelController.showAllHotels();
        String hotelId = view.getInput("Enter Hotel ID to book");
        Hotel hotel = hotelController.getHotelById(hotelId);

        if (hotel == null) {
            view.showError("Hotel not found.");
            return;
        }

        if (!hotel.hasAvailability()) {
            view.showError("No rooms available in " + hotel.getName());
            return;
        }

        view.showMessage("Room Types: STANDARD | DELUXE | SUITE");
        String roomTypeStr = view.getInput("Enter room type");
        int nights = view.getIntInput("Enter number of nights");
        String guestName = view.getInput("Enter guest name");
        String email = view.getInput("Enter contact email");
        String phone = view.getInput("Enter contact phone");

        RoomFactory.RoomType roomType;
        try {
            roomType = RoomFactory.fromString(roomTypeStr);
        } catch (IllegalArgumentException e) {
            view.showError("Invalid room type.");
            return;
        }

        String roomId = "R" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        int roomNumber = (int)(Math.random() * 400) + 100;

        // Factory Pattern: Create room based on type
        Room room = RoomFactory.createRoom(
                roomType, roomId, hotelId, guestName, roomNumber, config.getBasePricePerNight()
        );

        List<Room> rooms = new ArrayList<>();
        rooms.add(room);

        String bookingId = "BK" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // Builder Pattern: Construct Booking step by step
        Booking booking = new BookingBuilder(bookingId, user.getUserId())
                .hotel(hotel)
                .rooms(rooms)
                .nights(nights)
                .checkIn("2025-06-01")
                .checkOut("2025-06-0" + (1 + nights))
                .contactEmail(email)
                .contactPhone(phone)
                .paymentStatus("CONFIRMED")
                .build();

        bookings.add(booking);
        hotelController.decrementAvailability(hotelId);

        view.showMessage("Booking confirmed!");
        view.showBooking(booking);

        double finalBill = priceCalculator.calculateFinalBill(rooms, nights, false, false);
        view.showMessage(String.format("Total bill (incl. 18%% GST): ₹%.2f", finalBill));
    }

    public void viewUserBookings(User user) {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getUserId().equals(user.getUserId())) {
                userBookings.add(b);
            }
        }
        view.showBookings(userBookings);
    }

    public void cancelBooking(User user) {
        viewUserBookings(user);
        String bookingId = view.getInput("Enter Booking ID to cancel");

        Booking toCancel = null;
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(bookingId) && b.getUserId().equals(user.getUserId())) {
                toCancel = b;
                break;
            }
        }

        if (toCancel == null) {
            view.showError("Booking not found.");
            return;
        }

        bookings.remove(toCancel);
        hotelController.incrementAvailability(toCancel.getHotel().getHotelId());
        view.showMessage("Booking " + bookingId + " cancelled successfully.");
    }
}
