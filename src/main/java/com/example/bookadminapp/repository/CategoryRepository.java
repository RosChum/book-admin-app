package com.example.bookadminapp.repository;

import com.example.bookadminapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByName(String name);

   Optional <Category> findCategoryByNameEquals(String name);
}
