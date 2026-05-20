package lt.bropro.inventorymanager.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "companyInformation")
public class CompanyInformation {
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
    private String bankCode;
    private String bankAccountCode;

    public CompanyInformation(String companyName, String companyAddress, String companyCity, String companyCountry, String companyNumber, String companyVatNumber, String bankCode, String bankAccountCode) {
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyCity = companyCity;
        this.companyCountry = companyCountry;
        this.companyNumber = companyNumber;
        this.companyVatNumber = companyVatNumber;
        this.bankCode = bankCode;
        this.bankAccountCode = bankAccountCode;
    }

    public CompanyInformation() {
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccountCode() {
        return bankAccountCode;
    }

    public void setBankAccountCode(String bankAccountCode) {
        this.bankAccountCode = bankAccountCode;
    }
}
