package com.example.bookadminapp.service;

import com.example.bookadminapp.dto.BookDto;
import com.example.bookadminapp.dto.ResponseDto;
import com.example.bookadminapp.entity.Book;
import com.example.bookadminapp.entity.Category;
import com.example.bookadminapp.exception.ContentNotFoundException;
import com.example.bookadminapp.mapper.BookMapper;
import com.example.bookadminapp.repository.BookRepository;
import com.example.bookadminapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "cacheManager")
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final CacheManager cacheManager;

    public List<ResponseDto> findAll() {
        return bookRepository.findAll().stream().map(book -> {
            ResponseDto responseDto = bookMapper.convertToResponse(book);
            responseDto.setCategories(book.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
            return responseDto;
        }).collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "findBookByTitleAndAuthor", key = "#title + #author")
    public ResponseDto findBookByTitleAndAuthor(String title, String author) {
        Book book = bookRepository.findByTitleAndAuthor(title, author)
                .orElseThrow(() -> new ContentNotFoundException(MessageFormat
                        .format("Книга с названием {0} и автором {1} не найдена", title, author)));
        ResponseDto responseDto = bookMapper.convertToResponse(book);
        responseDto.setCategories(book.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        return responseDto;

    }

    @Cacheable(cacheNames = "findBookByCategory", key = "#category")
    public List<ResponseDto> findBookByCategory(String category) {
        if (category == null || category.isBlank()) return findAll();

        Category exestCategory = categoryRepository.findByName(category)
                .orElseThrow(() -> new ContentNotFoundException(MessageFormat
                        .format("Категория {0} не найдена", category)));
        return exestCategory.getBooks().stream().map(book -> {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setId(book.getId());
            responseDto.setTitle(book.getTitle());
            responseDto.setAuthor(book.getAuthor());
            responseDto.setCategories(book.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
            return responseDto;
        }).collect(Collectors.toList());

    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "findBookByTitleAndAuthor", key = "#dto.title + #dto.author", beforeInvocation = true),
            @CacheEvict(cacheNames = "findBookByCategory", key = "#dto.categories.get(0).name", beforeInvocation = true)
    })
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
        Book book = findById(id);
        clearCache(book.getTitle(), book.getAuthor(), book.getCategories());


        bookRepository.deleteById(id);
    }

    private Book findById(UUID id) {
        return bookRepository.findById(id).orElseThrow();
    }

    private void clearCache(String title, String author, List<Category> category) {
        cacheManager.getCache("findBookByTitleAndAuthor").evict(title + author);
        cacheManager.getCache("findBookByCategory").evict(category.get(0).getName());
    }

}
