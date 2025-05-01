package com.kush.todo.service;

import com.kush.todo.BaseTest;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

class TaskServiceTest extends BaseTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Test
    void findById() {
        Mockito.when(taskRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> taskService.findTaskById(1),
                "When no info in DB then exception should be thrown"
        );
    }

}
