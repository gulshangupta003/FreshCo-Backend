package com.freshco.repository;

import com.freshco.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    long countByUserId(long userId);

}
