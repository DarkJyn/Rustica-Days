package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.camera.GameCamera;
import com.mygdx.game.entities.Player;
import com.mygdx.game.input.PlayerInputHandler;
import com.mygdx.game.render.MapRenderer;

public class GameLaucher extends ApplicationAdapter {
    private SpriteBatch batch;
    private GameCamera camera;
    private MapRenderer mapRenderer;
    private Player player;
    private PlayerInputHandler inputHandler;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Initialize camera
        camera = new GameCamera(300, 225);

        // Initialize map
        mapRenderer = new MapRenderer("Map/MapFix.tmx");

        // Initialize player
        player = new Player(100, 100, "Player.png");

        // Initialize input handler
        inputHandler = new PlayerInputHandler(player);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // Update player based on input
        inputHandler.processInput(delta);

        // Update player
        player.update(delta);

        // Update camera to follow player
        camera.followTarget(player.getX(), player.getY());

        // Clear screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Render map
        mapRenderer.render(camera.getCamera());

        // Render player
        batch.setProjectionMatrix(camera.getCamera().combined);
        batch.begin();
        player.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        mapRenderer.dispose();
    }
}
