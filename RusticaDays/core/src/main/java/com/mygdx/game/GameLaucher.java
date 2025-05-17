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
import com.mygdx.game.entities.animations.WateringEffect;
import com.mygdx.game.entities.animations.LevelUpEffect;
import com.mygdx.game.entities.plants.PlantManager;
import com.mygdx.game.entities.plants.base.Plant;
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
import com.mygdx.game.items.tools.WateringCan;
import com.mygdx.game.render.MapRenderer;
import com.mygdx.game.render.RenderManager;
import com.mygdx.game.ui.InventoryUI;
import com.mygdx.game.ui.StatsBar;
import com.mygdx.game.module.SleepSystem; // Import SleepSystem

import java.util.ArrayList;
import java.util.Iterator;

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
    private RenderManager renderManager;

    // Level up effect
    private LevelUpEffect levelUpEffect;

    // Quản lý cây trồng
    private PlantManager plantManager;

    // Sleep System
    private SleepSystem sleepSystem;
    private Vector2 bedPosition = new Vector2(595, 370); // Vị trí giường (có thể thay đổi)
    private final float BED_WIDTH = 15f;
    private final float BED_HEIGHT = 30f;

    // Biến phục vụ cho tương tác trồng cây
    private int selectedSlotIndex = -1;

    // Biến để bật/tắt debug mode
    private boolean debugMode = false;

    // Danh sách hiệu ứng tưới nước
    private ArrayList<WateringEffect> wateringEffects = new ArrayList<>();

    // Stamina consumption constant
    private static final float PLANTING_STAMINA_COST = 5f;
    private static final float WATERING_STAMINA_COST = 5f;
    private static final float HARVESTING_STAMINA_COST = 10f;

    // Experience rewards
    private static final int PLANTING_XP_REWARD = 5;
    private static final int HARVESTING_XP_REWARD = 15;

    // Sleep system constants
    private static final float SLEEP_STAMINA_RESTORE = 100f; // Phục hồi toàn bộ stamina khi ngủ
    private static final float SLEEP_XP_REWARD = 0.1f; // XP nhận được khi ngủ

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

        // UI Stage
        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);

        // Inventory Logic + UI
        inventoryManager = new InventoryManager(42);
        inventoryUI = new InventoryUI(uiStage, inventoryManager);
        uiStage.addActor(inventoryUI.getQuickBar());

        // Đăng ký callback chọn slot
        inventoryUI.setSlotSelectionListener(new InventoryUI.SlotSelectionListener() {
            @Override
            public void onSlotSelected(int slotIndex) {
                if (selectedSlotIndex == slotIndex) {
                    selectedSlotIndex = -1;
                    System.out.println("Unselected slot");
                } else {
                    selectedSlotIndex = slotIndex;
                    System.out.println("Selected slot " + (selectedSlotIndex + 1));
                }
            }
        });

        // Thêm các mặt hàng vào inventory cho test
        addInitialItems();

        // Khởi tạo Player
        player = new Player(660, 360, "Player.png");

        // Khởi tạo Sleep System
        sleepSystem = new SleepSystem(bedPosition, BED_WIDTH, BED_HEIGHT, player,camera);

        // Khởi tạo NPC
        shopkeeper = new NPC(345, 460, "NPC.png", camera, inventoryManager, inventoryUI);

        // Khởi tạp nhận Input
        inputHandler = new PlayerInputHandler(player, mapRenderer.getMap());
        inputHandler.registerNPC(shopkeeper);

        // Stats Bar
        statsBar = new StatsBar(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Thiết lập một số giá trị ban đầu cho StatsBar
        statsBar.setMoney(500);
        statsBar.setExperience(90);
        statsBar.setStamina(100);

        // Khởi tạo PlantManager
        plantManager = new PlantManager(inventoryManager);

        renderManager = new RenderManager();

        // Thêm tất cả đối tượng vào RenderManager
        renderManager.add(player);

        // Khởi tạo hiệu ứng Level Up
        levelUpEffect = new LevelUpEffect();

        // Đăng ký listener cho level up event
        statsBar.setLevelUpListener(new StatsBar.LevelUpListener() {
            @Override
            public void onLevelUp(int newLevel) {
                showLevelUpEffect();
            }
        });
    }

    // Phương thức hiển thị hiệu ứng Level Up
    private void showLevelUpEffect() {
        // Khởi động hiệu ứng level up ở giữa màn hình
        levelUpEffect.start(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        System.out.println("Level Up! Current level: " + statsBar.getLevel());
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

        // Thêm tool vào inventory
        com.mygdx.game.items.tools.WateringCan wateringCan = new com.mygdx.game.items.tools.WateringCan();
        com.mygdx.game.items.tools.Sickle sickle = new com.mygdx.game.items.tools.Sickle();
        inventoryManager.addItem(wateringCan, 1);
        inventoryManager.addItem(sickle, 1);

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

        // Update sleep system trước tiên
        sleepSystem.update(delta);

        // Chỉ xử lý input và update player khi không đang trong animation ngủ
        if (!sleepSystem.shouldBlockPlayerMovement()) {
            // Cập nhật player
            player.update(delta);

            // Thêm kiểm tra giới hạn map cho player (tùy chọn)
            limitPlayerToMapBounds();

            // Xử lí Input
            inputHandler.processInput(delta);
        } else {
            // Trong khi ngủ, vẫn update player để animation chạy
            player.update(delta);
        }

        // Xử lý các hiệu ứng đặc biệt khi ngủ
        if(sleepSystem.getSleepEnd()){
            handleSleepEffects();
            sleepSystem.setSleepEnd(false);
        }

        // Cập nhật stats bar
//        statsBar.update(delta);

        // Cập nhật cây trồng
        if (plantManager != null) {
            plantManager.update(delta);
        }

        // Cập nhật stamina tự phục hồi dần theo thời gian (chỉ khi không ngủ)
//        if (!sleepSystem.isSleepAnimationPlaying()) {
//            updateStaminaRegeneration(delta);
//        }

        // Camera follow Player
        camera.followTarget(player.getX(), player.getY());

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Render các layer dưới player
        mapRenderer.renderBelowPlayer(camera.getCamera());

        batch.begin();
        batch.setProjectionMatrix(camera.getCamera().combined);
        renderManager.render(batch);
        batch.end();

        batch.begin();
        // Render hiệu ứng tưới nước
        Iterator<WateringEffect> it = wateringEffects.iterator();
        while (it.hasNext()) {
            WateringEffect effect = it.next();
            effect.update(delta);
            effect.render(batch);
            if (effect.isFinished()) it.remove();
        }
        batch.end();

        //Render các layer trên player
        mapRenderer.renderAbovePlayer(camera.getCamera());

        // Render Sleep System UI (prompt text)
        batch.begin();
        batch.setProjectionMatrix(camera.getCamera().combined);
        sleepSystem.render(batch);
        batch.end();

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

            // Render sleep system debug
            sleepSystem.renderDebug(shapeRenderer);
        }

        // Render NPC
        batch.begin();
        shopkeeper.render(batch);
        batch.end();

        // Cập nhật và render hiệu ứng Level Up
        batch.begin();
        if (levelUpEffect != null && levelUpEffect.isActive()) {
            levelUpEffect.update(delta);
            levelUpEffect.render(batch);
        }
        batch.end();

        // Kiểm tra nhấn phím I để toggle full inventory (chỉ khi không ngủ)
        if (Gdx.input.isKeyJustPressed(Input.Keys.I) && !sleepSystem.shouldBlockPlayerMovement()) {
            inventoryUI.toggleInventory();
        }

        // Xử lý tương tác với cây trồng (chỉ khi không ngủ)
        if (!sleepSystem.shouldBlockPlayerMovement()) {
            handleFarmingInput();
        }

        uiStage.act(delta);
        uiStage.draw();

        // Render sleep overlay (fade effect) - phải render cuối cùng
        batch.begin();
        sleepSystem.renderOverlay(batch);
        batch.end();
    }

    private void handleSleepEffects() {


        statsBar.setStamina(statsBar.getMaxStamina());
        // Tăng kinh nghiệm
        statsBar.addExperience(SLEEP_XP_REWARD);

        System.out.println("Player woke up! Stamina restored to 100, gained " + SLEEP_XP_REWARD + " XP");
    }

    private void updateStaminaRegeneration(float delta) {
        // Phục hồi stamina theo thời gian - 2 điểm stamina mỗi giây
        if (statsBar.getStamina() < 100) {
            statsBar.increaseStamina(2f * delta);
        }
    }

    private void handleFarmingInput() {
        // Lựa chọn slot (1-6 cho quickbar)
        for (int i = 0; i < 6; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                if (selectedSlotIndex == i) {
                    selectedSlotIndex = -1;
                    System.out.println("Unselected slot");
                } else {
                    selectedSlotIndex = i;
                    System.out.println("Selected slot " + (selectedSlotIndex + 1));
                }
            }
        }

        // Xử lý click chuột
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 worldCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.getCamera().unproject(worldCoordinates);

            float mouseWorldX = worldCoordinates.x;
            float mouseWorldY = worldCoordinates.y;

            // Nếu không có slot nào được chọn, hiển thị đếm ngược khi click vào cây
            if (selectedSlotIndex == -1) {
                if (plantManager.getFarmField().isInField(mouseWorldX, mouseWorldY)) {
                    System.out.println("Click detected in farm field at: " + mouseWorldX + ", " + mouseWorldY);
                    boolean showedCountdown = plantManager.showPlantCountdown(mouseWorldX, mouseWorldY);
                    if (showedCountdown) {
                        System.out.println("Hiển thị thông tin thời gian thành công");
                    } else {
                        System.out.println("Không tìm thấy cây tại vị trí này");
                    }
                } else {
                    System.out.println("Click outside farm field at: " + mouseWorldX + ", " + mouseWorldY);
                }
                return;
            }

            if (selectedSlotIndex >= 0 && selectedSlotIndex < inventoryManager.getSlots().size()) {
                InventorySlot slot = inventoryManager.getSlots().get(selectedSlotIndex);
                if (!slot.isEmpty()) {
                    Item item = slot.getItem();
                    // Nếu là hạt giống
                    if (item instanceof Seed) {
                        // Kiểm tra stamina trước khi trồng cây
                        if (statsBar.getStamina() >= PLANTING_STAMINA_COST) {
                            Seed seed = (Seed) item;
                            Plant planted = plantManager.plantSeed(seed, mouseWorldX, mouseWorldY);
                            if (planted != null) {
                                renderManager.add(planted);
                                System.out.println("Planted " + seed.getName() + " at " + mouseWorldX + ", " + mouseWorldY);
                                // Giảm stamina sau khi trồng cây thành công
                                statsBar.decreaseStamina(PLANTING_STAMINA_COST);
                                // Tăng kinh nghiệm
                                statsBar.addExperience(PLANTING_XP_REWARD);
                                inventoryUI.updateUI();
                            } else {
                                System.out.println("Cannot plant here");
                            }
                        } else {
                            System.out.println("Not enough stamina to plant! Needed: " + PLANTING_STAMINA_COST);
                        }
                        // Nếu là Tool
                    } else if (item instanceof com.mygdx.game.items.tools.Tool) {
                        com.mygdx.game.items.tools.Tool tool = (com.mygdx.game.items.tools.Tool) item;
                        // Nếu là WateringCan thì tạo hiệu ứng
                        if (tool instanceof WateringCan) {
                            // Kiểm tra stamina trước khi tưới nước
                            if (statsBar.getStamina() >= WATERING_STAMINA_COST) {
                                WateringCan wc = (WateringCan) tool;
                                WateringEffect effect = wc.useToolWithEffect(plantManager, mouseWorldX, mouseWorldY);
                                if (effect != null) {
                                    wateringEffects.add(effect);
                                    System.out.println("Watered plant at " + mouseWorldX + ", " + mouseWorldY);
                                    // Giảm stamina sau khi tưới nước thành công
                                    statsBar.decreaseStamina(WATERING_STAMINA_COST);
                                    inventoryUI.updateUI();
                                } else {
                                    System.out.println("No plant to water at this position or plant doesn't need water");
                                }
                            } else {
                                System.out.println("Not enough stamina to water! Needed: " + WATERING_STAMINA_COST);
                            }
                        } else {
                            // Kiểm tra stamina trước khi thu hoạch
                            if (statsBar.getStamina() >= HARVESTING_STAMINA_COST) {
                                boolean result = tool.useTool(plantManager, mouseWorldX, mouseWorldY);
                                if (tool.getName().toLowerCase().contains("liềm")) {
                                    if (result) {
                                        System.out.println("Harvested plant at " + mouseWorldX + ", " + mouseWorldY);
                                        // Giảm stamina sau khi thu hoạch thành công
                                        statsBar.decreaseStamina(HARVESTING_STAMINA_COST);
                                        // Tăng kinh nghiệm khi thu hoạch
                                        statsBar.addExperience(HARVESTING_XP_REWARD);
                                        inventoryUI.updateUI();
                                    } else {
                                        System.out.println("No plant to harvest at this position or plant is not ready");
                                    }
                                } else {
                                    System.out.println("Used tool: " + tool.getName());
                                    inventoryUI.updateUI();
                                }
                            } else {
                                System.out.println("Not enough stamina to harvest! Needed: " + HARVESTING_STAMINA_COST);
                            }
                        }
                    } else {
                        System.out.println("Selected item is not usable here");
                    }
                } else {
                    System.out.println("No item in selected slot");
                }
            }
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

        // Dispose sleep system
        if (sleepSystem != null) {
            sleepSystem.dispose();
        }

        // Dispose level up effect
        if (levelUpEffect != null) {
            levelUpEffect.dispose();
        }

        // Dispose static font in Plant if it exists
        if (com.mygdx.game.entities.plants.base.Plant.countdownFont != null) {
            com.mygdx.game.entities.plants.base.Plant.countdownFont.dispose();
        }
    }

    // Phương thức kiểm tra level up để gọi từ bên ngoài (nếu cần)
    public void triggerLevelUpEffect() {
        showLevelUpEffect();
    }

    // Getter methods cho sleep system (nếu cần)
    public SleepSystem getSleepSystem() {
        return sleepSystem;
    }

    public boolean isPlayerSleeping() {
        return sleepSystem != null && sleepSystem.isSleepAnimationPlaying();
    }
}
