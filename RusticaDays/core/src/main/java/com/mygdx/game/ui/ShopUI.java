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
    }

    public void render(SpriteBatch batch) {
        batch.end();
        batch.begin();
        batch.draw(ShopUI, 150, 100, 1000, 750);
    }

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}
