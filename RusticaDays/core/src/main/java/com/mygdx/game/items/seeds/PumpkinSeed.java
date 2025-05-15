package com.mygdx.game.items.seeds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Hạt giống lúa – đơn giản chỉ chứa thông tin metadata
 */
public class PumpkinSeed extends Seed {

    // Constructor với tham số tên và mô tả từ người gọi
    public PumpkinSeed(String name, String description, int price, TextureRegion textureOverride) {
        super(
            name,                                        // Tên
            description,                                 // Mô tả
            price,                                       // Giá mua
            textureOverride != null ? textureOverride : loadDefaultTexture(), // Sử dụng texture được truyền vào hoặc mặc định
            "com.mygdx.game.entities.plants.types.Pumpkin" // Tên class cây trồng
        );
    }

    // Constructor mặc định
    public PumpkinSeed() {
        super(
            "bí ngô",                                  // Tên
            "Hạt bí ngô để trồng lúa.",                  // Mô tả
            30,                                          // Giá mua
            loadDefaultTexture(),                        // Texture
            "com.mygdx.game.entities.plants.types.Pumpkin" // Tên class cây trồng
        );
    }

    private static TextureRegion loadDefaultTexture() {
        try {
            return new TextureRegion(new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/PumpkinSeed.png")));
        } catch (Exception e) {
            System.err.println("Could not load PumpkinSeed texture: " + e.getMessage());
            return null;
        }
    }
}
