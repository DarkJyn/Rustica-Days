package com.mygdx.game.items.seeds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Hạt giống tỏi – đơn giản chỉ chứa thông tin metadata
 */
public class GarlicSeed extends Seed {

    // Constructor với tham số tên và mô tả từ người gọi
    public GarlicSeed(String name, String description, int price, TextureRegion textureOverride) {
        super(
            name,                                        // Tên
            description,                                 // Mô tả
            price,                                       // Giá mua
            textureOverride != null ? textureOverride : loadDefaultTexture(), // Sử dụng texture được truyền vào hoặc mặc định
            "com.mygdx.game.entities.plants.types.Garlic" // Tên class cây trồng
        );
    }

    // Constructor mặc định
    public GarlicSeed() {
        super(
            "Garlic Seed",                                  // Tên
            "Seed to grow Garlic",                  // Mô tả
            100,                                   // Giá mua
            loadDefaultTexture(),                        // Texture
            "com.mygdx.game.entities.plants.types.Garlic" // Tên class cây trồng
        );
    }

    private static TextureRegion loadDefaultTexture() {
        try {
            return new TextureRegion(new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/GarlicSeed.png")));
        } catch (Exception e) {
            System.err.println("Could not load GarlicSeed texture: " + e.getMessage());
            return null;
        }
    }
}
