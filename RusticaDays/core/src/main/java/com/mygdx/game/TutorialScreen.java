package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.Pixmap;

public class TutorialScreen implements Screen {
    private MainApplication mainApp;
    private Stage stage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Skin skin;
    private BitmapFont titleFont;
    private BitmapFont contentFont;
    private BitmapFont movementFont;  // Font cho chữ Movement

    // Background animation
    private Animation<TextureRegion> backgroundAnimation;
    private float stateTime;
    private TextureRegion[] backgroundFrames;
    private Texture tutorialBackground;  // Thêm texture cho nền phụ

    // UI Components
    private TextButton continueButton;
    private TextButton backButton;
    private Texture tutorialTextTexture;  // Thêm texture cho TutorialText
    private Label movementLabel;  // Label cho chữ Movement
    private Label inventoryLabel;  // Label cho chữ Inventory
    private Label menuLabel;  // Label cho chữ Menu
    private Label farmingLabel;  // Label cho chữ Farming

    // WASD Tutorial Animation
    private Animation<TextureRegion> wasdAnimation;
    private Texture wasdTutorialTexture;
    private TextureRegion[] wasdFrames;
    private float wasdStateTime;

    // TabI Tutorial Animation
    private Animation<TextureRegion> tabiAnimation;
    private Texture tabiTutorialTexture;
    private TextureRegion[] tabiFrames;
    private float tabiStateTime;

    // ESC Tutorial Animation
    private Animation<TextureRegion> escAnimation;
    private Texture escTutorialTexture;
    private TextureRegion[] escFrames;
    private float escStateTime;

    // Cursor image
    private Texture cursorImage;

    // Button textures
    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private Texture buttonHoverTexture;

