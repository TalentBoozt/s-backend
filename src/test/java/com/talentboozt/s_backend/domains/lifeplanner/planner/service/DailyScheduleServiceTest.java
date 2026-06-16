package com.talentboozt.s_backend.domains.lifeplanner.planner.service;

import com.talentboozt.s_backend.domains.lifeplanner.ai.model.PlanResponse;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.StudyPlan;
import com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb.DailyScheduleRepository;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserPreferences;
import com.talentboozt.s_backend.domains.lifeplanner.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DailyScheduleServiceTest {

    private DailyScheduleRepository dailyScheduleRepository;
    private UserService userService;
    private DailyScheduleService dailyScheduleService;

    @BeforeEach
    void setUp() {
        dailyScheduleRepository = mock(DailyScheduleRepository.class);
        userService = mock(UserService.class);
        dailyScheduleService = new DailyScheduleService(dailyScheduleRepository, userService);
    }

    @Test
    void generateSchedulesForPlan_shouldScheduleTasksWithinSingleDay() {
        // Arrange
        StudyPlan plan = new StudyPlan();
        plan.setPlanId("plan-123");
        plan.setUserId("user-456");

        UserPreferences prefs = new UserPreferences();
        prefs.setWorkHoursStart("09:00");
        prefs.setWorkHoursEnd("17:00");
        prefs.setBreakFrequency(15);
        when(userService.getOrCreatePreferences("user-456")).thenReturn(prefs);

        List<PlanResponse.DailyTask> dailyTasks = new ArrayList<>();
        PlanResponse.DailyTask task1 = new PlanResponse.DailyTask();
        task1.setTitle("Task 1");
        task1.setEstimatedTime("45 min");
        task1.setCategory("Study");
        dailyTasks.add(task1);

        PlanResponse.DailyTask task2 = new PlanResponse.DailyTask();
        task2.setTitle("Task 2");
        task2.setEstimatedTime("30 min");
        task2.setCategory("Coding");
        dailyTasks.add(task2);

        when(dailyScheduleRepository.findByUserIdAndScheduleDate(eq("user-456"), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        // Act
        dailyScheduleService.generateSchedulesForPlan(plan, dailyTasks, Instant.now());

        // Assert
        ArgumentCaptor<List<DailySchedule>> captor = ArgumentCaptor.forClass(List.class);
        verify(dailyScheduleRepository, times(1)).saveAll(captor.capture());
        List<DailySchedule> savedSchedules = captor.getValue();

        assertThat(savedSchedules).hasSize(2); // In code, schedule is saved for every task in iteration
        DailySchedule firstTaskSchedule = savedSchedules.get(0);
        assertThat(firstTaskSchedule.getTasks()).hasSize(1);
        DailySchedule.ScheduleTask scheduledTask1 = firstTaskSchedule.getTasks().get(0);
        assertThat(scheduledTask1.getTitle()).isEqualTo("Task 1");
        assertThat(scheduledTask1.getStartTime()).isEqualTo("09:00");
        assertThat(scheduledTask1.getEndTime()).isEqualTo("09:45");

        DailySchedule secondTaskSchedule = savedSchedules.get(1);
        DailySchedule.ScheduleTask scheduledTask2 = secondTaskSchedule.getTasks().get(0);
        assertThat(scheduledTask2.getTitle()).isEqualTo("Task 2");
        // Start time = 09:45 + 15 min break = 10:00
        assertThat(scheduledTask2.getStartTime()).isEqualTo("10:00");
        assertThat(scheduledTask2.getEndTime()).isEqualTo("10:30");
    }

    @Test
    void generateSchedulesForPlan_shouldRolloverTasksToNextDayWhenExceedingWorkHoursEnd() {
        // Arrange
        StudyPlan plan = new StudyPlan();
        plan.setPlanId("plan-123");
        plan.setUserId("user-456");

        UserPreferences prefs = new UserPreferences();
        prefs.setWorkHoursStart("09:00");
        prefs.setWorkHoursEnd("09:30"); // Very short work hours for testing rollover
        prefs.setBreakFrequency(15);
        when(userService.getOrCreatePreferences("user-456")).thenReturn(prefs);

        List<PlanResponse.DailyTask> dailyTasks = new ArrayList<>();
        PlanResponse.DailyTask task1 = new PlanResponse.DailyTask();
        task1.setTitle("Task 1");
        task1.setEstimatedTime("60 min"); // End: 10:00. Next task after break: 10:15
        dailyTasks.add(task1);

        PlanResponse.DailyTask task2 = new PlanResponse.DailyTask();
        task2.setTitle("Task 2");
        task2.setEstimatedTime("60 min"); // End: 11:15 > 11:00 -> should trigger rollover
        dailyTasks.add(task2);

        when(dailyScheduleRepository.findByUserIdAndScheduleDate(eq("user-456"), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        // Act
        dailyScheduleService.generateSchedulesForPlan(plan, dailyTasks, Instant.now());

        // Assert
        ArgumentCaptor<List<DailySchedule>> captor = ArgumentCaptor.forClass(List.class);
        verify(dailyScheduleRepository, times(1)).saveAll(captor.capture());
        List<DailySchedule> savedSchedules = captor.getValue();

        assertThat(savedSchedules).hasSize(2);
        // Task 1 schedule
        DailySchedule sched1 = savedSchedules.get(0);
        assertThat(sched1.getScheduleDate()).isEqualTo(LocalDate.now());
        assertThat(sched1.getTasks().get(0).getStartTime()).isEqualTo("09:00");

        // Task 2 schedule rolled over to next day
        DailySchedule sched2 = savedSchedules.get(1);
        assertThat(sched2.getScheduleDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(sched2.getTasks().get(0).getStartTime()).isEqualTo("09:00");
    }

    @Test
    void generateSchedulesForPlan_shouldParseDurationOrUseFallback() {
        // Arrange
        StudyPlan plan = new StudyPlan();
        plan.setPlanId("plan-123");
        plan.setUserId("user-456");

        UserPreferences prefs = new UserPreferences();
        prefs.setWorkHoursStart("09:00");
        prefs.setWorkHoursEnd("17:00");
        when(userService.getOrCreatePreferences("user-456")).thenReturn(prefs);

        List<PlanResponse.DailyTask> dailyTasks = new ArrayList<>();
        PlanResponse.DailyTask taskWithNoDigits = new PlanResponse.DailyTask();
        taskWithNoDigits.setTitle("Task Unspecified");
        taskWithNoDigits.setEstimatedTime("Unknown mins");
        dailyTasks.add(taskWithNoDigits);

        when(dailyScheduleRepository.findByUserIdAndScheduleDate(eq("user-456"), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        // Act
        dailyScheduleService.generateSchedulesForPlan(plan, dailyTasks, Instant.now());

        // Assert
        ArgumentCaptor<List<DailySchedule>> captor = ArgumentCaptor.forClass(List.class);
        verify(dailyScheduleRepository, times(1)).saveAll(captor.capture());
        DailySchedule schedule = captor.getValue().get(0);
        DailySchedule.ScheduleTask task = schedule.getTasks().get(0);

        // Fallback duration is 60 minutes
        assertThat(task.getStartTime()).isEqualTo("09:00");
        assertThat(task.getEndTime()).isEqualTo("10:00");
    }

    @Test
    void addTaskToToday_shouldCreateNewScheduleStartingAtNineAMWhenNoneExists() {
        // Arrange
        when(dailyScheduleRepository.findByUserIdAndScheduleDate(eq("user-456"), eq(LocalDate.now())))
                .thenReturn(Optional.empty());

        // Act
        dailyScheduleService.addTaskToToday("user-456", "New Task", "Work", "45 min", "HIGH", "Notes");

        // Assert
        ArgumentCaptor<DailySchedule> captor = ArgumentCaptor.forClass(DailySchedule.class);
        verify(dailyScheduleRepository, times(1)).save(captor.capture());
        DailySchedule saved = captor.getValue();

        assertThat(saved.getUserId()).isEqualTo("user-456");
        assertThat(saved.getScheduleDate()).isEqualTo(LocalDate.now());
        assertThat(saved.getTasks()).hasSize(1);

        DailySchedule.ScheduleTask task = saved.getTasks().get(0);
        assertThat(task.getTitle()).isEqualTo("New Task");
        assertThat(task.getStartTime()).isEqualTo("09:00");
        assertThat(task.getEndTime()).isEqualTo("09:45");
        assertThat(task.isCompleted()).isFalse();
    }

    @Test
    void addTaskToToday_shouldAppendTaskFifteenMinutesAfterLastTask() {
        // Arrange
        DailySchedule existing = new DailySchedule();
        existing.setUserId("user-456");
        existing.setScheduleDate(LocalDate.now());
        existing.setTasks(new ArrayList<>());

        DailySchedule.ScheduleTask task1 = new DailySchedule.ScheduleTask();
        task1.setTaskId("task-111");
        task1.setEndTime("10:30");
        existing.getTasks().add(task1);

        when(dailyScheduleRepository.findByUserIdAndScheduleDate("user-456", LocalDate.now()))
                .thenReturn(Optional.of(existing));

        // Act
        dailyScheduleService.addTaskToToday("user-456", "Task 2", "Work", "45 min", "MEDIUM", "Notes");

        // Assert
        ArgumentCaptor<DailySchedule> captor = ArgumentCaptor.forClass(DailySchedule.class);
        verify(dailyScheduleRepository, times(1)).save(captor.capture());
        DailySchedule saved = captor.getValue();

        assertThat(saved.getTasks()).hasSize(2);
        DailySchedule.ScheduleTask task2 = saved.getTasks().get(1);
        assertThat(task2.getTitle()).isEqualTo("Task 2");
        // End time of last task was 10:30 + 15 min buffer = 10:45
        assertThat(task2.getStartTime()).isEqualTo("10:45");
        assertThat(task2.getEndTime()).isEqualTo("11:30");
    }

    @Test
    void updateTask_shouldModifyTaskWhenOwnerMatches() {
        // Arrange
        DailySchedule schedule = new DailySchedule();
        schedule.setScheduleId("sched-123");
        schedule.setUserId("user-456");
        schedule.setTasks(new ArrayList<>());

        DailySchedule.ScheduleTask task = new DailySchedule.ScheduleTask();
        task.setTaskId("task-123");
        task.setTitle("Old Title");
        schedule.getTasks().add(task);

        when(dailyScheduleRepository.findById("sched-123")).thenReturn(Optional.of(schedule));

        // Act
        dailyScheduleService.updateTask("user-456", "sched-123", "task-123", "New Title", "Category", "30 min", "HIGH", "Notes");

        // Assert
        verify(dailyScheduleRepository, times(1)).save(schedule);
        assertThat(task.getTitle()).isEqualTo("New Title");
        assertThat(task.getCategory()).isEqualTo("Category");
        assertThat(task.getEstimatedTime()).isEqualTo("30 min");
        assertThat(task.getPriority()).isEqualTo("HIGH");
        assertThat(task.getNotes()).isEqualTo("Notes");
    }

    @Test
    void updateTask_shouldNotModifyTaskWhenOwnerDoesNotMatch() {
        // Arrange
        DailySchedule schedule = new DailySchedule();
        schedule.setScheduleId("sched-123");
        schedule.setUserId("user-456");
        schedule.setTasks(new ArrayList<>());

        DailySchedule.ScheduleTask task = new DailySchedule.ScheduleTask();
        task.setTaskId("task-123");
        task.setTitle("Old Title");
        schedule.getTasks().add(task);

        when(dailyScheduleRepository.findById("sched-123")).thenReturn(Optional.of(schedule));

        // Act
        dailyScheduleService.updateTask("user-789", "sched-123", "task-123", "New Title", "Category", "30 min", "HIGH", "Notes");

        // Assert
        verify(dailyScheduleRepository, never()).save(any(DailySchedule.class));
        assertThat(task.getTitle()).isEqualTo("Old Title"); // Unchanged
    }

    @Test
    void deleteTask_shouldDeleteTaskWhenOwnerMatches() {
        // Arrange
        DailySchedule schedule = new DailySchedule();
        schedule.setScheduleId("sched-123");
        schedule.setUserId("user-456");
        schedule.setTasks(new ArrayList<>());

        DailySchedule.ScheduleTask task = new DailySchedule.ScheduleTask();
        task.setTaskId("task-123");
        schedule.getTasks().add(task);

        when(dailyScheduleRepository.findById("sched-123")).thenReturn(Optional.of(schedule));

        // Act
        dailyScheduleService.deleteTask("user-456", "sched-123", "task-123");

        // Assert
        verify(dailyScheduleRepository, times(1)).save(schedule);
        assertThat(schedule.getTasks()).isEmpty();
    }

    @Test
    void reorderTasks_shouldReorderTasksAndPreserveOmittedTasks() {
        // Arrange
        DailySchedule schedule = new DailySchedule();
        schedule.setScheduleId("sched-123");
        schedule.setUserId("user-456");
        schedule.setTasks(new ArrayList<>());

        DailySchedule.ScheduleTask t1 = new DailySchedule.ScheduleTask();
        t1.setTaskId("task-1");
        schedule.getTasks().add(t1);

        DailySchedule.ScheduleTask t2 = new DailySchedule.ScheduleTask();
        t2.setTaskId("task-2");
        schedule.getTasks().add(t2);

        DailySchedule.ScheduleTask t3 = new DailySchedule.ScheduleTask();
        t3.setTaskId("task-3");
        schedule.getTasks().add(t3);

        when(dailyScheduleRepository.findById("sched-123")).thenReturn(Optional.of(schedule));

        // Act - Reorder passing task-3 then task-1, omitting task-2
        List<String> taskIds = List.of("task-3", "task-1");
        dailyScheduleService.reorderTasks("user-456", "sched-123", taskIds);

        // Assert
        verify(dailyScheduleRepository, times(1)).save(schedule);
        // Order should be task-3, task-1, and task-2 appended at the end
        assertThat(schedule.getTasks().get(0).getTaskId()).isEqualTo("task-3");
        assertThat(schedule.getTasks().get(1).getTaskId()).isEqualTo("task-1");
        assertThat(schedule.getTasks().get(2).getTaskId()).isEqualTo("task-2");
    }
}
