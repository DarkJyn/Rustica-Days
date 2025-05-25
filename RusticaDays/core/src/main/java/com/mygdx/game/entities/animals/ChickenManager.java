package com.mygdx.game.entities.animals;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class ChickenManager {
    private List<Chicken> chickens;
    private static final float MIN_DISTANCE = 10f; // Khoảng cách tối thiểu giữa các con gà

    public ChickenManager() {
        chickens = new ArrayList<>();
        initializeChickens();
    }

    private void initializeChickens() {
        // Khởi tạo 5 con gà ở các vị trí khác nhau trong chuồng
        chickens.add(new Chicken(185, 390)); // Góc trái dưới
        chickens.add(new Chicken(195, 395)); // Giữa trái
        chickens.add(new Chicken(205, 400)); // Giữa phải
        chickens.add(new Chicken(215, 405)); // Góc phải trên
        chickens.add(new Chicken(200, 410)); // Góc trái trên
    }

    public void update(float delta) {
        // Cập nhật vị trí và kiểm tra va chạm
        for (int i = 0; i < chickens.size(); i++) {
            Chicken chicken1 = chickens.get(i);
            Vector2 pos1 = chicken1.getPosition();

            // Kiểm tra va chạm với các con gà khác
            for (int j = i + 1; j < chickens.size(); j++) {
                Chicken chicken2 = chickens.get(j);
                Vector2 pos2 = chicken2.getPosition();

                float distance = pos1.dst(pos2);
                if (distance < MIN_DISTANCE) {
                    // Tính vector đẩy
                    Vector2 pushDirection = new Vector2(pos1).sub(pos2).nor();
                    float pushDistance = (MIN_DISTANCE - distance) / 2;

                    // Đẩy cả hai con gà ra xa nhau
                    chicken1.setPosition(pos1.x + pushDirection.x * pushDistance,
                                      pos1.y + pushDirection.y * pushDistance);
                    chicken2.setPosition(pos2.x - pushDirection.x * pushDistance,
                                      pos2.y - pushDirection.y * pushDistance);
                }
            }

            chicken1.update(delta);
        }
    }

    public void render(SpriteBatch batch) {
        for (Chicken chicken : chickens) {
            chicken.render(batch);
        }
    }

    public void dispose() {
        for (Chicken chicken : chickens) {
            chicken.dispose();
        }
    }
}
