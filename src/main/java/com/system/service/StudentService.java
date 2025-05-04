package com.system.service;

import com.system.dto.StudentDTO;
import com.system.entity.Student;
import com.system.exception.ResourceNotFoundException;
import com.system.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    @Cacheable("students")
    public List<StudentDTO> getAll(){
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
        return modelMapper.map(studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student Is Not Found To Show")), StudentDTO.class);
    }

    @CacheEvict(value = "students", allEntries = true)
    public StudentDTO insert(StudentDTO dto){
        System.out.println(dto);
        Student student = modelMapper.map(dto, Student.class);
        student.setEmail(dto.getEmail());
        System.out.println(student);
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
    public StudentDTO update(Long id, StudentDTO dto){
        Student student = studentRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student Is Not Found To Update"));

        student.setFirstName(dto.getFirstName()!=null? dto.getFirstName() : student.getFirstName());
        student.setLastName(dto.getLastName()!=null? dto.getLastName(): student.getLastName());
        student.setEmail(dto.getEmail()!=null? dto.getEmail(): student.getEmail());

        return modelMapper.map(studentRepository.save(student), StudentDTO.class);

    }


    @CacheEvict(value = {"students","student"}, allEntries = true)
    public int delete(Long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student Is Not Found To Delete"));
        studentRepository.delete(student);
        return 1;
    }




}
