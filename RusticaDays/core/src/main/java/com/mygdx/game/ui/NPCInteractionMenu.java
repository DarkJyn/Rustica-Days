package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class NPCInteractionMenu {
    private boolean isVisible;
    private Texture menuBackground;
    private BitmapFont font;
    private String[] options;
    private Rectangle[] optionRects;
    private int selectedOption;
    private float menuX, menuY, menuWidth, menuHeight;
    private float optionHeight;
    private Color normalColor;
    private Color hoverColor;
    private Color selectedColor;
    private boolean mouseHover;
    private int mouseHoverIndex;

    public NPCInteractionMenu() {
        // Khởi tạo menu với các tùy chọn "Mua" và "Bán"
        options = new String[]{"Buy", "Sell"};
        optionRects = new Rectangle[options.length];
        selectedOption = 0;
        mouseHover = false;
        mouseHoverIndex = -1;

        // Kích thước menu
        menuWidth = 950;
        optionHeight = 50;
        menuHeight = optionHeight * options.length + 190; // 20 là padding

        // Màu sắc
        normalColor = Color.CORAL;
        hoverColor = Color.YELLOW;
        selectedColor = Color.BROWN;

        // Load font và hình nền
        font = new BitmapFont();
        font.setColor(Color.BROWN);
        // Khởi tạo font Pixellari từ file TTF
        initFont();

        menuBackground = new Texture("dialog_box.png"); // Thay thế bằng texture của bạn

        // Khởi tạo các rectangle cho mỗi tùy chọn để xử lý hover và click
        for (int i = 0; i < options.length; i++) {
            optionRects[i] = new Rectangle(0, 0, menuWidth - 20, optionHeight);
        }
    }

    public void update() {
        // Xử lý input từ bàn phím
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
//            selectedOption = (selectedOption - 1 + options.length) % options.length;
            selectedOption = selectedOption - 1;
            if(selectedOption < 0) {selectedOption = 0;}
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
//            selectedOption = (selectedOption + 1) % options.length;
            selectedOption = selectedOption + 1;
            if(selectedOption > options.length - 1) {selectedOption = options.length - 1;}
        }

        // Xử lý input từ chuột (hover)
        Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        mouseHover = false;
        mouseHoverIndex = -1;

        for (int i = 0; i < optionRects.length; i++) {
            if (optionRects[i].contains(mousePos.x, mousePos.y)) {
                mouseHover = true;
                mouseHoverIndex = i;
                break;
            }
        }

        // Cập nhật selectedOption nếu có hover chuột
        if (mouseHover) {
            selectedOption = mouseHoverIndex;
        }

        // Xử lý click chuột
        if (mouseHover && Gdx.input.justTouched()) {
            selectedOption = mouseHoverIndex;
        }
    }

    public void render(SpriteBatch batch) {
        if (!isVisible) return;

        // Vẽ hình nền
        batch.draw(menuBackground, menuX, menuY, menuWidth, menuHeight);
        font.setColor(selectedColor);
        font.draw(batch, "Welcome to my store!", menuX + 300, menuY + menuHeight - 60);
        font.draw(batch, "What do you want?", menuX + 300, menuY + menuHeight - 95);
        // Vẽ các tùy chọn
        for (int i = 0; i < options.length; i++) {
            Color optionColor = selectedColor;

            if (i == selectedOption) {
                optionColor = normalColor;
            } else if (i == mouseHoverIndex && mouseHover) {
                optionColor = hoverColor;
            }

            font.setColor(optionColor);
            float textX = menuX + 300;
            float textY = menuY + menuHeight - 150 - i * optionHeight;
            font.draw(batch, options[i], textX, textY);

            // Cập nhật vị trí của các rectangle
            optionRects[i].setPosition(menuX + 300, menuY + menuHeight - 180 - i * optionHeight);
        }
    }

    private void initFont() {
        // Tạo generator từ file Pixellari.ttf
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/PressStart2P.ttf"));

        // Thiết lập tham số font
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 25; // Kích thước font
        parameter.color = Color.WHITE;
//        parameter.borderWidth = 1;
//        parameter.borderColor = Color.BLACK;

        // Tạo font từ generator và parameter
        font = generator.generateFont(parameter);
    }

    public void setPosition(float x, float y) {
        this.menuX = x;
        this.menuY = y;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void dispose() {
        font.dispose();
        menuBackground.dispose();
    }
}
