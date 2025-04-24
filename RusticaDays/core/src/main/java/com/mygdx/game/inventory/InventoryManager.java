package com.mygdx.game.inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private final List<InventorySlot> slots;

    public InventoryManager(int size) {
        slots = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            slots.add(new InventorySlot());
        }
    }

    public List<InventorySlot> getSlots() {
        return slots;
    }

    public void addItem(InventoryItem item, int quantity) {
        for (InventorySlot slot : slots) {
            if (slot.isEmpty()) {
                slot.setItem(item, quantity);
                return;
            }
        }
        // Optionally handle full inventory
    }

    public void removeItem(InventoryItem item) {
        for (InventorySlot slot : slots) {
            if (!slot.isEmpty() && slot.getItem().equals(item)) {
                slot.clear();
                return;
            }
        }
    }
}
