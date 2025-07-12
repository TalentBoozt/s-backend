package com.talentboozt.s_backend.shared.security.cfg;

import io.micrometer.context.ContextSnapshot;
import io.micrometer.context.ContextSnapshotFactory;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.Nullable;

public class ContextPropagatingTaskDecorator implements TaskDecorator {

    private final ContextSnapshotFactory contextSnapshotFactory;

    public ContextPropagatingTaskDecorator() {
        this.contextSnapshotFactory = ContextSnapshotFactory.builder().build();
    }

    @Override
    public Runnable decorate(@Nullable Runnable task) {
        if (task == null) {
            return null;
        }

        ContextSnapshot snapshot = contextSnapshotFactory.captureAll();
        return () -> {
            try (ContextSnapshot.Scope scope = snapshot.setThreadLocals()) {
                task.run();
            }
        };
    }
}

