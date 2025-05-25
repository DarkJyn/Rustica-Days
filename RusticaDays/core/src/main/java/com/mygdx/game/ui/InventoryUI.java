package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
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
    private Array<Image> quickbarHighlightImages = new Array<>();
    private Array<Action> quickbarBlinkActions = new Array<>();


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
        slotTexture = new Texture(Gdx.files.internal("Inventory_slotUI_tmp1.png"));
        font = new BitmapFont(); // Sử dụng font mặc định

        quickbarTable = new Table();
        fullInventoryTable = new Table();
        //add recently
        uiStage.addActor(quickbarTable);

        fullInventoryTable.setVisible(false);
        uiStage.addActor(fullInventoryTable);

        quickbarTable.setPosition(450, 30);
        //quickbarTable.center().bottom();
        //quickbarTable.setSize(slotTexture.getWidth(), slotTexture.getHeight());

        fullInventoryTable.setPosition(650, 500);
        //fullInventoryTable.center();
        fullInventoryTable.setVisible(false);

        createQuickbar();
        createFullInventory();
    }

    private void createQuickbar() {
        // Làm mới quickbar table
        quickbarTable.clear();
        Texture backgroundTexture = new Texture(Gdx.files.internal("QuickBarUI.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        //new
        quickbarTable.setBackground(backgroundDrawable);


        // Thêm các ô vật phẩm vào quickbar
        for (int i = 0; i < 6; i++) {
            Stack slotStack = createSlotStack(i);
            quickbarTable.add(slotStack).space(12).pad(5).size(48, 60);
            //48 60
        }
        quickbarTable.pack();
        quickbarTable.pad(15);
    }

    public void setIndex(int selectedIndex) {
        for (int i = 0; i < quickbarHighlightImages.size; i++) {
            Image highlight = quickbarHighlightImages.get(i);
            highlight.clearActions(); // Dừng nháy nếu có

            if (i == selectedIndex) {
                highlight.setVisible(true);
                highlight.addAction(quickbarBlinkActions.get(i)); // Bắt đầu nhấp nháy
            } else {
                highlight.setVisible(false); // Tắt các ô khác
            }
        }
    }


    private void createFullInventory() {
        // Tạo background cho bảng
        Texture backgroundTexture = new Texture(Gdx.files.internal("FullInventoryUI.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));

        // Tạo bảng để chứa ô vật phẩm
        Table inventoryTable = new Table();
        //inventoryTable.setSize(312, 386);
        inventoryTable.setBackground(backgroundDrawable);
        inventoryTable.top();
        inventoryTable.center();
        inventoryTable.pad(15);


        // Thêm tiêu đề "Inventory" vào bảng nếu cần
//        if (font != null) {
//            Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
//            Label inventoryLabel = new Label("INVENTORY", labelStyle);
//            inventoryTable.add(inventoryLabel).colspan(6).padBottom(10).center().row();
//        }

        // Thêm các ô vật phẩm vào bảng
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 6; col++) {
                Stack slotStack = createSlotStack(index);
                inventoryTable.add(slotStack).pad(15).size(60, 60).center();
                index++;
            }
            inventoryTable.row();
        }

        fullInventoryTable.clear();
        fullInventoryTable.add(inventoryTable).fill();
    }

    private Stack createSlotStack(int index) {
        InventorySlot slot = inventoryManager.getSlots().get(index);

        // Tạo Stack để xếp chồng các thành phần
        Stack slotStack = new Stack();

        // Tạo nền slot
        Image slotBackground = new Image(new TextureRegion(slotTexture));
        slotBackground.setSize(70, 70);  // Kích thước lớn hơn
        slotBackground.setOrigin(Align.center); // Cho phép scale
        slotStack.add(slotBackground); // Thêm trước item
        quickbarHighlightImages.add(slotBackground);

        // Tạo hiệu ứng nhấp nháy + scale
        Action blinkAction = Actions.forever(
            Actions.sequence(
                Actions.parallel(
                    Actions.fadeOut(0.5f),
                    Actions.scaleTo(1.1f, 1.1f, 0.4f)
                ),
                Actions.parallel(
                    Actions.fadeIn(0.5f),
                    Actions.scaleTo(1.0f, 1.0f, 0.4f)
                )
            )
        );
        slotBackground.addAction(blinkAction);
        quickbarBlinkActions.add(blinkAction);

        // Nếu slot có vật phẩm, hiển thị vật phẩm lên trên slot
        if (!slot.isEmpty()) {
            Item item = slot.getItem();
            if (item.getTexture() != null) {
                Image itemImage = new Image(item.getTexture());
                itemImage.setSize(48, 48);  // Tuỳ chỉnh theo slotBackground
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
            }
        }

        // Đảm bảo background luôn nằm dưới cùng
        slotStack.addActorAt(0, slotBackground);

        slotStack.removeActor(slotBackground);
        slotStack.addActor(slotBackground); // Add lại để đưa lên trên cùng


        // Gắn chỉ số index vào Stack để xử lý drag-drop
        slotStack.setUserObject(index);
        dragHandler.addSource(slotStack, index);
        dragHandler.addTarget(slotStack, index);

        // Click chọn slot
        slotStack.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                if (slotSelectionListener != null) {
                    slotSelectionListener.onSlotSelected(index);
                }
                setIndex(index);
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
