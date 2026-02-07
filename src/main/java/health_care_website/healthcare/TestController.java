package health_care_website.healthcare;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TestController {
    private final TestRepository testRepository;

    @GetMapping("/tests")
    public List<Test> getTests(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return testRepository.findByCategoryIgnoreCase(category);
        }
        return testRepository.findAll();
    }

    private final RegistrationRepository registrationRepository;
    private final PatientRepository patientRepository;

    public TestController(TestRepository testRepository, RegistrationRepository registrationRepository,
            PatientRepository patientRepository) {
        this.testRepository = testRepository;
        this.registrationRepository = registrationRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/next-reg-no")
    public String getNextRegNo() {
        Integer maxId = registrationRepository.findMaxId();
        // Align with IPSM/2000 + regId sequence
        int nextId = (maxId == null ? 0 : maxId) + 1;
        return "IPSM/" + (2000 + nextId);
    }

    @GetMapping("/check-duplicate")
    public boolean checkDuplicate(@RequestParam String name, @RequestParam String mobile, @RequestParam String dob) {
        try {
            java.time.LocalDate birthDate = java.time.LocalDate.parse(dob);
            return patientRepository.existsByPatientNameAndMobileAndBirthDate(name, mobile, birthDate);
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/registrations")
    public List<Registration> getRegistrations() {
        return registrationRepository.findAll();
    }

    @GetMapping("/registrations/{id}")
    public Registration getRegistration(@PathVariable Integer id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + id));
    }

    @PostMapping("/registrations")
    @org.springframework.transaction.annotation.Transactional
    public Registration saveRegistration(@RequestBody java.util.Map<String, Object> data) {
        try {
            // Extract Patient Info
            java.util.Map<String, Object> pData = (java.util.Map<String, Object>) data.get("patient");
            String name = (String) pData.get("patientName");
            String mobile = (String) pData.get("mobile");
            java.time.LocalDate dob = java.time.LocalDate.parse((String) pData.get("birthDate"));

            Patient patient = patientRepository.findByPatientNameAndMobileAndBirthDate(name, mobile, dob);
            if (patient == null) {
                patient = new Patient();
                patient.setPatientName(name);
                patient.setMobile(mobile);
                patient.setBirthDate(dob);
                patient.setGender((String) pData.get("gender"));
                patient.setAge((Integer) pData.get("age"));
                patient.setAgeUnit((String) pData.get("ageUnit"));
                patient = patientRepository.save(patient);
            }

            // Create Registration
            Registration reg = new Registration();
            reg.setPatient(patient);
            reg.setRegDate(java.time.LocalDateTime.now());
            reg.setTotalAmount(new java.math.BigDecimal(data.get("totalAmount").toString()));
            reg.setPaidAmount(new java.math.BigDecimal(data.get("paidAmount").toString()));
            reg.setBalanceAmount(new java.math.BigDecimal(data.get("balanceAmount").toString()));
            reg.setDiscountAmount(new java.math.BigDecimal(data.get("discountAmount").toString()));
            reg.setReference((String) data.get("reference"));

            // Save investigations as JSON string
            if (data.containsKey("investigations")) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                reg.setInvestigations(mapper.writeValueAsString(data.get("investigations")));
            }

            return registrationRepository.save(reg);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save registration: " + e.getMessage());
        }
    }
}
