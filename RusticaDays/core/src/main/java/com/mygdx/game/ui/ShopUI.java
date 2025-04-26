package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class ShopUI {
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    // Danh sách các mặt hàng trong cửa hàng
    private List<ShopItem> items;

    // Các thuộc tính hiển thị
    private float shopWidth;
    private float shopHeight;
    private float shopX;
    private float shopY;

    // Nút đóng cửa hàng
    private Rectangle closeButton;

    public ShopUI() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.2f);

        // Thiết lập kích thước và vị trí cửa hàng
        shopWidth = Gdx.graphics.getWidth() - 100;
        shopHeight = Gdx.graphics.getHeight() - 100;
        shopX = 50;
        shopY = 50;

        // Thiết lập nút đóng
        closeButton = new Rectangle(shopX + shopWidth - 40, shopY + shopHeight - 40, 30, 30);

        // Khởi tạo danh sách mặt hàng
        initializeItems();
    }

    private void initializeItems() {
        items = new ArrayList<>();

        // Thêm các mặt hàng vào cửa hàng (thay đổi đường dẫn texture phù hợp)
        items.add(new ShopItem("Kiếm Sắt", 100, new Texture(Gdx.files.internal("coin.png"))));
        items.add(new ShopItem("Khiên Gỗ", 50, new Texture(Gdx.files.internal("coin.png"))));
        items.add(new ShopItem("Thuốc Hồi Phục", 25, new Texture(Gdx.files.internal("coin.png"))));
        items.add(new ShopItem("Giáp Da", 150, new Texture(Gdx.files.internal("coin.png"))));
    }

    public void render(SpriteBatch batch) {
        batch.end();

        // Vẽ background của cửa hàng
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 0.9f);
        shapeRenderer.rect(shopX, shopY, shopWidth, shopHeight);

        // Vẽ nút đóng
        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1);
        shapeRenderer.rect(closeButton.x, closeButton.y, closeButton.width, closeButton.height);
        shapeRenderer.end();

        batch.begin();

        // Vẽ tiêu đề cửa hàng
        font.setColor(Color.YELLOW);
//        font.draw(batch, "CỬA HÀNG", shopX + shopWidth/2 - 50, shopY + shopHeight - 20);
        font.draw(batch, "CỬA HÀNG", 400,400);

        // Vẽ chữ X trên nút đóng
        font.setColor(Color.WHITE);
        font.draw(batch, "X", closeButton.x + 10, closeButton.y + 20);

        // Vẽ hướng dẫn
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Nhấn ESC để đóng cửa hàng", shopX + 20, shopY + 30);

        // Vẽ các mặt hàng
        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);
            float itemX = shopX + 20;
            float itemY = shopY + shopHeight - 80 - (i * 60);

            // Vẽ icon mặt hàng
            batch.draw(item.getTexture(), itemX, itemY, 40, 40);

            // Vẽ tên mặt hàng
            font.setColor(Color.WHITE);
            font.draw(batch, item.getName(), itemX + 50, itemY + 25);

            // Vẽ giá
            font.setColor(Color.GOLD);
            font.draw(batch, item.getPrice() + " xu", itemX + 200, itemY + 25);

            // Vẽ nút mua
            font.setColor(Color.GREEN);
            font.draw(batch, "[Mua]", itemX + 300, itemY + 25);
        }
    }

    // Phương thức xử lý khi click chuột (nếu bạn muốn thêm)
    public boolean handleClick(float x, float y) {
        // Kiểm tra click vào nút đóng
        if (closeButton.contains(x, y)) {
            return true; // Trả về true để báo hiệu đóng cửa hàng
        }

        // Kiểm tra click vào các nút mua
        for (int i = 0; i < items.size(); i++) {
            float itemY = shopY + shopHeight - 80 - (i * 60);
            Rectangle buyButton = new Rectangle(shopX + 300, itemY, 40, 25);

            if (buyButton.contains(x, y)) {
                // Xử lý mua mặt hàng
                // TODO: Thêm logic mua hàng ở đây
                return false;
            }
        }

        return false;
    }

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        for (ShopItem item : items) {
            item.getTexture().dispose();
        }
    }

    // Lớp con để quản lý mặt hàng trong cửa hàng
    private class ShopItem {
        private String name;
        private int price;
        private Texture texture;

        public ShopItem(String name, int price, Texture texture) {
            this.name = name;
            this.price = price;
            this.texture = texture;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public Texture getTexture() {
            return texture;
        }
    }
}
