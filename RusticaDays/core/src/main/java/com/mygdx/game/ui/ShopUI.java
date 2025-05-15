package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.entities.animations.WateringEffect;
import com.mygdx.game.entities.plants.base.Plant;
import com.mygdx.game.entities.plants.types.Pumpkin;
import com.mygdx.game.inventory.InventoryManager;
import com.mygdx.game.inventory.InventorySlot;
import com.mygdx.game.items.base.Item;
import com.mygdx.game.items.seeds.*;
import com.mygdx.game.items.tools.WateringCan;

import java.util.ArrayList;
import java.util.List;

public class ShopUI{
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Texture ShopUI;
    private Texture closeButton;
    private Texture BuyButton;
    private boolean isShopOpen;

    // Các thuộc tính hiển thị
    private float shopWidth;
    private float shopHeight;
    private float shopX;
    private float shopY;

    // Nút chức năng cửa hàng
    private Rectangle closeButtonRectangle;
    private Rectangle helpButtonRectangle;
    private Rectangle cowButtonRectangle;

    private InventoryUI inventoryUI;
    private InventoryManager inventoryManager;

    //Nút mua vật phẩm
    private Rectangle buyRiceButtonRectangle;
    private Rectangle buyTomatoButtonRectangle;
    private Rectangle buyCarrotButtonRectangle;
    private Rectangle buyCabbageButtonRectangle;
    private Rectangle buyStrawberryButtonRectangle;
    private Rectangle buyPumpkinButtonRectangle;
    private Rectangle buyGarlicButtonRectangle;
    private Rectangle buyRadishButtonRectangle;

    // List Danh sách hạt giống trong cửa hàng
    private List<Seed> seedItems;

    public ShopUI(InventoryManager inventoryManager,InventoryUI inventoryUI) {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        this.isShopOpen = true;
        this.inventoryManager = inventoryManager;
        this.inventoryUI = inventoryUI;
        // Load Texture
        ShopUI = new Texture("ShopUI.png");
        closeButton = new Texture("CloseButton.png");
        BuyButton = new Texture("BuyButton.png");
        // Thiết lập kích thước và vị trí cửa hàng
        shopWidth = Gdx.graphics.getWidth() - 300;
        shopHeight = Gdx.graphics.getHeight() - 200;
        shopX = 150;
        shopY = 100;

        // Thiết lập nút đóng (góc trên bên phải)
        closeButtonRectangle = new Rectangle(shopX + shopWidth - 130, shopY + shopHeight - 100, 50, 50);
        helpButtonRectangle = new Rectangle(shopX + 90, shopY + shopHeight - 60, 40, 40);
        cowButtonRectangle = new Rectangle(shopX + 150, shopY + shopHeight - 60, 40, 40);
        buyRiceButtonRectangle = new Rectangle(shopX + 355, shopY + shopHeight - 260, 110, 43);
        buyTomatoButtonRectangle = new Rectangle(shopX + shopWidth - 200, shopY + shopHeight - 260, 110, 43);
        buyCarrotButtonRectangle = new Rectangle(shopX + shopWidth - 200, shopY + 208, 110, 43);
        buyCabbageButtonRectangle = new Rectangle(shopX + 355, shopY + 70, 110, 43);
        buyStrawberryButtonRectangle = new Rectangle(shopX + shopWidth - 200, shopY + 70, 110, 43);
        buyPumpkinButtonRectangle = new Rectangle(shopX + 355, shopY + shopHeight - 398, 110, 43);
        buyRadishButtonRectangle = new Rectangle(shopX + shopWidth - 200, shopY + shopHeight - 398, 110, 43);
        buyGarlicButtonRectangle = new Rectangle(shopX + 355, shopY + 208, 110, 43);
    }

