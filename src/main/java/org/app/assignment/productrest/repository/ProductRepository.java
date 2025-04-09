package org.app.assignment.productrest.repository;

import org.app.assignment.productrest.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCode(String code);

    Optional<Product> findByCode(String code);
}
