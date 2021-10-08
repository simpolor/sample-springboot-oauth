package io.simpolor.resource.model;

import lombok.*;

import java.util.List;

@Data
@Builder
public class StudentDto {

    private Long id;
    private String name;
    private Integer grade;
    private Integer age;
    private List<String> hobbies;
}
