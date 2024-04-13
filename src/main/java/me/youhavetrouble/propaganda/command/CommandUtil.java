package me.youhavetrouble.propaganda.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.youhavetrouble.propaganda.messages.AnnouncementType;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.flag.CommandFlag;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.velocity.parser.ServerParser;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class CommandUtil {

    /**
     * Retrieves the audience based on the given starting audience and command context.
     *
     * @param startingAudience The starting audience to filter.
     * @param context          The command context.
     * @return The filtered audience based on the command context, or null if no servers are specified.
     */
    @Nullable
    public static Audience getAudienceFromContext(Audience startingAudience, CommandContext<CommandSource> context) {
        Audience audience = startingAudience;

        if (context.flags().isPresent("server")) {
            Set<RegisteredServer> servers = new HashSet<>();
            context.flags().getAll("server").forEach(server -> servers.add((RegisteredServer) server));
            if (servers.isEmpty()) return null;

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
        return audience;
    }

    /**
     * Retrieves the announcement types based on the given command context.
     *
     * @param context The command context.
     * @return A set of announcement types based on the command context.
     * If no type flag was provided it defaults to chat type.
     */
    public static Set<AnnouncementType> getTypesFromContext(CommandContext<CommandSource> context) {
        Set<AnnouncementType> types = new HashSet<>();
        if (context.flags().isPresent("type")) {
            context.flags().getAll("type").forEach(type -> types.add((AnnouncementType) type));
        }
        if (types.isEmpty()) types.add(AnnouncementType.CHAT);
        return types;
    }

    public static CommandFlag<AnnouncementType> getAnnouncementTypeFlag() {
        return CommandFlag.builder("type")
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
                .build();
    }

    public static CommandFlag<RegisteredServer> getServerFlag() {
        return CommandFlag.builder("server")
                .withPermission(Permission.permission("propaganda.announce.server"))
                .withAliases("s")
                .asRepeatable()
                .withDescription(Description.of("Send the announcement to the server"))
                .withComponent(ServerParser.serverParser())
                .build();
    }

}
