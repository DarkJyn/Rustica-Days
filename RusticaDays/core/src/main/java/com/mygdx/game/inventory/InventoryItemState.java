package com.mygdx.game.inventory;

public class InventoryItemState {
    private String name;
    private int quantity;

    public InventoryItemState(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
} 