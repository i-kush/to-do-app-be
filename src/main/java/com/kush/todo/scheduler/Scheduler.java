package com.kush.todo.scheduler;

import com.kush.todo.service.AppUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Scheduler {

    private final AppUserService appUserService;

    @Scheduled(fixedDelayString = "${todo.job.user-unlocking}")
    public void unlockUsers() {
        appUserService.unlockUsers();
    }
}
