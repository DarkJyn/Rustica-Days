package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.inventory.InventoryManager;
import com.mygdx.game.inventory.InventorySlot;
import com.mygdx.game.items.base.Item;
import com.mygdx.game.sound.SoundManager;
import com.mygdx.game.ui.StatsBar;

public class SellUI {
    private Table mainTable;
    private Table tooltipTable;
    private Texture backgroundTexture;
    private Texture tooltipBackgroundTexture;
    private final InventoryManager inventoryManager;
    private final InventoryUI inventoryUI;
    private boolean isSellMenuOpen;

    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont tooltipFont;
    private StatsBar statsBar; // Để cập nhật tiền khi bán
    private Stage stage;
    private SoundManager soundManager = new SoundManager();

    public SellUI(InventoryManager inventoryManager, InventoryUI inventoryUI, StatsBar statsBar) {
        this.inventoryManager = inventoryManager;
        this.inventoryUI = inventoryUI;
        this.statsBar = statsBar;
        this.isSellMenuOpen = false;

        // Khởi tạo font
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(0.8f);

        titleFont = new BitmapFont();
        titleFont.setColor(Color.GOLD);
        titleFont.getData().setScale(1.2f);

        // Khởi tạo font cho tooltip
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20; // Kích thước font lớn hơn
        parameter.color = Color.WHITE;
        tooltipFont = generator.generateFont(parameter);
        generator.dispose();

        // Load textures
        backgroundTexture = new Texture(Gdx.files.internal("FullInventoryUI.png"));
        tooltipBackgroundTexture = new Texture(Gdx.files.internal("DescribBox.png"));
        TextureRegionDrawable tooltipBgDrawable = new TextureRegionDrawable(new TextureRegion(tooltipBackgroundTexture));

        // Khởi tạo bảng chính
        mainTable = new Table();
        mainTable.setVisible(false);

        // Khởi tạo tooltip
        tooltipTable = new Table();
        tooltipTable.setBackground(tooltipBgDrawable);
        tooltipTable.setVisible(false);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.addActor(mainTable);
        stage.addActor(tooltipTable);
    }

    public void openSellMenu(boolean open) {
        isSellMenuOpen = open;
        mainTable.setVisible(open);
        tooltipTable.setVisible(false);

        if (open) {
            createSellMenu();
        }
    }

    private void createSellMenu() {
        mainTable.clear();

        // Tạo background với kích thước cố định
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        mainTable.setBackground(backgroundDrawable);
        mainTable.setSize(312, 386); // Kích thước giống với InventoryUI

        // Thêm tiêu đề
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.GOLD);
        Label titleLabel = new Label("BÁN VẬT PHẨM", titleStyle);
//        mainTable.add(titleLabel).colspan(6).padBottom(10).padTop(5).center().row();

        // Thêm hướng dẫn
        Label.LabelStyle guideStyle = new Label.LabelStyle(font, Color.WHITE);
        Label guideLabel = new Label("Di chuột qua để xem thông tin - Click để bán", guideStyle);
//        mainTable.add(guideLabel).colspan(6).padBottom(15).center().row();

        // Thêm padding phía trên cho bảng slot
        Table slotTable = new Table();
        slotTable.padTop(15); // Dịch các slot lên trên
        slotTable.padLeft(15); // Tăng padding bên trái để dịch các slot sang trái thêm
        slotTable.padRight(15); // Tăng padding bên trái để dịch các slot sang trái thêm
        slotTable.padBottom(-5); // Tăng padding bên trái để dịch các slot sang trái thêm

