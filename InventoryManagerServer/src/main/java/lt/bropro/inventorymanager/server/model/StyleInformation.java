package lt.bropro.inventorymanager.server.model;

import jakarta.persistence.*;

import java.awt.*;
@Entity
@Table(name = "styleInformation")
public class StyleInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    private String websiteName;
    private String websiteLogo;
    @OneToOne(targetEntity = UiThemes.class, fetch = FetchType.EAGER)
    private UiThemes uiThemes;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getWebsiteLogo() {
        return websiteLogo;
    }

    public void setWebsiteLogo(String websiteLogo) {
        this.websiteLogo = websiteLogo;
    }

    public UiThemes getUiThemes() {
        return uiThemes;
    }

    public void setUiThemes(UiThemes uiThemes) {
        this.uiThemes = uiThemes;
    }
}
