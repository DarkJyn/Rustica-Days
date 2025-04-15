package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.camera.GameCamera;
import com.mygdx.game.entities.Player;
import com.mygdx.game.input.PlayerInputHandler;
import com.mygdx.game.render.MapRenderer;

public class GameLaucher extends ApplicationAdapter {
    private SpriteBatch batch;
    private GameCamera camera;
    private MapRenderer mapRenderer;
    private Player player;
    private PlayerInputHandler inputHandler;

    // Thêm biến để lưu kích thước map
    private float mapWidth;
    private float mapHeight;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Initialize camera
        float viewportWidth = 300;
        float viewportHeight = 225;
        camera = new GameCamera(viewportWidth, viewportHeight);

        // Initialize map
        mapRenderer = new MapRenderer("Map/MapFix.tmx");

        // Lấy kích thước map từ MapRenderer
        // Giả sử MapRenderer có phương thức để lấy kích thước map
        // Nếu không, bạn có thể thiết lập trực tiếp
        mapWidth = mapRenderer.getMapWidth(); // Thay thế bằng kích thước thật của map
        mapHeight = mapRenderer.getMapHeight(); // Thay thế bằng kích thước thật của map

        // Thiết lập giới hạn map cho camera
        camera.setWorldBounds(mapWidth, mapHeight);

        // Bật smooth camera (tùy chọn)
        camera.setSmoothCamera(true);
        camera.setLerpFactor(0.1f);

        // Initialize player
        player = new Player(100, 100, "Player.png");

        // Initialize input handler
        inputHandler = new PlayerInputHandler(player);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // Update player based on input
        inputHandler.processInput(delta);

        // Update player
        player.update(delta);

        // Thêm kiểm tra giới hạn map cho player (tùy chọn)
        limitPlayerToMapBounds();

        // Update camera to follow player
        camera.followTarget(player.getX(), player.getY());

        // Clear screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Render map
        mapRenderer.render(camera.getCamera());

        // Render player
        batch.setProjectionMatrix(camera.getCamera().combined);
        batch.begin();
        player.render(batch);
        batch.end();
    }

    // Thêm phương thức để giới hạn player trong map
    private void limitPlayerToMapBounds() {
        float playerX = player.getX();
        float playerY = player.getY();
        int frameWidth = (int) player.getBounds().width;
        int frameHeight = (int) player.getBounds().height;

        // Giới hạn player trong phạm vi map
        if (playerX < 0) {
            player.setX(0);
        }
        if (playerY < 0) {
            player.setY(0);
        }
        if (playerX + frameWidth > mapWidth) {
            player.setX(mapWidth - frameWidth);
        }
        if (playerY + frameHeight > mapHeight) {
            player.setY(mapHeight - frameHeight);
        }
    }

    @Override
    public void resize(int width, int height) {
        // Điều chỉnh camera khi kích thước màn hình thay đổi
        float aspectRatio = (float) width / (float) height;
        float viewportWidth = 300;
        float viewportHeight = viewportWidth / aspectRatio;
        camera.resize(viewportWidth, viewportHeight);
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        mapRenderer.dispose();
    }
}
