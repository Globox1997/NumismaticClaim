package net.numismaticclaim.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "numismaticclaim")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class NumismaticClaimConfig implements ConfigData {

    @Comment("Price per claim")
    public long claim_price = 10000;
    @Comment("Current extra claims * this * claim_price + claim_price = total price")
    public float claim_price_modificator = 0.1f;
    @Comment("Max amount of bonus chunk claims")
    public int max_bonus_claims = 200;
    @Comment("If false only villagers with nbt NumismaticClaimTrader = true sell")
    public boolean allow_all_villagers = false;

    @ConfigEntry.Category("overworld_settings")
    @Comment("All overworld settings apply only to wilderness")
    public boolean overworldPlayerBlockDestroy = true;
    @ConfigEntry.Category("overworld_settings")
    @Comment("Restrict block placement and specific tool rightclick")
    public boolean overworldPlayerSpecificItemUse = true;
    @ConfigEntry.Category("overworld_settings")
    public boolean overworldMobGrief = true;
    @ConfigEntry.Category("overworld_settings")
    public boolean overworldRaids = true;
    @ConfigEntry.Category("overworld_settings")
    public boolean overworldCropTrample = true;

}