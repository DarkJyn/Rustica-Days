package com.mygdx.game.items.seeds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Hạt giống cà chua – đơn giản chỉ chứa thông tin metadata
 */
public class TomatoSeed extends Seed {

    // Constructor với tham số tên và mô tả từ người gọi
    public TomatoSeed(String name, String description, int price, TextureRegion textureOverride) {
        super(
            name,                                        // Tên
            description,                                 // Mô tả
            price,                                       // Giá mua
            textureOverride != null ? textureOverride : loadDefaultTexture(), // Sử dụng texture được truyền vào hoặc mặc định
            "com.mygdx.game.entities.plants.types.Tomato" // Tên class cây trồng
        );
    }

    // Constructor mặc định
    public TomatoSeed() {
        super(
            "Hạt cà chua",                               // Tên
            "Hạt giống để trồng cà chua.",               // Mô tả
            50,                                           // Giá mua
            loadDefaultTexture(),                         // Texture
            "com.mygdx.game.entities.plants.types.Tomato" // Tên class cây trồng
        );
    }

    private static TextureRegion loadDefaultTexture() {
        try {
            return new TextureRegion(new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/TomatoSeed.png")));
        } catch (Exception e) {
            System.err.println("Could not load TomatoSeed texture: " + e.getMessage());
            return null;
        }
    }
}
