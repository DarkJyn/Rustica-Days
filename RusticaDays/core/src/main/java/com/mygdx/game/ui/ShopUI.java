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
    private Texture closeButtonPressed;
    private Texture BuyButton;
    private Texture BuyButtonPressed;
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
    private StatsBar statsBar;

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

    // Biến để theo dõi nút đang được nhấn
    private Rectangle pressedButton;
    private boolean isCloseButtonPressed;

    // Giá của các loại hạt giống
    private static final int RICE_SEED_PRICE = 30;
    private static final int TOMATO_SEED_PRICE = 50;
    private static final int CARROT_SEED_PRICE = 40;
    private static final int CABBAGE_SEED_PRICE = 45;
    private static final int STRAWBERRY_SEED_PRICE = 60;
    private static final int PUMPKIN_SEED_PRICE = 30;
    private static final int GARLIC_SEED_PRICE = 30;
    private static final int RADISH_SEED_PRICE = 30;

    public ShopUI(InventoryManager inventoryManager, InventoryUI inventoryUI, StatsBar statsBar) {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        this.isShopOpen = true;
        this.inventoryManager = inventoryManager;
        this.inventoryUI = inventoryUI;
        this.statsBar = statsBar;
        // Load Texture
        ShopUI = new Texture("ShopUI.png");
        closeButton = new Texture("CloseButton.png");
        closeButtonPressed = new Texture("CloseButtonPressed.png");
        BuyButton = new Texture("BuyButton.png");
        BuyButtonPressed = new Texture("BuyButtonPressed.png");
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
            
            // Vẽ nút đóng với texture tương ứng
            if (isCloseButtonPressed) {
                batch.draw(closeButtonPressed, closeButtonRectangle.x, closeButtonRectangle.y, closeButtonRectangle.width, closeButtonRectangle.height);
            } else {
                batch.draw(closeButton, closeButtonRectangle.x, closeButtonRectangle.y, closeButtonRectangle.width, closeButtonRectangle.height);
            }
            
            // Vẽ các nút Buy với texture tương ứng
            drawBuyButton(batch, buyRiceButtonRectangle);
            drawBuyButton(batch, buyTomatoButtonRectangle);
            drawBuyButton(batch, buyCarrotButtonRectangle);
            drawBuyButton(batch, buyStrawberryButtonRectangle);
            drawBuyButton(batch, buyCabbageButtonRectangle);
            drawBuyButton(batch, buyPumpkinButtonRectangle);
            drawBuyButton(batch, buyGarlicButtonRectangle);
            drawBuyButton(batch, buyRadishButtonRectangle);
            
            handleShopInput();
        }
    }

    private void drawBuyButton(SpriteBatch batch, Rectangle buttonRect) {
        if (buttonRect == pressedButton) {
            batch.draw(BuyButtonPressed, buttonRect.x, buttonRect.y, buttonRect.width, buttonRect.height);
        } else {
            batch.draw(BuyButton, buttonRect.x, buttonRect.y, buttonRect.width, buttonRect.height);
        }
    }

    private void handleShopInput() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Reset pressedButton if mouse is not pressed
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            pressedButton = null;
            isCloseButtonPressed = false;
        }

        // Xử lý click chuột
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Kiểm tra nếu click vào nút đóng
            if (closeButtonRectangle.contains(mouseX, mouseY)) {
                isCloseButtonPressed = true;
                isShopOpen = false;
                System.out.println("Close button clicked, shop is now closed");
            }
            // Kiểm tra các nút Buy
            else if(buyTomatoButtonRectangle.contains(mouseX, mouseY)) {
                if (canAfford(TOMATO_SEED_PRICE)) {
                    pressedButton = buyTomatoButtonRectangle;
                    TomatoSeed tomatoSeed = new TomatoSeed();
                    inventoryManager.addItem(tomatoSeed,1);
                    deductMoney(TOMATO_SEED_PRICE);
                    inventoryUI.updateUI();
                    System.out.println("Bought tomato seed for " + TOMATO_SEED_PRICE + " coins");
                } else {
                    System.out.println("Not enough money to buy tomato seed!");
                }
            }
            else if(buyCarrotButtonRectangle.contains(mouseX, mouseY)) {
                if (canAfford(CARROT_SEED_PRICE)) {
                    pressedButton = buyCarrotButtonRectangle;
                    CarrotSeed carrotSeed = new CarrotSeed();
                    inventoryManager.addItem(carrotSeed,1);
                    deductMoney(CARROT_SEED_PRICE);
                    inventoryUI.updateUI();
                    System.out.println("Bought carrot seed for " + CARROT_SEED_PRICE + " coins");
                } else {
                    System.out.println("Not enough money to buy carrot seed!");
                }
            }
            else if (buyCabbageButtonRectangle.contains(mouseX, mouseY)) {
                if (canAfford(CABBAGE_SEED_PRICE)) {
                    pressedButton = buyCabbageButtonRectangle;
                    EggplantSeed eggplantSeed = new EggplantSeed();
                    inventoryManager.addItem(eggplantSeed,1);
                    deductMoney(CABBAGE_SEED_PRICE);
                    inventoryUI.updateUI();
                    System.out.println("Bought cabbage seed for " + CABBAGE_SEED_PRICE + " coins");
                } else {
                    System.out.println("Not enough money to buy cabbage seed!");
                }
            }
            else if(buyStrawberryButtonRectangle.contains(mouseX,mouseY)){
                if (canAfford(STRAWBERRY_SEED_PRICE)) {
                    pressedButton = buyStrawberryButtonRectangle;
                    CornSeed cornSeed = new CornSeed();
                    inventoryManager.addItem(cornSeed,1);
                    deductMoney(STRAWBERRY_SEED_PRICE);
                    inventoryUI.updateUI();
                    System.out.println("Bought strawberry seed for " + STRAWBERRY_SEED_PRICE + " coins");
                } else {
                    System.out.println("Not enough money to buy strawberry seed!");
                }
            }
            else if(buyRiceButtonRectangle.contains(mouseX,mouseY)){
                if (canAfford(RICE_SEED_PRICE)) {
                    pressedButton = buyRiceButtonRectangle;
                    RiceSeed riceSeed = new RiceSeed();
                    inventoryManager.addItem(riceSeed,1);
                    deductMoney(RICE_SEED_PRICE);
                    inventoryUI.updateUI();
                    System.out.println("Bought rice seed for " + RICE_SEED_PRICE + " coins");
                } else {
                    System.out.println("Not enough money to buy rice seed!");
                }
            }
            else if(buyPumpkinButtonRectangle.contains(mouseX,mouseY)){
                if (canAfford(PUMPKIN_SEED_PRICE)) {
                    pressedButton = buyPumpkinButtonRectangle;
                    PumpkinSeed pumpkinSeed = new PumpkinSeed();
                    inventoryManager.addItem(pumpkinSeed,1);
                    deductMoney(PUMPKIN_SEED_PRICE);
                    inventoryUI.updateUI();
                    System.out.println("Bought pumpkin seed for " + PUMPKIN_SEED_PRICE + " coins");
                } else {
                    System.out.println("Not enough money to buy pumpkin seed!");
                }
            }
            else if (buyRadishButtonRectangle.contains(mouseX,mouseY)) {
                if (canAfford(RADISH_SEED_PRICE)) {
                    pressedButton = buyRadishButtonRectangle;
                    RadishSeed radishSeed = new RadishSeed();
                    inventoryManager.addItem(radishSeed,1);
                    deductMoney(RADISH_SEED_PRICE);
                    inventoryUI.updateUI();
                    System.out.println("Bought radish seed for " + RADISH_SEED_PRICE + " coins");
                } else {
                    System.out.println("Not enough money to buy radish seed!");
                }
            }
            else if(buyGarlicButtonRectangle.contains(mouseX,mouseY)) {
                if (canAfford(GARLIC_SEED_PRICE)) {
                    pressedButton = buyGarlicButtonRectangle;
                    GarlicSeed garlicSeed = new GarlicSeed();
                    inventoryManager.addItem(garlicSeed,1);
                    deductMoney(GARLIC_SEED_PRICE);
                    inventoryUI.updateUI();
                    System.out.println("Bought garlic seed for " + GARLIC_SEED_PRICE + " coins");
                } else {
                    System.out.println("Not enough money to buy garlic seed!");
                }
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
        if (BuyButtonPressed != null) {
            BuyButtonPressed.dispose();
        }
        if (closeButtonPressed != null) {
            closeButtonPressed.dispose();
        }
    }

    // Phương thức để kiểm tra và trừ tiền khi mua
    private boolean canAfford(int price) {
        return statsBar.getMoney() >= price;
    }

    private void deductMoney(int amount) {
        statsBar.setMoney(statsBar.getMoney() - amount);
    }
}
