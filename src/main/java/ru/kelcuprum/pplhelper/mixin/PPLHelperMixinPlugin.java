package ru.kelcuprum.pplhelper.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import ru.kelcuprum.alinlib.AlinLogger;
import ru.kelcuprum.alinlib.config.Config;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class PPLHelperMixinPlugin implements IMixinConfigPlugin {
    public static final AlinLogger LOG = new AlinLogger("PPL Helper > Mixin");
    public static boolean isInstalledABI = FabricLoader.getInstance().isModLoaded("actionbarinfo");
    public static Config config = new Config("config/pplhelper/config.json");
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(!mixinClassName.startsWith("ru.kelcuprum.pplhelper.mixin."))
            return false;
        if(mixinClassName.startsWith("ru.kelcuprum.pplhelper.mixin.ABIMixin")){
            if(isInstalledABI) LOG.warn("Mixin %s for %s loaded, %s", mixinClassName, targetClassName, "Action Bar Info installed");
            return isInstalledABI;
        }
        if(mixinClassName.startsWith("ru.kelcuprum.pplhelper.mixin.april")){
            return (LocalDate.now().getMonthValue() == 4 && LocalDate.now().getDayOfMonth() == 1) || config.getBoolean("IM_A_TEST_SUBJECT.APRIL", false);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
