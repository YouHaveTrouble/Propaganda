package me.youhavetrouble.propaganda;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.youhavetrouble.propaganda.command.AnnounceCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "propaganda",
        name = "Propaganda",
        version = "1.0",
        description = "Velocity announcement plugin",
        url = "https://youhavetrouble.me",
        authors = {"YouHaveTrouble"}
)
public class Propaganda {

    @Inject
    private Logger logger;

    private final ProxyServer server;

    private final Path configFolder;

    private PropagandaConfig config;

    private CommandManager<CommandSource> commandManager;

    @Inject
    public Propaganda(ProxyServer server, @DataDirectory final Path configFolder) {
        this.server = server;
        this.configFolder = configFolder;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.commandManager = new VelocityCommandManager<>(
                server.getPluginManager().ensurePluginContainer(this),
                server,
                ExecutionCoordinator.asyncCoordinator(),
                SenderMapper.identity()
        );
        AnnounceCommand.createCommand(this);
        reloadPlugin();
    }

    @Subscribe
    public void onProxyReload(ProxyReloadEvent event) {
        reloadPlugin();
    }

    public void reloadPlugin() {
        File folder = configFolder.toFile();
        File file = new File(folder, "config.toml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try (InputStream input = getClass().getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return;
            }
        }
        config = new PropagandaConfig(new Toml().read(file));
    }

    public PropagandaConfig getConfig() {
        return config;
    }

    public Audience serverAudience(String serverName) {
       return this.server.getServer(serverName).orElse(null);
    }

    public ProxyServer getServer() {
        return server;
    }

    public Component prefixComponent(Component component) {
        Component base = Component.empty();
        if (config == null) return null;
        return base.append(config.prefix).append(component);
    }

    public CommandManager<CommandSource> getCommandManager() {
        return commandManager;
    }

    public Logger getLogger() {
        return logger;
    }
}
