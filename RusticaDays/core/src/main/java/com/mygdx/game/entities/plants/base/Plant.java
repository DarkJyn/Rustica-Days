package com.mygdx.game.entities.plants.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.entities.GameObject;
import com.mygdx.game.entities.plants.states.GrowthState;
import com.mygdx.game.items.crops.Harvest;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.Texture;

/**
 * Lớp cơ sở cho tất cả các loại cây trồng trong game.
 * Cây sẽ phát triển qua từng giai đoạn nếu được tưới nước.
 * Khi trưởng thành, có thể thu hoạch và tạo ra sản phẩm.
 */
public abstract class Plant implements GameObject {
    protected Rectangle bounds;                          // Khu vực chiếm dụng của cây
    protected GrowthState growthState;                   // Trạng thái phát triển hiện tại
    protected float growthTimer;                         // Bộ đếm thời gian phát triển
    protected ObjectMap<GrowthState, Float> stageGrowthTimes;  // Thời gian phát triển cần thiết cho từng giai đoạn
    protected boolean needsWater;                        // Cây có cần được tưới không
    protected float waterTimer;                          // Thời gian từ lần tưới gần nhất

    protected ObjectMap<GrowthState, TextureRegion[]> growthTextures; // Texture theo từng giai đoạn

    // Biến phục vụ hoạt ảnh
    protected float animationTimer;
    protected int currentAnimationFrame;
    protected static final int ANIMATION_FRAMES = 4;      // Số frame cho hoạt ảnh
    protected static final float FRAME_DURATION = 0.25f;  // Thời gian mỗi frame

    // Biến phục vụ hiển thị đếm ngược
    protected boolean showCountdown = false;
    protected static final float COUNTDOWN_DISPLAY_DURATION = 2f; // 1 giây hiển thị
    protected long countdownEndTimeMillis = 0; // Thời điểm kết thúc hiển thị
    public static BitmapFont countdownFont = null;
    protected static GlyphLayout glyphLayout = new GlyphLayout();
    protected float countdownDisplayTimer = 0f;

    // Icon giọt nước khi cây cần tưới
    private static TextureRegion waterIcon = null;
    private static void loadWaterIcon() {
        if (waterIcon == null) {
            try {
                Texture waterTexture = new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Crops/water.png"));
                waterIcon = new TextureRegion(waterTexture);
            } catch (Exception e) {
                System.err.println("Could not load water icon: " + e.getMessage());
            }
        }
    }

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

        // Khởi tạo font cho đếm ngược nếu chưa có
        if (countdownFont == null) {
            initCountdownFont();
        }
    }

    /**
     * Khởi tạo font hiển thị đếm ngược
     */
    private static void initCountdownFont() {
        countdownFont = new BitmapFont(); // giống inventory
        countdownFont.setColor(Color.WHITE);
        countdownFont.getData().setScale(0.3f); // tăng size cho dễ đọc
        glyphLayout = new GlyphLayout();
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
    @Override
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
    @Override
    public void render(SpriteBatch batch) {
        if (isVisible()) {
            TextureRegion texture = getGrowthTexture();
            if (texture != null) {
                batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
            }
            // Vẽ icon giọt nước nếu cây cần tưới và chưa trưởng thành
            if (needsWater && growthState != GrowthState.MATURE) {
                loadWaterIcon();
                if (waterIcon != null) {
                    float iconSize = Math.min(bounds.width, bounds.height) * 0.3f;
                    float iconX = bounds.x + bounds.width / 2 - iconSize / 2 + 4;
                    float iconY = bounds.y + bounds.height - iconSize * 0.2f - 1;
                    batch.draw(waterIcon, iconX, iconY, iconSize, iconSize);
                }
            }
        }
    }

    /**
     * Vẽ thông tin đếm ngược sau khi vẽ cây để đảm bảo hiển thị trên cây
     */
    public void renderCountdown(SpriteBatch batch) {
        if (!isVisible() || !showCountdown) {
            return;
        }

        // Nếu cây cần tưới nước thì không hiển thị thời gian đếm ngược
        if (needsWater && growthState != GrowthState.MATURE) {
            return;
        }

        String text;
        Color textColor = new Color(1, 1, 1, 1); // Mặc định màu trắng

        // Xác định nội dung và màu sắc dựa vào trạng thái
        if (growthState == GrowthState.MATURE) {
            text = "Thu hoạch!";
            textColor = new Color(0, 1, 0, 1); // Màu xanh lá
        } else if (growthState == GrowthState.HARVESTED) {
            return; // Không hiện gì cho cây đã thu hoạch
        } else {
            // Đối với các cây chưa trưởng thành, hiển thị thời gian
            float timeRemaining = calculateTimeToHarvest();
            int minutes = (int) (timeRemaining / 60);
            int seconds = (int) (timeRemaining % 60);
            text = String.format("%02d:%02d", minutes, seconds);
        }

        // Vẽ text
        countdownFont.setColor(textColor);
        glyphLayout.setText(countdownFont, text);
        float textX = bounds.x + bounds.width / 2 - glyphLayout.width / 2;
        float textY = bounds.y + bounds.height + glyphLayout.height - 2;
        countdownFont.draw(batch, text, textX, textY);
    }

    /**
     * Tính toán tổng thời gian còn lại đến khi thu hoạch
     */
    public float calculateTimeToHarvest() {
        float remaining = 0;

        // Tính thời gian còn lại ở giai đoạn hiện tại
        Float currentStageTime = stageGrowthTimes.get(growthState);
        if (currentStageTime != null) {
            remaining += Math.max(0, currentStageTime - growthTimer);
        }

        // Tính thời gian cho các giai đoạn tiếp theo (nếu có)
        GrowthState[] states = GrowthState.values();
        for (int i = growthState.ordinal() + 1; i < states.length; i++) {
            if (states[i] == GrowthState.MATURE || states[i] == GrowthState.HARVESTED) {
                break;
            }
            Float stageTime = stageGrowthTimes.get(states[i]);
            if (stageTime != null) {
                remaining += stageTime;
            }
        }

        return remaining;
    }

    /**
     * Kích hoạt hiển thị đếm ngược
     */
    public void toggleCountdownDisplay() {
        showCountdown = true;
        countdownDisplayTimer = 0f;
        System.out.println("Toggle countdown: " + this + " at " + System.currentTimeMillis());
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
    public boolean needsWater() {
        // Khi cây đã trưởng thành thì không cần tưới nữa
        return needsWater && growthState != GrowthState.MATURE;
    }

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

    public void updateCountdown(float deltaTime) {
        if (showCountdown) {
            countdownDisplayTimer += deltaTime;
            System.out.println("Update countdown: " + this + " timer=" + countdownDisplayTimer);
            if (countdownDisplayTimer >= COUNTDOWN_DISPLAY_DURATION) {
                showCountdown = false;
                countdownDisplayTimer = 0f;
                System.out.println("Hide countdown: " + this + " at " + System.currentTimeMillis());
            }
        }
    }

    // Implemention of GameObject interface methods
    @Override
    public Vector2 getPosition() {
        return new Vector2(bounds.x, bounds.y);
    }

    @Override
    public float getDepth() {
        // Dùng tọa độ y của phần đáy cây để xác định độ sâu
        // Sử dụng phần đáy của cây, không phải phần giữa để xử lý đúng vị trí
        return bounds.y;
    }

    public static void initFont() {
        // Tạo generator từ file Pixellari.ttf
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/PressStart2P.ttf"));

        // Thiết lập tham số font
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 13; // Kích thước font
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;

        countdownFont = generator.generateFont(parameter);

        generator.dispose();
    }
}
