package com.akash.medicalproj.repository;

import com.akash.medicalproj.entity.ServiceProvider;
import com.akash.medicalproj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    Optional<ServiceProvider> findByUser(User user);
    Optional<ServiceProvider> findByUserId(Long userId);
    List<ServiceProvider> findByIsActiveTrue();
    List<ServiceProvider> findByCityIgnoreCase(String city);
}
