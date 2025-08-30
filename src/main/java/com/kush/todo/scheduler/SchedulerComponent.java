package com.kush.todo.scheduler;

import com.kush.todo.service.AppUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerComponent {

    private final AppUserService appUserService;

    @Scheduled(fixedDelayString = "${todo.job.user-unlocking}")
    public void unlockUsers() {
        appUserService.unlockUsers();
    }
}
