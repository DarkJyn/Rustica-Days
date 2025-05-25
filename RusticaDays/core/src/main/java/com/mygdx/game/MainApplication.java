package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class MainApplication extends Game {
    private GameWrapper gameWrapper;
    private MainMenuScreen mainMenuScreen;

    @Override
    public void create() {
        // Khởi tạo main menu screen đầu tiên
        mainMenuScreen = new MainMenuScreen(this);
        setScreen(mainMenuScreen);
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
    }
}
