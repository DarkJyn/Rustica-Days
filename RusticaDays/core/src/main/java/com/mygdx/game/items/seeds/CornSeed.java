package com.mygdx.game.items.seeds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Hạt giống ngô – đơn giản chỉ chứa thông tin metadata
 */
public class CornSeed extends Seed {

    // Constructor với tham số tên và mô tả từ người gọi
    public CornSeed(String name, String description, int price, TextureRegion textureOverride) {
        super(
            name,                                        // Tên
            description,                                 // Mô tả
            price,                                       // Giá mua
            textureOverride != null ? textureOverride : loadDefaultTexture(), // Sử dụng texture được truyền vào hoặc mặc định
            "com.mygdx.game.entities.plants.types.Corn" // Tên class cây trồng
        );
    }

    // Constructor mặc định
    public CornSeed() {
        super(
            "Strawberry Seed",                               // Tên
            "Seed to grow strawberries",               // Mô tả
            60,                                           // Giá mua
            loadDefaultTexture(),                         // Texture
            "com.mygdx.game.entities.plants.types.Corn" // Tên class cây trồng
        );
    }

    private static TextureRegion loadDefaultTexture() {
        try {
            return new TextureRegion(new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/CornSeed.png")));
        } catch (Exception e) {
            System.err.println("Could not load CornSeed texture: " + e.getMessage());
            return null;
        }
    }
}
