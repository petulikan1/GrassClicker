package me.petulikan1.grassclicker;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.Material;

public enum Blocks {
	//POÈET POINTÙ//LEVEL//ŠANCE
    GRASS_BLOCK(1, 0, 20),
    PODZOL(2, 0, 10),
    DIRT(2, 1, 20),
    COARSE_DIRT(4, 1, 10),
    SPRUCE_LOG(5, 2, 20),
    OAK_LOG(5, 2, 20),
    BIRCH_LOG(5, 2, 20),
    ACACIA_LOG(6, 2, 10),
    DARK_OAK_LOG(6, 3, 20),
    JUNGLE_LOG(6, 3, 20),
    GRAVEL(8, 4, 20),
    SAND(8, 4, 20),
    RED_SAND(8, 4, 20),
    SANDSTONE(11, 5, 20),
    RED_SANDSTONE(11, 5, 20),
    STONE(14, 6, 20),
    GRANITE(15, 6, 20),
    ANDESITE(15, 6, 20),
    DIORITE(15, 6, 20),
    POLISHED_ANDESITE(19, 7, 20),
    POLISHED_GRANITE(19, 7, 20),
    POLISHED_DIORITE(19, 7, 20),
    COAL_ORE(25, 8, 20),
    IRON_ORE(27, 8, 20),
    GOLD_ORE(29, 9, 20),
    LAPIS_ORE(34, 9, 20),
    REDSTONE_ORE(42, 10, 20),
    DIAMOND_ORE(55, 11, 10),
    EMERALD_ORE(71, 11, 2),
    OBSIDIAN(66, 12, 10),
    CRIMSON_NYLIUM(71, 13, 20),
    WARPED_NYLIUM(71, 13, 20),
    NETHERRACK(71, 13, 20),
    NETHER_GOLD_ORE(74, 13, 20),
    CRIMSON_STEM(79, 14, 20),
    WARPED_STEM(79, 14, 20),
    CRIMSON_HYPHAE(79, 14, 20),
    WARPED_HYPHAE(79, 14, 20),
    SHROOMLIGHT(81, 14, 20),
    SOUL_SAND(83, 15, 20),
    SOUL_SOIL(83, 15, 20),
    BLACKSTONE(83, 15, 20),
    BASALT(88, 16, 20),
    POLISHED_BASALT(88, 16, 20),
    MAGMA_BLOCK(94, 16, 10),
    CRYING_OBSIDIAN(105, 17, 10),
    GLOWSTONE(100, 17, 10),
    ANCIENT_DEBRIS(120, 17, 2),
    CHISELED_POLISHED_BLACKSTONE(124, 18, 10),
    POLISHED_BLACKSTONE_BRICKS(124, 18, 10),
    POLISHED_BLACKSTONE(124, 18, 10),
    GILDED_BLACKSTONE(129, 18, 10),
    PURPUR_BLOCK(144, 20, 10),
    PURPUR_PILLAR(144, 20, 10),
    BEDROCK(1, -4, 1);
	
    private int i;
    private int i2;
    private int a;

    private Blocks(int clicks, int requiredLevel, int chance) {
        this.i = clicks;
        this.i2 = requiredLevel;
        this.a = chance;
    }

    public int getClicks() {
        return this.i;
    }

    public int getRequiredLevel() {
        return this.i2;
    }

    public int getMaxLevel() {
        return this.i2 + 3;
    }

    public Material toMaterial() {
        return Material.matchMaterial(this.name());
    }

    public static List<Blocks> getByLevel(int current) {
        List<Blocks> blocks = Lists.newArrayList();
        Blocks[] var2 = values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Blocks b = var2[var4];
            if (b.getRequiredLevel() <= current && (b.getMaxLevel() == -1 || b.getMaxLevel() >= current)) {
                blocks.add(b);
            }
        }

        return blocks;
    }

    public int getChance() {
        return this.a <= 0 ? 1 : this.a;
    }
}
