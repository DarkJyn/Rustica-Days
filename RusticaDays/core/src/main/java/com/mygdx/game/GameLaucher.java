package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
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
import java.util.Random;

public class GameLaucher extends Game {
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
    private Texture fButton;
    private Texture spaceButton;
    private float stateTime;
    private TextureRegion currentFrame;
    private Animation<TextureRegion> fButtonAnimation;
    private TextureRegion currentSpaceFrame;
    private Animation<TextureRegion> spaceButtonAnimation;
    private RenderManager renderManager;
    private BitmapFont font;

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
    private static final float FISHING_STAMINA_COST = 10f;

    // Experience rewards
    private static final int PLANTING_XP_REWARD = 5;
    private static final int FISHING_XP_REWARD = 10;
    private static final int HARVESTING_XP_REWARD = 15;

    // Sleep system constants
    private static final float SLEEP_STAMINA_RESTORE = 100f; // Phục hồi toàn bộ stamina khi ngủ
    private static final float SLEEP_XP_REWARD = 0.1f; // XP nhận được khi ngủ

    // Thêm biến trạng thái câu cá
    private boolean isFishing = false;
    private float fishingTimer = 0f;
    private int fishingPhase = 0; // 0: cast, 1: wait, 2: hook, 3: pull
    private float fishingWaitDuration = 0f; // Thời gian ngồi câu random
    private Random fishingRandom = new Random();

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

        // Khởi tạo font
        initFont();

        // Khởi tạo Inventory
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

        // Khởi tạo Player
        player = new Player(370, 400, "Player.png", inventoryManager);

        // Khởi tạo StatsBar
        statsBar = new StatsBar(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        statsBar.setMoney(10);
        statsBar.setExperience(0);
        statsBar.setStamina(100);

        // Khởi tạo Shopkeeper
        shopkeeper = new NPC(345, 460, "NPC.png", camera, inventoryManager, inventoryUI, statsBar);
        shopkeeper.setStage(uiStage);

        // Khởi tạo Input Handler
        inputHandler = new PlayerInputHandler(player, mapRenderer.getMap());
        inputHandler.registerNPC(shopkeeper);

        // Khởi tạo PlantManager
        plantManager = new PlantManager(inventoryManager);

        // Khởi tạo SleepSystem
        sleepSystem = new SleepSystem(bedPosition, BED_WIDTH, BED_HEIGHT, player, camera);

        // Khởi tạo RenderManager
        renderManager = new RenderManager();
        renderManager.add(player);

        // Khởi tạo Level Up Effect
        levelUpEffect = new LevelUpEffect();
        statsBar.setLevelUpListener(new StatsBar.LevelUpListener() {
            @Override
            public void onLevelUp(int newLevel) {
                showLevelUpEffect();
            }
        });

        // Khởi tạo các animation
        fButton = new Texture("FbuttonAni.png");
        spaceButton = new Texture("SpaceAni.png");
        createAnimations();
        stateTime = 0f;
        currentFrame = fButtonAnimation.getKeyFrame(0);
        currentSpaceFrame = spaceButtonAnimation.getKeyFrame(0);

        // Thêm vật phẩm và tiền khởi đầu cho game mới
        addInitialItems();
    }

