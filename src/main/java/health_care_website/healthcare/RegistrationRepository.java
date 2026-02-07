package health_care_website.healthcare;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Integer> {
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(regId), 0) FROM Registration")
    Integer findMaxId();
}
