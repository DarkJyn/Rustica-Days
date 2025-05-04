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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.inventory.DragAndDropHandler;
import com.mygdx.game.inventory.InventoryManager;
import com.mygdx.game.inventory.InventorySlot;
import com.mygdx.game.items.base.Item;

public class InventoryUI {
    private final Table quickbarTable;
    private final Table fullInventoryTable;
    private final InventoryManager inventoryManager;
    private final DragAndDropHandler dragHandler;
    private boolean isFullInventoryVisible = false;
    private Stage uiStage;
    private Texture slotTexture;
    private BitmapFont font;
    private SlotSelectionListener slotSelectionListener;

    public interface SlotSelectionListener {
        void onSlotSelected(int slotIndex);
    }

    public void setSlotSelectionListener(SlotSelectionListener listener) {
        this.slotSelectionListener = listener;
    }

    public InventoryUI(Stage stage, InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        this.dragHandler = new DragAndDropHandler(stage, inventoryManager, this);
        this.uiStage = stage;

        // Tải texture slot một lần duy nhất
        slotTexture = new Texture(Gdx.files.internal("Skin/InventoryUI/Inventory_slot.png"));
        font = new BitmapFont(); // Sử dụng font mặc định

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
        // Làm mới quickbar table
        quickbarTable.clear();

        // Thêm các ô vật phẩm vào quickbar
        for (int i = 0; i < 6; i++) {
            Stack slotStack = createSlotStack(i);
            quickbarTable.add(slotStack).pad(5).size(48, 60);
        }
    }

    private void createFullInventory() {
        // Tạo background cho bảng
        Texture backgroundTexture = new Texture(Gdx.files.internal("Skin/InventoryUI/InventoryBackground.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));

        // Tạo bảng để chứa ô vật phẩm
        Table inventoryTable = new Table();
        inventoryTable.setBackground(backgroundDrawable);
        inventoryTable.top();
        inventoryTable.center();
        inventoryTable.pad(10);

        // Thêm tiêu đề "Inventory" vào bảng nếu cần
        if (font != null) {
            Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
            Label inventoryLabel = new Label("INVENTORY", labelStyle);
            inventoryTable.add(inventoryLabel).colspan(6).padBottom(10).center().row();
        }

        // Thêm các ô vật phẩm vào bảng
        int index = 0;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                Stack slotStack = createSlotStack(index);
                inventoryTable.add(slotStack).pad(4).size(48, 60);
                index++;
            }
            inventoryTable.row();
        }

        fullInventoryTable.clear();
        fullInventoryTable.add(inventoryTable).expand().fill();
    }

    private Stack createSlotStack(int index) {
        InventorySlot slot = inventoryManager.getSlots().get(index);

        // Tạo Stack để xếp chồng các thành phần
        Stack slotStack = new Stack();

        // Tạo hình ảnh slot làm nền
        Image slotBackground = new Image(new TextureRegion(slotTexture));
        slotStack.add(slotBackground);

        // Nếu slot có vật phẩm, hiển thị vật phẩm lên trên slot
        if (!slot.isEmpty()) {
            Item item = slot.getItem();
            if (item.getTexture() != null) {
                Image itemImage = new Image(item.getTexture());
                slotStack.add(itemImage);

                // Thêm nhãn hiển thị số lượng nếu lớn hơn 1
                if (slot.getQuantity() > 1) {
                    Label.LabelStyle quantityStyle = new Label.LabelStyle(font, Color.WHITE);
                    Label quantityLabel = new Label(String.valueOf(slot.getQuantity()), quantityStyle);
                    Table quantityTable = new Table();
                    quantityTable.right().bottom();
                    quantityTable.add(quantityLabel).pad(2);
                    slotStack.add(quantityTable);
                }
            }
        }

        // Thêm xử lý drag-and-drop
        slotStack.setUserObject(index); // Lưu index của slot vào userObject để truy xuất khi cần
        dragHandler.addSource(slotStack, index);
        dragHandler.addTarget(slotStack, index);

        // Thêm sự kiện click để chọn slot
        slotStack.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                if (slotSelectionListener != null) {
                    slotSelectionListener.onSlotSelected(index);
                }
                return true;
            }
        });

        return slotStack;
    }

    public void toggleInventory() {
        isFullInventoryVisible = !isFullInventoryVisible;
        fullInventoryTable.setVisible(isFullInventoryVisible);

        // Đảm bảo đã được add vào stage một lần duy nhất
        if (fullInventoryTable.getStage() == null) {
            uiStage.addActor(fullInventoryTable);
        }
    }

    // Phương thức cập nhật UI khi dữ liệu thay đổi
    public void updateUI() {
        createQuickbar();
        createFullInventory();
    }

    public Table getQuickBar() {
        return quickbarTable;
    }

    public Table getFullInventory() {
        return fullInventoryTable;
    }

    public void dispose() {
        if (slotTexture != null) {
            slotTexture.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}
