package com.mygdx.game;

import com.mygdx.game.entities.plants.states.GrowthState;

public class PlantState {
    private String type;
    private int row;
    private int col;
    private GrowthState growthState;
    private float growthTimer;
    private float waterTimer;
    private boolean needsWater;

    public PlantState() {}

    public PlantState(String type, int row, int col, GrowthState growthState, float growthTimer, float waterTimer, boolean needsWater) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.growthState = growthState;
        this.growthTimer = growthTimer;
        this.waterTimer = waterTimer;
        this.needsWater = needsWater;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public GrowthState getGrowthState() {
        return growthState;
    }

    public void setGrowthState(GrowthState growthState) {
        this.growthState = growthState;
    }

    public float getGrowthTimer() {
        return growthTimer;
    }

    public void setGrowthTimer(float growthTimer) {
        this.growthTimer = growthTimer;
    }

    public float getWaterTimer() {
        return waterTimer;
    }

    public void setWaterTimer(float waterTimer) {
        this.waterTimer = waterTimer;
    }

    public boolean isNeedsWater() {
        return needsWater;
    }

    public void setNeedsWater(boolean needsWater) {
        this.needsWater = needsWater;
    }
} 