package net.minecraftforge.legacyjavafixer.sort;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.base.Throwables;
import com.google.common.primitives.Ints;

import cpw.mods.fml.common.launcher.FMLInjectionAndSortingTweaker;
import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.ReflectionHelper;

/**
 * The `LegacyJavaSortTweaker` class is responsible for replacing the default sorting tweaker
 * with a custom implementation to ensure compatibility with legacy Java versions.
 * It modifies the sorting behavior of tweakers in the LaunchWrapper.
 * <p>
 * Created by Lex Manos on 14/10/2014.
 * Modified by covers1624 on 1/6/21 to ensure compatibility with legacy Java versions.
 */
public class LegacyJavaSortTweaker implements ITweaker {

    /**
     * Constructor for the `LegacyJavaSortTweaker` class.
     * Replaces the `FMLInjectionAndSortingTweaker` with a custom `SortReplacement` tweaker.
     */
    public LegacyJavaSortTweaker() {
        @SuppressWarnings("unchecked")
        ListIterator<ITweaker> itr = ((List<ITweaker>) Launch.blackboard.get("Tweaks")).listIterator();
        ITweaker replacement = new SortReplacement();
        while (itr.hasNext()) {
            ITweaker t = itr.next();
            FMLRelaunchLog.log.info("[LegacyJavaFixer] Tweaker: " + t);
            if (t instanceof FMLInjectionAndSortingTweaker) {
                itr.set(replacement);
                FMLRelaunchLog.info("[LegacyJavaFixer] Replacing tweaker %s with %s", t, replacement);
            }
        }
    }

    /**
     * The `SortReplacement` class is a custom implementation of `ITweaker` that
     * replaces the default sorting behavior of tweakers in the LaunchWrapper.
     */
    public static class SortReplacement implements ITweaker {
        private boolean hasRun = false;
        Class<?> wrapperCls = null;
        Field wrapperField = null;
        Map<String, Integer> tweakSorting = null;

        /**
         * Constructor for the `SortReplacement` class.
         * Initializes reflection-based access to the `FMLPluginWrapper` class and its `sortIndex` field.
         */
        SortReplacement() {
            try {
                wrapperCls = Class.forName("cpw.mods.fml.relauncher.CoreModManager$FMLPluginWrapper", false, SortReplacement.class.getClassLoader());
                wrapperField = wrapperCls.getDeclaredField("sortIndex");
                wrapperField.setAccessible(true);
                tweakSorting = ReflectionHelper.getPrivateValue(CoreModManager.class, null, "tweakSorting");
            } catch (Exception e) {
                e.printStackTrace();
                Throwables.propagate(e);
            }
        }

        /**
         * Injects the custom sorting logic into the class loader.
         * Ensures that the sorting logic is only executed once.
         *
         * @param classLoader The `LaunchClassLoader` instance.
         */
        @Override
        public void injectIntoClassLoader(LaunchClassLoader classLoader) {
            if (!hasRun) {
                FMLRelaunchLog.log.info("[LegacyJavaFixer] Replacing sort");
                sort();
                URL is = FMLInjectionAndSortingTweaker.class.getResource("/cpw/mods/fml/common/launcher/TerminalTweaker.class");
                if (is != null) {
                    FMLRelaunchLog.log.info("[LegacyJavaFixer] Detected TerminalTweaker");
                    @SuppressWarnings("unchecked")
                    List<String> newTweaks = (List<String>) Launch.blackboard.get("TweakClasses");
                    newTweaks.add("cpw.mods.fml.common.launcher.TerminalTweaker");
                }
            }
            hasRun = true;
        }

        /**
         * Custom sorting logic for the tweakers.
         * Replaces the default sorting behavior with a custom implementation.
         */
        @SuppressWarnings("unchecked")
        private void sort() {
            List<ITweaker> tweakers = (List<ITweaker>) Launch.blackboard.get("Tweaks");
            // Sorts the tweakers using a custom comparator.
            ITweaker[] toSort = tweakers.toArray(new ITweaker[tweakers.size()]);
            Arrays.sort(toSort, new Comparator<ITweaker>() {
                @Override
                public int compare(ITweaker o1, ITweaker o2) {
                    return Ints.saturatedCast((long) getIndex(o1) - (long) getIndex(o2));
                }

                /**
                 * Retrieves the sorting index for a given tweaker.
                 *
                 * @param t The `ITweaker` instance.
                 * @return The sorting index of the tweaker.
                 */
                private int getIndex(ITweaker t) {
                    try {
                        if (t instanceof SortReplacement) return Integer.MIN_VALUE;
                        if (wrapperCls.isInstance(t)) return wrapperField.getInt(t);
                        if (tweakSorting.containsKey(t.getClass().getName())) return tweakSorting.get(t.getClass().getName());
                    } catch (Exception e) {
                        Throwables.propagate(e);
                    }
                    return 0;
                }
            });
            // Updates the tweakers list with the sorted order.
            for (int j = 0; j < toSort.length; j++) {
                tweakers.set(j, toSort[j]);
            }
        }

        @Override
        public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}

        @Override
        public String[] getLaunchArguments() {
            return new String[0];
        }

        @Override
        public String getLaunchTarget() {
            return "";
        }
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

    @Override
    public String getLaunchTarget() {
        return "";
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {}
}