    // Phương thức hiển thị hiệu ứng Level Up
    private void showLevelUpEffect() {
        // Khởi động hiệu ứng level up ở giữa màn hình
        levelUpEffect.start(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        System.out.println("Level Up! Current level: " + statsBar.getLevel());
    }

    private void addInitialItems() {
        // Tạo các mặt hàng hạt giống sử dụng constructor mặc định
        RiceSeed riceSeed = new RiceSeed();

        // Thêm hạt giống vào inventory
        inventoryManager.addItem(riceSeed, 3);

        // Thêm tool vào inventory
        com.mygdx.game.items.tools.WateringCan wateringCan = new com.mygdx.game.items.tools.WateringCan();
        com.mygdx.game.items.tools.Sickle sickle = new com.mygdx.game.items.tools.Sickle();
        com.mygdx.game.items.tools.FishingRod fishingrod = new com.mygdx.game.items.tools.FishingRod();

        inventoryManager.addItem(wateringCan, 1);
        inventoryManager.addItem(sickle, 1);
        inventoryManager.addItem(fishingrod, 1);

        // Cập nhật UI sau khi thêm vật phẩm
        inventoryUI.updateUI();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = fButtonAnimation.getKeyFrame(stateTime * 2, true);
        currentSpaceFrame = spaceButtonAnimation.getKeyFrame(stateTime * 2, true);
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

        // Xác định vùng nước cho phép câu cá
        boolean inFishingZone = false;
        float px = player.getX();
        float py = player.getY();
        if (px >= 475.75125 && px <= 501.8265 && py >= 131.38489 && py <= 157.67522) {
            inFishingZone = true;
        }


        // Kiểm tra chọn cần câu
        boolean holdingFishingRod = false;
        if (selectedSlotIndex >= 0 && selectedSlotIndex < inventoryManager.getSlots().size()) {
            InventorySlot slot = inventoryManager.getSlots().get(selectedSlotIndex);
            if (!slot.isEmpty() && slot.getItem() instanceof com.mygdx.game.items.tools.FishingRod) {
                holdingFishingRod = true;
            }
        }
        // Xử lý hiệu ứng hoạt ảnh câu cá (lặp lại)
        if (isFishing) {
            boolean shouldStopFishing = false;
            String stopReason = "";

            // Kết thúc câu cá nếu không còn chọn cần câu
            if (!holdingFishingRod) {
                shouldStopFishing = true;
                stopReason = "bỏ chọn cần câu";
            }

            // Kết thúc câu cá nếu ra khỏi vùng câu cá
            if (!inFishingZone) {
                shouldStopFishing = true;
                stopReason = "ra khỏi vùng nước";
            }

            // Kiểm tra xem người chơi có đang di chuyển không
            boolean isMoving = Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
                Gdx.input.isKeyPressed(Input.Keys.UP) ||
                Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
                Gdx.input.isKeyPressed(Input.Keys.A) ||
                Gdx.input.isKeyPressed(Input.Keys.D) ||
                Gdx.input.isKeyPressed(Input.Keys.W) ||
                Gdx.input.isKeyPressed(Input.Keys.S);

            if (isMoving) {
                shouldStopFishing = true;
                stopReason = "di chuyển";
            }

            // Áp dụng kết thúc câu cá nếu cần
            if (shouldStopFishing || (isFishing && Gdx.input.isKeyPressed(Input.Keys.SPACE))){
                isFishing = false;
                player.standStill();
                System.out.println("Kết thúc câu cá do " + stopReason);
            } else {
                // Tiếp tục câu cá khi không di chuyển
                fishingTimer += delta;
                if (fishingPhase == 0) { // FISH_CAST
                    player.setState(com.mygdx.game.entities.animations.PlayerAnimationManager.PlayerState.FISH_CAST);
                    if (fishingTimer > 2.0f) {
                        fishingPhase = 1;
                        fishingTimer = 0f;
                        // Reset animation timer khi đổi phase
                        player.getAnimationManager().resetStateTime();
                        // Random thời gian ngồi câu (7-17s)
                        fishingWaitDuration = 7f + fishingRandom.nextFloat() * 10f;
                    }
                } else if (fishingPhase == 1) { // FISH_WAIT
                    player.setState(com.mygdx.game.entities.animations.PlayerAnimationManager.PlayerState.FISH_WAIT);
                    if (fishingTimer > fishingWaitDuration) {
                        fishingPhase = 2;
                        fishingTimer = 0f;
                        // Reset animation timer khi đổi phase
                        player.getAnimationManager().resetStateTime();
                    }
                } else if (fishingPhase == 2) { // FISH_HOOK
                    player.setState(com.mygdx.game.entities.animations.PlayerAnimationManager.PlayerState.FISH_HOOK);
                    if (fishingTimer > 2.0f) {
                        fishingPhase = 3;
                        fishingTimer = 0f;
                        // Reset animation timer khi đổi phase
                        player.getAnimationManager().resetStateTime();
                    }
                } else if (fishingPhase == 3) { // FISH_PULL
                    player.setState(com.mygdx.game.entities.animations.PlayerAnimationManager.PlayerState.FISH_PULL);
                    if (fishingTimer > 2.0f) {
                        fishingPhase = 0;
                        fishingTimer = 0f;
                        // Reset animation timer khi đổi phase
                        player.getAnimationManager().resetStateTime();
                        // Sau khi kéo cần xong, random cá
                        com.mygdx.game.entities.animals.FishType fish = player.tryCatchFish();
                        if (fish != null) {
                            statsBar.addExperience(FISHING_XP_REWARD);
                            player.showNotification(com.mygdx.game.items.animalproducts.FishItem.getFishName(fish));
                            inventoryUI.updateUI();
                        } else {
                            player.showNotification("Nothing!");
                        }
                    }
                }
            }
        } else {
            // Kiểm tra xem có thể bắt đầu câu cá tự động khi dfừng lại không
            boolean playerStopped = !Gdx.input.isKeyPressed(Input.Keys.LEFT) &&
                !Gdx.input.isKeyPressed(Input.Keys.RIGHT) &&
                !Gdx.input.isKeyPressed(Input.Keys.UP) &&
                !Gdx.input.isKeyPressed(Input.Keys.DOWN) &&
                !Gdx.input.isKeyPressed(Input.Keys.A) &&
                !Gdx.input.isKeyPressed(Input.Keys.D) &&
                !Gdx.input.isKeyPressed(Input.Keys.W) &&
                !Gdx.input.isKeyPressed(Input.Keys.S);

            // Bắt đầu câu cá tự động khi dừng lại trong vùng câu cá và đang cầm cần câu
            if (playerStopped && inFishingZone && holdingFishingRod && !isFishing && Gdx.input.isKeyPressed(Input.Keys.F)){
                if( statsBar.getStamina() >= FISHING_STAMINA_COST){
                    statsBar.decreaseStamina(FISHING_STAMINA_COST);
                    isFishing = true;
                    fishingTimer = 0f;
                    fishingPhase = 0;
                    fishingWaitDuration = 0f;
                    player.getAnimationManager().resetStateTime();
                    System.out.println("Tự động bắt đầu câu cá khi dừng lại trong vùng nước");
                }
                else{
                    System.out.println("Not enough stamina to fishing");
                }
            }

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

        // Render đếm ngược cho cây trồng
        batch.begin();
        batch.setProjectionMatrix(camera.getCamera().combined);
        plantManager.getFarmField().renderCountdowns(batch);
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
        if ((Gdx.input.isKeyJustPressed(Input.Keys.TAB) || Gdx.input.isKeyJustPressed(Input.Keys.I)) && !sleepSystem.shouldBlockPlayerMovement()) {
            inventoryUI.toggleInventory();
        }

        // Xử lý tương tác với cây trồng
        float playerX = player.getX();
        float playerY = player.getY();
        if (playerX > 452.6428 && playerX < 555.06213 && playerY > 304.81705 && playerY < 435.53748) {
            handleFarmingInput();
        }
        if(inFishingZone && !isFishing){
            batch.setProjectionMatrix(camera.getCamera().combined);
            batch.begin();
            batch.draw(currentFrame,player.getPosition().x - 4, player.getPosition().y + 24, 18, 18);
            batch.end();
        }

        if(isFishing){
//            batch.setProjectionMatrix(camera.getCamera().combined);
            batch.begin();
            font.draw(batch, "Press       to stop fishing", player.getPosition().x - 80,  player.getPosition().y + 20);
            batch.draw(currentSpaceFrame,player.getPosition().x + 20 , player.getPosition().y - 5, 144, 48);
            batch.end();
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

    private void initFont() {
        // Tạo generator từ file Pixellari.ttf
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/PressStart2P.ttf"));

        // Thiết lập tham số font
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 20; // Kích thước font
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;

        // Tạo font từ generator và parameter
        font = generator.generateFont(parameter);
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
    private void createAnimations() {
        TextureRegion[][] tmp = TextureRegion.split(
            fButton,
            fButton.getWidth() / 2,
            fButton.getHeight());

        TextureRegion[] fButtonFrames = new TextureRegion[2];

        for (int i = 0; i < 2; i++) {
            fButtonFrames[i] = tmp[0][i];
        }

        fButtonAnimation = new Animation<>(2, fButtonFrames);
        TextureRegion[][] tmp1 = TextureRegion.split(
            spaceButton,
            spaceButton.getWidth() / 2,
            spaceButton.getHeight());

        TextureRegion[] spaceButtonFrames = new TextureRegion[2];

        for (int i = 0; i < 2; i++) {
            spaceButtonFrames[i] = tmp1[0][i];
        }

        spaceButtonAnimation = new Animation<>(2, spaceButtonFrames);
    }

    public Player getPlayer() {
        return player;
    }

    public StatsBar getStatsBar() {
        return statsBar;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public InventoryUI getInventoryUI() {
        return inventoryUI;
    }

    /**
     * Lấy PlantManager để có thể truy cập và thao tác với cây trồng
     */
    public PlantManager getPlantManager() {
        return plantManager;
    }

    public RenderManager getRenderManager() {
        return renderManager;
    }
}
