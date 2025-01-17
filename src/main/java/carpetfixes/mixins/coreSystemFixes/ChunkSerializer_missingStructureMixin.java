package carpetfixes.mixins.coreSystemFixes;


import carpetfixes.CarpetFixesSettings;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.EnumSet;
import java.util.Map;

@Mixin(ChunkSerializer.class)
public class ChunkSerializer_missingStructureMixin {

    private static final ThreadLocal<Boolean> preSaveList = ThreadLocal.withInitial(() -> false);


    @Inject(
            method = "readStructureReferences",
            require = 0,
            at = @At("TAIL")
    )
    private static void readInvalidStructures(ChunkPos pos, NbtCompound nbt, CallbackInfoReturnable<Map<StructureFeature<?>, LongSet>> cir) {
        if (CarpetFixesSettings.missingStructureCorruptionFix && cir.getReturnValue().remove(null) != null) {
            ChunkSerializer_missingStructureMixin.preSaveList.set(true);
        }
    }


    @Inject(
            method = "deserialize",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;setStructureReferences(Ljava/util/Map;)V",
                    shift = At.Shift.AFTER
            ))
    private static void saveWithoutInvalidStructures(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir, UpgradeData upgradeData, boolean bl, NbtList nbtList, int i, ChunkSection chunkSections[], boolean bl2, ChunkManager chunkManager, LightingProvider lightingProvider, Registry registry, long j, ChunkStatus.ChunkType k, BlendingData l, Chunk palettedContainer, NbtCompound palettedContainer2, EnumSet chunkSection, NbtCompound protoChunk) {
        if (ChunkSerializer_missingStructureMixin.preSaveList.get()) {
            ChunkSerializer_missingStructureMixin.preSaveList.set(false);
            palettedContainer.setShouldSave(true);
        }
    }
}
