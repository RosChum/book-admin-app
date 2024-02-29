package com.example.bookadminapp.dto;

import com.example.bookadminapp.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class BookDto implements Serializable {

    private UUID id;

    @NotBlank(message = "Название книги не должно быть пустым")
    private String title;

    @NotBlank(message = "ФИО автора не должно быть пустым")
    private String author;

    private List<Category> categories;


}
