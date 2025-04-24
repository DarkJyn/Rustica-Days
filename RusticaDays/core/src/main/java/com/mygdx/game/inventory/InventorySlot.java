package com.mygdx.game.inventory;

public class InventorySlot {
    private InventoryItem item;
    private int quantity;

    public void setItem(InventoryItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public InventoryItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void clear() {
        this.item = null;
        this.quantity = 0;
    }

    public boolean isEmpty() {
        return item == null;
    }
}
