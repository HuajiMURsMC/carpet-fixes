package carpetfixes.mixins.optimizations.random;

import carpetfixes.CarpetFixesSettings;
import carpetfixes.helpers.XoroshiroCustomRandom;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(GeneratorOptions.class)
public class GeneratorOptions_randomMixin {


    @Redirect(
            method = "getDefaultOptions(Lnet/minecraft/util/registry/DynamicRegistryManager;)Lnet/minecraft/world/gen/GeneratorOptions;",
            at = @At(
                    value = "NEW",
                    target = "java/util/Random"
            )
    )
    private static Random customRandomDefault() {
        return CarpetFixesSettings.optimizedRandom ? new XoroshiroCustomRandom() : new Random();
    }


    @Redirect(
            method = "fromProperties(Lnet/minecraft/util/registry/DynamicRegistryManager;Ljava/util/Properties;)Lnet/minecraft/world/gen/GeneratorOptions;",
            at = @At(
                    value = "NEW",
                    target = "java/util/Random"
            )
    )
    private static Random customRandom() {
        return CarpetFixesSettings.optimizedRandom ? new XoroshiroCustomRandom() : new Random();
    }
}
