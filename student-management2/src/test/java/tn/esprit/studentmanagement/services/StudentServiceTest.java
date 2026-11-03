
package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setIdStudent(1L);
        student.setFirstName("Asma");
        student.setLastName("Tamarni");
        student.setEmail("asma@example.com");
        student.setPhone("12345678");
        student.setDateOfBirth(LocalDate.of(2002, 1, 1));
        student.setAddress("Tunis");
    }

    @Test
    void getAllStudents_shouldReturnStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student));

        List<Student> result = studentService.getAllStudents();

        assertEquals(1, result.size());
        assertEquals("Asma", result.get(0).getFirstName());
        verify(studentRepository).findAll();
    }

    @Test
    void getAllStudents_shouldReturnEmptyListWhenNoStudents() {
        when(studentRepository.findAll()).thenReturn(List.of());

        List<Student> result = studentService.getAllStudents();

        assertTrue(result.isEmpty());
        verify(studentRepository).findAll();
    }

    @Test
    void getStudentById_shouldReturnStudentWhenFound() {
        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        Student result = studentService.getStudentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdStudent());
        assertEquals("Asma", result.getFirstName());
        verify(studentRepository).findById(1L);
    }

    @Test
    void getStudentById_shouldReturnNullWhenNotFound() {
        when(studentRepository.findById(99L))
                .thenReturn(Optional.empty());

        Student result = studentService.getStudentById(99L);

        assertNull(result);
        verify(studentRepository).findById(99L);
    }

    @Test
    void saveStudent_shouldReturnSavedStudent() {
        when(studentRepository.save(student)).thenReturn(student);

        Student result = studentService.saveStudent(student);

        assertNotNull(result);
        assertEquals(1L, result.getIdStudent());
        verify(studentRepository).save(student);
    }

    @Test
    void saveStudent_shouldPropagateException() {
        when(studentRepository.save(student))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(
                RuntimeException.class,
                () -> studentService.saveStudent(student)
        );

        verify(studentRepository).save(student);
    }

    @Test
    void deleteStudent_shouldCallRepository() {
        doNothing().when(studentRepository).deleteById(1L);

        assertDoesNotThrow(() -> studentService.deleteStudent(1L));

        verify(studentRepository).deleteById(1L);
    }

    @Test
    void deleteStudent_shouldPropagateException() {
        doThrow(new RuntimeException("Database error"))
                .when(studentRepository).deleteById(1L);

        assertThrows(
                RuntimeException.class,
                () -> studentService.deleteStudent(1L)
        );

        verify(studentRepository).deleteById(1L);
    }
}