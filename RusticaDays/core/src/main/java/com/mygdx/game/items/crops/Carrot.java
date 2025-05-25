package com.mygdx.game.items.crops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Lớp đại diện cho sản phẩm cà rốt thu hoạch
 */
public class Carrot extends Harvest {
    private static final String TEXTURE_PATH = "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/Carrot1.png";

    public Carrot(String name, int price, int quantity) {
        super(name, price, TEXTURE_PATH);
        setQuantity(quantity);
    }

    @Override
    public String toString() {
        return name + " x" + quantity + " (Giá: " + price + ")";
    }
}
