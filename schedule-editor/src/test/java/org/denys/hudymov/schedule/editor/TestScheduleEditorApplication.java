package org.denys.hudymov.schedule.editor;


import org.springframework.boot.SpringApplication;

public class TestScheduleEditorApplication {

    public static void main(String[] args) {
        SpringApplication.from(ScheduleEditorApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
