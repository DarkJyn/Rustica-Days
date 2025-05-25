package com.mygdx.game.items.crops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Lớp đại diện cho sản phẩm bắp ngô
 */
public class Corn extends Harvest {
    private static final String TEXTURE_PATH = "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/Corn1.png";

    public Corn(String name, int price, int quantity) {
        super(name, price, TEXTURE_PATH);
        setQuantity(quantity);
    }

    @Override
    public String toString() {
        return name + " x" + quantity + " (Giá: " + price + ")";
    }
}
