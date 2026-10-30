package com.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(Task.TaskStatus.PENDING);
        task1.setPriority(Task.TaskPriority.HIGH);

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(Task.TaskStatus.IN_PROGRESS);
        task2.setPriority(Task.TaskPriority.MEDIUM);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        Optional<Task> foundTask = taskService.getTaskById(1L);

        assertTrue(foundTask.isPresent());
        assertEquals(task1.getTitle(), foundTask.get().getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_WhenTaskNotExists_ShouldReturnEmpty() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Task> foundTask = taskService.getTaskById(999L);

        assertFalse(foundTask.isPresent());
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void createTask_ShouldReturnSavedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        Task savedTask = taskService.createTask(task1);

        assertNotNull(savedTask);
        assertEquals(task1.getTitle(), savedTask.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_WhenTaskExists_ShouldReturnUpdatedTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        Task updatedTask = taskService.updateTask(1L, task2);

        assertNotNull(updatedTask);
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_WhenTaskNotExists_ShouldThrowException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.updateTask(999L, task1));
        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        doNothing().when(taskRepository).delete(any(Task.class));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(any(Task.class));
    }

    @Test
    void deleteTask_WhenTaskNotExists_ShouldThrowException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(999L));
        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void getTasksByStatus_ShouldReturnTasksWithGivenStatus() {
        when(taskRepository.findByStatus(Task.TaskStatus.PENDING)).thenReturn(Arrays.asList(task1));

        List<Task> tasks = taskService.getTasksByStatus(Task.TaskStatus.PENDING);

        assertEquals(1, tasks.size());
        assertEquals(Task.TaskStatus.PENDING, tasks.get(0).getStatus());
        verify(taskRepository, times(1)).findByStatus(Task.TaskStatus.PENDING);
    }

    @Test
    void getTasksByPriority_ShouldReturnTasksWithGivenPriority() {
        when(taskRepository.findByPriority(Task.TaskPriority.HIGH)).thenReturn(Arrays.asList(task1));

        List<Task> tasks = taskService.getTasksByPriority(Task.TaskPriority.HIGH);

        assertEquals(1, tasks.size());
        assertEquals(Task.TaskPriority.HIGH, tasks.get(0).getPriority());
        verify(taskRepository, times(1)).findByPriority(Task.TaskPriority.HIGH);
    }
}
