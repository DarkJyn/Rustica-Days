package com.mygdx.game.items.tools;

import com.mygdx.game.entities.plants.PlantManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import java.util.List;
import com.mygdx.game.entities.animations.WateringEffect;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.plants.base.FarmField;

public class WateringCan extends Tool {
    private static Animation<TextureRegion> wateringAnimation;
    private static Texture wateringSheet;
    static {
        wateringSheet = new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Player/Player_Modular/Tools/Iron_Tools/Watering.png"));
        TextureRegion[][] tmp = TextureRegion.split(wateringSheet, wateringSheet.getWidth() / 6, wateringSheet.getHeight());
        TextureRegion[] frames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) frames[i] = tmp[0][i];
        wateringAnimation = new Animation<>(0.22f, frames);
        wateringAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public WateringCan() {
        super("Bình tưới nước", "Dùng để tưới cây", 50, "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Player/Player_Modular/Tools/Iron_Tools/WateringCan.png");
    }

    // Trả về hiệu ứng nếu tưới thành công, ngược lại trả về null
    public WateringEffect useToolWithEffect(PlantManager plantManager, float x, float y) {
        int[] cell = plantManager.getFarmField().getCellIndex(x, y);
        boolean result = plantManager.waterPlant(x, y);
        if (result && cell != null) {
            Vector2 center = plantManager.getFarmField().getCellCenter(cell[0], cell[1]);
            int frameWidth = wateringSheet.getWidth() / 6;
            int frameHeight = wateringSheet.getHeight();
            float effectX = center.x - frameWidth / 2f;
            float effectY = center.y - frameHeight / 2f + 8;
            return new WateringEffect(effectX, effectY, wateringAnimation);
        }
        return null;
    }

    @Override
    public boolean useTool(PlantManager plantManager, float x, float y) {
        return plantManager.waterPlant(x, y);
    }

    // Cho phép truy cập animation từ ngoài nếu cần
    public static Animation<TextureRegion> getWateringAnimation() {
        return wateringAnimation;
    }
}
