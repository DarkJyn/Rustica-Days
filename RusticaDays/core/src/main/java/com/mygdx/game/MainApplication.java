package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.List;
import com.mygdx.game.entities.plants.base.Plant;
import com.mygdx.game.entities.plants.base.FarmField;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.inventory.InventoryItemState;
import com.mygdx.game.inventory.InventoryManager;
import com.mygdx.game.inventory.InventorySlot;
import com.mygdx.game.items.base.Item;
import com.mygdx.game.items.base.ItemFactory;
import com.mygdx.game.render.RenderManager;

public class MainApplication extends Game {
    private GameWrapper gameWrapper;
    private MainMenuScreen mainMenuScreen;
    private static final String SAVE_FILE_PATH = "game_state.json";
    private static final String DEFAULT_SAVE_PATH = "default_game_state.json";
    private float autoSaveTimer = 0f;
    private static final float AUTO_SAVE_INTERVAL = 60f; // Auto save every minute

    @Override
    public void create() {
        // Khởi tạo main menu screen đầu tiên
        mainMenuScreen = new MainMenuScreen(this);
        setScreen(mainMenuScreen);

        // Add shutdown hook to save game when application is terminated
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application is shutting down, saving game state...");
            saveGame();
        }));
    }

    @Override
    public void render() {
        super.render();

        // Auto save logic
        if (gameWrapper != null) {
            autoSaveTimer += Gdx.graphics.getDeltaTime();
            if (autoSaveTimer >= AUTO_SAVE_INTERVAL) {
                saveGame();
                autoSaveTimer = 0f;
            }
        }
    }

    // Phương thức để chuyển sang game mới
    public void startNewGame() {
        if (gameWrapper != null) {
            gameWrapper.dispose();
        }

        // Load default game state
        File defaultFile = new File(DEFAULT_SAVE_PATH);
        if (defaultFile.exists()) {
            try {
                // Copy default save file to current save file
                String localPath = Gdx.files.getLocalStoragePath() + SAVE_FILE_PATH;
                String workingPath = SAVE_FILE_PATH;

                // Copy to local storage
                try (FileReader reader = new FileReader(defaultFile);
                     FileWriter writer = new FileWriter(localPath)) {
                    char[] buffer = new char[1024];
                    int length;
                    while ((length = reader.read(buffer)) > 0) {
                        writer.write(buffer, 0, length);
                    }
                }

                // Copy to working directory
                try (FileReader reader = new FileReader(defaultFile);
                     FileWriter writer = new FileWriter(workingPath)) {
                    char[] buffer = new char[1024];
                    int length;
                    while ((length = reader.read(buffer)) > 0) {
                        writer.write(buffer, 0, length);
                    }
                }

                // Load the default game state
                gameWrapper = new GameWrapper();
                setScreen(gameWrapper);
                loadGame();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to reset game state: " + e.getMessage());
                // If reset fails, start a completely new game
                gameWrapper = new GameWrapper();
                setScreen(gameWrapper);
                saveGame();
            }
        } else {
            // If no default save file exists, start a completely new game
        gameWrapper = new GameWrapper();
        setScreen(gameWrapper);
            saveGame();
        }
    }

    // Phương thức để quay lại main menu
    public void returnToMainMenu() {
        if (gameWrapper != null) {
            // Lưu game state trước khi tạm dừng
            saveGame();
            // Tạm dừng game thay vì dispose để có thể save
            gameWrapper.pause();
        }
        setScreen(mainMenuScreen);
    }

    // Method to save the current game state
    public void saveGame() {
        if (gameWrapper == null) {
            System.out.println("Cannot save game: gameWrapper is null");
            return;
        }

        System.out.println("Starting to save game state...");
        GameState gameState = new GameState();

        // Populate gameState with current game data
        gameState.setPlayerX(gameWrapper.getGameLaucher().getPlayer().getX());
        gameState.setPlayerY(gameWrapper.getGameLaucher().getPlayer().getY());
        gameState.setPlayerLevel(gameWrapper.getGameLaucher().getStatsBar().getLevel());
        gameState.setPlayerExperience(gameWrapper.getGameLaucher().getStatsBar().getExperience());
        gameState.setPlayerStamina(gameWrapper.getGameLaucher().getStatsBar().getStamina());
        gameState.setPlayerMoney(gameWrapper.getGameLaucher().getStatsBar().getMoney());

        // Save inventory items with their quantities
        System.out.println("Saving inventory items...");
        List<InventoryItemState> inventoryItems = gameWrapper.getGameLaucher().getInventoryManager().getInventoryItems();
        for (InventoryItemState item : inventoryItems) {
            System.out.println("Saving item: " + item.getName() + " with quantity: " + item.getQuantity());
        }
        gameState.setInventoryItems(inventoryItems);
        System.out.println("Finished saving inventory items");

        System.out.println("Player data saved: X=" + gameState.getPlayerX() +
                         ", Y=" + gameState.getPlayerY() +
                         ", Level=" + gameState.getPlayerLevel() +
                         ", Money=" + gameState.getPlayerMoney());

        // Save plant information
        List<PlantState> plantStates = new ArrayList<>();
        FarmField farmField = gameWrapper.getGameLaucher().getPlantManager().getFarmField();
        for (int row = 0; row < FarmField.ROWS; row++) {
            for (int col = 0; col < FarmField.COLS; col++) {
                Plant plant = farmField.getPlantAt(row, col);
                if (plant != null) {
                    PlantState plantState = new PlantState(
                        plant.getClass().getSimpleName(),
                        row,
                        col,
                        plant.getGrowthState(),
                        plant.getGrowthTimer(),
                        plant.getWaterTimer(),
                        plant.isNeedsWater()
                    );
                    plantStates.add(plantState);
                    System.out.println("Plant saved: " + plantState.getType() +
                                     " at [" + row + "," + col + "]" +
                                     " State: " + plantState.getGrowthState());
                }
            }
        }
        gameState.setPlants(plantStates);
        System.out.println("Total plants saved: " + plantStates.size());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            // Ensure the save directory exists
            File saveDir = new File(Gdx.files.getLocalStoragePath());
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // Save to both local storage and working directory
            String localPath = Gdx.files.getLocalStoragePath() + SAVE_FILE_PATH;
            String workingPath = SAVE_FILE_PATH;

            System.out.println("Saving to local storage: " + localPath);
            // Save to local storage
            try (FileWriter writer = new FileWriter(localPath)) {
                gson.toJson(gameState, writer);
                System.out.println("Game state saved successfully to local storage");
            }

            System.out.println("Saving to working directory: " + workingPath);
            // Save to working directory
            try (FileWriter writer = new FileWriter(workingPath)) {
                gson.toJson(gameState, writer);
                System.out.println("Game state saved successfully to working directory");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save game state: " + e.getMessage());
        }
    }

    // Phương thức để tiếp tục game đã lưu
    public void loadGame() {
        // Try to load from local storage first
        String localPath = Gdx.files.getLocalStoragePath() + SAVE_FILE_PATH;
        File saveFile = new File(localPath);

        // If not found in local storage, try working directory
        if (!saveFile.exists()) {
            saveFile = new File(SAVE_FILE_PATH);
        }

        if (!saveFile.exists()) {
            // Nếu không có file save, bắt đầu game mới
            startNewGame();
            return;
        }

        // Khởi tạo gameWrapper trước
        if (gameWrapper != null) {
            gameWrapper.dispose();
        }
        gameWrapper = new GameWrapper();
            setScreen(gameWrapper);

        Gson gson = new Gson();
        try (FileReader reader = new FileReader(saveFile)) {
            GameState gameState = gson.fromJson(reader, GameState.class);
            if (gameState != null) {
                // Apply loaded game state to the game
                gameWrapper.getGameLaucher().getPlayer().setX(370); // Default X position
                gameWrapper.getGameLaucher().getPlayer().setY(400); // Default Y position
                gameWrapper.getGameLaucher().getStatsBar().setLevel(gameState.getPlayerLevel());
                gameWrapper.getGameLaucher().getStatsBar().setExperience(gameState.getPlayerExperience());
                gameWrapper.getGameLaucher().getStatsBar().setStamina(gameState.getPlayerStamina());
                gameWrapper.getGameLaucher().getStatsBar().setMoney(gameState.getPlayerMoney());

                // Load inventory items with their quantities
                if (gameState.getInventoryItems() != null) {
                    System.out.println("Loading inventory items...");
                    InventoryManager inventoryManager = gameWrapper.getGameLaucher().getInventoryManager();
                    // Clear existing inventory first
                    for (int i = 0; i < inventoryManager.getSlots().size(); i++) {
                        inventoryManager.getSlots().get(i).clear();
                    }
                    // Add items with their quantities
                    for (InventoryItemState itemState : gameState.getInventoryItems()) {
                        System.out.println("Loading item: " + itemState.getName() + " with quantity: " + itemState.getQuantity());
                        Item item = ItemFactory.createItemByName(itemState.getName());
                        if (item != null) {
                            inventoryManager.addItem(item, itemState.getQuantity());
                            System.out.println("Successfully added item: " + item.getName() + " with quantity: " + itemState.getQuantity());
                        } else {
                            System.out.println("Failed to create item: " + itemState.getName());
                        }
                    }
                    // Update inventory UI
                    gameWrapper.getGameLaucher().getInventoryUI().updateUI();
                    System.out.println("Finished loading inventory items");
                } else {
                    System.out.println("No inventory items to load");
                }

                // Load plant information
                if (gameState.getPlants() != null) {
                    FarmField farmField = gameWrapper.getGameLaucher().getPlantManager().getFarmField();
                    RenderManager renderManager = gameWrapper.getGameLaucher().getRenderManager();

                    // Clear existing plants
                    for (int row = 0; row < FarmField.ROWS; row++) {
                        for (int col = 0; col < FarmField.COLS; col++) {
                            Plant existingPlant = farmField.getPlantAt(row, col);
                            if (existingPlant != null) {
                                renderManager.remove(existingPlant);
                                farmField.removePlantAt(row, col);
                            }
                        }
                    }

                    // Add loaded plants
                    for (PlantState plantState : gameState.getPlants()) {
                        Plant plant = createPlantFromState(plantState);
                        if (plant != null) {
                            farmField.setPlantAt(plantState.getRow(), plantState.getCol(), plant);
                            renderManager.add(plant); // Add to render manager
                        }
                    }
                }

                System.out.println("Game state loaded successfully from: " + saveFile.getAbsolutePath());
            } else {
                // Nếu gameState là null, bắt đầu game mới
                startNewGame();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load game state: " + e.getMessage());
            // Nếu có lỗi khi đọc file, bắt đầu game mới
            startNewGame();
        }
    }

    // Phương thức để reset game về trạng thái mặc định
    public void resetGame() {
        // Load default game state
        File defaultFile = new File(DEFAULT_SAVE_PATH);
        if (defaultFile.exists()) {
            try {
                // Copy default save file to current save file
                String localPath = Gdx.files.getLocalStoragePath() + SAVE_FILE_PATH;
                String workingPath = SAVE_FILE_PATH;

                // Copy to local storage
                try (FileReader reader = new FileReader(defaultFile);
                     FileWriter writer = new FileWriter(localPath)) {
                    char[] buffer = new char[1024];
                    int length;
                    while ((length = reader.read(buffer)) > 0) {
                        writer.write(buffer, 0, length);
                    }
                }

                // Copy to working directory
                try (FileReader reader = new FileReader(defaultFile);
                     FileWriter writer = new FileWriter(workingPath)) {
                    char[] buffer = new char[1024];
                    int length;
                    while ((length = reader.read(buffer)) > 0) {
                        writer.write(buffer, 0, length);
                    }
                }

                // Load the reset game state
                loadGame();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to reset game state: " + e.getMessage());
            }
        } else {
            // If no default save file exists, start a new game
            startNewGame();
        }
    }

    private Plant createPlantFromState(PlantState plantState) {
        try {
            String className = "com.mygdx.game.entities.plants.types." + plantState.getType();
            Class<?> plantClass = Class.forName(className);

            // Lấy vị trí trung tâm của ô đất
            Vector2 cellCenter = gameWrapper.getGameLaucher().getPlantManager()
                .getFarmField().getCellCenter(plantState.getRow(), plantState.getCol());

            // Tạo cây với vị trí và kích thước phù hợp
            Plant plant = (Plant) plantClass.getDeclaredConstructor(float.class, float.class, float.class, float.class)
                .newInstance(cellCenter.x - 8, cellCenter.y - 8, 16, 16);

            // Thiết lập trạng thái của cây
            plant.setGrowthState(plantState.getGrowthState());
            plant.setGrowthTimer(plantState.getGrowthTimer());
            plant.setWaterTimer(plantState.getWaterTimer());
            plant.setNeedsWater(plantState.isNeedsWater());

            return plant;
        } catch (Exception e) {
            System.err.println("Failed to create plant from state: " + e.getMessage());
            e.printStackTrace(); // In ra stack trace để debug
            return null;
        }
    }

    // Kiểm tra xem có game đã lưu không
    public boolean hasSavedGame() {
        String localPath = Gdx.files.getLocalStoragePath() + SAVE_FILE_PATH;
        File localFile = new File(localPath);
        File workingFile = new File(SAVE_FILE_PATH);
        return localFile.exists() || workingFile.exists();
    }

    @Override
    public void dispose() {
        // Lưu game state trước khi thoát
        saveGame();
        super.dispose();
        if (gameWrapper != null) {
            gameWrapper.dispose();
        }
        if (mainMenuScreen != null) {
            mainMenuScreen.dispose();
        }
    }
}
