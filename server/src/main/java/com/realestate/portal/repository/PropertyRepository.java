package com.realestate.portal.repository;

import com.realestate.portal.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    List<Property> findByTypeIgnoreCase(String type);
    
    List<Property> findByPriceBetween(Double minPrice, Double maxPrice);
    
    @Query("SELECT p FROM Property p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Property> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Property p WHERE " +
           "(:keyword IS NULL OR " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:type IS NULL OR LOWER(p.type) = LOWER(:type)) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Property> searchProperties(
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);
}
