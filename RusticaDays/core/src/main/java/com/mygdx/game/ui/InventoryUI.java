package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.inventory.DragAndDropHandler;
import com.mygdx.game.inventory.InventoryManager;
import com.mygdx.game.inventory.InventorySlot;

public class InventoryUI {
    private final Table quickbarTable;
    private final Table fullInventoryTable;
    private final InventoryManager inventoryManager;
    private final DragAndDropHandler dragHandler;
    private boolean isFullInventoryVisible = false;
    private Stage uiStage;

    public InventoryUI(Stage stage, InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        this.dragHandler = new DragAndDropHandler(stage);
        this.uiStage = stage;

        quickbarTable = new Table();
        fullInventoryTable = new Table();

        fullInventoryTable.setVisible(false);
        uiStage.addActor(fullInventoryTable);

        quickbarTable.setPosition(700, 50);
        fullInventoryTable.setPosition(700, 500);
        fullInventoryTable.setVisible(false);

        createQuickbar();
        createFullInventory();
    }

    private void createQuickbar() {
        for (int i = 0; i < 6; i++) {
            Button btn = createSlotButton(i);
            quickbarTable.add(btn).pad(5);
        }
    }

    private void createFullInventory() {
        // Tạo background cho bảng (nâu)
        Texture backgroundTexture = new Texture(Gdx.files.internal("Skin/InventoryUI/InventoryBackground.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));

        // Tạo bảng để chứa ô vật phẩm
        Table inventoryTable = new Table();
        inventoryTable.setBackground(backgroundDrawable);
        inventoryTable.top();
        inventoryTable.center();
        inventoryTable.pad(10);

        // Tạo label cho tiêu đề "Inventory"
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        //labelStyle.font = new BitmapFont(Gdx.files.internal("Font/PixeloidSansBold.fnt"));
        //Label inventoryLabel = new Label("Inventory", labelStyle);
        //inventoryLabel.setFontScale(2); // Phóng to chữ "Inventory"

        // Thêm tiêu đề vào bảng
        //inventoryTable.add(inventoryLabel).colspan(6).padBottom(10).center().row();

        // Thêm các ô vật phẩm vào bảng
        int index = 0;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                Button slotButton = createSlotButton(index);
                inventoryTable.add(slotButton).pad(4).size(80);
                index++;
            }
            inventoryTable.row();
        }

        fullInventoryTable.clear();
        fullInventoryTable.add(inventoryTable).expand().fill();
    }


    private Button createSlotButton(int index) {
        InventorySlot slot = inventoryManager.getSlots().get(index);
        TextureRegion region = slot.isEmpty() ? null : slot.getItem().getTexture();

        // Kiểm tra nếu region là null và thay thế bằng TextureRegion từ file hình ảnh PNG
        if (region == null) {
            // Đảm bảo bạn có đường dẫn chính xác tới file hình ảnh PNG
            Texture defaultTexture = new Texture(Gdx.files.internal("Skin/InventoryUI/Inventory_slot.png"));
            region = new TextureRegion(defaultTexture); // Sử dụng TextureRegion để tạo drawable
        }

        // Sử dụng TextureRegionDrawable cho button
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);
        Button button = new Button(drawable);

        // Thêm chức năng kéo thả (drag-and-drop)
        dragHandler.addSource(button);
        dragHandler.addTarget(button);

        return button;
    }

    public void toggleInventory() {
        isFullInventoryVisible = !isFullInventoryVisible;
        fullInventoryTable.setVisible(isFullInventoryVisible);

        // Đảm bảo đã được add vào stage một lần duy nhất
        if (fullInventoryTable.getStage() == null) {
            uiStage.addActor(fullInventoryTable);
        }
    }


    public Table getQuickBar() {
        return quickbarTable;
    }

    public Table getFullInventory() {
        return fullInventoryTable;
    }

}
