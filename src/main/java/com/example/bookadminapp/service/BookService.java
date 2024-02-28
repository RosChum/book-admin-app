package com.example.bookadminapp.service;

import com.example.bookadminapp.dto.BookDto;
import com.example.bookadminapp.entity.Book;
import com.example.bookadminapp.exception.ContentNotFoundException;
import com.example.bookadminapp.mapper.BookMapper;
import com.example.bookadminapp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final BookService bookService;

    private final BookMapper bookMapper;

    public List<BookDto> findAll() {

        return bookRepository.findAll().stream().map(book -> {
            BookDto bookDto = bookMapper.convertToDto(book);
            bookDto.setCategories(book.getCategories());
            return bookDto;
        }).collect(Collectors.toList());
    }

    public BookDto findById(UUID id) {
        Book exestBook = bookRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException(MessageFormat.format("Книга с id {0} не найдена", id)));
        BookDto bookDto = bookMapper.convertToDto(exestBook);
        bookDto.setCategories(exestBook.getCategories());
        return bookDto;

    }

    public BookDto findBookByTitleAndAuthor(String title, String author) {
        Book book = bookRepository.findByTitleAndAuthor(title,author)
                .orElseThrow(()-> new ContentNotFoundException(MessageFormat.format("Книга с названием {0} и автором {1} не найдена", title,author)));
        BookDto bookDto = bookMapper.convertToDto(book);
        bookDto.setCategories(book.getCategories());
        return bookDto;

    }

    public List<BookDto> findBookByCategory(){

    }

    @Transactional
    public BookDto create(BookDto dto) {
        Book book = bookMapper.convertToEntity(dto);
        book.setCategories(dto.getCategories());
        BookDto bookDto = bookMapper.convertToDto(bookRepository.save(book));
        bookDto.setCategories(book.getCategories());
        return bookDto;

    }

    @Transactional
    public BookDto update(UUID id, BookDto dto) {
        Book exestBook = bookRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException(MessageFormat.format("Книга с id {0} не найдена", id)));
        if (dto.getCategories() != null) {
            exestBook.setCategories(dto.getCategories());
        }
        exestBook.setAuthor(dto.getAuthor());
        exestBook.setTitle(dto.getTitle());
        BookDto bookDto = bookMapper.convertToDto(bookRepository.save(exestBook));
        bookDto.setCategories(exestBook.getCategories());
        return bookDto;
    }

    public void delete(UUID id) {
        bookRepository.deleteById(id);
    }

}
