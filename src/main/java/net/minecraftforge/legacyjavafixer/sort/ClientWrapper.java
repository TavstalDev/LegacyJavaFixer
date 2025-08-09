package net.minecraftforge.legacyjavafixer.sort;

import net.minecraft.launchwrapper.Launch;

/**
 * The `ClientWrapper` class is responsible for injecting a dummy coremod into the
 * system properties and then launching the Minecraft client using the `Launch` class.
 * This is primarily used to ensure compatibility with legacy Java versions.
 * <p>
 * Created by covers1624 on 1/6/21.
 */
public class ClientWrapper {

    // System property key for loading Forge Mod Loader (FML) coremods.
    private static final String FML_COREMODS_SYS_PROP = "fml.coreMods.load";

    // Fully qualified name of the dummy coremod plugin to be injected.
    private static final String DUMMY_PLUGIN = "net.minecraftforge.legacyjavafixer.sort.LegacyJavaSortDummyLoadingPlugin";

    /**
     * The main entry point of the application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        // Inject the dummy coremod into the system properties.
        injectDummyCoreMod();
        // Launch the Minecraft client with the provided arguments.
        Launch.main(args);
    }

    /**
     * Injects the dummy coremod into the `fml.coreMods.load` system property.
     * If coremods are already specified, the dummy coremod is prepended to the list.
     */
    protected static void injectDummyCoreMod() {
        // Retrieve the current value of the `fml.coreMods.load` system property.
        String coreMods = System.getProperty(FML_COREMODS_SYS_PROP, "");
        if (!coreMods.isEmpty()) {
            // If coremods are already specified, prepend the dummy coremod.
            coreMods = DUMMY_PLUGIN + "," + coreMods;
        } else {
            // If no coremods are specified, set the dummy coremod as the value.
            coreMods = DUMMY_PLUGIN;
        }
        // Update the system property with the modified coremods list.
        System.setProperty(FML_COREMODS_SYS_PROP, coreMods);
    }
}