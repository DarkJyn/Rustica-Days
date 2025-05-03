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
    private Texture ShopUI;
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

        // Load Texture
        ShopUI = new Texture("ShopUI.png");
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
        batch.begin();
        batch.draw(ShopUI, 150, 100, 1000, 750);
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
