package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainApplication extends Game {
    private GameWrapper gameWrapper;
    private MainMenuScreen mainMenuScreen;
    private TutorialScreen tutorialScreen;
    private Texture cursorTexture;
    private SpriteBatch cursorBatch;
    private int cursorOffsetX = 0;
    private int cursorOffsetY = 0;
    private float cursorScale = 0.8f;  // Thêm hệ số tỷ lệ cho con trỏ

    @Override
    public void create() {
        // Khởi tạo custom cursor
        cursorTexture = new Texture(Gdx.files.internal("cursor.png"));
        cursorBatch = new SpriteBatch();
        Gdx.input.setCursorCatched(false);
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(new Pixmap(1, 1, Pixmap.Format.RGBA8888), 0, 0));

        // Khởi tạo main menu screen đầu tiên
        mainMenuScreen = new MainMenuScreen(this);
        tutorialScreen = new TutorialScreen(this);
        setScreen(mainMenuScreen);
    }

    @Override
    public void render() {
        super.render();

        // Vẽ custom cursor
        cursorBatch.begin();
        float cursorX = Gdx.input.getX() - cursorOffsetX;
        float cursorY = Gdx.graphics.getHeight() - Gdx.input.getY() - cursorOffsetY - 50;
        float scaledWidth = cursorTexture.getWidth() * cursorScale;
        float scaledHeight = cursorTexture.getHeight() * cursorScale;
        cursorBatch.draw(cursorTexture, cursorX, cursorY, scaledWidth, scaledHeight);
        cursorBatch.end();
    }

    // Phương thức để hiển thị tutorial
    public void showTutorial() {
        setScreen(tutorialScreen);
    }

    // Phương thức để chuyển sang game mới
    public void startNewGame() {
        if (gameWrapper != null) {
            gameWrapper.dispose();
        }
        gameWrapper = new GameWrapper();
        setScreen(gameWrapper);
    }

    // Phương thức để quay lại main menu
    public void returnToMainMenu() {
        if (gameWrapper != null) {
            // Tạm dừng game thay vì dispose để có thể save
            gameWrapper.pause();
        }
        setScreen(mainMenuScreen);
    }

    // Phương thức để tiếp tục game đã lưu
    public void loadGame() {
        if (gameWrapper != null) {
            gameWrapper.resume();
            setScreen(gameWrapper);
        } else {
            // Nếu chưa có game nào, tạo game mới
            startNewGame();
        }
    }

    // Kiểm tra xem có game đã lưu không
    public boolean hasSavedGame() {
        return gameWrapper != null;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (gameWrapper != null) {
            gameWrapper.dispose();
        }
        if (mainMenuScreen != null) {
            mainMenuScreen.dispose();
        }
        if (tutorialScreen != null) {
            tutorialScreen.dispose();
        }
        if (cursorTexture != null) {
            cursorTexture.dispose();
        }
        if (cursorBatch != null) {
            cursorBatch.dispose();
        }
    }
}
