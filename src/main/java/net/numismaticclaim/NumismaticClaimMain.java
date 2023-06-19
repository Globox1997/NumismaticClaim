package net.numismaticclaim;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.numismaticclaim.access.VillagerAccess;
import net.numismaticclaim.config.NumismaticClaimConfig;
import net.numismaticclaim.network.NumismaticClaimServerPacket;

public class NumismaticClaimMain implements ModInitializer {

    public static final boolean isVillagerQuestsLoaded = FabricLoader.getInstance().isModLoaded("villagerquests");
    public static NumismaticClaimConfig CONFIG = new NumismaticClaimConfig();

    @Override
    public void onInitialize() {
        AutoConfig.register(NumismaticClaimConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(NumismaticClaimConfig.class).getConfig();
        NumismaticClaimServerPacket.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((CommandManager.literal("summon").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(2);
            })).then(CommandManager.literal("numismaticclaim:villager").then(CommandManager.argument("pos", Vec3ArgumentType.vec3()).executes((commandContext) -> {
                return executeSummon(commandContext.getSource(), Vec3ArgumentType.getVec3(commandContext, "pos"));
            }))));

        });
    }

    private static int executeSummon(ServerCommandSource source, Vec3d pos) {
        World world = source.getWorld();
        VillagerEntity villagerEntity = new VillagerEntity(EntityType.VILLAGER, world);
        villagerEntity.refreshPositionAndAngles(BlockPos.ofFloored(pos), source.getPlayer().getYaw(), 0f);
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean("NumismaticClaimTrader", true);
        villagerEntity.initialize(source.getWorld(), world.getLocalDifficulty(BlockPos.ofFloored(pos)), SpawnReason.COMMAND, null, nbtCompound);
        ((VillagerAccess) villagerEntity).setNumismaticClaimTrader(true);
        world.spawnEntity(villagerEntity);
        source.sendFeedback(() -> Text.translatable("commands.summon.success", villagerEntity.getDisplayName()), true);
        return 1;
    }

}
