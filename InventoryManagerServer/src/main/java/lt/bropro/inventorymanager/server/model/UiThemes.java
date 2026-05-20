package lt.bropro.inventorymanager.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "uiThemes")
public class UiThemes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    private String themeName;
    private String primaryColor;
    private String secondaryColor;
    private String buttonColor;
    private String objectColor;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getThemeName() {
        return themeName;
    }
    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }
    public String getPrimaryColor() {
        return primaryColor;
    }
    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }
    public String getSecondaryColor() {
        return secondaryColor;
    }
    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }
    public String getObjectColor() {
        return objectColor;
    }
    public void setObjectColor(String objectColor) {
        this.objectColor = objectColor;
    }
    public String getButtonColor() {
        return buttonColor;
    }
    public void setButtonColor(String buttonColor) {
        this.buttonColor = buttonColor;
    }
}
