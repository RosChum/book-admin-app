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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "cacheManager")
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final CacheManager cacheManager;

    public List<ResponseDto> findAll() {
        List<Category> testCategory = categoryRepository.findAll();
        testCategory.forEach(System.out::println);

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

    @Transactional
    public ResponseDto create(BookDto dto) {
        Book book = bookMapper.convertToEntity(dto);
        book.setCategories(createCategory(book.getCategories()));
        ResponseDto responseDto = bookMapper.convertToResponse(bookRepository.save(book));
        responseDto.setCategories(book.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        clearCache(book.getTitle(), book.getAuthor(), book.getCategories());
        return responseDto;

    }


    @Transactional
    public ResponseDto update(UUID id, BookDto dto) {
        Book exestBook = bookRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException(MessageFormat.format("Книга с id {0} не найдена", id)));
        clearCache(exestBook.getTitle(), exestBook.getAuthor(), exestBook.getCategories());
        if (dto.getCategories() != null) {
            exestBook.setCategories(createCategory(dto.getCategories()));
        }
        exestBook.setAuthor(dto.getAuthor());
        exestBook.setTitle(dto.getTitle());
        exestBook.setCategories(createCategory(dto.getCategories()));
        ResponseDto responseDto = bookMapper.convertToResponse(bookRepository.save(exestBook));
        responseDto.setCategories(exestBook.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        return responseDto;
    }

    public void delete(UUID id) {
        Book book = findById(id);
        clearCache(book.getTitle(), book.getAuthor(), book.getCategories());
        bookRepository.deleteById(id);
    }

    private Book findById(UUID id) {
        return bookRepository.findById(id).orElseThrow();
    }

    private void clearCache(String title, String author, List<Category> categories) {
        log.info(" clearCache  title" + title + " author " + author + "categories" + categories);
        Objects.requireNonNull(cacheManager.getCache("findBookByTitleAndAuthor")).evict(title + author);
        categories.forEach(category -> {
            Objects.requireNonNull(cacheManager.getCache("findBookByCategory")).evict(category.getName());
        });
    }

    private List<Category> createCategory(List<Category> categories){
     return categories.stream().map(category -> {
            Category exestCategory = categoryRepository.findCategoryByNameEquals(category.getName()).orElse(new Category());
            if (exestCategory.getId() == null){
                exestCategory.setName(category.getName());
                exestCategory.setBooks(category.getBooks());
                categoryRepository.save(exestCategory);
            }
            return exestCategory;
        }).collect(Collectors.toList());
    }

}
