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
                if (slot.getQuantity() == 1){
                    slot.clear();
                }
                else {
                    slot.setItem(slot.getItem(), slot.getQuantity() - 1);
                }
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

    public void setInventoryItems(List<InventoryItemState> itemStates) {
        // Xóa sạch inventory hiện tại
        for (InventorySlot slot : slots) {
            slot.clear();
        }
        // Thêm lại các item từ danh sách tên và số lượng
        for (InventoryItemState itemState : itemStates) {
            Item item = com.mygdx.game.items.base.ItemFactory.createItemByName(itemState.getName());
            if (item != null) {
                addItem(item, itemState.getQuantity());
            }
        }
    }

    public List<InventoryItemState> getInventoryItems() {
        List<InventoryItemState> itemStates = new ArrayList<>();
        for (InventorySlot slot : slots) {
            if (!slot.isEmpty()) {
                Item item = slot.getItem();
                String itemName;
                if (item instanceof com.mygdx.game.items.animalproducts.FishItem) {
                    // Special handling for fish items
                    com.mygdx.game.items.animalproducts.FishItem fishItem = (com.mygdx.game.items.animalproducts.FishItem) item;
                    itemName = "Fish_" + fishItem.getFishType().name();
                } else {
                    itemName = item.getName();
                }
                itemStates.add(new InventoryItemState(itemName, slot.getQuantity()));
                System.out.println("Saving item: " + itemName + " with quantity: " + slot.getQuantity());
            }
        }
        return itemStates;
    }
}

