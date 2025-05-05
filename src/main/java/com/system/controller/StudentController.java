package com.system.controller;

import com.system.dto.StudentDTO;
import com.system.repository.StudentRepository;
import com.system.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @Operation(summary = "get all students", description = "get all students from db")
    @ApiResponse(responseCode = "200", description = "get students successfully")
    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok().body(studentService.getAll());
    }


    @Operation(summary = "get student by id", description = "get student record from db")
    @ApiResponse(responseCode = "200", description = "get student successfully")
    @ApiResponse(responseCode = "404", description = "student not found")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity.ok().body(studentService.getById(id));
    }

    @Operation(summary = "delete student by id", description = "delete student record from db")
    @ApiResponse(responseCode = "204", description = "delete student successfully")
    @ApiResponse(responseCode = "404", description = "student not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        return ResponseEntity.ok().body(studentService.delete(id));
    }


    @Operation(summary = "insert student", description = "insert one student record from db")
    @ApiResponse(responseCode = "201", description = "inserted student successfully")
//    @PreAuthorize()
    @PostMapping
    public ResponseEntity<?> insert(@RequestBody StudentDTO studentDTO){
        System.out.println(studentDTO);
        return ResponseEntity.ok().body(studentService.insert(studentDTO));
    }

    @Operation(summary = "update student", description = "update a student record in db")
    @ApiResponse(responseCode = "200", description = "updated student successfully")
    @ApiResponse(responseCode = "404", description = "student not found")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody StudentDTO studentDTO){
        return ResponseEntity.ok().body(studentService.update(id, studentDTO));
    }

}
