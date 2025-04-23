package com.mygdx.game.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class GameCamera{
    private OrthographicCamera camera;
    private float viewportWidth;
    private float viewportHeight;

    // Thêm biến để lưu giới hạn của map
    private float worldWidth;
    private float worldHeight;

    // Thêm biến để tạo smooth camera movement
    private float lerpFactor = 0.1f;
    private boolean smoothCamera = true;

    public GameCamera(float viewportWidth, float viewportHeight) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    // Thêm phương thức để thiết lập giới hạn map
    public void setWorldBounds(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void followTarget(float x, float y) {
        // Tính toán vị trí mới của camera, với player ở giữa
        float targetX = x + 10; // Offset hiện tại của bạn
        float targetY = y + 10;

        // Giới hạn camera trong phạm vi map
        if (worldWidth > 0 && worldHeight > 0) {
            targetX = MathUtils.clamp(targetX, viewportWidth / 2, worldWidth - viewportWidth / 2);
            targetY = MathUtils.clamp(targetY, viewportHeight / 2, worldHeight - viewportHeight / 2);
        }

        // Áp dụng smooth camera movement nếu được bật
        if (smoothCamera) {
            camera.position.x = MathUtils.lerp(camera.position.x, targetX, lerpFactor);
            camera.position.y = MathUtils.lerp(camera.position.y, targetY, lerpFactor);
        } else {
            camera.position.set(targetX, targetY, 0);
        }

        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    // Thêm các phương thức tiện ích
    public void resize(float width, float height) {
        viewportWidth = width;
        viewportHeight = height;
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    public void setSmoothCamera(boolean smooth) {
        this.smoothCamera = smooth;
    }

    public void setLerpFactor(float factor) {
        this.lerpFactor = MathUtils.clamp(factor, 0.01f, 1f);
    }
}
