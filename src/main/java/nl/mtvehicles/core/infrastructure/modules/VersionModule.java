package nl.mtvehicles.core.infrastructure.modules;

import lombok.Getter;
import lombok.Setter;
import nl.mtvehicles.core.Main;
import nl.mtvehicles.core.infrastructure.annotations.VersionSpecific;
import nl.mtvehicles.core.infrastructure.enums.PluginVersion;
import nl.mtvehicles.core.infrastructure.enums.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Module containing information about the plugin and server version
 */
public class VersionModule {
    private static @Getter
    @Setter
    VersionModule instance;

    /**
     * The plugin's version as String (e.g. '2.4.2')
     */
    public static String pluginVersionString;
    /**
     * The plugin's version as enum
     * @see PluginVersion
     */
    public static PluginVersion pluginVersion;
    /**
     * True if the plugin is a pre-release, release candidate or a dev-version
     */
    public static boolean isPreRelease;
    /**
     * The server's minecraft version (e.g. '1_16_R3')
     */
    private static String serverVersion;
    /**
     * The server's software (e.g. 'Paper')
     */
    public static String serverSoftware;
    private Logger logger = Main.instance.getLogger();

    public VersionModule() {
        PluginDescriptionFile pdf = Main.instance.getDescription();
        pluginVersionString = pdf.getVersion();
        pluginVersion = PluginVersion.getPluginVersion();

        //Pre-releases should thus be named "vX.Y.Z-preU" etc... (Instead of pre, dev for developing and rc for release candidates are acceptable too.)
        isPreRelease = pluginVersionString.toLowerCase().contains("pre") || pluginVersionString.toLowerCase().contains("rc") || pluginVersionString.toLowerCase().contains("dev");
        serverSoftware = Bukkit.getName();

        //Check Server Version
        if(serverSoftware.contains("CraftBukkit")){
            serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } else {
            serverVersion = Bukkit.getServer().getMinecraftVersion();
        }
    }

    /**
     * Get the server version as enum
     * @return Server version
     */
    @VersionSpecific
    public static ServerVersion getServerVersion(){
        ServerVersion returns = null;
        switch (serverVersion) {
            case "1.12.2":
            case "v1_12_R1":
                returns = ServerVersion.v1_12;
                break;
            case "1.13.2":
            case "v1_13_R2":
                returns = ServerVersion.v1_13;
                break;
            case "1.15.2":
            case "v1_15_R1":
                returns = ServerVersion.v1_15;
                break;
            case "1.16.5":
            case "v1_16_R3":
                returns = ServerVersion.v1_16;
                break;
            case "1.17.1":
            case "v1_17_R1":
                returns = ServerVersion.v1_17;
                break;
            case "1.18":
            case "v1_18_R1":
                returns = ServerVersion.v1_18_R1;
                break;
            case "1.18.2":
            case "v1_18_R2":
                returns = ServerVersion.v1_18_R2;
                break;
            case "1.19":
            case "v1_19_R1":
                returns = ServerVersion.v1_19_R1;
                break;
            case "1.19.2":
            case "v1_19_R2":
                returns = ServerVersion.v1_19_R2;
                break;
            case "1.19.4":
            case "v1_19_R3":
                returns = ServerVersion.v1_19_R3;
                break;
            case "1.20.2":
            case "v1_20_R2":
                returns = ServerVersion.v1_20_R2;
                break;
            case "1.20.4":
            case "v1_20_R3":
                returns = ServerVersion.v1_20_R3;
                break;
            case "1.20.6":
            case "v1_20_R4":
                returns = ServerVersion.v1_20_R4;
                break;
            case "1.21.1":
            case "v1_21_R1":
                returns = ServerVersion.v1_21_R1;
                break;
            case "1.21.3":
            case "v1_21_R2":
                returns = ServerVersion.v1_21_R2;

        }
        return returns;
    }

    /**
     * Check whether the server version is supported by the plugin.
     * Otherwise, send a warning and disable the plugin.
     * @return True if the server version is supported
     */
    @VersionSpecific
    public boolean isSupportedVersion(){

        List<String> highestVersions = Arrays.asList(
                "1.12.2", "1.13.2", "1.15.2", "1.16.5", "1.17.1", "1.18.2", "1.19.4", "1.20.6", "1.21.3",
                "v1_21_R2", "v1_20_R4", "v1_19_R3", "v1_18_R2", "v1_17_R1", "v1_16_R3", "v1_15_R1", "v1_13_R2", "v1_12_R1"

        );

        //Check Server Version
        if(serverSoftware.contains("CraftBukkit")){
            serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } else {
            serverVersion = Bukkit.getServer().getMinecraftVersion();
        }

        if (getServerVersion() == null) {
            logger.severe("--------------------------");
            logger.severe("Your Server version is not supported. The plugin will NOT load.");
            logger.severe("Check the supported versions here: https://wiki.mtvehicles.eu/faq.html");
            logger.severe("--------------------------");
            Main.disablePlugin();
            return false;
        }

        else if (!highestVersions.contains(serverVersion)) {
            logger.warning("--------------------------");
            logger.warning("Your Server does not run the latest patch version (e.g. you may be running 1.18 instead of 1.18.2 etc...).");
            logger.warning("The plugin WILL load but it MAY NOT work properly. UPDATE.");
            logger.warning("Check the supported versions here: https://wiki.mtvehicles.eu/faq.html");
            logger.warning("--------------------------");
        }

        else if (!serverSoftware.equals("Spigot") && !serverSoftware.equals("Paper") && !serverSoftware.equals("CraftBukkit")){
            logger.warning("--------------------------");
            logger.warning("Your Server is not running Spigot, nor Paper (" + serverSoftware + " detected).");
            logger.warning("The plugin WILL load but it MAY NOT work properly. Full support is guaranteed only on Spigot/Paper.");
            logger.warning("We'll be more than happy to help you on our Discord server (https://discord.gg/vehicle).");
            logger.warning("--------------------------");
        }

        return true;
    }
}
