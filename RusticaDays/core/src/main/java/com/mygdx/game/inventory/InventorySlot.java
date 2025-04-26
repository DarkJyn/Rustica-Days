package com.mygdx.game.inventory;

import com.mygdx.game.items.base.Item;

public class InventorySlot {
    private Item item;
    private int quantity;

    public InventorySlot() {
        this.item = null;
        this.quantity = 0;
    }

    public boolean isEmpty() {
        return item == null || quantity <= 0;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public void clear() {
        this.item = null;
        this.quantity = 0;
    }
}
