package net.minecraftforge.legacyjavafixer.sort;

import cpw.mods.fml.relauncher.ServerLaunchWrapper;

/**
 * The `ServerWrapper` class extends the `ClientWrapper` class and serves as the entry point
 * for launching the Minecraft server. It ensures that the dummy coremod is injected
 * before delegating to the `ServerLaunchWrapper`.
 * <p>
 * Created by covers1624 on 1/6/21.
 */
public class ServerWrapper extends ClientWrapper {

    /**
     * The main entry point of the server application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        // Inject the dummy coremod into the system properties.
        injectDummyCoreMod();
        // Launch the Minecraft server with the provided arguments.
        ServerLaunchWrapper.main(args);
    }
}