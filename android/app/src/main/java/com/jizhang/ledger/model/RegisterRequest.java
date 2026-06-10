package com.jizhang.ledger.model;

public class RegisterRequest {
    private String phone;
    private String password;
    private String name;
    private Integer age;
    private String occupation;
    private String gender;

    public RegisterRequest(String phone, String password, String name, Integer age, String occupation, String gender) {
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.age = age;
        this.occupation = occupation;
        this.gender = gender;
    }

    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public Integer getAge() { return age; }
    public String getOccupation() { return occupation; }
    public String getGender() { return gender; }
}
