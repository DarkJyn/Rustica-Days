package com.mygdx.game.items.crops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Lớp đại diện cho trái cà chua sau khi thu hoạch
 */
public class Tomato extends Harvest {

    private static final String TEXTURE_PATH = "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/Tomato1.png";

    public Tomato(String name, int price, int quantity) {
        super(name, price, TEXTURE_PATH);
        setQuantity(quantity);
    }

    @Override
    public String toString() {
        return name + " x" + quantity + " (Giá: " + price + ")";
    }
}
