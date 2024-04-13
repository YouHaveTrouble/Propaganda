package me.youhavetrouble.propaganda.command;


import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.youhavetrouble.propaganda.Propaganda;
import me.youhavetrouble.propaganda.messages.ActionbarAnnouncement;
import me.youhavetrouble.propaganda.messages.AnnouncementType;
import me.youhavetrouble.propaganda.messages.ChatAnnouncement;
import me.youhavetrouble.propaganda.messages.TitleAnnouncement;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.flag.CommandFlag;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.velocity.parser.ServerParser;

import java.util.*;


public class AnnounceCommand {

    public static void createCommand(Propaganda plugin) {
        CommandManager<CommandSource> commandManager = plugin.getCommandManager();
        Command<CommandSource> command = commandManager
                .commandBuilder("announce")
                .commandDescription(Description.of("Send an announcement"))
                .flag(CommandFlag.builder("type")
                        .withPermission(Permission.permission("propaganda.announce.type"))
                        .withAliases("t")
                        .asRepeatable()
                        .withDescription(Description.of("The type of announcement to send"))
                        .withComponent(
                                EnumParser.enumComponent(AnnouncementType.class)
                                        .name("type")
                                        .description(Description.of("The type of announcement to send"))
                                        .optional(DefaultValue.constant(AnnouncementType.CHAT))
                                        .build()
                        )
                )
                .flag(CommandFlag.builder("server")
                        .withPermission(Permission.permission("propaganda.announce.server"))
                        .withAliases("s")
                        .asRepeatable()
                        .withDescription(Description.of("Send the announcement to the server"))
                        .withComponent(ServerParser.serverParser())
                        .build()
                )
                .required("message", StringParser.quotedStringParser())
                .handler(context -> {
                    Audience audience = plugin.getServer();
                    String message = context.get("message");
                    Component messageComponent = MiniMessage.miniMessage().deserialize(message);
                    Component noNewlineMessageComponent = MiniMessage.miniMessage().deserialize(message.replace("<newline>", ""));

                    if (context.flags().isPresent("server")) {
                        Set<RegisteredServer> servers = new HashSet<>();
                        context.flags().getAll("server").forEach(server -> servers.add((RegisteredServer) server));
                        if (servers.isEmpty()) {
                            context.sender().sendMessage(Component.text("No valid server specified"));
                            return;
                        }
                        audience = audience.filterAudience(aud -> {
                            if (aud instanceof Player player) {
                                ServerConnection server = player.getCurrentServer().orElse(null);
                                if (server == null) return false;
                                RegisteredServer registeredServer = server.getServer();
                                if (registeredServer == null) return false;

                                return servers.contains(registeredServer);
                            }
                            return false;
                        });
                    }

                    if (context.flags().isPresent("type")) {
                        Set<AnnouncementType> types = new HashSet<>();

                        context.flags().getAll("type").forEach(type -> {
                            try {
                                types.add(AnnouncementType.valueOf(type.toString().toUpperCase()));
                            } catch (IllegalArgumentException ignored) {
                            }
                        });

                        if (types.isEmpty()) {
                            context.sender().sendMessage(Component.text("No valid announcement type specified"));
                            return;
                        }

                        if (types.contains(AnnouncementType.CHAT)) {
                            new ChatAnnouncement.Builder(messageComponent)
                                    .prefix(plugin.getConfig().prefix)
                                    .build()
                                    .send(audience);
                        }
                        if (types.contains(AnnouncementType.TITLE)) {
                            new TitleAnnouncement.Builder(noNewlineMessageComponent)
                                    .mode(TitleAnnouncement.Builder.TitleMode.SUBTITLE)
                                    .build()
                                    .send(audience);
                        }
                        if (types.contains(AnnouncementType.ACTIONBAR)) {
                            new ActionbarAnnouncement(noNewlineMessageComponent)
                                    .send(audience);
                        }
                        return;
                    }

                    new ChatAnnouncement.Builder(messageComponent)
                            .prefix(plugin.getConfig().prefix)
                            .build()
                            .send(audience);


                })
                .build();
        commandManager.command(command);
    }

}
