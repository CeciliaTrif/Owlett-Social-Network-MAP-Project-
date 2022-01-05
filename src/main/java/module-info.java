module map.teamprojectmap_ceciliaandrei_javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens map.socialnetwork to javafx.fxml;
    exports map.socialnetwork;
    exports map.socialnetwork.controller;
    opens map.socialnetwork.controller to javafx.fxml;

    opens map.socialnetwork.domain.model to javafx.base;
    opens map.socialnetwork.views.wrapper to javafx.base;
}