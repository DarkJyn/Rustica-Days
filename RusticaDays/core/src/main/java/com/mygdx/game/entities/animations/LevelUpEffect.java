package com.mygdx.game.entities.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class LevelUpEffect {
    private Texture texture;
    private float x, y;               // Vị trí hiện tại
    private float centerX, centerY;   // Vị trí trung tâm màn hình
    private float scale;              // Tỷ lệ phóng to
    private float alpha;              // Độ trong suốt
    private float stateTime;          // Thời gian trạng thái
    private float duration;           // Thời lượng hiệu ứng
    private boolean active;           // Hiệu ứng có đang hoạt động không
    private final float maxScale;     // Tỷ lệ phóng to tối đa
    private final float startScale;   // Tỷ lệ ban đầu

    public LevelUpEffect() {
        texture = new Texture(Gdx.files.internal("LvUp.png"));
        duration = 2.0f;              // Hiệu ứng kéo dài 2 giây
        active = false;
        startScale = 0.5f;
        maxScale = 1.2f;
    }

    public void start(float screenWidth, float screenHeight) {
        // Đặt vị trí ở giữa màn hình
        centerX = screenWidth / 2;
        centerY = screenHeight / 2;

        x = centerX - texture.getWidth() / 2;
        y = centerY - texture.getHeight() / 2;

        stateTime = 0;
        scale = startScale;
        alpha = 0;
        active = true;
    }

    public void update(float delta) {
        if (!active) return;

        stateTime += delta;

        if (stateTime <= duration) {
            // Giai đoạn 1: Hiện lên và phóng to (0 -> 1/3 thời gian)
            if (stateTime < duration / 3) {
                float progress = stateTime / (duration / 3);
                scale = Interpolation.bounceOut.apply(startScale, maxScale, progress);
                alpha = Interpolation.fade.apply(0, 1, progress);
            }
            // Giai đoạn 2: Giữ nguyên (1/3 -> 2/3 thời gian)
            else if (stateTime < 2 * duration / 3) {
                scale = maxScale;
                alpha = 1;

                // Thêm hiệu ứng nhảy nhẹ
                float bounceTime = (stateTime - duration / 3) / (duration / 3);
                float bounceOffset = (float) Math.sin(bounceTime * Math.PI * 2) * 5;
                y = centerY - texture.getHeight() / 2 + bounceOffset;
            }
            // Giai đoạn 3: Mờ dần và biến mất (2/3 -> kết thúc)
            else {
                float progress = (stateTime - 2 * duration / 3) / (duration / 3);
                alpha = Interpolation.fade.apply(1, 0, progress);
            }
        } else {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (!active) return;

        batch.setColor(1, 1, 1, alpha);

        // Căn giữa hình ảnh theo tỷ lệ
        float width = texture.getWidth() * scale;
        float height = texture.getHeight() * scale;
        float drawX = centerX - width / 2;
        float drawY = centerY - height / 2;

        batch.draw(texture, drawX, drawY, width, height);
        batch.setColor(1, 1, 1, 1); // Reset màu
    }

    public boolean isActive() {
        return active;
    }

    public void dispose() {
        texture.dispose();
    }
}
