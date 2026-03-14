package controller;

import factory.HotelPrototype;
import model.entity.Hotel;
import view.IView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// SRP: Only handles hotel search and management
public class HotelController {

    private IView view;
    private List<Hotel> hotels = new ArrayList<>();
    private HotelPrototype prototype = new HotelPrototype();

    public HotelController(IView view) {
        this.view = view;
        loadHotels();
    }

    private void loadHotels() {
        // Use Prototype Pattern to load hotel templates
        hotels.add(prototype.getHotelTemplate("TAJ", 45));
        hotels.add(prototype.getHotelTemplate("LEELA", 30));
        hotels.add(prototype.getHotelTemplate("OBEROI", 12));
        hotels.add(prototype.getHotelTemplate("IBIS", 60));
    }

    public void showAllHotels() {
        view.showHotels(hotels);
    }

    public void searchByCity() {
        String city = view.getInput("Enter city to search");
        List<Hotel> results = hotels.stream()
                .filter(h -> h.getCity().equalsIgnoreCase(city) && h.hasAvailability())
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            view.showMessage("No hotels found in " + city);
        } else {
            view.showHotels(results);
        }
    }

    public Hotel getHotelById(String hotelId) {
        return hotels.stream()
                .filter(h -> h.getHotelId().equalsIgnoreCase(hotelId))
                .findFirst()
                .orElse(null);
    }

    public List<Hotel> getAllHotels() {
        return hotels;
    }

    public void decrementAvailability(String hotelId) {
        Hotel h = getHotelById(hotelId);
        if (h != null && h.hasAvailability()) {
            h.setAvailableRooms(h.getAvailableRooms() - 1);
        }
    }

    public void incrementAvailability(String hotelId) {
        Hotel h = getHotelById(hotelId);
        if (h != null) {
            h.setAvailableRooms(h.getAvailableRooms() + 1);
        }
    }
}
