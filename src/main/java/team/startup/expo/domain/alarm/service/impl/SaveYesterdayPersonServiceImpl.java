package team.startup.expo.domain.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import team.startup.expo.domain.alarm.service.SaveYesterdayPersonService;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.expo.repository.ExpoRepository;
import team.startup.expo.global.annotation.TransactionService;

import java.util.List;

@TransactionService
@RequiredArgsConstructor
public class SaveYesterdayPersonServiceImpl implements SaveYesterdayPersonService {

    private final ExpoRepository expoRepository;

    public void execute() {
        List<Expo> expoList = expoRepository.findAll();

        expoList.forEach(expo -> {
            expo.saveYesterdayApplicationPerson(expo.getApplicationPerson());
            expoRepository.save(expo);
        });
    }
}
