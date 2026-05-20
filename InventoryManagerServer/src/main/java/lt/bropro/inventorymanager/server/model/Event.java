package lt.bropro.inventorymanager.server.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
public class    Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    private String title;
    private String address;
    private LocalDate date;
    private String meetupTime;
    private String arrivalTime;
    private String readyTime;
    private String soundCheckTime;
    private String guestTime;
    private String startTime;
    private String endTime;
    private String comments;
    @OneToMany(targetEntity = StoredItems.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<StoredItems> storedItemsList;
    @ManyToMany(targetEntity = Employee.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Employee> employeeList;
    @OneToMany(targetEntity = Invoice.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Invoice> invoiceList;

    public Event(String title, String address, LocalDate date, String meetupTime, String arrivalTime, String readyTime, String soundCheckTime, String guestTime, String endTime, String startTime, String comments) {
        this.title = title;
        this.address = address;
        this.date = date;
        this.meetupTime = meetupTime;
        this.arrivalTime = arrivalTime;
        this.readyTime = readyTime;
        this.soundCheckTime = soundCheckTime;
        this.guestTime = guestTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.comments = comments;
    }

    public Event() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<StoredItems> getStoredItemsList() {
        return storedItemsList;
    }

    public void setStoredItemsList(List<StoredItems> storedItemsList) {
        this.storedItemsList = storedItemsList;
    }

    public Set<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(Set<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public List<Invoice> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(List<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    public String getMeetupTime() {
        return meetupTime;
    }

    public void setMeetupTime(String meetupTime) {
        this.meetupTime = meetupTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(String readyTime) {
        this.readyTime = readyTime;
    }

    public String getSoundCheckTime() {
        return soundCheckTime;
    }

    public void setSoundCheckTime(String soundCheckTime) {
        this.soundCheckTime = soundCheckTime;
    }

    public String getGuestTime() {
        return guestTime;
    }

    public void setGuestTime(String guestTime) {
        this.guestTime = guestTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
