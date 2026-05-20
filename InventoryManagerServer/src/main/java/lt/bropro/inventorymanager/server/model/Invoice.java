package lt.bropro.inventorymanager.server.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    private int invoiceNumber;
    private LocalDate date;
    private String invoiceStatus;
    @OneToMany(targetEntity = InvoiceItem.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<InvoiceItem> invoiceItemList;
    @ManyToOne(targetEntity = ContactsBook.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private ContactsBook contactsBook;

    public Invoice(int invoiceNumber, LocalDate date, ContactsBook contactsBook) {
        this.invoiceNumber = invoiceNumber;
        this.date = date;
        this.contactsBook = contactsBook;
        this.invoiceStatus = "Unpaid";
    }

    public Invoice(){ }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ContactsBook getContactsBook() {
        return contactsBook;
    }

    public void setContactsBook(ContactsBook contactsBook) {
        this.contactsBook = contactsBook;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String paid) {
        this.invoiceStatus = paid;
    }

    public List<InvoiceItem> getInvoiceItemList() {
        if (invoiceItemList == null) {
            invoiceItemList = new java.util.ArrayList<>();
        }
        return invoiceItemList;
    }

    public void setInvoiceItemList(List<InvoiceItem> invoiceItemList) {
        this.invoiceItemList = invoiceItemList;
    }
}
