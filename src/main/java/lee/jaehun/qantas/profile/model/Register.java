package lee.jaehun.qantas.profile.model;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Register {

    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private Date dob;
    private List<Address> address = new ArrayList<>();

    public Register() {
    }

    public User getUser(PasswordEncoder passwordEncoder) {
        User user = new User(getUsername(), getPassword(), true);
        if (passwordEncoder != null) {
            user.setPassword(passwordEncoder.encode(password));
        }
        return user;
    }

    public Profile getProfile() {
        return new Profile(getFirstname(), getLastname(), getDob(), getAddress());
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }
}
