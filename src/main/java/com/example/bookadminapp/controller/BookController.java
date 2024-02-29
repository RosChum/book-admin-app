package com.example.bookadminapp.controller;

import com.example.bookadminapp.dto.BookDto;
import com.example.bookadminapp.dto.ResponseDto;
import com.example.bookadminapp.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<ResponseDto>> findAll() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/find")
    public ResponseEntity<ResponseDto> findBookByTitleAndAuthor(@RequestParam String title, @RequestParam String author) {
        return ResponseEntity.ok(bookService.findBookByTitleAndAuthor(title, author));

    }

    @GetMapping("/find/category")
    public ResponseEntity<List<ResponseDto>> findBookByCategory(@RequestParam String category) {
        return ResponseEntity.ok(bookService.findBookByCategory(category));
    }

    @PostMapping("/create")
    public ResponseEntity<BookDto> create(@RequestBody @Valid BookDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookDto> update(@PathVariable UUID id, @RequestBody @Valid BookDto dto) {
        return ResponseEntity.ok().body(bookService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
