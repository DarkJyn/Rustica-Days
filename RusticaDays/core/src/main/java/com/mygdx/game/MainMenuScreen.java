package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class MainMenuScreen implements Screen {
    private MainApplication mainApp;
    private Stage stage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Skin skin;
    private BitmapFont font;

    // Background animation
    private Animation<TextureRegion> backgroundAnimation;
    private float stateTime;
    private TextureAtlas backgroundAtlas;
    private TextureRegion[] backgroundFrames;  // Thêm mảng để lưu các frame

    // Title image
    private Texture titleTexture;

    // UI Components
    private TextButton newGameButton;
    private TextButton loadGameButton;
    private TextButton settingsButton;
    private TextButton exitButton;

    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private Texture buttonHoverTexture;

    public MainMenuScreen(MainApplication mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        stage = new Stage(new FitViewport(1302, 942, camera));
        Gdx.input.setInputProcessor(stage);

        // Load background animation frames
        backgroundFrames = new TextureRegion[80];
        for (int i = 0; i < 80; i++) {
            String framePath = String.format("BackGrMainMenu/frame_%02d_delay-0.1s.png", i);
            backgroundFrames[i] = new TextureRegion(new Texture(Gdx.files.internal(framePath)));
        }
        backgroundAnimation = new Animation<>(0.15f, backgroundFrames);  // Tăng delay lên 0.2s để animation chậm hơn 2 lần
        stateTime = 0f;

        // Load title image
        titleTexture = new Texture(Gdx.files.internal("TextMenu.png"));

        // Load button textures
        buttonTexture = new Texture(Gdx.files.internal("button.png"));
        buttonPressedTexture = new Texture(Gdx.files.internal("button_pressed.png"));
        buttonHoverTexture = new Texture(Gdx.files.internal("button_hover.png"));

        // Load and generate custom font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/dogicapixelbold.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 20;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        skin = new Skin();
        skin.add("default-font", font);

        // Tạo style cho button với press effect và hover effect
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.GRAY;
//        buttonStyle.overFontColor = Color.YELLOW;

        // Set button background
        Drawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        Drawable buttonPressedDrawable = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));
        Drawable buttonHoverDrawable = new TextureRegionDrawable(new TextureRegion(buttonHoverTexture));

        buttonStyle.up = buttonDrawable;
        buttonStyle.down = buttonPressedDrawable;
        buttonStyle.over = buttonHoverDrawable;

        skin.add("default", buttonStyle);

        // Tạo style cho label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

        createUI();
    }

    private void createUI() {
        // Tạo các button
        newGameButton = new TextButton("New Game", skin);
        newGameButton.getLabel().setFontScale(1.5f);
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainApp.showTutorial();
            }
        });

        loadGameButton = new TextButton("Load Game", skin);
        loadGameButton.getLabel().setFontScale(1.5f);
        loadGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainApp.loadGame();
            }
        });

        settingsButton = new TextButton("Settings", skin);
        settingsButton.getLabel().setFontScale(1.5f);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement settings screen
                System.out.println("Settings clicked - Not implemented yet");
            }
        });

        exitButton = new TextButton("Exit", skin);
        exitButton.getLabel().setFontScale(1.5f);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Tạo layout table
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Thêm các component vào table
        table.add().height(300).padBottom(50); // Tăng khoảng cách từ title xuống
        table.row();
        table.add(newGameButton).width(360).height(95).padBottom(15).padTop(70); // Thêm padding phía trên
        table.row();
        table.add(loadGameButton).width(360).height(95).padBottom(15);
        table.row();
        table.add(settingsButton).width(360).height(95).padBottom(15);
        table.row();
        table.add(exitButton).width(360).height(95);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // Cập nhật trạng thái button dựa trên việc có saved game hay không
        loadGameButton.setDisabled(!mainApp.hasSavedGame());

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update animation time
        stateTime += delta;

        // Draw background animation
        batch.begin();
        TextureRegion currentFrame = backgroundAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, 0, 0, stage.getWidth(), stage.getHeight());

        // Draw title image
        float titleWidth = titleTexture.getWidth(); // Scale down to 50%
        float titleHeight = titleTexture.getHeight();
        float titleX = (stage.getWidth() - titleWidth) + 175;
        float titleY = stage.getHeight() - titleHeight + 50; // 50 pixels from top
        batch.draw(titleTexture, titleX, titleY, titleWidth, titleHeight);

        batch.end();

        // Update và render stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (batch != null) batch.dispose();
        if (skin != null) skin.dispose();
        if (font != null) font.dispose();
        if (backgroundFrames != null) {
            for (TextureRegion frame : backgroundFrames) {
                if (frame != null && frame.getTexture() != null) {
                    frame.getTexture().dispose();
                }
            }
        }
        if (titleTexture != null) titleTexture.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
        if (buttonPressedTexture != null) buttonPressedTexture.dispose();
        if (buttonHoverTexture != null) buttonHoverTexture.dispose();
    }
}
