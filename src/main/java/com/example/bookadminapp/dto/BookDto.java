package com.example.bookadminapp.dto;

import com.example.bookadminapp.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BookDto {

    private UUID id;

    private String title;

    private String author;

    private List<Category> categories;


}
