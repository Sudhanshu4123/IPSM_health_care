package health_care_website.healthcare;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tests")
public class Test {
    @Id
    @Column(name = "test_code")
    private String testCode;

    @Column(name = "test_name")
    private String testName;

    private String category;
    private BigDecimal mrp;

    // Getters and Setters
    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getMrp() {
        return mrp;
    }

    public void setMrp(BigDecimal mrp) {
        this.mrp = mrp;
    }
}
