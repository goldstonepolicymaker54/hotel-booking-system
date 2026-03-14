package model.service;

import model.entity.Booking;
import model.entity.Hotel;
import model.entity.Room;

import java.util.List;

// Builder Pattern: Constructs complex Booking objects step by step
public class BookingBuilder {
    private Booking.Builder builder;

    public BookingBuilder(String bookingId, String userId) {
        this.builder = new Booking.Builder(bookingId, userId);
    }

    public BookingBuilder hotel(Hotel hotel) {
        builder.hotel(hotel);
        return this;
    }

    public BookingBuilder rooms(List<Room> rooms) {
        builder.rooms(rooms);
        return this;
    }

    public BookingBuilder checkIn(String checkInDate) {
        builder.checkIn(checkInDate);
        return this;
    }

    public BookingBuilder checkOut(String checkOutDate) {
        builder.checkOut(checkOutDate);
        return this;
    }

    public BookingBuilder nights(int nights) {
        builder.nights(nights);
        return this;
    }

    public BookingBuilder contactEmail(String email) {
        builder.contactEmail(email);
        return this;
    }

    public BookingBuilder contactPhone(String phone) {
        builder.contactPhone(phone);
        return this;
    }

    public BookingBuilder paymentStatus(String status) {
        builder.paymentStatus(status);
        return this;
    }

    public Booking build() {
        return builder.build();
    }
}
