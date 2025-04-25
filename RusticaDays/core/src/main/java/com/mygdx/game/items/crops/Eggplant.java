package com.mygdx.game.items.crops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Lớp đại diện cho sản phẩm cà tím thu hoạch
 */
public class Eggplant extends Harvest {
    private static final String TEXTURE_PATH = "crops/Eggplant1.png";

    public Eggplant(String name, int price, int quantity) {
        super(name, price, TEXTURE_PATH);
        setQuantity(quantity);
        initTexture();
    }

    @Override
    protected void initTexture() {
        texture = new TextureRegion(new Texture(TEXTURE_PATH));
    }

    @Override
    public String toString() {
        return name + " x" + quantity + " (Giá: " + price + ")";
    }
}