        // Hiển thị các slot trong inventory
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 6; col++) {
                if (index < inventoryManager.getSlots().size()) {
                    Stack slotStack = createSellSlot(index);
                    slotTable.add(slotStack).pad(15).size(48, 60).center();
                    index++;
                }
            }
            slotTable.row();
        }

        // Thêm bảng slot vào bảng chính
        mainTable.add(slotTable).colspan(6).row();

        // Thêm nút đóng
        Table buttonTable = new Table();
        Label closeLabel = new Label("Close", guideStyle);
        closeLabel.setFontScale(1); // Tăng kích thước chữ lên 1.5 lần
        Table closeButton = new Table();
        closeButton.setBackground(createBackgroundDrawable(new Color(0.8f, 0.2f, 0.2f, 1f)));
        closeButton.add(closeLabel).pad(10); // Tăng padding của nút
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openSellMenu(false);
            }
        });

        buttonTable.add(closeButton).pad(10);
        mainTable.row();
        mainTable.add(buttonTable).colspan(6).center().padTop(10).padBottom(-50); // Tăng padding phía trên để đẩy nút xuống

        // Đặt vị trí
        mainTable.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        mainTable.setOrigin(Align.center);

        mainTable.pack();
        mainTable.setPosition(
            Gdx.graphics.getWidth() / 2f - mainTable.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - mainTable.getHeight() / 2f
        );
    }

    private Stack createSellSlot(final int index) {
        final InventorySlot slot = inventoryManager.getSlots().get(index);

        // Tạo Stack để xếp chồng các thành phần
        final Stack slotStack = new Stack();

        // Nếu slot có vật phẩm, hiển thị vật phẩm lên trên slot
        if (!slot.isEmpty()) {
            final Item item = slot.getItem();
            if (item.getTexture() != null) {
                Image itemImage = new Image(item.getTexture());
                slotStack.add(itemImage);

                // Thêm nhãn hiển thị số lượng nếu lớn hơn 1
                if (slot.getQuantity() > 1) {
                    Label.LabelStyle quantityStyle = new Label.LabelStyle(font, Color.WHITE);
                    Label quantityLabel = new Label(String.valueOf(slot.getQuantity()), quantityStyle);
                    quantityLabel.setFontScale(1.5f);
                    Table quantityTable = new Table();
                    quantityTable.setFillParent(true);
                    quantityTable.bottom().right();
                    quantityTable.add(quantityLabel).padBottom(-10);
                    slotStack.add(quantityTable);
                }

                // Thêm event listeners
                slotStack.addListener(new ClickListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        if (!slot.isEmpty()) {
                            showTooltip(item, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
                        }
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        tooltipTable.setVisible(false);
                    }

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (!slot.isEmpty()) {
                            sellItem(index);
                        }
                    }
                });
            }
        } else {
            // Nếu không có vật phẩm, không cho phép tương tác
            slotStack.setTouchable(Touchable.disabled);
        }

        return slotStack;
    }

    private void showTooltip(Item item, float x, float y) {
        tooltipTable.clear();
        tooltipTable.setVisible(true);

        // Tính giá bán (50% giá gốc)
        int sellPrice = (int)(item.getPrice() * 0.5f);

        // Thêm nội dung tooltip với font mới
        Label.LabelStyle nameStyle = new Label.LabelStyle(tooltipFont, Color.BROWN);
        Label nameLabel = new Label(item.getName(), nameStyle);
        nameLabel.setWrap(true);
        nameLabel.setAlignment(Align.left);

        Label.LabelStyle descStyle = new Label.LabelStyle(tooltipFont, Color.WHITE);
        Label descLabel = new Label(item.getDescription(), descStyle);
        descLabel.setWrap(true);
        descLabel.setAlignment(Align.left);

        Label.LabelStyle priceStyle = new Label.LabelStyle(tooltipFont, Color.CORAL);
        Label priceLabel = new Label("Cost: " + sellPrice + " Coin", priceStyle);
        priceLabel.setAlignment(Align.left);

        // Thêm padding bên trái và phải khác nhau
        tooltipTable.add(nameLabel).width(300).pad(8, 0, 8, 8).row();
        tooltipTable.add(descLabel).width(300).pad(8, 0, 8, 8).row();
        tooltipTable.add(priceLabel).width(300).pad(8, 0, 8, 8);

        tooltipTable.pack();

        // Điều chỉnh vị trí tooltip để không vượt ra ngoài màn hình
        float tooltipX = Math.min(x + 10, Gdx.graphics.getWidth() - tooltipTable.getWidth() - 10);
        float tooltipY = Math.min(y + 10, Gdx.graphics.getHeight() - tooltipTable.getHeight() - 10);
        tooltipTable.setPosition(tooltipX, tooltipY);
    }

    private void sellItem(int slotIndex) {
        InventorySlot slot = inventoryManager.getSlots().get(slotIndex);
        if (!slot.isEmpty()) {
            Item item = slot.getItem();
            int sellPrice = (int)(item.getPrice() * 0.5f);

            // Thêm tiền vào ví người chơi
            statsBar.addMoney(sellPrice);
            soundManager.playShoppingSound();

            // Giảm số lượng vật phẩm trong inventory
            inventoryManager.removeItem(item);

            // Cập nhật UI
            inventoryUI.updateUI();
            createSellMenu(); // Refresh lại UI bán hàng

            System.out.println("Đã bán " + item.getName() + " với giá " + sellPrice + " vàng");
        }
    }

    private TextureRegionDrawable createBackgroundDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        return drawable;
    }

    public void render(Batch batch) {
        // Không cần vẽ gì trong render vì đã sử dụng scene2d
    }

    public boolean isSellMenuOpen() {
        return isSellMenuOpen;
    }

    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (tooltipFont != null) {
            tooltipFont.dispose();
        }
        if (tooltipBackgroundTexture != null) {
            tooltipBackgroundTexture.dispose();
        }
    }
}
