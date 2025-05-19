package team.startup.expo.domain.alarm.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import team.startup.expo.domain.alarm.service.SendParticipantNumberByExpoService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.global.annotation.ReadOnlyTransactionService;
import team.startup.expo.global.thirdparty.discord.properties.DiscordProperties;

import java.time.LocalDate;
import java.util.List;

@ReadOnlyTransactionService
@RequiredArgsConstructor
public class SendParticipantNumberByExpoServiceImpl implements SendParticipantNumberByExpoService {

    private final DiscordProperties discordProperties;
    private final ExpoRepository expoRepository;

    public void execute() {
        List<Expo> expoList = expoRepository.findAll();

        expoList.forEach(expo -> {
            JsonObject statusField = new JsonObject();
            statusField.add("name", new JsonPrimitive("박람회 이름"));
            statusField.add("value", new JsonPrimitive(expo.getTitle()));

            JsonObject yesterdayPersonField = new JsonObject();
            yesterdayPersonField.add("name", new JsonPrimitive("어제 박람회 등록 인원"));
            yesterdayPersonField.add("value", new JsonPrimitive(expo.getYesterdayApplicationPerson()));

            JsonObject nowPersonField = new JsonObject();
            nowPersonField.add("name", new JsonPrimitive(LocalDate.now() + " 박람회 등록 인원"));
            nowPersonField.add("value", new JsonPrimitive(expo.getApplicationPerson()));

            String yesterdayAndTodayPersonDifference = String.valueOf(expo.getApplicationPerson() - expo.getYesterdayApplicationPerson());

            JsonObject yesterdayAndTodayPersonDifferenceField = new JsonObject();
            yesterdayAndTodayPersonDifferenceField.add("name", new JsonPrimitive("추가 등록 인원"));
            yesterdayAndTodayPersonDifferenceField.add("value", new JsonPrimitive("+" + yesterdayAndTodayPersonDifference));

            JsonArray fields = new JsonArray();
            fields.add(statusField);
            fields.add(yesterdayPersonField);
            fields.add(nowPersonField);
            fields.add(yesterdayAndTodayPersonDifferenceField);

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("content", new JsonPrimitive(""));

            JsonObject embed = new JsonObject();
            embed.add("description", new JsonPrimitive("박람회별 등록 인원 로그"));
            embed.add("color", new JsonPrimitive(16711680));
            embed.add("fields", fields);

            JsonArray embeds = new JsonArray();
            embeds.add(embed);
            jsonObject.add("embeds", embeds);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
            restTemplate.postForObject(discordProperties.getParticipantNumberURL(), entity, String.class);
        });
    }
}
