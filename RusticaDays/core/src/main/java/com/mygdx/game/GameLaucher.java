package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.camera.GameCamera;
import com.mygdx.game.entities.Player;
import com.mygdx.game.input.PlayerInputHandler;
import com.mygdx.game.entities.NPC;
import com.mygdx.game.render.MapRenderer;
import com.mygdx.game.ui.StatsBar;

public class GameLaucher extends ApplicationAdapter {
    private SpriteBatch batch;
    private GameCamera camera;
    private MapRenderer mapRenderer;
    private Player player;
    private NPC shopkeeper;
    private PlayerInputHandler inputHandler;
    private StatsBar statsBar;

    private ShapeRenderer shapeRenderer;
    //Biến để lưu kích thước map
    private float mapWidth;
    private float mapHeight;

    // Biến để bật/tắt debug mode
    private boolean debugMode = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

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

        // Khởi tạo StatsBar
        statsBar = new StatsBar(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Thiết lập một số giá trị ban đầu cho StatsBar
        statsBar.setMoney(500);
        statsBar.setExperience(50);
        statsBar.setStamina(100);

        // Khởi tạo NPC
        shopkeeper = new NPC(343, 455, "NPC.png");

        // Khởi tạo Player
        player = new Player(350, 400, "Player.png");

        // Khởi tạp nhận Input
        inputHandler = new PlayerInputHandler(player, mapRenderer.getMap());
        inputHandler.registerNPC(shopkeeper);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // Bật/tắt debug mode khi nhấn F3
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
            inputHandler.getCollisionHandler().setDebugRender(debugMode);
            System.out.println("Debug mode: " + (debugMode ? "ON" : "OFF"));
        }



        // Cập nhật di chuyển
//        inputHandler.processInput(delta);

        // Cập nhật tọa độ Player
        player.update(delta);

        // Thêm kiểm tra giới hạn map cho player (tùy chọn)
        limitPlayerToMapBounds();

        // Chỉ xử lý input khi không đang tương tác trong cửa hàng
        if (!inputHandler.isInteractingWithNPC() ||
            (inputHandler.getInteractingNPC() != null && !inputHandler.getInteractingNPC().isShopOpen())) {
            // Xử lý input
            inputHandler.processInput(delta);
        } else {
            // Nếu đang trong cửa hàng, chỉ xử lý input liên quan đến NPC
            inputHandler.handleNPCInteraction();
        }

        // Camera follow Player
        camera.followTarget(player.getX(), player.getY());

        // Clear screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Render map
        mapRenderer.render(camera.getCamera());

        // Render StatsBar
        statsBar.render(batch);

        // Render debug collision nếu đang ở chế độ debug
        if (debugMode) {
            shapeRenderer.setProjectionMatrix(camera.getCamera().combined);
            inputHandler.getCollisionHandler().renderCollisions(shapeRenderer);

            // Vẽ hitbox của player
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(player.getX(), player.getY(), player.getBounds().width, player.getBounds().height);
            shapeRenderer.end();
        }

        // Render player
        batch.setProjectionMatrix(camera.getCamera().combined);
        batch.begin();
        player.render(batch);
        shopkeeper.render(batch);
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
        // Điều chỉnh camera khi kích thước màn hình thay đổi
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
        shopkeeper.dispose();
        mapRenderer.dispose();
        statsBar.dispose();
        shapeRenderer.dispose();
    }
}
