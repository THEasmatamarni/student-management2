
package tn.esprit.studentmanagement.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.util.List;

@Service
public class StudentService implements IStudentService {

    private static final Logger logger =
            LogManager.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Student> getAllStudents() {
        logger.info("Fetching all students");

        List<Student> students = studentRepository.findAll();

        logger.debug("Number of students found: {}", students.size());
        return students;
    }

    @Override
    public Student getStudentById(Long id) {
        logger.info("Fetching student with id: {}", id);

        Student student = studentRepository.findById(id).orElse(null);

        if (student == null) {
            logger.warn("Student not found with id: {}", id);
        } else {
            logger.debug("Student found with id: {}", id);
        }

        return student;
    }

    @Override
    public Student saveStudent(Student student) {
        if (student.getIdStudent() == null) {
            logger.info("Creating a new student");
        } else {
            logger.info("Updating student with id: {}",
                    student.getIdStudent());
        }

        try {
            Student savedStudent = studentRepository.save(student);
            logger.debug("Student saved with id: {}",
                    savedStudent.getIdStudent());
            return savedStudent;
        } catch (RuntimeException exception) {
            logger.error("Failed to save student", exception);
            throw exception;
        }
    }

    @Override
    public void deleteStudent(Long id) {
        logger.info("Deleting student with id: {}", id);

        try {
            studentRepository.deleteById(id);
            logger.debug("Student deleted with id: {}", id);
        } catch (RuntimeException exception) {
            logger.error("Failed to delete student with id: {}", id, exception);
            throw exception;
        }
    }
}