package health_care_website.healthcare;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestRepository extends JpaRepository<Test, String> {
    List<Test> findByCategoryIgnoreCase(String category);
}
