
package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudentServiceIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void saveStudent_shouldPersistStudentInDatabase() {
        Student student = new Student();
        student.setFirstName("Test");
        student.setLastName("Student");
        student.setEmail("test@example.com");
        student.setPhone("12345678");
        student.setDateOfBirth(LocalDate.of(2002, 1, 1));
        student.setAddress("Tunis");

        Student savedStudent = studentService.saveStudent(student);

        assertNotNull(savedStudent.getIdStudent());

        Student foundStudent = studentRepository
                .findById(savedStudent.getIdStudent())
                .orElseThrow();

        assertEquals("Test", foundStudent.getFirstName());
        assertEquals("Student", foundStudent.getLastName());
    }
}