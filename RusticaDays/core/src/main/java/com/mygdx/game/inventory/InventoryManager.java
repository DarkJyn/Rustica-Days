package com.mygdx.game.inventory;

import com.mygdx.game.items.base.Item;
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

    public void addItem(Item item, int quantity) {
        // Trước tiên, thử thêm vào slot đã có item cùng loại để stack
        for (InventorySlot slot : slots) {
            if (!slot.isEmpty() && slot.getItem().canStackWith(item)) {
                slot.setItem(slot.getItem(), slot.getQuantity() + quantity);
                return;
            }
        }

        // Nếu không thể stack, tìm slot trống
        for (InventorySlot slot : slots) {
            if (slot.isEmpty()) {
                slot.setItem(item, quantity);
                return;
            }
        }
        // Optionally handle full inventory
    }

    public void removeItem(Item item) {
        for (InventorySlot slot : slots) {
            if (!slot.isEmpty() && slot.getItem().equals(item)) {
                slot.clear();
                return;
            }
        }
    }

    public void removeItemQuantity(Item item, int quantity) {
        for (InventorySlot slot : slots) {
            if (!slot.isEmpty() && slot.getItem().equals(item)) {
                if (slot.getQuantity() <= quantity) {
                    slot.clear();
                } else {
                    slot.setItem(slot.getItem(), slot.getQuantity() - quantity);
                }
                return;
            }
        }
    }
}

