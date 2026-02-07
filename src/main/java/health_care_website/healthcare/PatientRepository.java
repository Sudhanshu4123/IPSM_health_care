package health_care_website.healthcare;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    @Query("SELECT COALESCE(MAX(patientId), 0) FROM Patient")
    Integer findMaxId();

    boolean existsByPatientNameAndMobileAndBirthDate(String patientName, String mobile, LocalDate birthDate);

    Patient findByPatientNameAndMobileAndBirthDate(String patientName, String mobile, LocalDate birthDate);
}
