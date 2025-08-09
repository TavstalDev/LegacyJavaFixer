package net.minecraftforge.legacyjavafixer.sort;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;

import java.util.List;
import java.util.Map;

/**
 * The `LegacyJavaSortDummyLoadingPlugin` class serves as a dummy plugin to add a cascading tweaker.
 * This allows the tweaker to be provided via the `-Dfml.coreMods.load` system property.
 * <p>
 * Created by covers1624 on 1/6/21.
 */
@IFMLLoadingPlugin.SortingIndex(-2147483648)
public class LegacyJavaSortDummyLoadingPlugin implements IFMLLoadingPlugin {

    /**
     * Constructor for the `LegacyJavaSortDummyLoadingPlugin` class.
     * Adds the custom tweaker to the `TweakClasses` list in the LaunchWrapper's blackboard.
     */
    public LegacyJavaSortDummyLoadingPlugin() {
        @SuppressWarnings("unchecked")
        List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses");
        // Add the custom tweaker to the LaunchWrapper, similar to how FMLTweaker does it.
        tweakClasses.add("net.minecraftforge.legacyjavafixer.sort.LegacyJavaSortTweaker");
    }

    /**
     * Returns an empty array of library request classes.
     * This is used for supporting Minecraft versions 1.6.4 and below.
     *
     * @return An empty array of library request class names.
     */
    public String[] getLibraryRequestClass() {
        return new String[0];
    }

    /**
     * Returns an empty array of ASM transformer class names.
     * This indicates that no ASM transformers are used by this plugin.
     *
     * @return An empty array of ASM transformer class names.
     */
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    /**
     * Returns null as no mod container class is provided by this plugin.
     *
     * @return Null, indicating no mod container class.
     */
    @Override
    public String getModContainerClass() {
        return null;
    }

    /**
     * Returns null as no setup class is provided by this plugin.
     *
     * @return Null, indicating no setup class.
     */
    @Override
    public String getSetupClass() {
        return null;
    }

    /**
     * Injects data into the plugin. This implementation does nothing.
     *
     * @param map A map of data to be injected into the plugin.
     */
    @Override
    public void injectData(Map<String, Object> map) {
        // No data injection is performed by this plugin.
    }

    /**
     * Returns null as no access transformer class is provided by this plugin.
     *
     * @return Null, indicating no access transformer class.
     */
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}