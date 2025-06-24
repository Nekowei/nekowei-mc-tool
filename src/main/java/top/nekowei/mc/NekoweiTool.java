package top.nekowei.mc;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.BlockState;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class NekoweiTool implements ModInitializer {
	public static final String MOD_ID = "nekowei-tool";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> {
                    dispatcher.register(CommandManager.literal("removeInvisibleBlock")
                            .then(CommandManager.argument("x1", IntegerArgumentType.integer())
                                    .then(CommandManager.argument("y1", IntegerArgumentType.integer())
                                            .then(CommandManager.argument("z1", IntegerArgumentType.integer())
                                                    .then(CommandManager.argument("x2", IntegerArgumentType.integer())
                                                            .then(CommandManager.argument("y2", IntegerArgumentType.integer())
                                                                    .then(CommandManager.argument("z2", IntegerArgumentType.integer())
                                                                            .executes(this::checkRange)
                                                                    ))))))
                    );
                });

		LOGGER.info("Hello Fabric world!");
	}

    private int checkRange(CommandContext<ServerCommandSource> context) {
        int x1 = IntegerArgumentType.getInteger(context, "x1");
        int y1 = IntegerArgumentType.getInteger(context, "y1");
        int z1 = IntegerArgumentType.getInteger(context, "z1");
        int x2 = IntegerArgumentType.getInteger(context, "x2");
        int y2 = IntegerArgumentType.getInteger(context, "y2");
        int z2 = IntegerArgumentType.getInteger(context, "z2");
        context.getSource().sendFeedback(() -> Text.literal("Called /removeInvisibleBlock: "
                + x1 + " " + y1 + " " + z1 + "" + x2 + " " + y2 + " " + z2), false);

        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        ServerWorld world = player.getServerWorld();
        // 确定包围盒的最小和最大坐标
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        int maxZ = Math.max(z1, z2);

        Map<String, List<Vec3i>> map = new HashMap<>();

        BlockPos.Mutable mutPos = new BlockPos.Mutable();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    mutPos.set(x, y, z);
                    BlockState state = world.getBlockState(mutPos);
                    if (state.isAir()) {
                        continue;
                    }
                    mutPos.set(x + 1, y, z);
                    BlockState s1 = world.getBlockState(mutPos);
                    mutPos.set(x - 1, y, z);
                    BlockState s2 = world.getBlockState(mutPos);
                    mutPos.set(x, y + 1, z);
                    BlockState s3 = world.getBlockState(mutPos);
                    mutPos.set(x, y - 1, z);
                    BlockState s4 = world.getBlockState(mutPos);
                    mutPos.set(x, y, z + 1);
                    BlockState s5 = world.getBlockState(mutPos);
                    mutPos.set(x, y, z - 1);
                    BlockState s6 = world.getBlockState(mutPos);
                    if (s1.isAir() || s1.isTransparent()
                            || s2.isAir() || s2.isTransparent()
                            || s3.isAir() || s3.isTransparent()
                            || s4.isAir() || s4.isTransparent()
                            || s5.isAir() || s5.isTransparent()
                            || s6.isAir() || s6.isTransparent()) {
                        continue;
                    }
                    var c = new Vec3i(x, y, z);
                    map.compute(state.getBlock().getTranslationKey(),  (k, v) -> {
                        if (v == null) {
                            v = new ArrayList<>();
                        }
                        v.add(c);
                        return v;
                    });
                }
            }
        }

        map.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().size()))
                .forEach(e -> context.getSource()
                        .sendFeedback(() -> Text.literal(e.getKey() + ":" + e.getValue().size()), false));
        map.values().forEach(v -> v.forEach(c ->
                world.removeBlock(new BlockPos(c), false)));
        return 1;
    }

}