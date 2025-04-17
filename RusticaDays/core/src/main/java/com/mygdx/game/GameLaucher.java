package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.camera.GameCamera;
import com.mygdx.game.entities.Player;
import com.mygdx.game.input.PlayerInputHandler;
import com.mygdx.game.render.MapRenderer;
import com.mygdx.game.ui.StatsBar;

public class GameLaucher extends ApplicationAdapter {
    private SpriteBatch batch;
    private GameCamera camera;
    private MapRenderer mapRenderer;
    private Player player;
    private PlayerInputHandler inputHandler;
    private StatsBar statsBar;

    //Biến để lưu kích thước map
    private float mapWidth;
    private float mapHeight;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Khởi tạo Camera 480 360
        float viewportWidth = 434;
        float viewportHeight = 314;
        camera = new GameCamera(viewportWidth, viewportHeight);

        // Khởi tạo Map
        mapRenderer = new MapRenderer("Map/MapFix.tmx");

        // Lấy kích thước map từ MapRenderer
        mapWidth = mapRenderer.getMapWidth();
        mapHeight = mapRenderer.getMapHeight();

        // Thiết lập giới hạn map cho camera
        camera.setWorldBounds(mapWidth, mapHeight);

        // Bật smooth camera
        camera.setSmoothCamera(true);
        camera.setLerpFactor(0.1f);

        // Khởi tạo Player
        player = new Player(300, 300, "Player.png");

        // Khởi tạp nhận Input
        inputHandler = new PlayerInputHandler(player,mapRenderer.getMap());

        // Khởi tạo StatsBar
        statsBar = new StatsBar(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // Cập nhận di chuyển
        inputHandler.processInput(delta);

        // Cập nhật tọa độ Player
        player.update(delta);



        // Thêm kiểm tra giới hạn map cho player (tùy chọn)
        limitPlayerToMapBounds();

        // Camera follow Player
        camera.followTarget(player.getX(), player.getY());

        // Clear screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Render map
        mapRenderer.render(camera.getCamera());

        // Render StatsBar
        statsBar.render(batch);

        // Render player
        batch.setProjectionMatrix(camera.getCamera().combined);
        batch.begin();
        player.render(batch);
        batch.end();
    }

    // Phương thức limit Player trong Map
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
//         Điều chỉnh camera khi kích thước màn hình thay đổi
        float aspectRatio = (float) width / (float) height;
        float viewportWidth = 350;
        float viewportHeight = viewportWidth / aspectRatio;
        camera.resize(viewportWidth, viewportHeight);
        statsBar.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        mapRenderer.dispose();
        statsBar.dispose();
    }
}
