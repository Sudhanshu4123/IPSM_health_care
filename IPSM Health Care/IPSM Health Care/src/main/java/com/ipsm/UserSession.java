package com.ipsm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSession {
    private String username;
    private String role;
    private String department;

    // Permissions
    @JsonProperty("regNew")
    private boolean regNew;
    @JsonProperty("regEdit")
    private boolean regEdit;
    @JsonProperty("regManage")
    private boolean regManage;
    @JsonProperty("invStatus")
    private boolean invStatus;
    @JsonProperty("invReprint")
    private boolean invReprint;
    @JsonProperty("repOutstanding")
    private boolean repOutstanding;
    @JsonProperty("repSummary")
    private boolean repSummary;
    @JsonProperty("repLedger")
    private boolean repLedger;
    @JsonProperty("repBusiness")
    private boolean repBusiness;
    @JsonProperty("repSales")
    private boolean repSales;
    @JsonProperty("testStatus")
    private boolean testStatus;

    // Default constructor for Jackson
    public UserSession() {
    }

    // Single constructor to avoid ambiguity
    public UserSession(String username, String role, String department,
            boolean regNew, boolean regEdit, boolean regManage,
            boolean invStatus, boolean invReprint,
            boolean repOutstanding, boolean repSummary, boolean repLedger,
            boolean repBusiness, boolean repSales, boolean testStatus) {
        this.username = username;
        this.role = role;
        this.department = department;
        this.regNew = regNew;
        this.regEdit = regEdit;
        this.regManage = regManage;
        this.invStatus = invStatus;
        this.invReprint = invReprint;
        this.repOutstanding = repOutstanding;
        this.repSummary = repSummary;
        this.repLedger = repLedger;
        this.repBusiness = repBusiness;
        this.repSales = repSales;
        this.testStatus = testStatus;
    }

    // Constructor for manual admin login in LoginFrame
    public UserSession(String username, String role,
            boolean regNew, boolean regEdit, boolean regManage,
            boolean invStatus, boolean invReprint,
            boolean repOutstanding, boolean repSummary, boolean repLedger,
            boolean repBusiness, boolean repSales, boolean testStatus) {
        this(username, role, "ADMIN", regNew, regEdit, regManage, invStatus, invReprint,
                repOutstanding, repSummary, repLedger, repBusiness, repSales, testStatus);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean canRegNew() {
        return regNew;
    }

    public void setRegNew(boolean regNew) {
        this.regNew = regNew;
    }

    public boolean canRegEdit() {
        return regEdit;
    }

    public void setRegEdit(boolean regEdit) {
        this.regEdit = regEdit;
    }

    public boolean canRegManage() {
        return regManage;
    }

    public void setRegManage(boolean regManage) {
        this.regManage = regManage;
    }

    public boolean canInvStatus() {
        return invStatus;
    }

    public void setInvStatus(boolean invStatus) {
        this.invStatus = invStatus;
    }

    public boolean canInvReprint() {
        return invReprint;
    }

    public void setInvReprint(boolean invReprint) {
        this.invReprint = invReprint;
    }

    public boolean canRepOutstanding() {
        return repOutstanding;
    }

    public void setRepOutstanding(boolean repOutstanding) {
        this.repOutstanding = repOutstanding;
    }

    public boolean canRepSummary() {
        return repSummary;
    }

    public void setRepSummary(boolean repSummary) {
        this.repSummary = repSummary;
    }

    public boolean canRepLedger() {
        return repLedger;
    }

    public void setRepLedger(boolean repLedger) {
        this.repLedger = repLedger;
    }

    public boolean canRepBusiness() {
        return repBusiness;
    }

    public void setRepBusiness(boolean repBusiness) {
        this.repBusiness = repBusiness;
    }

    public boolean canRepSales() {
        return repSales;
    }

    public void setRepSales(boolean repSales) {
        this.repSales = repSales;
    }

    public boolean canTestStatus() {
        return testStatus;
    }

    public void setTestStatus(boolean testStatus) {
        this.testStatus = testStatus;
    }
}
