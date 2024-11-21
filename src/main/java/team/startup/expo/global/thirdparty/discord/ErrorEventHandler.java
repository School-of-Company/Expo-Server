package team.startup.expo.global.thirdparty.discord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import team.startup.expo.global.exception.ErrorResponse;
import team.startup.expo.global.filter.event.ErrorLoggingEvent;
import team.startup.expo.global.thirdparty.discord.properties.DiscordProperties;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ErrorEventHandler {

    private final ObjectMapper objectMapper;
    private final DiscordProperties discordProperties;

    @EventListener
    public void exceptionLoggingHandler(ErrorLoggingEvent errorLoggingEvent) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(errorLoggingEvent.getStatus(), errorLoggingEvent.getMessage());
        String responseString = objectMapper.writeValueAsString(errorResponse);

        JsonObject statusField = new JsonObject();
        statusField.add("name", new JsonPrimitive("상태코드"));
        statusField.add("value", new JsonPrimitive(errorLoggingEvent.getStatus()));

        JsonObject contentTypeField = new JsonObject();
        contentTypeField.add("name", new JsonPrimitive("CONTENT-TYPE"));
        contentTypeField.add("value", new JsonPrimitive(MediaType.APPLICATION_JSON_VALUE));

        JsonObject writerField = new JsonObject();
        writerField.add("name", new JsonPrimitive("Exception"));
        writerField.add("value", new JsonPrimitive(responseString));

        JsonArray fields = new JsonArray();
        fields.add(statusField);
        fields.add(contentTypeField);
        fields.add(writerField);

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("content", new JsonPrimitive(""));

        JsonObject embed = new JsonObject();
        embed.add("description", new JsonPrimitive("에러 로그"));
        embed.add("color", new JsonPrimitive(16711680));
        embed.add("fields", fields);

        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        jsonObject.add("embeds", embeds);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
        restTemplate.postForObject(discordProperties.getErrorURL(), entity, String.class);
    }
}
