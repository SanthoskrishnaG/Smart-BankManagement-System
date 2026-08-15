package backend;

import java.io.Serializable;

public class Customer implements Serializable {
    private String customerId;
    private String name;
    private String email;
    private String phone;
    private String pin;

    public Customer(String customerId, String name, String email, String phone, String pin) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.pin = pin;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPin() { return pin; }

    @Override
    public String toString() {
        return "Customer ID: " + customerId + ", Name: " + name + ", Email: " + email + ", Phone: " + phone;
    }
}
