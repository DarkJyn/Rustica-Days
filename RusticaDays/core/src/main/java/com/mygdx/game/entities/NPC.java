package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.camera.GameCamera;
import com.mygdx.game.inventory.InventoryManager;
import com.mygdx.game.ui.InventoryUI;
import com.mygdx.game.ui.NPCInteractionMenu;
import com.mygdx.game.ui.SellUI;
import com.mygdx.game.ui.ShopUI;
import com.mygdx.game.ui.StatsBar;

public class NPC extends Sprite {
    private boolean isInteractable;
    private boolean isShowingDialog;
    private boolean isShopOpen;
    private boolean isMenuOpen;
    private boolean isSellMenuOpen;
    private GameCamera camera;
    private Rectangle interactionZone;  // Vùng xung quanh NPC để kiểm tra tương tác
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private ShopUI shopUI;
    private SellUI sellUI;
    private Texture fButton;
    private Texture npcHelloTextBox;
    private InventoryUI inventoryUI;
    private InventoryManager inventoryManager;
    private StatsBar statsBar;
    // Current animation state
    private TextureRegion currentFrame;
    private Animation<TextureRegion> fButtonAnimation;
    private float stateTime;
    // Thêm menu tương tác
    private NPCInteractionMenu interactionMenu;

    public NPC(float x, float y, String spritesheetPath, GameCamera gameCamera, InventoryManager inventoryManager, InventoryUI inventoryUI, StatsBar statsBar) {
        super(new Texture(spritesheetPath));
        this.setPosition(x, y);
        camera = gameCamera;
        this.inventoryManager = inventoryManager;
        this.inventoryUI = inventoryUI;
        this.statsBar = statsBar;
        // Tạo vùng tương tác xung quanh NPC
        this.interactionZone = new Rectangle(x, y, getWidth() + 50, getHeight() + 50);
        this.isInteractable = false;
        this.isShowingDialog = false;
        this.isShopOpen = false;
        this.isMenuOpen = false;
        this.isSellMenuOpen = false;

        // Khởi tạo font để hiển thị text
        font = new BitmapFont();
        // Khởi tạo shape renderer để vẽ hộp thoại
        shapeRenderer = new ShapeRenderer();

        // Khởi tạo giao diện cửa hàng
        shopUI = new ShopUI(inventoryManager, inventoryUI);

        // Khởi tạo giao diện bán hàng
        sellUI = new SellUI(inventoryManager, inventoryUI, statsBar);

        // Load Texture
        npcHelloTextBox = new Texture("[NPC]TextBoxHello.png");
        fButton = new Texture("FbuttonAni.png");
        createAnimations();
        stateTime = 0f;
        currentFrame = fButtonAnimation.getKeyFrame(0);

        // Khởi tạo menu tương tác
        interactionMenu = new NPCInteractionMenu();
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
            // Nếu người chơi rời đi và menu đang mở, đóng menu
            if (isMenuOpen) {
                isMenuOpen = false;
                interactionMenu.setVisible(false);
            }
            // Nếu người chơi rời đi và cửa hàng đang mở, đóng cửa hàng
            if (isShopOpen) {
                isShopOpen = false;
            }
            // Nếu người chơi rời đi và menu bán hàng đang mở, đóng menu bán hàng
            if (isSellMenuOpen) {
                isSellMenuOpen = false;
                sellUI.openSellMenu(false);
            }
        }

        currentFrame = fButtonAnimation.getKeyFrame(stateTime * 2, true);

        // Cập nhật menu tương tác nếu đang mở
        if (isMenuOpen) {
            interactionMenu.update();

            // Xử lý lựa chọn từ menu
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                handleMenuSelection(interactionMenu.getSelectedOption());
            }
        }
    }

    public void render(SpriteBatch batch) {
        // Vẽ NPC
        // super.draw(batch);

        // Hiển thị biểu tượng tương tác nếu player đang ở gần
        if (isInteractable && !isShowingDialog && !isShopOpen && !isMenuOpen && !isSellMenuOpen) {
            batch.end();
            batch.setProjectionMatrix(camera.getCamera().combined);
            batch.begin();
            batch.draw(currentFrame, getX(), getY() + 12, getWidth(), getHeight());
        }

        // Hiển thị hộp thoại nếu đang tương tác
        if (isShowingDialog && !isShopOpen && !isMenuOpen && !isSellMenuOpen){
            batch.end();
            batch.begin();
            batch.draw(npcHelloTextBox, getX() - 300, getY() - 400, getWidth(), getHeight());
        }

        // Hiển thị menu tương tác
        if (isMenuOpen) {
            interactionMenu.render(batch);
        }

        // Hiển thị cửa hàng
        if (isShopOpen) {
            shopUI.render(batch);
        }

        // Hiển thị menu bán hàng
        if (isSellMenuOpen) {
            sellUI.render(batch);
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
        if (isInteractable && !isShowingDialog && !isMenuOpen && !isShopOpen && !isSellMenuOpen) {
            // Khi tương tác, mở menu thay vì hộp thoại
            isShowingDialog = true;
            showInteractionMenu();
        }
    }

    private void showInteractionMenu() {
        isMenuOpen = true;
        isShowingDialog = false;

        // Đặt vị trí của menu gần NPC
        interactionMenu.setPosition(getX() - 175, getY() - 300);
        interactionMenu.setVisible(true);
    }

    private void handleMenuSelection(int selection) {
        isMenuOpen = false;
        interactionMenu.setVisible(false);

        switch (selection) {
            case 0: // Mua
                openShop();
                break;
            case 1: // Bán
                openSellMenu();
                break;
        }
    }

    public void openShop() {
        shopUI.openShop(true);
        isShopOpen = true;
    }

    public void openSellMenu() {
        sellUI.openSellMenu(true);
        isSellMenuOpen = true;
    }

    public void closeShop() {
        isShopOpen = false;
        shopUI.openShop(false);
    }

    public void closeSellMenu() {
        isSellMenuOpen = false;
        sellUI.openSellMenu(false);
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

    public boolean isMenuOpen() {
        return isMenuOpen;
    }

    public boolean isShopOpen() {
        return isShopOpen && shopUI.isShopOpen();
    }

    public boolean isSellMenuOpen() {
        return isSellMenuOpen && sellUI.isSellMenuOpen();
    }

    public void setStage(com.badlogic.gdx.scenes.scene2d.Stage stage) {
        sellUI.setStage(stage);
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
        shopUI.dispose();
        sellUI.dispose();
        interactionMenu.dispose();
    }
}
