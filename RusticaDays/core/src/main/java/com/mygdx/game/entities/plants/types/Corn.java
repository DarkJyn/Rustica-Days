package com.mygdx.game.entities.plants.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.entities.plants.base.Plant;
import com.mygdx.game.entities.plants.states.GrowthState;
import com.mygdx.game.items.crops.Harvest;

/**
 * Cây ngô - chỉ trồng, tưới và thu hoạch
 */
public class Corn extends Plant {

    private static final float WATER_NEED_INTERVAL = 15f;       // Cần tưới mỗi 15 giây
    private static final int FIXED_YIELD = 2;                    // Số lượng thu hoạch
    private static final int FIXED_PRICE = 1200;                  // Giá bán cố định
    private static final int FIXED_EXP = 100;

    public Corn(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    protected void initGrowthTimes() {
        // Tổng cộng 4 giai đoạn, tổng 35s là cây trưởng thành
        stageGrowthTimes.put(GrowthState.SEED, 7f);
        stageGrowthTimes.put(GrowthState.SPROUT, 7f);
        stageGrowthTimes.put(GrowthState.GROWING, 14f);
        stageGrowthTimes.put(GrowthState.FLOWERING, 7f);
        stageGrowthTimes.put(GrowthState.MATURE, Float.MAX_VALUE); // Chờ thu hoạch
    }

    @Override
    protected void initTextures() {
        try {
            Texture sheet = new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/Corn.png"));
            int sheetFrames = 7;
            int frameWidth = sheet.getWidth() / sheetFrames;
            int frameHeight = sheet.getHeight();
            GrowthState[] states = GrowthState.values();
            for (int i = 0; i < states.length; i++) {
                int frameIdx = (i == 0) ? 0 : i + 1;
                if (frameIdx >= sheetFrames) frameIdx = sheetFrames - 1;
                TextureRegion region = new TextureRegion(sheet, frameIdx * frameWidth, 0, frameWidth, frameHeight);
                growthTextures.put(states[i], new TextureRegion[]{region});
            }
        } catch (Exception e) {
            System.err.println("Could not load Corn texture: " + e.getMessage());
            createDefaultTexture();
        }
    }

    private void createDefaultTexture() {
        // Tạo texture đơn giản cho mỗi giai đoạn
        for (GrowthState state : GrowthState.values()) {
            TextureRegion[] frames = new TextureRegion[1];
            frames[0] = new TextureRegion(new Texture(Gdx.files.internal("assets/[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/CornSeed.png")));
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
        return new com.mygdx.game.items.crops.Corn(
            "Strawberry",
            FIXED_PRICE,
            FIXED_YIELD
        );
    }
}
