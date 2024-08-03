package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    @Query("SELECT new com.wha.warehousemanagement.dtos.responses.CategoryResponse(c.id, c.name, c.description) FROM Category c ORDER BY c.id desc")
    List<CategoryResponse> getAllCategoryResponses();

    @Query("select c from Category c " +
            "where (:search is null or LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "order by c.name asc ")
    List<Category> findAllCategoriesWithSearch(@Param("search") String search);
}
