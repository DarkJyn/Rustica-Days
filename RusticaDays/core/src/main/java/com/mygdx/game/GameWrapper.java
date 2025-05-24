package com.mygdx.game;

import com.badlogic.gdx.Screen;

public class GameWrapper implements Screen {
    private GameLaucher gameLaucher;

    public GameWrapper() {
        gameLaucher = new GameLaucher();
    }

    @Override
    public void show() {
        // Khởi tạo game khi screen được hiển thị
        gameLaucher.create();
    }

    @Override
    public void render(float delta) {
        // Delegate render call to GameLaucher
        gameLaucher.render();
    }

    @Override
    public void resize(int width, int height) {
        // Delegate resize call to GameLaucher
        gameLaucher.resize(width, height);
    }

    @Override
    public void pause() {
        // Delegate pause call to GameLaucher
        gameLaucher.pause();
    }

    @Override
    public void resume() {
        // Delegate resume call to GameLaucher
        gameLaucher.resume();
    }

    @Override
    public void hide() {
        // Called when this screen is no longer the current screen
        // Có thể để trống hoặc thêm logic cần thiết
    }

    @Override
    public void dispose() {
        // Delegate dispose call to GameLaucher
        if (gameLaucher != null) {
            gameLaucher.dispose();
        }
    }

    // Getter để truy cập GameLaucher nếu cần
    public GameLaucher getGameLaucher() {
        return gameLaucher;
    }
}
