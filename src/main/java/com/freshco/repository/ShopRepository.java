package com.freshco.repository;

import com.freshco.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    boolean existsByOwnerId(Long id);

    Optional<Shop> findByOwnerId(Long id);

}
