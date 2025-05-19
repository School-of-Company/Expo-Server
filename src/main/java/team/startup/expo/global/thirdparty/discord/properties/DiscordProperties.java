package team.startup.expo.global.thirdparty.discord.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties("discord")
public class DiscordProperties {
    private final String errorURL;
    private final String participantNumberURL;
}
