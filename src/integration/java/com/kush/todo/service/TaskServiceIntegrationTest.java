package com.kush.todo.service;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.dto.response.TaskDto;
import com.kush.todo.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

class TaskServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Test
    void findTaskById() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> taskService.findTaskById(1),
                "When no info in DB then exception should be thrown"
        );
    }

    @Test
    void whenPresentEntityRequestedByIdThenItShouldBeReturned() {
        TaskDto createdTask = taskService.create(new TaskDto(1, "test"));

        TaskDto foundTask = Assertions.assertDoesNotThrow(
                () -> taskService.findTaskById(createdTask.id()),
                "No exceptions on finding should appear"
        );
        Assertions.assertEquals(createdTask, foundTask, "Created and found entities are not the same");
    }
}
