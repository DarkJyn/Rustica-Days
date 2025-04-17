package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StatsBar {
    // Constants
    private static final float PADDING = 10f;
    private static final float BAR_WIDTH = 150f;
    private static final float BAR_HEIGHT = 15f;

    // UI components
    private Stage stage;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
//    private SpriteBatch batch;

    // Game stats
    private int level;
    private int experience;
    private int maxExperience;
    private int money;
    private float stamina;
    private float maxStamina;

    public StatsBar(SpriteBatch batch, int screenWidth, int screenHeight) {
        // Initialize viewport and stage
        viewport = new FitViewport(screenWidth, screenHeight, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        // Initialize rendering tools
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        // Initialize game stats with default values
        level = 1;
        experience = 0;
        maxExperience = 100;
        money = 0;
        stamina = 100;
        maxStamina = 100;
    }

    public void update(float delta) {
        // You can update animations or other dynamic elements here
    }

    public void render(SpriteBatch batch) {
        // Update viewport
        viewport.apply();

        // Begin shape renderer for bars
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Draw stamina bar
        renderStaminaBar(batch);

        // Draw experience bar and level
        renderExperienceBar(batch);

        // Draw money
        renderMoney(batch);
    }

    private void renderStaminaBar(SpriteBatch batch) {
        // Background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(PADDING, viewport.getWorldHeight() - PADDING - BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);

        // Foreground (actual stamina)
        shapeRenderer.setColor(Color.GREEN);
        float staminaWidth = (stamina / maxStamina) * BAR_WIDTH;
        shapeRenderer.rect(PADDING, viewport.getWorldHeight() - PADDING - BAR_HEIGHT, staminaWidth, BAR_HEIGHT);
        shapeRenderer.end();

        // Draw "STAMINA" text
        batch.begin();
        font.draw(batch, "STAMINA", PADDING, viewport.getWorldHeight() - PADDING + 5);
        batch.end();
    }

    private void renderExperienceBar(SpriteBatch batch) {
        // Background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(PADDING, viewport.getWorldHeight() - PADDING * 2 - BAR_HEIGHT * 2, BAR_WIDTH, BAR_HEIGHT);

        // Foreground (actual experience)
        shapeRenderer.setColor(Color.BLUE);
        float expWidth = ((float)experience / maxExperience) * BAR_WIDTH;
        shapeRenderer.rect(PADDING, viewport.getWorldHeight() - PADDING * 2 - BAR_HEIGHT * 2, expWidth, BAR_HEIGHT);
        shapeRenderer.end();

        // Draw level and XP text
        batch.begin();
        font.draw(batch, "LV." + level, PADDING - 5, viewport.getWorldHeight() - PADDING * 2 - BAR_HEIGHT + 5);
        font.draw(batch, "XP: " + experience + "/" + maxExperience, PADDING + BAR_WIDTH + 10,
            viewport.getWorldHeight() - PADDING * 2 - BAR_HEIGHT * 2 + BAR_HEIGHT/2 + 5);
        batch.end();
    }

    private void renderMoney(SpriteBatch batch) {
        batch.begin();
        font.draw(batch, "TIỀN: " + money, PADDING, viewport.getWorldHeight() - PADDING * 3 - BAR_HEIGHT * 2);
        batch.end();
    }

    // Getters and setters
    public void setStamina(float stamina) {
        this.stamina = Math.max(0, Math.min(maxStamina, stamina));
    }

    public void decreaseStamina(float amount) {
        setStamina(stamina - amount);
    }

    public void increaseStamina(float amount) {
        setStamina(stamina + amount);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExperience(int experience) {
        this.experience = experience;
        while (this.experience >= maxExperience) {
            this.experience -= maxExperience;
            level++;
            maxExperience = calculateNextLevelExp();
        }
    }

    public void addExperience(int amount) {
        setExperience(experience + amount);
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    private int calculateNextLevelExp() {
        // Simple formula for increasing required XP per level
        return 100 + (level - 1) * 50;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        stage.dispose();
    }
}
