package com.mygdx.game;

import java.util.List;
import com.mygdx.game.inventory.InventoryItemState;

public class GameState {
    private float playerX;
    private float playerY;
    private int playerLevel;
    private float playerExperience;
    private float playerStamina;
    private int playerMoney;
    private List<InventoryItemState> inventoryItems;
    private List<PlantState> plants;

    // Getters and setters
    public float getPlayerX() {
        return playerX;
    }

    public void setPlayerX(float playerX) {
        this.playerX = playerX;
    }

    public float getPlayerY() {
        return playerY;
    }

    public void setPlayerY(float playerY) {
        this.playerY = playerY;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }

    public float getPlayerExperience() {
        return playerExperience;
    }

    public void setPlayerExperience(float playerExperience) {
        this.playerExperience = playerExperience;
    }

    public float getPlayerStamina() {
        return playerStamina;
    }

    public void setPlayerStamina(float playerStamina) {
        this.playerStamina = playerStamina;
    }

    public int getPlayerMoney() {
        return playerMoney;
    }

    public void setPlayerMoney(int playerMoney) {
        this.playerMoney = playerMoney;
    }

    public List<InventoryItemState> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(List<InventoryItemState> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public List<PlantState> getPlants() {
        return plants;
    }

    public void setPlants(List<PlantState> plants) {
        this.plants = plants;
    }
} 