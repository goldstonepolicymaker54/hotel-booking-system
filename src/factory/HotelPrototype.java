package factory;

import model.entity.Hotel;

import java.util.HashMap;
import java.util.Map;

// Prototype Pattern: Clones hotel objects for different seasons/availability
public class HotelPrototype {

    private Map<String, Hotel> hotelRegistry = new HashMap<>();

    public HotelPrototype() {
        // Pre-populate registry with prototype hotels
        hotelRegistry.put("TAJ", new Hotel("H001", "Taj Palace", "Mansingh Road", "Delhi", 200, 200, 5.0));
        hotelRegistry.put("LEELA", new Hotel("H002", "The Leela", "HAL Airport Road", "Bengaluru", 150, 150, 5.0));
        hotelRegistry.put("OBEROI", new Hotel("H003", "The Oberoi", "Marine Drive", "Mumbai", 120, 120, 4.5));
        hotelRegistry.put("IBIS", new Hotel("H004", "Ibis Chennai", "Mount Road", "Chennai", 100, 100, 3.5));
    }

    // Returns a cloned hotel with updated availability
    public Hotel getHotelTemplate(String hotelKey, int availableRooms) {
        Hotel prototype = hotelRegistry.get(hotelKey.toUpperCase());
        if (prototype == null) {
            throw new IllegalArgumentException("Hotel prototype not found: " + hotelKey);
        }
        Hotel clone = prototype.clone();
        clone.setAvailableRooms(availableRooms);
        return clone;
    }

    public Map<String, Hotel> getAllPrototypes() {
        return hotelRegistry;
    }
}
