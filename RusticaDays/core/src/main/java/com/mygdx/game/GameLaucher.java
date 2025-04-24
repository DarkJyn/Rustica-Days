package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.camera.GameCamera;
import com.mygdx.game.entities.Player;
import com.mygdx.game.input.PlayerInputHandler;
import com.mygdx.game.inventory.InventoryManager;
import com.mygdx.game.render.MapRenderer;
import com.mygdx.game.ui.InventoryUI;
import com.mygdx.game.ui.StatsBar;

public class GameLaucher extends ApplicationAdapter {
    private SpriteBatch batch;
    private GameCamera camera;
    private MapRenderer mapRenderer;
    private Player player;
    private PlayerInputHandler inputHandler;
    private StatsBar statsBar;

    // Inventory
    private InventoryUI inventoryUI;
    private InventoryManager inventoryManager;
    private Stage uiStage;
    private boolean showFullInventory = false;


    // Biến để lưu kích thước map
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
        camera.setSmoothCamera(true);
        camera.setLerpFactor(0.1f);

        // Khởi tạo Player
        player = new Player(300, 300, "Player.png");

        // Input
        inputHandler = new PlayerInputHandler(player, mapRenderer.getMap());

        // Stats Bar
        statsBar = new StatsBar(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // UI Stage
        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);

        // Inventory Logic + UI
        inventoryManager = new InventoryManager(42);
        inventoryUI = new InventoryUI(uiStage, inventoryManager);
        uiStage.addActor(inventoryUI.getQuickBar());
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // Input xử lý di chuyển nhân vật
        inputHandler.processInput(delta);

        // Cập nhật player
        player.update(delta);
        limitPlayerToMapBounds();
        camera.followTarget(player.getX(), player.getY());

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Vẽ map
        mapRenderer.render(camera.getCamera());

        // StatsBar
        statsBar.render(batch);

        // Player
        batch.setProjectionMatrix(camera.getCamera().combined);
        batch.begin();
        player.render(batch);
        batch.end();

        // Kiểm tra nhấn phím I để toggle full inventory
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryUI.toggleInventory();
        }

        uiStage.act(delta);
        uiStage.draw();
    }

    private void limitPlayerToMapBounds() {
        float playerX = player.getX();
        float playerY = player.getY();
        int frameWidth = (int) player.getBounds().width;
        int frameHeight = (int) player.getBounds().height;

        if (playerX < 0) player.setX(0);
        if (playerY < 0) player.setY(0);
        if (playerX + frameWidth > mapWidth) player.setX(mapWidth - frameWidth);
        if (playerY + frameHeight > mapHeight) player.setY(mapHeight - frameHeight);
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        float viewportWidth = 350;
        float viewportHeight = viewportWidth / aspectRatio;
        camera.resize(viewportWidth, viewportHeight);
        statsBar.resize(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        mapRenderer.dispose();
        statsBar.dispose();
        uiStage.dispose();
    }
}