    public void render(SpriteBatch batch) {
        if(isShopOpen){
            batch.end();
            batch.begin();
            batch.draw(ShopUI, shopX, shopY, shopWidth, shopHeight);
            batch.draw(closeButton, closeButtonRectangle.x, closeButtonRectangle.y, closeButtonRectangle.width, closeButtonRectangle.height);
            batch.draw(BuyButton,buyRiceButtonRectangle.x,buyRiceButtonRectangle.y,buyRiceButtonRectangle.width,buyRiceButtonRectangle.height);
            batch.draw(BuyButton,buyTomatoButtonRectangle.x,buyTomatoButtonRectangle.y,buyTomatoButtonRectangle.width,buyTomatoButtonRectangle.height);
            batch.draw(BuyButton,buyCarrotButtonRectangle.x,buyCarrotButtonRectangle.y,buyCarrotButtonRectangle.width,buyCarrotButtonRectangle.height);
            batch.draw(BuyButton,buyStrawberryButtonRectangle.x,buyStrawberryButtonRectangle.y,buyStrawberryButtonRectangle.width,buyStrawberryButtonRectangle.height);
            batch.draw(BuyButton,buyCabbageButtonRectangle.x,buyCabbageButtonRectangle.y,buyCabbageButtonRectangle.width,buyCabbageButtonRectangle.height);
            batch.draw(BuyButton,buyPumpkinButtonRectangle.x,buyPumpkinButtonRectangle.y,buyPumpkinButtonRectangle.width,buyPumpkinButtonRectangle.height);
            batch.draw(BuyButton,buyGarlicButtonRectangle.x,buyGarlicButtonRectangle.y,buyGarlicButtonRectangle.width,buyGarlicButtonRectangle.height);
            batch.draw(BuyButton,buyRadishButtonRectangle.x,buyRadishButtonRectangle.y,buyRadishButtonRectangle.width,buyRadishButtonRectangle.height);
            handleShopInput();
        }
    }

    private void handleShopInput() {
        // Xử lý click chuột
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            // Chuyển đổi tọa độ Y từ hệ tọa độ chuột sang hệ tọa độ của game
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Kiểm tra nếu click vào nút đóng
            if (closeButtonRectangle.contains(mouseX, mouseY)) {
                isShopOpen = false;
                System.out.println("Close button clicked, shop is now closed");
            }
            else if(buyTomatoButtonRectangle.contains(mouseX, mouseY)) {
                TomatoSeed tomatoSeed = new TomatoSeed();
                inventoryManager.addItem(tomatoSeed,1);
                inventoryUI.updateUI();
                System.out.println("buy tomato");
            }
            else if(buyCarrotButtonRectangle.contains(mouseX, mouseY)) {
                CarrotSeed carrotSeed = new CarrotSeed();
                inventoryManager.addItem(carrotSeed,1);
                inventoryUI.updateUI();
                System.out.println("buy carrot");
            }
            else if (buyCabbageButtonRectangle.contains(mouseX, mouseY)) {
                EggplantSeed eggplantSeed = new EggplantSeed();
                inventoryManager.addItem(eggplantSeed,1);
                inventoryUI.updateUI();
                System.out.println("buy cabbage");
            }
            else if(buyStrawberryButtonRectangle.contains(mouseX,mouseY)){
                CornSeed cornSeed = new CornSeed();
                inventoryManager.addItem(cornSeed,1);
                inventoryUI.updateUI();
                System.out.println("buy straw");
            }
            else if(buyRiceButtonRectangle.contains(mouseX,mouseY)){
                RiceSeed riceSeed = new RiceSeed();
                inventoryManager.addItem(riceSeed,1);
                inventoryUI.updateUI();
                System.out.println("buy rice");
            }
            else if(buyPumpkinButtonRectangle.contains(mouseX,mouseY)){
                PumpkinSeed pumpkinSeed = new PumpkinSeed();
                inventoryManager.addItem(pumpkinSeed,1);
                inventoryUI.updateUI();
                System.out.println("buy pumpkin");
            }
            else if (buyRadishButtonRectangle.contains(mouseX,mouseY)) {
                RadishSeed radishSeed = new RadishSeed();
                inventoryManager.addItem(radishSeed,1);
                inventoryUI.updateUI();
                System.out.println("buy radish");
            }
            else if(buyGarlicButtonRectangle.contains(mouseX,mouseY)) {
                GarlicSeed garlicSeed = new GarlicSeed();
                inventoryManager.addItem(garlicSeed,1);
                inventoryUI.updateUI();
                System.out.println("buy garlic");
            }
        }
    }
    public boolean isShopOpen() {
        return isShopOpen;
    }
    public void openShop(boolean status){
        isShopOpen = status;
    }
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}
