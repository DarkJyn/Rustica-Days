package com.mygdx.game.entities.plants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.plants.base.Plant;
import com.mygdx.game.entities.plants.states.GrowthState;
import com.mygdx.game.entities.plants.types.Carrot;
import com.mygdx.game.entities.plants.types.Corn;
import com.mygdx.game.entities.plants.types.Eggplant;
import com.mygdx.game.entities.plants.types.Rice;
import com.mygdx.game.entities.plants.types.Tomato;
import com.mygdx.game.inventory.InventoryManager;
import com.mygdx.game.items.base.Item;
import com.mygdx.game.items.crops.Harvest;
import com.mygdx.game.items.seeds.Seed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Quản lý tất cả các cây trồng trong game
 */
public class PlantManager {
    private List<Plant> plants;
    private InventoryManager inventoryManager;

    public PlantManager(InventoryManager inventoryManager) {
        this.plants = new ArrayList<>();
        this.inventoryManager = inventoryManager;
    }

    /**
     * Cập nhật tất cả cây trồng
     */
    public void update(float deltaTime) {
        for (Plant plant : plants) {
            plant.update(deltaTime);
        }
    }

    /**
     * Vẽ tất cả cây trồng
     */
    public void render(SpriteBatch batch) {
        for (Plant plant : plants) {
            plant.render(batch);
        }
    }

    /**
     * Trồng cây từ hạt giống
     * @param seed Hạt giống
     * @param x Tọa độ X
     * @param y Tọa độ Y
     * @return true nếu trồng thành công
     */
    public boolean plantSeed(Seed seed, float x, float y) {
        // Kiểm tra xem vị trí này đã có cây chưa
        if (isPositionOccupied(x, y)) {
            return false;
        }

        // Kiểm tra loại hạt giống và tạo cây tương ứng
        String seedName = seed.getName().toLowerCase();
        Plant plant = null;

        // Tạo plant tương ứng với loại hạt
        if (seedName.contains("cà chua")) {
            plant = new Tomato(x, y, 32, 32);
        } else if (seedName.contains("cà rốt")) {
            plant = new Carrot(x, y, 32, 32);
        } else if (seedName.contains("ngô")) {
            plant = new Corn(x, y, 32, 32);
        } else if (seedName.contains("lúa")) {
            plant = new Rice(x, y, 32, 32);
        } else if (seedName.contains("cà tím")) {
            plant = new Eggplant(x, y, 32, 32);
        }

        if (plant != null) {
            plants.add(plant);

            // Giảm số lượng hạt giống trong túi
            inventoryManager.removeItemQuantity(seed, 1);
            return true;
        }

        return false;
    }

    /**
     * Tưới nước cho cây ở vị trí x, y
     */
    public boolean waterPlant(float x, float y) {
        Plant plant = getPlantAt(x, y);
        if (plant != null && plant.needsWater()) {
            return plant.water();
        }
        return false;
    }

    /**
     * Thu hoạch cây ở vị trí x, y
     */
    public boolean harvestPlant(float x, float y) {
        Plant plant = getPlantAt(x, y);
        if (plant != null && plant.getGrowthState() == GrowthState.MATURE) {
            Harvest harvest = plant.harvest();
            if (harvest != null) {
                // Thêm sản phẩm vào túi đồ
                inventoryManager.addItem(harvest, 1);

                // Xóa cây đã thu hoạch khỏi danh sách
                // plants.remove(plant);

                return true;
            }
        }
        return false;
    }

    /**
     * Xóa cây đã thu hoạch khỏi danh sách
     */
    public void cleanupHarvestedPlants() {
        Iterator<Plant> iterator = plants.iterator();
        while (iterator.hasNext()) {
            Plant plant = iterator.next();
            if (plant.getGrowthState() == GrowthState.HARVESTED) {
                iterator.remove();
            }
        }
    }

    /**
     * Kiểm tra xem vị trí x, y đã có cây chưa
     */
    private boolean isPositionOccupied(float x, float y) {
        return getPlantAt(x, y) != null;
    }

    /**
     * Lấy cây ở vị trí x, y
     */
    private Plant getPlantAt(float x, float y) {
        Vector2 position = new Vector2(x, y);
        for (Plant plant : plants) {
            if (plant.isPositionOver(position)) {
                return plant;
            }
        }
        return null;
    }

    /**
     * Lấy danh sách cây
     */
    public List<Plant> getPlants() {
        return plants;
    }
}
