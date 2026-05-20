package lt.bropro.inventorymanager.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contactsBook")
public class ContactsBook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    private String companyName;
    private String companyAddress;
    private String companyCity;
    private String companyCountry;
    private String companyNumber;
    private String companyVatNumber;

    public ContactsBook(String companyName, String companyAddress, String companyCity, String companyCountry, String companyNumber, String companVatNumber) {
        this.companyName = companyName;
        this.companyCity = companyCity;
        this.companyCountry = companyCountry;
        this.companyAddress = companyAddress;
        this.companyNumber = companyNumber;
        this.companyVatNumber = companVatNumber;
    }

    public ContactsBook() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyVatNumber() {
        return companyVatNumber;
    }

    public void setCompanyVatNumber(String companVatNumber) {
        this.companyVatNumber = companVatNumber;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public String getCompanyCountry() {
        return companyCountry;
    }

    public void setCompanyCountry(String companyCountry) {
        this.companyCountry = companyCountry;
    }
}
