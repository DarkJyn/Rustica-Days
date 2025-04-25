package com.mygdx.game.items.crops;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.items.base.Item;

/**
 * Lớp đại diện cho vật phẩm thu hoạch – chỉ dùng để đem bán và có thông tin giống cây
 */
public abstract class Harvest extends Item {
    /**
     * Khởi tạo vật phẩm thu hoạch với texture path
     */
    public Harvest(String name, int price, String texturePath) {
        super(name, null, price, ItemType.HARVEST, texturePath);
    }

    /**
     * Khởi tạo vật phẩm thu hoạch với texture có sẵn
     */
    public Harvest(String name, int price, TextureRegion texture) {
        super(name, null, price, ItemType.HARVEST, texture);
    }

    protected abstract void initTexture();

    @Override
    public String toString() {
        return name + " (Giá: " + price + ")";
    }
}
