package lt.bropro.inventorymanager.server.database;

import lt.bropro.inventorymanager.server.model.StyleInformation;
import lt.bropro.inventorymanager.server.model.UiThemes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UiThemeRepository extends JpaRepository<UiThemes, Long> {
    Optional<UiThemes> findByThemeName(String themeName);
}
