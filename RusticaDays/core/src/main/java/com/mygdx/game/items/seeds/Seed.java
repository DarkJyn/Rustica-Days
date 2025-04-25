package com.mygdx.game.items.seeds;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.items.base.Item;

/**
 * Hạt giống – chỉ chứa thông tin cơ bản và biết cây trồng tương ứng.
 */
public class Seed extends Item {
    private String plantClassName; // Tên class cây tương ứng

    public Seed(String name, String description, int price, String texturePath, String plantClassName) {
        super(name, description, price, ItemType.SEED, texturePath);
        this.plantClassName = plantClassName;
    }

    public Seed(String name, String description, int price, TextureRegion texture, String plantClassName) {
        super(name, description, price, ItemType.SEED, texture);
        this.plantClassName = plantClassName;
    }

    public String getPlantClassName() {
        return plantClassName;
    }

    @Override
    public String toString() {
        return name + " (Giá: " + price + ", trồng thành: " + plantClassName + ")";
    }
}
