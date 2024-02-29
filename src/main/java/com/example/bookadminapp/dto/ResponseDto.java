package com.example.bookadminapp.dto;

import com.example.bookadminapp.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
@Data
public class ResponseDto implements Serializable {
    private UUID id;

    private String title;


    private String author;

    private List<String> categories;
}