    public TutorialScreen(MainApplication mainApp) {
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
        backgroundAnimation = new Animation<>(0.15f, backgroundFrames);
        stateTime = 0f;

        // Load tutorial background
        tutorialBackground = new Texture(Gdx.files.internal("TutorialBG.png"));

        // Load tutorial text image
        tutorialTextTexture = new Texture(Gdx.files.internal("TutorialText.png"));

        // Load and initialize WASD tutorial animation
        wasdTutorialTexture = new Texture(Gdx.files.internal("WASDTutorial.png"));
        wasdFrames = new TextureRegion[5];
        int frameWidth = wasdTutorialTexture.getWidth() / 5;
        for (int i = 0; i < 5; i++) {
            wasdFrames[i] = new TextureRegion(wasdTutorialTexture, i * frameWidth, 0, frameWidth, wasdTutorialTexture.getHeight());
        }
        wasdAnimation = new Animation<>(0.4f, wasdFrames);
        wasdStateTime = 0f;

        // Load and initialize TabI tutorial animation
        tabiTutorialTexture = new Texture(Gdx.files.internal("TabITutorial.png"));
        tabiFrames = new TextureRegion[3];
        int tabiFrameWidth = tabiTutorialTexture.getWidth() / 3;
        for (int i = 0; i < 3; i++) {
            tabiFrames[i] = new TextureRegion(tabiTutorialTexture, i * tabiFrameWidth, 0, tabiFrameWidth, tabiTutorialTexture.getHeight());
        }
        tabiAnimation = new Animation<>(0.4f, tabiFrames);
        tabiStateTime = 0f;

        // Load and initialize ESC tutorial animation
        escTutorialTexture = new Texture(Gdx.files.internal("ESCTutorial.png"));
        escFrames = new TextureRegion[2];
        int escFrameWidth = escTutorialTexture.getWidth() / 2;
        for (int i = 0; i < 2; i++) {
            escFrames[i] = new TextureRegion(escTutorialTexture, i * escFrameWidth, 0, escFrameWidth, escTutorialTexture.getHeight());
        }
        escAnimation = new Animation<>(0.4f, escFrames);
        escStateTime = 0f;

        // Load cursor image
        cursorImage = new Texture(Gdx.files.internal("cursor.png"));

        // Load button textures (reuse from MainMenu)
        buttonTexture = new Texture(Gdx.files.internal("button.png"));
        buttonPressedTexture = new Texture(Gdx.files.internal("button_pressed.png"));
        buttonHoverTexture = new Texture(Gdx.files.internal("button_hover.png"));

        // Load and generate custom fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Pixellari.ttf"));

        // Title font (larger)
        FreeTypeFontParameter titleParameter = new FreeTypeFontParameter();
        titleParameter.size = 36;
        titleParameter.color = Color.YELLOW;
        titleFont = generator.generateFont(titleParameter);

        // Content font (smaller)
        FreeTypeFontParameter contentParameter = new FreeTypeFontParameter();
        contentParameter.size = 18;
        contentParameter.color = Color.WHITE;
        contentFont = generator.generateFont(contentParameter);

        // Movement font
        FreeTypeFontParameter movementParameter = new FreeTypeFontParameter();
        movementParameter.size = 24;
        movementParameter.color = new Color(0.6f, 0.3f, 0.1f, 1.0f);  // Màu nâu
        movementFont = generator.generateFont(movementParameter);

        generator.dispose();

        skin = new Skin();
        skin.add("title-font", titleFont);
        skin.add("content-font", contentFont);
        skin.add("movement-font", movementFont);

        // Create button style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = contentFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.GRAY;

        // Set button background
        Drawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        Drawable buttonPressedDrawable = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));
        Drawable buttonHoverDrawable = new TextureRegionDrawable(new TextureRegion(buttonHoverTexture));

        buttonStyle.up = buttonDrawable;
        buttonStyle.down = buttonPressedDrawable;
        buttonStyle.over = buttonHoverDrawable;

        skin.add("default", buttonStyle);

        // Create label styles
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = titleFont;
        titleLabelStyle.fontColor = Color.YELLOW;
        skin.add("title", titleLabelStyle);

        Label.LabelStyle contentLabelStyle = new Label.LabelStyle();
        contentLabelStyle.font = contentFont;
        contentLabelStyle.fontColor = Color.WHITE;
        skin.add("content", contentLabelStyle);

        Label.LabelStyle movementLabelStyle = new Label.LabelStyle();
        movementLabelStyle.font = movementFont;
        movementLabelStyle.fontColor = Color.BROWN;
        skin.add("movement", movementLabelStyle);

        createUI();
    }

    private void createUI() {
        // Create buttons
        continueButton = new TextButton("Continue to Game", skin);
        continueButton.getLabel().setFontScale(1.3f);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Start the actual game
                mainApp.startNewGame();
            }
        });

        backButton = new TextButton("Back to Menu", skin);
        backButton.getLabel().setFontScale(1.3f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Return to main menu
                mainApp.setScreen(new MainMenuScreen(mainApp));
            }
        });

        // Create Movement label
        movementLabel = new Label("Movement", skin, "movement");
        movementLabel.setFontScale(1.5f);
        stage.addActor(movementLabel);

        // Create Inventory label
        inventoryLabel = new Label("Inventory", skin, "movement");
        inventoryLabel.setFontScale(1.5f);
        stage.addActor(inventoryLabel);

        // Create Menu label
        menuLabel = new Label("Menu", skin, "movement");
        menuLabel.setFontScale(1.5f);
        stage.addActor(menuLabel);

        // Create Farming label
        farmingLabel = new Label("Farming", skin, "movement");
        farmingLabel.setFontScale(1.5f);
        stage.addActor(farmingLabel);

        // Create main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        mainTable.padTop(50);

        // Add spacing for title image (will be drawn in render method)
        mainTable.add().height(tutorialTextTexture.getHeight()).padBottom(30);
        mainTable.row();

        // Add empty space to push buttons to bottom half
        mainTable.add().expand().fill();
        mainTable.row();

        // Add button table
        Table buttonTable = new Table();
        buttonTable.add(backButton).width(240).height(72).padRight(20);
        buttonTable.add(continueButton).width(300).height(72);

        mainTable.add(buttonTable).padBottom(50);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update animation time
        stateTime += delta;
        wasdStateTime += delta;
        tabiStateTime += delta;
        escStateTime += delta;

        // Draw background animation
        batch.begin();
        TextureRegion currentFrame = backgroundAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, 0, 0, stage.getWidth(), stage.getHeight());

        // Draw tutorial background on top
        batch.draw(tutorialBackground, 0, 50, stage.getWidth(), stage.getHeight() - 50);

        // Draw tutorial text image
        float textX = (stage.getWidth() - tutorialTextTexture.getWidth()) / 2;
        float textY = stage.getHeight() - tutorialTextTexture.getHeight() + 25;
        batch.draw(tutorialTextTexture, textX, textY);

        // Draw WASD tutorial animation
        TextureRegion wasdFrame = wasdAnimation.getKeyFrame(wasdStateTime, true);
        float wasdX = (stage.getWidth() - wasdFrame.getRegionWidth() * 5) / 2 - 200;
        float wasdY = stage.getHeight() - tutorialTextTexture.getHeight() - wasdFrame.getRegionHeight() * 5 + 100;
        batch.draw(wasdFrame, wasdX, wasdY, wasdFrame.getRegionWidth() * 4, wasdFrame.getRegionHeight() * 4);

        // Draw TabI tutorial animation
        TextureRegion tabiFrame = tabiAnimation.getKeyFrame(tabiStateTime, true);
        float tabiX = wasdX + wasdFrame.getRegionWidth() * 4 + 250;
        float tabiY = wasdY;
        batch.draw(tabiFrame, tabiX, tabiY, tabiFrame.getRegionWidth() * 4, tabiFrame.getRegionHeight() * 4);

        // Draw ESC tutorial animation
        TextureRegion escFrame = escAnimation.getKeyFrame(escStateTime, true);
        float escX = wasdX + 30;
        float escY = wasdY - escFrame.getRegionHeight() - 200;
        batch.draw(escFrame, escX, escY, escFrame.getRegionWidth() * 4, escFrame.getRegionHeight() * 4);

        // Draw cursor image to the right of ESC frame
        float cursorX = escX + escFrame.getRegionWidth() * 4 + 350;
        float cursorY = escY + 15;
        batch.draw(cursorImage, cursorX, cursorY, cursorImage.getWidth(), cursorImage.getHeight());

        // Update Movement label position
        movementLabel.setPosition(
            wasdX + wasdFrame.getRegionWidth() - 25,
            wasdY - wasdFrame.getRegionHeight()- 20
        );

        // Update Inventory label position
        inventoryLabel.setPosition(
            tabiX + tabiFrame.getRegionWidth() - 25,
            wasdY - wasdFrame.getRegionHeight()- 20
        );

        // Update Menu label position
        menuLabel.setPosition(
            escX + escFrame.getRegionWidth() - 7,
            escY - escFrame.getRegionHeight() - 20
        );

        // Update Farming label position
        farmingLabel.setPosition(
            cursorX + cursorImage.getWidth() - 70,
            escY - escFrame.getRegionHeight() - 20
        );

        batch.end();

        // Update and render stage
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
        stage.dispose();
        batch.dispose();
        skin.dispose();
        titleFont.dispose();
        contentFont.dispose();
        movementFont.dispose();
        tutorialBackground.dispose();
        tutorialTextTexture.dispose();
        buttonTexture.dispose();
        buttonPressedTexture.dispose();
        buttonHoverTexture.dispose();
        wasdTutorialTexture.dispose();
        tabiTutorialTexture.dispose();
        escTutorialTexture.dispose();
        cursorImage.dispose();
        for (TextureRegion frame : backgroundFrames) {
            frame.getTexture().dispose();
        }
    }
}
