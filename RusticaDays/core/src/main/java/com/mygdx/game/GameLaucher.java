package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.camera.GameCamera;
import com.mygdx.game.entities.Player;
import com.mygdx.game.entities.plants.PlantManager;
import com.mygdx.game.input.PlayerInputHandler;
import com.mygdx.game.entities.NPC;
import com.mygdx.game.inventory.InventoryManager;
import com.mygdx.game.inventory.InventorySlot;
import com.mygdx.game.items.base.Item;
import com.mygdx.game.items.seeds.Seed;
import com.mygdx.game.items.seeds.TomatoSeed;
import com.mygdx.game.items.seeds.CarrotSeed;
import com.mygdx.game.items.seeds.CornSeed;
import com.mygdx.game.items.seeds.RiceSeed;
import com.mygdx.game.items.seeds.EggplantSeed;
import com.mygdx.game.render.MapRenderer;
import com.mygdx.game.ui.InventoryUI;
import com.mygdx.game.ui.StatsBar;

public class GameLaucher extends ApplicationAdapter {
    private SpriteBatch batch;
    private GameCamera camera;
    private MapRenderer mapRenderer;
    private Player player;
    private NPC shopkeeper;
    private PlayerInputHandler inputHandler;
    private StatsBar statsBar;
    private InventoryUI inventoryUI;
    private InventoryManager inventoryManager;
    private Stage uiStage;
    private boolean showFullInventory = false;
    private ShapeRenderer shapeRenderer;
    private float mapWidth;
    private float mapHeight;

    // Quản lý cây trồng
    private PlantManager plantManager;

    // Biến phục vụ cho tương tác trồng cây
    private boolean plantMode = false;
    private boolean waterMode = false;
    private boolean harvestMode = false;
    private int selectedSlotIndex = 0;

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
        camera.setSmoothCamera(true);
        camera.setLerpFactor(0.1f);

        // Khởi tạo Player
        player = new Player(350, 350, "Player.png");
        // Khởi tạo NPC
        shopkeeper = new NPC(345, 460, "NPC.png",camera);

        // Khởi tạp nhận Input
        inputHandler = new PlayerInputHandler(player, mapRenderer.getMap());
        inputHandler.registerNPC(shopkeeper);

        // Stats Bar
        statsBar = new StatsBar(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // UI Stage
        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);

        // Inventory Logic + UI
        inventoryManager = new InventoryManager(42);
        inventoryUI = new InventoryUI(uiStage, inventoryManager);
        uiStage.addActor(inventoryUI.getQuickBar());

        // Thiết lập một số giá trị ban đầu cho StatsBar
        statsBar.setMoney(500);
        statsBar.setExperience(50);
        statsBar.setStamina(100);

        // Khởi tạo PlantManager
        plantManager = new PlantManager(inventoryManager);

        // Thêm các mặt hàng vào inventory cho test
        addInitialItems();
    }

    private void addInitialItems() {
        // Tạo các mặt hàng hạt giống sử dụng constructor mặc định
        TomatoSeed tomatoSeed = new TomatoSeed();
        CarrotSeed carrotSeed = new CarrotSeed();
        CornSeed cornSeed = new CornSeed();
        RiceSeed riceSeed = new RiceSeed();
        EggplantSeed eggplantSeed = new EggplantSeed();

        // Thêm hạt giống vào inventory
        inventoryManager.addItem(tomatoSeed, 1);
        inventoryManager.addItem(carrotSeed, 7);
        inventoryManager.addItem(cornSeed, 1);
        inventoryManager.addItem(riceSeed, 1);
        inventoryManager.addItem(eggplantSeed, 5);
        inventoryManager.addItem(eggplantSeed, 5);

        // Cập nhật UI sau khi thêm vật phẩm
        inventoryUI.updateUI();
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

        // Cập nhật player
        player.update(delta);

        // Cập nhật cây trồng
        if (plantManager != null) {
            plantManager.update(delta);
        }

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

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Render các layer dưới player
        mapRenderer.renderBelowPlayer(camera.getCamera());

        // Player
        batch.setProjectionMatrix(camera.getCamera().combined);
        batch.begin();
        player.render(batch);
        batch.end();

        //Render các layer trên player
        mapRenderer.renderAbovePlayer(camera.getCamera());

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



        // Render cây trồng
        batch.begin();
        if (plantManager != null) {
            plantManager.render(batch);
        }
        batch.end();

        // Kiểm tra nhấn phím I để toggle full inventory
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryUI.toggleInventory();
        }

        // Xử lý tương tác với cây trồng
        handleFarmingInput();

        uiStage.act(delta);
        uiStage.draw();

        // Render NPC
        batch.begin();
        shopkeeper.render(batch);
        batch.end();
    }

    private void handleFarmingInput() {
        // Chuyển đổi chế độ
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            plantMode = true;
            waterMode = false;
            harvestMode = false;
            System.out.println("Plant mode activated");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            plantMode = false;
            waterMode = true;
            harvestMode = false;
            System.out.println("Water mode activated");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            plantMode = false;
            waterMode = false;
            harvestMode = true;
            System.out.println("Harvest mode activated");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            plantMode = waterMode = harvestMode = false;
            System.out.println("Farming modes deactivated");
        }

        // Lựa chọn slot (1-6 cho quickbar)
        for (int i = 0; i < 6; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                selectedSlotIndex = i;
                System.out.println("Selected slot " + (selectedSlotIndex + 1));
            }
        }

        // Xử lý click chuột
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 worldCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.getCamera().unproject(worldCoordinates);

            // Tọa độ chuột trong thế giới
            float mouseWorldX = worldCoordinates.x;
            float mouseWorldY = worldCoordinates.y;

            if (plantMode) {
                tryPlantSeed(mouseWorldX, mouseWorldY);
            } else if (waterMode) {
                tryWaterPlant(mouseWorldX, mouseWorldY);
            } else if (harvestMode) {
                tryHarvestPlant(mouseWorldX, mouseWorldY);
            }
        }
    }

    private void tryPlantSeed(float x, float y) {
        // Kiểm tra xem slot đang chọn có phải là hạt giống không
        if (selectedSlotIndex < inventoryManager.getSlots().size()) {
            InventorySlot slot = inventoryManager.getSlots().get(selectedSlotIndex);
            if (!slot.isEmpty()) {
                Item item = slot.getItem();
                if (item instanceof Seed) {
                    Seed seed = (Seed) item;
                    boolean planted = plantManager.plantSeed(seed, x, y);
                    if (planted) {
                        System.out.println("Planted " + seed.getName() + " at " + x + ", " + y);
                    } else {
                        System.out.println("Cannot plant here");
                    }
                } else {
                    System.out.println("Selected item is not a seed");
                }
            } else {
                System.out.println("No item in selected slot");
            }
        }
    }

    private void tryWaterPlant(float x, float y) {
        boolean watered = plantManager.waterPlant(x, y);
        if (watered) {
            System.out.println("Watered plant at " + x + ", " + y);
        } else {
            System.out.println("No plant to water at this position or plant doesn't need water");
        }
    }

    private void tryHarvestPlant(float x, float y) {
        boolean harvested = plantManager.harvestPlant(x, y);
        if (harvested) {
            System.out.println("Harvested plant at " + x + ", " + y);
        } else {
            System.out.println("No plant to harvest at this position or plant is not ready");
        }
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
        // Điều chỉnh camera khi kích thước màn hình thay đổi

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
        shopkeeper.dispose();
        mapRenderer.dispose();
        statsBar.dispose();
        inventoryUI.dispose();
        uiStage.dispose();
        shapeRenderer.dispose();
    }
}
