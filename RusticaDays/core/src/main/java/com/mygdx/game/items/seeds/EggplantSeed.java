package com.mygdx.game.items.seeds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Hạt giống cà tím – đơn giản chỉ chứa thông tin metadata
 */
public class EggplantSeed extends Seed {

    // Constructor với tham số tên và mô tả từ người gọi
    public EggplantSeed(String name, String description, int price, TextureRegion textureOverride) {
        super(
            name,                                        // Tên
            description,                                 // Mô tả
            price,                                       // Giá mua
            textureOverride != null ? textureOverride : loadDefaultTexture(), // Sử dụng texture được truyền vào hoặc mặc định
            "com.mygdx.game.entities.plants.types.Eggplant" // Tên class cây trồng
        );
    }

    // Constructor mặc định
    public EggplantSeed() {
        super(
            "Cabbage Seed",                                // Tên
            "Seed to grow cabbage",                // Mô tả
            45,                                           // Giá mua
            loadDefaultTexture(),                         // Texture
            "com.mygdx.game.entities.plants.types.Eggplant" // Tên class cây trồng
        );
    }

    private static TextureRegion loadDefaultTexture() {
        try {
            return new TextureRegion(new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/EggplantSeed.png")));
        } catch (Exception e) {
            System.err.println("Could not load EggplantSeed texture: " + e.getMessage());
            return null;
        }
    }
}
