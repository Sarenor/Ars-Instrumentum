package de.sarenor.arsinstrumentum.items.curios.armarium;

public enum Slots {
    SLOT_ONE, SLOT_TWO, SLOT_THREE;

    public static Slots getNextSlot(Slots slots) {
        return switch (slots) {
            case SLOT_ONE -> SLOT_TWO;
            case SLOT_TWO -> SLOT_THREE;
            case SLOT_THREE -> SLOT_ONE;
        };
    }

    public static Slots getSlotForInt(int slotIndex) {
        return switch (slotIndex) {
            case 1 -> SLOT_ONE;
            case 2 -> SLOT_TWO;
            case 3 -> SLOT_THREE;
            default -> SLOT_ONE;
        };
    }
}
