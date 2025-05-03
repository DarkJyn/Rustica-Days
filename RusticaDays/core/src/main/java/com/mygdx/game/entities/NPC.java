package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.camera.GameCamera;
import com.mygdx.game.ui.ShopUI;

public class NPC extends Sprite {
    private boolean isInteractable;
    private boolean isShowingDialog;
    private boolean isShopOpen;
    private GameCamera camera;
    private Rectangle interactionZone;  // Vùng xung quanh NPC để kiểm tra tương tác
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private ShopUI shopUI;
    private Texture fButton;
    private Texture npcHelloTextBox;
    // Current animation state
    private TextureRegion currentFrame;
    private Animation<TextureRegion> fButtonAnimation;
    private float stateTime;

    public NPC( float x, float y, String spritesheetPath, GameCamera gameCamera) {
        super(new Texture(spritesheetPath));
        this.setPosition(x, y);
        camera = gameCamera;
        // Tạo vùng tương tác xung quanh NPC
        this.interactionZone = new Rectangle(x, y, getWidth() + 50, getHeight() + 50);
        this.isInteractable = false;
        this.isShowingDialog = false;
        this.isShopOpen = false;

        // Khởi tạo font để hiển thị text
        font = new BitmapFont();
        // Khởi tạo shape renderer để vẽ hộp thoại
        shapeRenderer = new ShapeRenderer();

        // Khởi tạo giao diện cửa hàng
        shopUI = new ShopUI();

        // Load Texture
        npcHelloTextBox = new Texture("[NPC]TextBoxHello.png");
        fButton = new Texture("FbuttonAni.png");
        createAnimations();
        stateTime = 0f;
        currentFrame = fButtonAnimation.getKeyFrame(0);
    }

    public void update(Player player) {
        stateTime += Gdx.graphics.getDeltaTime();
        // Cập nhật vùng tương tác theo vị trí hiện tại của NPC
        interactionZone.setPosition(getX() - 10, getY() - 10);

        // Kiểm tra xem player có ở trong vùng tương tác không
        float playerCenterX = player.getX() + player.getWidth() / 2;
        float playerCenterY = player.getY() + player.getHeight() / 2;

        if (interactionZone.contains(playerCenterX, playerCenterY)) {
            isInteractable = true;
        } else {
            isInteractable = false;
            // Nếu người chơi rời đi và đang hiển thị hộp thoại, đóng hộp thoại
            if (isShowingDialog) {
                isShowingDialog = false;
            }
            // Nếu người chơi rời đi và cửa hàng đang mở, đóng cửa hàng
            if (isShopOpen) {
                isShopOpen = false;
            }
        }

        currentFrame = fButtonAnimation.getKeyFrame(stateTime * 2, true);

    }

    public void render(SpriteBatch batch) {
        // Vẽ NPC
//        super.draw(batch);

        // Hiển thị biểu tượng tương tác nếu player đang ở gần
        if (isInteractable && !isShowingDialog && !isShopOpen) {
            batch.end();
            batch.setProjectionMatrix(camera.getCamera().combined);
            batch.begin();
            batch.draw(currentFrame, getX(), getY() + 12, getWidth(), getHeight());
        }

        // Hiển thị hộp thoại nếu đang tương tác
        if (isShowingDialog && !isShopOpen){
            batch.end();
            batch.begin();
            batch.draw(npcHelloTextBox, getX() - 300, getY() - 400, getWidth() * 70, getHeight() * 28);
        }

        // Hiển thị cửa hàng
        if (isShopOpen) {
            shopUI.render(batch);
        }
    }
    private void createAnimations() {

        TextureRegion[][] tmp = TextureRegion.split(
            fButton,
            fButton.getWidth() / 2,
            fButton.getHeight());

        TextureRegion[] fButtonFrames = new TextureRegion[2];

        for (int i = 0; i < 2; i++) {
            fButtonFrames[i] = tmp[0][i];
        }

        fButtonAnimation = new Animation<>(2, fButtonFrames);
   }
    public void interact() {
        if (isInteractable && !isShowingDialog) {
            isShowingDialog = true;
        }
    }

    public void openShop() {
        if (isShowingDialog) {
            isShowingDialog = false;
            isShopOpen = true;
        }
    }

    public void closeShop() {
        isShopOpen = false;
    }

    // Getter/Setter cần thiết
    public Rectangle getInteractionZone() {
        return interactionZone;
    }

    public boolean isInteractable() {
        return isInteractable;
    }

    public boolean isShowingDialog() {
        return isShowingDialog;
    }

    public boolean isShopOpen() {
        return isShopOpen;
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
        shopUI.dispose();
    }
}
