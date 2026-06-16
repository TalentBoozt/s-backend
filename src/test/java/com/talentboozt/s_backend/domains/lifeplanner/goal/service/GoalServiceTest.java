package com.talentboozt.s_backend.domains.lifeplanner.goal.service;

import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.goal.repository.mongodb.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GoalServiceTest {

    private GoalRepository goalRepository;
    private GoalService goalService;

    @BeforeEach
    void setUp() {
        goalRepository = mock(GoalRepository.class);
        goalService = new GoalService(goalRepository);
    }

    @Test
    void createGoal_shouldInitializeStatusAndTimestamps() {
        // Arrange
        Goal goal = new Goal();
        goal.setGoalId("goal-123");
        goal.setTitle("Read a book");

        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Goal result = goalService.createGoal(goal);

        // Assert
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    void updateGoal_shouldModifyFieldsAndTimestampsWhenOwnerMatches() {
        // Arrange
        Goal existingGoal = new Goal();
        existingGoal.setGoalId("goal-123");
        existingGoal.setUserId("user-456");
        existingGoal.setTitle("Read 1 chapter");
        existingGoal.setCreatedAt(Instant.now().minusSeconds(3600));
        existingGoal.setUpdatedAt(existingGoal.getCreatedAt());

        Goal updatedGoal = new Goal();
        updatedGoal.setTitle("Read 5 chapters");
        updatedGoal.setDescription("Finish the book");

        when(goalRepository.findById("goal-123")).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Goal result = goalService.updateGoal("goal-123", "user-456", updatedGoal);

        // Assert
        assertThat(result.getTitle()).isEqualTo("Read 5 chapters");
        assertThat(result.getDescription()).isEqualTo("Finish the book");
        assertThat(result.getUpdatedAt()).isAfter(existingGoal.getCreatedAt());
        verify(goalRepository, times(1)).save(existingGoal);
    }

    @Test
    void updateGoal_shouldThrowExceptionWhenOwnerMismatched() {
        // Arrange
        Goal existingGoal = new Goal();
        existingGoal.setGoalId("goal-123");
        existingGoal.setUserId("user-456");

        Goal updatedGoal = new Goal();

        when(goalRepository.findById("goal-123")).thenReturn(Optional.of(existingGoal));

        // Act & Assert
        assertThatThrownBy(() -> goalService.updateGoal("goal-123", "user-789", updatedGoal))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Goal not found or unauthorized");
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void updateGoal_shouldThrowExceptionWhenGoalDoesNotExist() {
        // Arrange
        Goal updatedGoal = new Goal();
        when(goalRepository.findById("goal-123")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> goalService.updateGoal("goal-123", "user-456", updatedGoal))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Goal not found or unauthorized");
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void deleteGoal_shouldDeleteGoalWhenOwnerMatches() {
        // Arrange
        Goal existingGoal = new Goal();
        existingGoal.setGoalId("goal-123");
        existingGoal.setUserId("user-456");

        when(goalRepository.findById("goal-123")).thenReturn(Optional.of(existingGoal));

        // Act
        goalService.deleteGoal("goal-123", "user-456");

        // Assert
        verify(goalRepository, times(1)).delete(existingGoal);
    }

    @Test
    void deleteGoal_shouldThrowExceptionWhenOwnerMismatchedOrGoalDoesNotExist() {
        // Arrange
        Goal existingGoal = new Goal();
        existingGoal.setGoalId("goal-123");
        existingGoal.setUserId("user-456");

        when(goalRepository.findById("goal-123")).thenReturn(Optional.of(existingGoal));

        // Act & Assert for mismatched owner
        assertThatThrownBy(() -> goalService.deleteGoal("goal-123", "user-789"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Goal not found or unauthorized");
        verify(goalRepository, never()).delete(any(Goal.class));

        // Act & Assert for non-existent goal
        when(goalRepository.findById("goal-999")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> goalService.deleteGoal("goal-999", "user-456"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Goal not found or unauthorized");
    }
}
