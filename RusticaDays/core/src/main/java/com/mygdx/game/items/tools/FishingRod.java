package com.mygdx.game.items.tools;

import com.mygdx.game.entities.plants.PlantManager;

public class FishingRod extends Tool {
    public FishingRod() {
        super("Cần câu", "Dùng để câu cá", 100, "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Player/Player_Modular/Tools/Iron_Tools/FishingRod.png");
    }

    @Override
    public boolean useTool(PlantManager plantManager, float x, float y) {
        return plantManager.harvestPlant(x, y);
    }
}
