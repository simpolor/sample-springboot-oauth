package io.simpolor.resource.controller;

import io.simpolor.resource.model.StudentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {

    @GetMapping
    public List<StudentDto> list(){
        return Arrays.asList(
                StudentDto.builder()
                        .id(1L)
                        .name("john")
                        .age(18)
                        .grade(2)
                        .hobbies(Arrays.asList("dance", "soccer"))
                        .build(),
                StudentDto.builder()
                        .id(2L)
                        .name("anna")
                        .age(17)
                        .grade(1)
                        .hobbies(Arrays.asList("sing"))
                        .build());
    }

    @GetMapping("/{id}")
    public StudentDto detail(@PathVariable Long id) {
        return StudentDto.builder()
                .id(id)
                .name("john")
                .age(18)
                .grade(2)
                .hobbies(Arrays.asList("dance", "soccer"))
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping
    public void register(@RequestBody StudentDto studentDto) {
        log.info("studentDto : {}", studentDto);
    }

    @PreAuthorize("#oauth2.hasScope('write')")
    @PutMapping("/{id}")
    public void modify(@PathVariable Long id,
                       @RequestBody StudentDto studentDto) {

        log.info("id : {}, studentDto : {}", id, studentDto);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("id : {}", id);
    }
}
