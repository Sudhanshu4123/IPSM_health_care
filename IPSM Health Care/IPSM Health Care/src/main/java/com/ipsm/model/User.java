package com.ipsm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Integer userId;
    private String username;
    private String password;
    private String role;
    private String department;
    private String staffId;

    private boolean regNew;
    private boolean regEdit;
    private boolean regManage;
    private boolean invStatus;
    private boolean invReprint;
    private boolean repOutstanding;
    private boolean repSummary;
    private boolean repLedger;
    private boolean repBusiness;
    private boolean repSales;
    private boolean testStatus;

    public User() {
    }

    public User(String username, String password, String role, String department,
            boolean regNew, boolean regEdit, boolean regManage,
            boolean invStatus, boolean invReprint,
            boolean repOutstanding, boolean repSummary, boolean repLedger,
            boolean repBusiness, boolean repSales, boolean testStatus) {
        this.username = username;
        this.password = password;
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

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public boolean isRegNew() {
        return regNew;
    }

    public void setRegNew(boolean regNew) {
        this.regNew = regNew;
    }

    public boolean isRegEdit() {
        return regEdit;
    }

    public void setRegEdit(boolean regEdit) {
        this.regEdit = regEdit;
    }

    public boolean isRegManage() {
        return regManage;
    }

    public void setRegManage(boolean regManage) {
        this.regManage = regManage;
    }

    public boolean isInvStatus() {
        return invStatus;
    }

    public void setInvStatus(boolean invStatus) {
        this.invStatus = invStatus;
    }

    public boolean isInvReprint() {
        return invReprint;
    }

    public void setInvReprint(boolean invReprint) {
        this.invReprint = invReprint;
    }

    public boolean isRepOutstanding() {
        return repOutstanding;
    }

    public void setRepOutstanding(boolean repOutstanding) {
        this.repOutstanding = repOutstanding;
    }

    public boolean isRepSummary() {
        return repSummary;
    }

    public void setRepSummary(boolean repSummary) {
        this.repSummary = repSummary;
    }

    public boolean isRepLedger() {
        return repLedger;
    }

    public void setRepLedger(boolean repLedger) {
        this.repLedger = repLedger;
    }

    public boolean isRepBusiness() {
        return repBusiness;
    }

    public void setRepBusiness(boolean repBusiness) {
        this.repBusiness = repBusiness;
    }

    public boolean isRepSales() {
        return repSales;
    }

    public void setRepSales(boolean repSales) {
        this.repSales = repSales;
    }

    public boolean isTestStatus() {
        return testStatus;
    }

    public void setTestStatus(boolean testStatus) {
        this.testStatus = testStatus;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
