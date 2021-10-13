package io.simpolor.client.controller;

import io.simpolor.client.dto.StudentDto;
import io.simpolor.client.repository.entity.Student;
import io.simpolor.client.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/student")
@RequiredArgsConstructor
public class StudentController {

	private final StudentService studentService;

	@GetMapping(value = "list")
	public ModelAndView list(ModelAndView mav) {

		Long totalCount = studentService.getTotalCount();
		List<StudentDto> studentDtos = StudentDto.of(studentService.getAll());

		mav.addObject("totalCount", totalCount);
		mav.addObject("studentDtos", studentDtos);
		mav.setViewName("student_list");

		return mav;
	}

	@GetMapping(value="/detail/{seq}")
	public ModelAndView detail(ModelAndView mav, @PathVariable long seq) {

		Student student = studentService.get(seq);

		mav.addObject("studentDto", StudentDto.of(student));
		mav.setViewName("student_detail");
		return mav;
	}

	@GetMapping(value="/register")
	public ModelAndView studentRegisterForm(ModelAndView mav) {

		mav.setViewName("student_register");
		return mav;
	}

	@PostMapping(value = "/register")
	public ModelAndView register(ModelAndView mav, StudentDto studentDto) {

		log.info("studentDto : {}", studentDto.toString());

		Student student = studentService.create(studentDto.toEntity());

		mav.setViewName("redirect:/student/detail/"+student.getStudentId());
		return mav;
	}

	@GetMapping(value="/modify/{seq}")
	public ModelAndView studentModifyForm(ModelAndView mav, @PathVariable long seq) {

		Student student = studentService.get(seq);

		mav.addObject("studentDto", StudentDto.of(student));
		mav.setViewName("student_modify");
		return mav;
	}

	@PostMapping(value="/modify/{seq}")
	public ModelAndView modify(ModelAndView mav, @PathVariable long seq, StudentDto studentDto) {

		log.info("studentDto : {}", studentDto.toString());

		studentDto.setStudentId(seq);
		studentService.update(studentDto.toEntity());

		mav.setViewName("redirect:/student/detail/"+seq);
		return mav;
	}

	@PostMapping(value="/delete/{seq}")
	public ModelAndView delete(ModelAndView mav, @PathVariable long seq) {

		studentService.delete(seq);

		mav.setViewName("redirect:/student/list");
		return mav;
	}

}
