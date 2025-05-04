package com.mygdx.game.items.tools;

import com.mygdx.game.items.base.Item;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.items.base.Item;
import com.mygdx.game.entities.plants.PlantManager;

public abstract class Tool extends Item {
    public enum ToolType {
        WATERING_CAN, // Bình tưới
        SCYTHE,       // Liềm
        FISHING_ROD,  // Cần câu
    }

    public Tool(String name, String description, int price, String texturePath) {
        super(name, description, price, ItemType.TOOL, texturePath);
    }

    // Sử dụng tool lên vị trí (x, y) trên ruộng
    public abstract boolean useTool(PlantManager plantManager, float x, float y);
}
