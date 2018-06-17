package lee.jaehun.qantas.profile.model;

import javax.persistence.*;

@Entity
public class Address {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private AddressType type;
    private String value;

    public Address() {
    }

    public Address(AddressType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
