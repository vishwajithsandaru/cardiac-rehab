package com.dexter.fyp.backend.repository;
import com.dexter.fyp.backend.entity.MedicalRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordsRepository extends JpaRepository<MedicalRecords, Long> {
}
