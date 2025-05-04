package com.mygdx.game.items.tools;

import com.mygdx.game.entities.plants.PlantManager;

public class Sickle extends Tool {
    public Sickle() {
        super("Liềm", "Dùng để thu hoạch cây trồng", 100, "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Player/Player_Modular/Tools/Iron_Tools/Sickle.png");
    }

    @Override
    public boolean useTool(PlantManager plantManager, float x, float y) {
        return plantManager.harvestPlant(x, y);
    }
}
