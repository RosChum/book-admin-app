package com.example.bookadminapp.repository;

import com.example.bookadminapp.entity.Book;
import com.example.bookadminapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

   Optional<Book> findByTitleAndAuthor(String title, String author);
   List<Book> findAllByCategoriesIn(List<Category> categories);
}
