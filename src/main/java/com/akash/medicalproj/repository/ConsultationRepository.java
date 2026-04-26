package com.akash.medicalproj.repository;

import com.akash.medicalproj.entity.Consultation;
import com.akash.medicalproj.entity.Doctor;
import com.akash.medicalproj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByUser(User user);
    List<Consultation> findByDoctor(Doctor doctor);
    Optional<Consultation> findByRoomId(String roomId);
}
