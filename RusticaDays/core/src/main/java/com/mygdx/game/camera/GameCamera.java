package com.mygdx.game.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameCamera {
    private OrthographicCamera camera;

    public GameCamera(float viewportWidth, float viewportHeight) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);
    }

    public void followTarget(float x, float y) {
        // Add offset for centering
        camera.position.set(x + 10, y + 10, 0);
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
