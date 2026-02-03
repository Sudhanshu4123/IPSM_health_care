package com.ipsm.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    private String department;

    // Permissions
    private Boolean regNew = true;
    private Boolean regEdit = true;
    private Boolean regManage = true;
    private Boolean invStatus = true;
    private Boolean invReprint = true;
    private Boolean repOutstanding = true;
    private Boolean repSummary = true;
    private Boolean repLedger = true;
    private Boolean repBusiness = true;
    private Boolean repSales = true;
    private Boolean testStatus = false;

    public User() {
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

    public Boolean getRegNew() {
        return regNew;
    }

    public void setRegNew(Boolean regNew) {
        this.regNew = regNew;
    }

    public Boolean getRegEdit() {
        return regEdit;
    }

    public void setRegEdit(Boolean regEdit) {
        this.regEdit = regEdit;
    }

    public Boolean getRegManage() {
        return regManage;
    }

    public void setRegManage(Boolean regManage) {
        this.regManage = regManage;
    }

    public Boolean getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Boolean invStatus) {
        this.invStatus = invStatus;
    }

    public Boolean getInvReprint() {
        return invReprint;
    }

    public void setInvReprint(Boolean invReprint) {
        this.invReprint = invReprint;
    }

    public Boolean getRepOutstanding() {
        return repOutstanding;
    }

    public void setRepOutstanding(Boolean repOutstanding) {
        this.repOutstanding = repOutstanding;
    }

    public Boolean getRepSummary() {
        return repSummary;
    }

    public void setRepSummary(Boolean repSummary) {
        this.repSummary = repSummary;
    }

    public Boolean getRepLedger() {
        return repLedger;
    }

    public void setRepLedger(Boolean repLedger) {
        this.repLedger = repLedger;
    }

    public Boolean getRepBusiness() {
        return repBusiness;
    }

    public void setRepBusiness(Boolean repBusiness) {
        this.repBusiness = repBusiness;
    }

    public Boolean getRepSales() {
        return repSales;
    }

    public void setRepSales(Boolean repSales) {
        this.repSales = repSales;
    }

    public Boolean getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(Boolean testStatus) {
        this.testStatus = testStatus;
    }
}
