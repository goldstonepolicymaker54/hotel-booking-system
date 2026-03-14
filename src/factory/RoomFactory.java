package factory;

import model.entity.DeluxeRoom;
import model.entity.Room;
import model.entity.StandardRoom;
import model.entity.SuiteRoom;

// Factory Method Pattern: Creates Room objects based on room type
// OCP: Add new room types without modifying this factory interface
public class RoomFactory {

    public enum RoomType {
        STANDARD, DELUXE, SUITE
    }

    public static Room createRoom(RoomType type, String roomId, String hotelId,
                                   String guestName, int roomNumber, double basePrice) {
        switch (type) {
            case STANDARD:
                return new StandardRoom(roomId, hotelId, guestName, roomNumber, basePrice);
            case DELUXE:
                return new DeluxeRoom(roomId, hotelId, guestName, roomNumber, basePrice);
            case SUITE:
                return new SuiteRoom(roomId, hotelId, guestName, roomNumber, basePrice);
            default:
                throw new IllegalArgumentException("Unknown room type: " + type);
        }
    }

    public static RoomType fromString(String input) {
        switch (input.toUpperCase()) {
            case "STANDARD": return RoomType.STANDARD;
            case "DELUXE":   return RoomType.DELUXE;
            case "SUITE":    return RoomType.SUITE;
            default: throw new IllegalArgumentException("Invalid room type: " + input);
        }
    }
}
