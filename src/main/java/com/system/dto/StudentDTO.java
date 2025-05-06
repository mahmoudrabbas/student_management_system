package com.system.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.system.config.SanitizingDeserializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO implements Serializable {
    private Long id;
    @NotBlank(message = "First Name Is Required")
    @JsonDeserialize(using = SanitizingDeserializer.class)
    private String firstName;
    @JsonDeserialize(using = SanitizingDeserializer.class)
    @NotBlank(message = "Last Name Is Required")
    private String lastName;

    @Email(message = "Enter A Valid Email")
    @NotBlank(message = "Email is Required")
    private String email;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private String createdAt;

}
