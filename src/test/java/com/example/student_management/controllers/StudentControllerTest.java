package com.example.student_management.controllers;

import com.example.student_management.entities.Student;
import com.example.student_management.services.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Student> studentList;

    @BeforeEach
    void setUp() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2000, Calendar.FEBRUARY, 15);
        Student student1 = new Student("Dupont", "Jean", cal1.getTime());
        student1.setId(1);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2001, Calendar.JUNE, 20);
        Student student2 = new Student("Martin", "Marie", cal2.getTime());
        student2.setId(2);

        studentList = Arrays.asList(student1, student2);
    }


    @Test
    void testSaveStudent() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2002, Calendar.APRIL, 10);
        Student newStudent = new Student("Durand", "Pierre", cal.getTime());
        Student savedStudent = new Student("Durand", "Pierre", cal.getTime());
        savedStudent.setId(3);

        when(studentService.save(any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(post("/students/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nom").value("Durand"))
                .andExpect(jsonPath("$.prenom").value("Pierre"));
    }


    void testDeleteStudent() throws Exception {
        when(studentService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/students/delete/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    void testDeleteStudentNotFound() throws Exception {
        when(studentService.delete(999)).thenReturn(false);

        mockMvc.perform(delete("/students/delete/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void testFindAllStudents() throws Exception {
        when(studentService.findAll()).thenReturn(studentList);

        mockMvc.perform(get("/students/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nom").value("Dupont"))
                .andExpect(jsonPath("$[0].prenom").value("Jean"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nom").value("Martin"))
                .andExpect(jsonPath("$[1].prenom").value("Marie"));
    }


    @Test
    void testFindAllStudentsEmpty() throws Exception {
        when(studentService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/students/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    void testCountStudents() throws Exception {
        when(studentService.countStudents()).thenReturn(5L);

        mockMvc.perform(get("/students/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }


    @Test
    void testCountStudentsZero() throws Exception {
        when(studentService.countStudents()).thenReturn(0L);

        mockMvc.perform(get("/students/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }


    @Test
    void testFindByYear() throws Exception {
        Collection<Object[]> yearData = Arrays.asList(
            new Object[]{2000, 3L},
            new Object[]{2001, 5L},
            new Object[]{2002, 2L}
        );

        doReturn(yearData).when(studentService).findNbrStudentByYear();

        mockMvc.perform(get("/students/byYear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }


    @Test
    void testFindByYearEmpty() throws Exception {
        doReturn(List.of()).when(studentService).findNbrStudentByYear();

        mockMvc.perform(get("/students/byYear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
