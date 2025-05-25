package com.mygdx.game.entities.plants.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Quản lý một ruộng gồm nhiều ô đất, mỗi ô có thể trồng 1 cây.
 * Ruộng này có 3 hàng, 5 cột, chỉ cho phép trồng cây trong giới hạn đã định.
 */
public class FarmField {
    public static final int ROWS = 5;
    public static final int COLS = 3;
    // Các góc của ruộng
    public static final float FIELD_LEFT = 482.728f;
    public static final float FIELD_RIGHT = 524.6635f;
    public static final float FIELD_TOP = 397.8851f;
    public static final float FIELD_BOTTOM = 322.8851f;

    private Plant[][] plants;
    private float cellWidth;
    private float cellHeight;

    public FarmField() {
        plants = new Plant[ROWS][COLS];
        cellWidth = (FIELD_RIGHT - FIELD_LEFT) / COLS;
        cellHeight = (FIELD_TOP - FIELD_BOTTOM) / ROWS;
    }

    /**
     * Kiểm tra vị trí (x, y) có nằm trong ruộng không
     */
    public boolean isInField(float x, float y) {
        return x >= FIELD_LEFT && x < FIELD_RIGHT && y >= FIELD_BOTTOM && y < FIELD_TOP;
    }

    /**
     * Lấy chỉ số hàng/cột từ vị trí thực tế (x, y)
     */
    public int[] getCellIndex(float x, float y) {
        if (!isInField(x, y)) return null;
        int col = (int)((x - FIELD_LEFT) / cellWidth);
        int row = (int)((y - FIELD_BOTTOM) / cellHeight);
        // Đảm bảo không vượt quá chỉ số
        if (col < 0) col = 0; if (col >= COLS) col = COLS - 1;
        if (row < 0) row = 0; if (row >= ROWS) row = ROWS - 1;
        return new int[]{row, col};
    }

    /**
     * Trồng cây vào ô (row, col), trả về true nếu thành công
     */
    public boolean plantAt(int row, int col, Plant plant) {
        if (!isValidCell(row, col) || plants[row][col] != null) return false;
        // Không thay đổi bounds của cây nữa
        plants[row][col] = plant;
        return true;
    }

    /**
     * Lấy cây tại ô (row, col)
     */
    public Plant getPlantAt(int row, int col) {
        if (!isValidCell(row, col)) return null;
        return plants[row][col];
    }

    /**
     * Xóa cây tại ô (row, col)
     */
    public void removePlantAt(int row, int col) {
        if (isValidCell(row, col)) plants[row][col] = null;
    }

    /**
     * Kiểm tra chỉ số hợp lệ
     */
    public boolean isValidCell(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    /**
     * Vẽ toàn bộ ruộng và cây trồng
     */
    public void render(SpriteBatch batch) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Plant plant = plants[row][col];
                if (plant != null) {
                    plant.render(batch);
                }
            }
        }
    }

    /**
     * Vẽ đếm ngược cho tất cả cây trồng
     */
    public void renderCountdowns(SpriteBatch batch) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Plant plant = plants[row][col];
                if (plant != null) {
                    plant.renderCountdown(batch);
                }
            }
        }
    }

    /**
     * Lấy vị trí trung tâm của ô (row, col)
     */
    public Vector2 getCellCenter(int row, int col) {
        float x = FIELD_LEFT + col * cellWidth + cellWidth / 2f;
        float y = FIELD_BOTTOM + row * cellHeight + cellHeight / 2f;
        return new Vector2(x, y);
    }

    /**
     * Lấy toàn bộ lưới cây
     */
    public Plant[][] getGrid() {
        return plants;
    }

    public float getCellWidth() {
        return cellWidth;
    }
    public float getCellHeight() {
        return cellHeight;
    }

    /**
     * Đặt cây vào vị trí cụ thể trong ruộng
     * @param row Hàng
     * @param col Cột
     * @param plant Cây cần đặt
     * @return true nếu đặt thành công, false nếu vị trí không hợp lệ hoặc đã có cây
     */
    public boolean setPlantAt(int row, int col, Plant plant) {
        if (isValidCell(row, col)) {
            plants[row][col] = plant;
            return true;
        }
        return false;
    }
}
