package com.mygdx.game.entities.plants.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.entities.plants.base.Plant;
import com.mygdx.game.entities.plants.states.GrowthState;
import com.mygdx.game.items.crops.Harvest;

/**
 * Cây cà tím - chỉ trồng, tưới và thu hoạch
 */
public class Eggplant extends Plant {

    private static final float WATER_NEED_INTERVAL = 12f;       // Cần tưới mỗi 12 giây
    private static final int FIXED_YIELD = 4;                    // Số lượng thu hoạch
    private static final int FIXED_PRICE = 110;                  // Giá bán cố định
    private static final int FIXED_EXP = 100;

    public Eggplant(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    protected void initGrowthTimes() {
        // Tổng cộng 4 giai đoạn, tổng 28s là cây trưởng thành
        stageGrowthTimes.put(GrowthState.SEED, 5f);
        stageGrowthTimes.put(GrowthState.SPROUT, 5f);
        stageGrowthTimes.put(GrowthState.GROWING, 12f);
        stageGrowthTimes.put(GrowthState.FLOWERING, 6f);
        stageGrowthTimes.put(GrowthState.MATURE, Float.MAX_VALUE); // Chờ thu hoạch
    }

    @Override
    protected void initTextures() {
        try {
            Texture eggplantSheet = new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/Eggplant.png"));

            int frameCount = GrowthState.values().length;
            int frameWidth = eggplantSheet.getWidth() / frameCount;
            int frameHeight = eggplantSheet.getHeight();

            for (int i = 0; i < frameCount; i++) {
                // Cắt từng vùng theo chỉ số cột i
                TextureRegion region = new TextureRegion(eggplantSheet, i * frameWidth, 0, frameWidth, frameHeight);

                TextureRegion[] frames = new TextureRegion[] { region };

                GrowthState state = GrowthState.values()[i];
                growthTextures.put(state, frames);
            }
        } catch (Exception e) {
            System.err.println("Could not load Eggplant texture: " + e.getMessage());
            // Tạo texture mặc định
            createDefaultTexture();
        }
    }

    private void createDefaultTexture() {
        // Tạo texture đơn giản cho mỗi giai đoạn
        for (GrowthState state : GrowthState.values()) {
            TextureRegion[] frames = new TextureRegion[1];
            frames[0] = new TextureRegion(new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/EggplantSeed.png")));
            growthTextures.put(state, frames);
        }
    }

    @Override
    public void update(float deltaTime) {
        // Cập nhật thời gian tưới
        waterTimer += deltaTime;
        if (waterTimer >= WATER_NEED_INTERVAL) {
            needsWater = true;
        }

        // Nếu cây cần nước thì không phát triển tiếp
        if (needsWater) return;

        // Nếu không cần nước thì tiếp tục cập nhật tăng trưởng
        super.update(deltaTime);
    }

    @Override
    protected Harvest createHarvestedCrop() {
        return new com.mygdx.game.items.crops.Eggplant(
            "Cà tím",
            FIXED_PRICE,
            FIXED_YIELD
        );
    }
}
