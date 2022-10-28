package net.numismaticclaim.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "numismaticclaim")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class NumismaticClaimConfig implements ConfigData {

    @Comment("Price per claim")
    public long claim_price = 10000;
    @Comment("Current extra claims * this * claim_price = total price")
    public float claim_price_modificator = 0.1f;
    @Comment("Max amount of bonus chunk claims")
    public int max_bonus_claims = 200;
    @Comment("If false only villagers with nbt NumismaticClaimTrader = true sell")
    public boolean allow_all_villagers = false;
}