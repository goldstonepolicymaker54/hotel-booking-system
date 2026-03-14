package model.service;

import model.entity.Room;
import java.util.List;

// Extra feature: Handles discount and tax calculations
// SRP: Only responsible for price computation logic
public class PriceCalculator {

    private static final double GST_RATE = 0.18; // 18% GST
    private static final double WEEKEND_SURCHARGE = 0.10; // 10% weekend surcharge
    private static final double LOYALTY_DISCOUNT = 0.05; // 5% loyalty discount

    public double calculateSubtotal(List<Room> rooms, int nights) {
        return rooms.stream()
                .mapToDouble(Room::getFinalPrice)
                .sum() * nights;
    }

    public double applyGST(double amount) {
        return amount + (amount * GST_RATE);
    }

    public double applyWeekendSurcharge(double amount, boolean isWeekend) {
        if (isWeekend) {
            return amount + (amount * WEEKEND_SURCHARGE);
        }
        return amount;
    }

    public double applyLoyaltyDiscount(double amount, boolean isLoyaltyMember) {
        if (isLoyaltyMember) {
            return amount - (amount * LOYALTY_DISCOUNT);
        }
        return amount;
    }

    public double calculateFinalBill(List<Room> rooms, int nights, boolean isWeekend, boolean isLoyaltyMember) {
        double subtotal = calculateSubtotal(rooms, nights);
        subtotal = applyWeekendSurcharge(subtotal, isWeekend);
        subtotal = applyLoyaltyDiscount(subtotal, isLoyaltyMember);
        return applyGST(subtotal);
    }
}
