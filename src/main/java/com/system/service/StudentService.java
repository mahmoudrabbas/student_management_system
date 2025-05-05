package com.system.service;

import com.system.dto.StudentDTO;
import com.system.entity.Student;
import com.system.exception.ResourceNotFoundException;
import com.system.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Cacheable("students")
    public List<StudentDTO> getAll(){
        logger.info("returning all students");
        return studentRepository.findAll()
                .stream().map(student ->{
                    return new StudentDTO(student.getId(),
                            student.getFirstName(),
                            student.getLastName(),
                            student.getEmail());
                })
                .collect(Collectors.toList());
    }

    @Cacheable(value = "student", key = "#id")
    public StudentDTO getById(Long id){
        logger.info("returning a student with id {}", id);
        return modelMapper.map(studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student Is Not Found To Show")), StudentDTO.class);
    }

    @CacheEvict(value = "students", allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public StudentDTO insert(StudentDTO dto){
        logger.info("new student {}", dto.getFirstName()+" "+ dto.getLastName());
        Student student = modelMapper.map(dto, Student.class);
        student.setEmail(dto.getEmail());
        studentRepository.save(student);
        return dto;
    }

    @Caching(
            put = {
                    @CachePut(value = "student", key = "#id")
            },
            evict = {
                    @CacheEvict(value = "students", allEntries = true)
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    public StudentDTO update(Long id, StudentDTO dto){
        logger.info("update student with id {} and name {}", id, dto.getFirstName()+" "+ dto.getLastName());
        Student student = studentRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student Is Not Found To Update"));

        student.setFirstName(dto.getFirstName()!=null? dto.getFirstName() : student.getFirstName());
        student.setLastName(dto.getLastName()!=null? dto.getLastName(): student.getLastName());
        student.setEmail(dto.getEmail()!=null? dto.getEmail(): student.getEmail());

        return modelMapper.map(studentRepository.save(student), StudentDTO.class);

    }


    @CacheEvict(value = {"students","student"}, allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public int delete(Long id){
        logger.info("delete student with id {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student Is Not Found To Delete"));
        studentRepository.delete(student);
        return 1;
    }




}
