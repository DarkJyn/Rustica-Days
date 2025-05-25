package com.mygdx.game.items.base;

public class ItemFactory {
    public static Item createItemByName(String name) {
        System.out.println("Creating item with name: " + name);
        // Create tools
        if (name.equals("Bình tưới nước")) {
            System.out.println("Creating WateringCan");
            return new com.mygdx.game.items.tools.WateringCan();
        }
        if (name.equals("Liềm")) {
            System.out.println("Creating Sickle");
            return new com.mygdx.game.items.tools.Sickle();
        }
        if (name.equals("Cần câu")) {
            System.out.println("Creating FishingRod");
            return new com.mygdx.game.items.tools.FishingRod();
        }

        // Create seeds
        if (name.equals("Rice Seed")) {
            System.out.println("Creating RiceSeed");
            return new com.mygdx.game.items.seeds.RiceSeed();
        }
        if (name.equals("Tomato Seed")) {
            System.out.println("Creating TomatoSeed");
            return new com.mygdx.game.items.seeds.TomatoSeed();
        }
        if (name.equals("Carrot Seed")) {
            System.out.println("Creating CarrotSeed");
            return new com.mygdx.game.items.seeds.CarrotSeed();
        }
        if (name.equals("Corn Seed")) {
            System.out.println("Creating CornSeed");
            return new com.mygdx.game.items.seeds.CornSeed();
        }
        if (name.equals("Eggplant Seed")) {
            System.out.println("Creating EggplantSeed");
            return new com.mygdx.game.items.seeds.EggplantSeed();
        }
        if (name.equals("Garlic Seed")) {
            System.out.println("Creating GarlicSeed");
            return new com.mygdx.game.items.seeds.GarlicSeed();
        }
        if (name.equals("Pumpkin Seed")) {
            System.out.println("Creating PumpkinSeed");
            return new com.mygdx.game.items.seeds.PumpkinSeed();
        }
        if (name.equals("Radish Seed")) {
            System.out.println("Creating RadishSeed");
            return new com.mygdx.game.items.seeds.RadishSeed();
        }

        // Create harvested crops
        if (name.equals("Rice")) {
            System.out.println("Creating Rice");
            return new com.mygdx.game.items.crops.Rice("Lúa", 30, 15);
        }
        if (name.equals("Tomato")) {
            System.out.println("Creating Tomato");
            return new com.mygdx.game.items.crops.Tomato("Cà chua", 60, 30);
        }
        if (name.equals("Carrot")) {
            System.out.println("Creating Carrot");
            return new com.mygdx.game.items.crops.Carrot("Cà rốt", 80, 40);
        }
        if (name.equals("Corn")) {
            System.out.println("Creating Corn");
            return new com.mygdx.game.items.crops.Corn("Ngô", 100, 50);
        }
        if (name.equals("Eggplant")) {
            System.out.println("Creating Eggplant");
            return new com.mygdx.game.items.crops.Eggplant("Bắp cải", 120, 60);
        }
        if (name.equals("Garlic")) {
            System.out.println("Creating Garlic");
            return new com.mygdx.game.items.crops.Garlic("Tỏi", 150, 75);
        }
        if (name.equals("Pumpkin")) {
            System.out.println("Creating Pumpkin");
            return new com.mygdx.game.items.crops.Pumpkin("Bí ngô", 200, 100);
        }
        if (name.equals("Radish")) {
            System.out.println("Creating Radish");
            return new com.mygdx.game.items.crops.Radish("Củ cải", 90, 45);
        }

        // Create animal products
        if (name.startsWith("Fish_")) {
            String fishType = name.substring(5); // Remove "Fish_" prefix
            try {
                System.out.println("Creating FishItem with type: " + fishType);
                com.mygdx.game.entities.animals.FishType type = com.mygdx.game.entities.animals.FishType.valueOf(fishType);
                return new com.mygdx.game.items.animalproducts.FishItem(type);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid fish type: " + fishType);
                return null;
            }
        }

        System.out.println("Failed to create item: " + name);
        return null;
    }
}
