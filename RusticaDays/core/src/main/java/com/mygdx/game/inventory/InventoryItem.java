package com.mygdx.game.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class InventoryItem {
    private String name;
    private TextureRegion texture;

    public InventoryItem(String name, TextureRegion texture) {
        this.name = name;
        this.texture = texture;
    }

    public String getName() {
        return name;
    }

    public TextureRegion getTexture() {
        return texture;
    }
}
