package boost.brain.course.tasks.controller.dto;

import lombok.Data;

@Data
public class TaskDto {
    private long id;
    private int project;
    private String author;
    private String implementer;
    private String name;
    private String text;
    private long createDate;
    private long updateDate;

}