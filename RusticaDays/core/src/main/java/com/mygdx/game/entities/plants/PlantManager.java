package com.mygdx.game.entities.plants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.plants.base.Plant;
import com.mygdx.game.entities.plants.states.GrowthState;
import com.mygdx.game.entities.plants.types.*;
import com.mygdx.game.inventory.InventoryManager;
import com.mygdx.game.items.base.Item;
import com.mygdx.game.items.crops.Harvest;
import com.mygdx.game.items.seeds.Seed;
import com.mygdx.game.entities.plants.base.FarmField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Quản lý tất cả các cây trồng trong game
 */
public class PlantManager {
    private List<Plant> plants;
    private InventoryManager inventoryManager;
    private FarmField farmField;

    public PlantManager(InventoryManager inventoryManager) {
        this.plants = new ArrayList<>();
        this.inventoryManager = inventoryManager;
        this.farmField = new FarmField();
    }

    /**
     * Cập nhật tất cả cây trồng
     */
    public void update(float deltaTime) {
        for (int row = 0; row < FarmField.ROWS; row++) {
            for (int col = 0; col < FarmField.COLS; col++) {
                Plant plant = farmField.getPlantAt(row, col);
                if (plant != null) {
                    plant.updateCountdown(deltaTime);
                    plant.update(deltaTime);
                }
            }
        }
    }

    /**
     * Vẽ tất cả cây trồng
     */
    public void render(SpriteBatch batch) {
        // Đầu tiên vẽ tất cả cây trồng
        farmField.render(batch);

        // Sau đó vẽ thông tin đếm ngược lên trên cây
        farmField.renderCountdowns(batch);
    }

    /**
     * Trồng cây từ hạt giống
     * @param seed Hạt giống
     * @param x Tọa độ X
     * @param y Tọa độ Y
     * @return true nếu trồng thành công
     */
    public Plant plantSeed(Seed seed, float x, float y) {
        if (!farmField.isInField(x, y)) {
            return null;
        }
        int[] cell = farmField.getCellIndex(x, y);
        if (cell == null) return null;
        int row = cell[0];
        int col = cell[1];
        if (farmField.getPlantAt(row, col) != null) {
            System.out.println("Outside");
            return null;
        }
        // Tính vị trí căn giữa cây trong ô đất
        float cellWidth = farmField.getCellWidth();
        float cellHeight = farmField.getCellHeight();
        float width = 16f;
        float height = 16f;
        float xPos = FarmField.FIELD_LEFT + col * cellWidth + (cellWidth - width) / 2f;
        float yPos = FarmField.FIELD_BOTTOM + row * cellHeight + (cellHeight - height) / 2f;
        String seedName = seed.getName().toLowerCase();
        Plant plant = null;
        if (seedName.contains("tomato")) {
            plant = new Tomato(xPos, yPos, width, height);
        } else if (seedName.contains("carrot")) {
            plant = new Carrot(xPos, yPos, width, height);
        } else if (seedName.contains("strawberry")) {
            plant = new Corn(xPos, yPos, width, height);
        } else if (seedName.contains("rice")) {
            plant = new Rice(xPos, yPos, width, height);
        } else if (seedName.contains("cabbage")) {
            plant = new Eggplant(xPos, yPos, width, height);
        } else if(seedName.contains("pumpkin")) {
            plant = new Pumpkin(xPos, yPos, width, height + 4);
        } else if (seedName.contains("radish")){
            plant = new Radish(xPos, yPos, width, height);
        } else if (seedName.contains("garlic")) {
            plant = new Garlic(xPos, yPos, width, height + 2);
        }
        if (plant != null) {
            if (farmField.plantAt(row, col, plant)) {
                inventoryManager.removeItemQuantity(seed, 1);
                return plant;
            }
        }
        return null;
    }

    /**
     * Tưới nước cho cây ở vị trí x, y
     */
    public boolean waterPlant(float x, float y) {
        int[] cell = farmField.getCellIndex(x, y);
        if (cell == null) return false;
        Plant plant = farmField.getPlantAt(cell[0], cell[1]);
        if (plant != null && plant.needsWater()) {
            return plant.water();
        }
        return false;
    }

    /**
     * Thu hoạch cây ở vị trí x, y
     */
    public boolean harvestPlant(float x, float y) {
        int[] cell = farmField.getCellIndex(x, y);
        if (cell == null) return false;
        Plant plant = farmField.getPlantAt(cell[0], cell[1]);
        if (plant != null && plant.getGrowthState() == GrowthState.MATURE) {
            Harvest harvest = plant.harvest();
            if (harvest != null) {
                inventoryManager.addItem(harvest, 1);
                // Xóa cây đã thu hoạch khỏi ruộng
                farmField.removePlantAt(cell[0], cell[1]);
                return true;
            }
        }
        return false;
    }

    /**
     * Hiển thị đếm ngược trên cây ở vị trí x, y
     */
    public boolean showPlantCountdown(float x, float y) {
        System.out.println("Attempting to show countdown at: " + x + ", " + y);
        int[] cell = farmField.getCellIndex(x, y);
        if (cell == null) {
            System.out.println("Failed to get cell index for position");
            return false;
        }
        System.out.println("Found cell at row: " + cell[0] + ", col: " + cell[1]);
        Plant plant = farmField.getPlantAt(cell[0], cell[1]);
        if (plant != null) {
            System.out.println("Found plant in state: " + plant.getGrowthState() + ", needs water: " + plant.needsWater());
            plant.toggleCountdownDisplay();
            return true;
        } else {
            System.out.println("No plant found at this cell");
            return false;
        }
    }

    /**
     * Xóa cây đã thu hoạch khỏi ruộng
     */
    public void cleanupHarvestedPlants() {
        for (int row = 0; row < FarmField.ROWS; row++) {
            for (int col = 0; col < FarmField.COLS; col++) {
                Plant plant = farmField.getPlantAt(row, col);
                if (plant != null && plant.getGrowthState() == GrowthState.HARVESTED) {
                    farmField.removePlantAt(row, col);
                }
            }
        }
    }

    /**
     * Kiểm tra xem vị trí x, y đã có cây chưa
     */
    private boolean isPositionOccupied(float x, float y) {
        int[] cell = farmField.getCellIndex(x, y);
        if (cell == null) return false;
        return farmField.getPlantAt(cell[0], cell[1]) != null;
    }

    /**
     * Lấy cây ở vị trí x, y
     */
    private Plant getPlantAt(float x, float y) {
        int[] cell = farmField.getCellIndex(x, y);
        if (cell == null) return null;
        return farmField.getPlantAt(cell[0], cell[1]);
    }

    /**
     * Lấy danh sách cây (flatten từ FarmField)
     */
    public List<Plant> getPlants() {
        List<Plant> allPlants = new ArrayList<>();
        for (int row = 0; row < FarmField.ROWS; row++) {
            for (int col = 0; col < FarmField.COLS; col++) {
                Plant plant = farmField.getPlantAt(row, col);
                if (plant != null) {
                    allPlants.add(plant);
                }
            }
        }
        return allPlants;
    }

    public FarmField getFarmField() {
        return farmField;
    }
}
