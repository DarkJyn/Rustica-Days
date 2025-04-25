package com.mygdx.game.entities.plants.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.entities.plants.states.GrowthState;
import com.mygdx.game.items.crops.Harvest;

/**
 * Lớp cơ sở cho tất cả các loại cây trồng trong game.
 * Cây sẽ phát triển qua từng giai đoạn nếu được tưới nước.
 * Khi trưởng thành, có thể thu hoạch và tạo ra sản phẩm.
 */
public abstract class Plant {
    protected Rectangle bounds;                          // Khu vực chiếm dụng của cây
    protected GrowthState growthState;                   // Trạng thái phát triển hiện tại
    protected float growthTimer;                         // Bộ đếm thời gian phát triển
    protected ObjectMap<GrowthState, Float> stageGrowthTimes;  // Thời gian phát triển cần thiết cho từng giai đoạn
    protected boolean needsWater;                        // Cây có cần được tưới không
    protected float waterTimer;                          // Thời gian từ lần tưới gần nhất (có thể dùng cho visual hoặc sau này)

    protected ObjectMap<GrowthState, TextureRegion[]> growthTextures; // Texture theo từng giai đoạn

    // Biến phục vụ hoạt ảnh
    protected float animationTimer;
    protected int currentAnimationFrame;
    protected static final int ANIMATION_FRAMES = 4;      // Số frame cho hoạt ảnh
    protected static final float FRAME_DURATION = 0.25f;  // Thời gian mỗi frame

    /**
     * Tạo mới một cây trồng với tọa độ và kích thước cụ thể.
     */
    public Plant(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
        this.growthState = GrowthState.SEED;
        this.growthTimer = 0f;
        this.waterTimer = 0f;
        this.needsWater = true;
        this.animationTimer = 0f;
        this.currentAnimationFrame = 0;

        this.stageGrowthTimes = new ObjectMap<>();
        this.growthTextures = new ObjectMap<>();

        initGrowthTimes();   // Thiết lập thời gian cho từng giai đoạn phát triển
        initTextures();      // Thiết lập texture cho từng giai đoạn
    }

    /**
     * Thiết lập thời gian cần thiết để chuyển qua từng giai đoạn phát triển.
     */
    protected abstract void initGrowthTimes();

    /**
     * Nạp texture cho từng giai đoạn phát triển (và từng frame hoạt ảnh).
     */
    protected abstract void initTextures();

    /**
     * Cập nhật cây mỗi frame.
     */
    public void update(float deltaTime) {
        // Cập nhật hoạt ảnh
        animationTimer += deltaTime;
        if (animationTimer >= FRAME_DURATION) {
            animationTimer -= FRAME_DURATION;
            currentAnimationFrame = (currentAnimationFrame + 1) % ANIMATION_FRAMES;
        }

        // Nếu chưa tưới hoặc đã thu hoạch, không phát triển
        if (needsWater || growthState == GrowthState.HARVESTED) return;

        // Cập nhật thời gian phát triển và kiểm tra giai đoạn tiếp theo
        growthTimer += deltaTime;
        checkGrowthStage();
    }

    /**
     * Kiểm tra xem cây đã đủ điều kiện để chuyển sang giai đoạn mới chưa.
     */
    protected void checkGrowthStage() {
        Float stageTime = stageGrowthTimes.get(growthState);
        if (stageTime != null && growthTimer >= stageTime) {
            growthTimer = 0f;
            int nextStageIndex = growthState.ordinal() + 1;

            // Nếu chưa đến giai đoạn cuối, chuyển sang giai đoạn tiếp theo và yêu cầu tưới lại
            if (nextStageIndex < GrowthState.values().length) {
                growthState = GrowthState.values()[nextStageIndex];
                needsWater = true;
            }
        }
    }

    /**
     * Tưới nước cho cây nếu đang cần.
     */
    public boolean water() {
        if (growthState != GrowthState.HARVESTED && needsWater) {
            needsWater = false;
            waterTimer = 0f;
            return true;
        }
        return false;
    }

    /**
     * Thu hoạch cây nếu đã trưởng thành.
     */
    public Harvest harvest() {
        if (growthState == GrowthState.MATURE) {
            growthState = GrowthState.HARVESTED;
            return createHarvestedCrop();
        }
        return null;
    }

    /**
     * Tạo sản phẩm thu hoạch của cây.
     */
    protected abstract Harvest createHarvestedCrop();

    /**
     * Vẽ cây lên màn hình.
     */
    public void render(SpriteBatch batch) {
        if (isVisible()) {
            TextureRegion texture = getGrowthTexture();
            if (texture != null) {
                batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
    }

    /**
     * Lấy frame texture hiện tại dựa trên trạng thái phát triển.
     */
    protected TextureRegion getGrowthTexture() {
        TextureRegion[] frames = growthTextures.get(growthState);
        if (frames != null && frames.length > 0) {
            return frames[currentAnimationFrame % frames.length];
        }
        return null;
    }

    /**
     * Trả về frame cụ thể cho trạng thái và index hoạt ảnh.
     */
    public TextureRegion getTextureForState(GrowthState state, int frame) {
        TextureRegion[] frames = growthTextures.get(state);
        if (frames != null && frames.length > 0) {
            return frames[frame % frames.length];
        }
        return null;
    }

    /**
     * Xác định xem cây có nên được hiển thị trên màn hình không.
     */
    public boolean isVisible() {
        return growthState != GrowthState.HARVESTED;
    }

    /**
     * Kiểm tra xem một vị trí có nằm trong vùng của cây không.
     */
    public boolean isPositionOver(Vector2 position) {
        return bounds.contains(position.x, position.y);
    }

    // Getters
    public Rectangle getBounds() { return bounds; }
    public GrowthState getGrowthState() { return growthState; }
    public boolean needsWater() { return needsWater; }

    /**
     * Tính toán tiến độ phát triển (0 - 1) của giai đoạn hiện tại.
     */
    public float getGrowthProgress() {
        Float stageTime = stageGrowthTimes.get(growthState);
        if (stageTime != null && stageTime > 0) {
            return growthTimer / stageTime;
        }
        return 1.0f;
    }

    public int getCurrentAnimationFrame() { return currentAnimationFrame; }
}
