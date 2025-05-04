package com.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO implements Serializable {
    private Long id;
    @NotBlank(message = "First Name Is Required")
    private String firstName;
    @NotBlank(message = "Last Name Is Required")
    private String lastName;

    @Email(message = "Enter A Valid Email")
    @NotBlank(message = "Email is Required")
    private String email;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private String createdAt;

}
