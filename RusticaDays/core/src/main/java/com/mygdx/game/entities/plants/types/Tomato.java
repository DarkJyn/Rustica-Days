package com.mygdx.game.entities.plants.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.entities.plants.base.Plant;
import com.mygdx.game.entities.plants.states.GrowthState;
import com.mygdx.game.items.crops.Harvest;

/**
 * Cây cà chua - chỉ trồng, tưới và thu hoạch
 */
public class Tomato extends Plant {

    private static final float WATER_NEED_INTERVAL = 10f;       // Cần tưới mỗi 10 giây
    private static final int FIXED_YIELD = 5;                   // Số lượng thu hoạch
    private static final int FIXED_PRICE = 80;                 // Giá bán cố định
    private static final int FIXED_EXP = 100;

    public Tomato(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    protected void initGrowthTimes() {
        // Tổng cộng 4 giai đoạn, tổng 25s là cây trưởng thành
        stageGrowthTimes.put(GrowthState.SEED, 5f);
        stageGrowthTimes.put(GrowthState.SPROUT, 5f);
        stageGrowthTimes.put(GrowthState.GROWING, 10f);
        stageGrowthTimes.put(GrowthState.FLOWERING, 5f);
        stageGrowthTimes.put(GrowthState.MATURE, Float.MAX_VALUE); // Chờ thu hoạch
    }

    @Override
    protected void initTextures() {
        try {
            Texture tomatoSheet = new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/Tomato.png"));

            int sheetFrames = 7; // 7 trạng thái trên ảnh
            int[] mapping = {0, 2, 3, 4, 5, 6}; // Bỏ qua frame 1 (index 1)
            int frameWidth = tomatoSheet.getWidth() / sheetFrames;
            int frameHeight = tomatoSheet.getHeight();

            GrowthState[] states = GrowthState.values();
            for (int i = 0; i < states.length; i++) {
                int frameIdx = (i < mapping.length) ? mapping[i] : mapping[mapping.length-1];
                TextureRegion region = new TextureRegion(tomatoSheet, frameIdx * frameWidth, 0, frameWidth, frameHeight);
                TextureRegion[] frames = new TextureRegion[] { region };
                growthTextures.put(states[i], frames);
            }
        } catch (Exception e) {
            System.err.println("Could not load Tomato texture: " + e.getMessage());
            createDefaultTexture();
        }
    }

    private void createDefaultTexture() {
        // Tạo texture đơn giản cho mỗi giai đoạn
        for (GrowthState state : GrowthState.values()) {
            TextureRegion[] frames = new TextureRegion[1];
            frames[0] = new TextureRegion(new Texture(Gdx.files.internal("assets/[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/TomatoSeed.png")));
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
        return new com.mygdx.game.items.crops.Tomato(
            "Tomato",
            FIXED_PRICE,
            FIXED_YIELD
        );
    }
}
