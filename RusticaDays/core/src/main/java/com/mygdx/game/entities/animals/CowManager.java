package com.mygdx.game.entities.animals;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CowManager {
    private List<Cow> cows;
    private Random random;
    private static final float MIN_DISTANCE = 30f; // Khoảng cách tối thiểu giữa các con bò (lớn hơn gà vì bò to hơn)

    public CowManager() {
        cows = new ArrayList<>();
        random = new Random();
        initializeCows();
    }

    private void initializeCows() {
        // Initialize 5 cows at different positions within the barn
        // Tọa độ được điều chỉnh để phù hợp với map
        float[][] initialPositions = {
            {170, 100},  // Position 1 - Khu vực chuồng bò phải
            {190, 150},  // Position 2 - Khu vực chuồng bò phải
            {100, 120},  // Position 3 - Khu vực chuồng bò trái
            {130, 90},   // Position 4 - Khu vực chuồng bò trái
            {80, 140}    // Position 5 - Khu vực chuồng bò trái
        };

        for (float[] pos : initialPositions) {
            cows.add(new Cow(pos[0], pos[1]));
        }
    }

    public void update(float delta) {
        // Cập nhật vị trí và kiểm tra va chạm
        for (int i = 0; i < cows.size(); i++) {
            Cow cow1 = cows.get(i);
            Vector2 pos1 = cow1.getPosition();

            // Kiểm tra va chạm với các con bò khác
            for (int j = i + 1; j < cows.size(); j++) {
                Cow cow2 = cows.get(j);
                Vector2 pos2 = cow2.getPosition();

                float distance = pos1.dst(pos2);
                if (distance < MIN_DISTANCE) {
                    // Tính vector đẩy
                    Vector2 pushDirection = new Vector2(pos1).sub(pos2).nor();
                    float pushDistance = (MIN_DISTANCE - distance) / 2;

                    // Đẩy cả hai con bò ra xa nhau
                    cow1.setPosition(pos1.x + pushDirection.x * pushDistance,
                                   pos1.y + pushDirection.y * pushDistance);
                    cow2.setPosition(pos2.x - pushDirection.x * pushDistance,
                                   pos2.y - pushDirection.y * pushDistance);
                }
            }

            cow1.update(delta);
        }
    }

    public void render(SpriteBatch batch) {
        // Lưu lại projection matrix hiện tại
        batch.setProjectionMatrix(batch.getProjectionMatrix());

        for (Cow cow : cows) {
            cow.render(batch);
        }
    }

    public void dispose() {
        for (Cow cow : cows) {
            cow.dispose();
        }
    }
}
