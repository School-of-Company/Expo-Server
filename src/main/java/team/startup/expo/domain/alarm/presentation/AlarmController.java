package team.startup.expo.domain.alarm.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.startup.expo.domain.alarm.service.SaveYesterdayPersonService;
import team.startup.expo.domain.alarm.service.SendParticipantNumberByExpoService;

@Component
@RequiredArgsConstructor
public class AlarmController {

    private final SendParticipantNumberByExpoService sendParticipantNumberByExpoService;
    private final SaveYesterdayPersonService saveYesterdayPersonService;

    @Scheduled(cron = "0 0 8,12,16,20 * * *")
    public void sendParticipantNumberByExpo() {
        sendParticipantNumberByExpoService.execute();
    }


    @Scheduled(cron = "0 0 0 * * *")
    public void saveYesterdayPerson() {
        saveYesterdayPersonService.execute();
    }
}
