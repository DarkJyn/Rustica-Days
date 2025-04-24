package com.mygdx.game.inventory;

public class Item {
    private String name;
    private String description;
    private int id;
    private int value;

    // Constructor
    public Item(String name, String description, int id, int value) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.value = value;
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getId() { return id; }
    public int getValue() { return value; }
}
