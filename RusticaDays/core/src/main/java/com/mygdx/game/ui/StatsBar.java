package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
// Thêm import cho FreeType
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class StatsBar {
    // Constants
    private static final float PADDING = 10f;
    private static final float BAR_WIDTH = 175f;
    private static final float BAR_HEIGHT = 20f;

    // UI components
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont moneyFont; // Font cho money
    private BitmapFont lvFont; // Font cho level
    private OrthographicCamera hudCamera;

    // Game stats
    private int level;
    private int experience;
    private int maxExperience;
    private int money;
    private float stamina;
    private float maxStamina;
    private Texture staminaIcon;
    private Texture coinIcon;
    private Texture statsBackground;
    private Texture Bar;

    public StatsBar(int screenWidth, int screenHeight) {
        // Initialize camera and viewport for HUD
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, screenWidth, screenHeight);
        viewport = new FitViewport(screenWidth, screenHeight, hudCamera);

        // Initialize rendering tools
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        // Khởi tạo font Pixellari từ file TTF
        initFont();

        // Load textures
        staminaIcon = new Texture(Gdx.files.internal("StaminaIcon.png"));
        statsBackground = new Texture(Gdx.files.internal("StatsBackGr.png"));
        coinIcon = new Texture(Gdx.files.internal("coin.png"));
        Bar = new Texture(Gdx.files.internal("Bar.png"));

        // Initialize game stats with default values
        level = 99;
        experience = 50;
        maxExperience = 100;
        money = 0;
        stamina = 100;
        maxStamina = 100;
    }

    private void initFont() {
        // Tạo generator từ file Pixellari.ttf
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/PressStart2P.ttf"));

        // Thiết lập tham số font
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 20; // Kích thước font
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;

        // Tạo font từ generator và parameter
        moneyFont = generator.generateFont(parameter);

        // Thiết lập tham số font
        FreeTypeFontParameter lvparams = new FreeTypeFontParameter();
        lvparams.size = 10; // Kích thước font
        lvparams.color = Color.WHITE;
        lvparams.borderWidth = 1;
        lvparams.borderColor = Color.BLACK;

        // Tạo font từ generator và parameter
        lvFont = generator.generateFont(lvparams);

        // Giải phóng generator
        generator.dispose();
    }

    public void update(float delta) {
        // Cập nhật stamina khi player di chuyển (sẽ được gọi từ GameLaucher)
    }

    public void render(SpriteBatch batch) {
        // Set projection matrix to HUD camera
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        batch.setProjectionMatrix(hudCamera.combined);

        // Draw Back
        renderBackground(batch);

        // Draw stamina bar
        renderStaminaBar(batch);

        // Draw experience bar and level
        renderExperienceBar(batch);

        // Draw money
        renderMoney(batch);
    }

    private void renderStaminaBar(SpriteBatch batch) {
        // Draw "STAMINA" Icon
        batch.begin();
        float iconSize = 90;
        batch.draw(staminaIcon, PADDING + 5, viewport.getWorldHeight() - PADDING - 95, iconSize, iconSize);
        batch.end();
        // Background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(PADDING + 80, viewport.getWorldHeight() - PADDING - BAR_HEIGHT - 20, BAR_WIDTH, BAR_HEIGHT + 3);

        // Foreground (actual stamina)
        shapeRenderer.setColor(Color.YELLOW);
        float staminaWidth = (stamina / maxStamina) * BAR_WIDTH;
        shapeRenderer.rect(PADDING + 80, viewport.getWorldHeight() - PADDING - BAR_HEIGHT - 20, staminaWidth, BAR_HEIGHT + 3);
        shapeRenderer.end();

        batch.begin();
        batch.draw(Bar, PADDING + 80, viewport.getWorldHeight() - PADDING - 40,180, 25);
        batch.end();

    }

    private void renderExperienceBar(SpriteBatch batch) {
        // Background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(PADDING + 80, viewport.getWorldHeight() - PADDING * 3 - BAR_HEIGHT * 3, BAR_WIDTH, BAR_HEIGHT + 3);

        // Foreground (actual experience)
        shapeRenderer.setColor(Color.BLUE);
        float expWidth = ((float)experience / maxExperience) * BAR_WIDTH;
        shapeRenderer.rect(PADDING + 80, viewport.getWorldHeight() - PADDING * 3 - BAR_HEIGHT * 3, expWidth, BAR_HEIGHT + 3);
        shapeRenderer.end();

        // Draw level text with pixellariFont
        batch.begin();
        // Sử dụng pixellariFont thay vì font mặc định
        lvFont.draw(batch, "Lv." + level, PADDING + 20, viewport.getWorldHeight() - PADDING * 6 - BAR_HEIGHT + 5);
        batch.end();

        batch.begin();
        batch.draw(Bar, PADDING + 80, viewport.getWorldHeight() - PADDING - 80,180, 25);
        batch.end();
    }

    private void renderMoney(SpriteBatch batch) {
        // Draw coin icon
        batch.begin();
        float iconSize = 40;
        batch.draw(coinIcon, PADDING + 20, viewport.getWorldHeight() - PADDING * 5 - 80, iconSize, iconSize);
        batch.end();

        // Draw money text with pixellariFont
        batch.begin();
        // Sử dụng pixellariFont thay vì font mặc định
        moneyFont.draw(batch, "" + money, PADDING + 80, viewport.getWorldHeight() - PADDING * 4 - BAR_HEIGHT * 3 - 10);
        batch.end();
    }

    private void renderBackground(SpriteBatch batch) {
        batch.begin();
        float bgWidth = BAR_WIDTH - 25 + PADDING * 2 + 115; // thêm không gian cho icon
        float bgHeight = BAR_HEIGHT * 3 + PADDING * 8; // đủ cao cho 3 thanh + khoảng cách
        float bgX = PADDING; // dịch trái một chút để chứa cả icon
        float bgY = viewport.getWorldHeight() - PADDING - bgHeight;
        batch.draw(statsBackground, bgX, bgY, bgWidth, bgHeight);
        batch.end();
    }

    // Getters and setters
    // ...

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        moneyFont.dispose(); // Thêm việc giải phóng font mới
        lvFont.dispose(); // Thêm việc giải phóng font mới
        staminaIcon.dispose();
        statsBackground.dispose();
        coinIcon.dispose();
    }

    // Các phương thức còn lại giữ nguyên như cũ
    public float getStamina() {
        return stamina;
    }

    public void setStamina(float stamina) {
        this.stamina = Math.max(0, Math.min(maxStamina, stamina));
    }

    public void decreaseStamina(float amount) {
        setStamina(stamina - amount);
    }

    public void increaseStamina(float amount) {
        setStamina(stamina + amount);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    private int calculateNextLevelExp() {
        // Formula for increasing required XP per level
        return 100 + (level - 1) * 50;
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudCamera.position.set(width/2f, height/2f, 0);
    }
}
