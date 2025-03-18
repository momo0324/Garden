package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import model.LogSystem;

public class LogController {
    private final LogSystem logSystem = LogSystem.getInstance(); // ✅ Ensure Singleton is Used
    private Timeline logUpdateTimeline;
    private boolean isAutoUpdateEnabled = true;

    @FXML
    private TextArea logArea;
    @FXML
    private ImageView catGif;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private Button returnButton;

    public void initialize() {
        // ✅ Remove incorrect instance creation

        // ✅ Fix: Initialize ComboBox items in Java instead of FXML
        filterComboBox.getItems().addAll("All", "Plant Event", "System Action", "Environmental Change");
        filterComboBox.setOnAction(e -> filterLogs());

        // Initialize return button
        returnButton.setVisible(false);
        returnButton.setOnAction(e -> returnToLiveUpdate());

        // ✅ Load existing logs on window open
        loadLogs();
        // ✅ Load cat.gif from resources
        Image gif = new Image(getClass().getResource("/images/cat.gif").toExternalForm());
        catGif.setImage(gif);

        // ✅ Start real-time updates
        startLogUpdates();
    }

    private void returnToLiveUpdate() {
        isAutoUpdateEnabled = true;
        filterComboBox.setValue("All");
        returnButton.setVisible(false);
        updateLogArea();
    }

    /** ✅ Start a timeline to refresh logs every second */
    private void startLogUpdates() {
        logUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (isAutoUpdateEnabled) {
                updateLogArea();
            }
        }));
        logUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        logUpdateTimeline.play();
    }

    /** ✅ Load logs into the logArea when window opens */
    private void loadLogs() {
        Platform.runLater(() -> {
            logArea.setText(logSystem.getAllLogs());
            System.out.println("📜 Logs loaded: " + logSystem.getAllLogs().split("\n").length + " entries.");
        });
    }

    /** ✅ Refresh logs without disrupting user scroll */
    private void updateLogArea() {
        Platform.runLater(() -> {
            double scrollPosition = getScrollPosition(logArea); // ✅ Save current scroll position
            boolean isAtBottom = isUserAtBottom(logArea); // ✅ Check if user is already at the bottom

            logArea.setText(logSystem.getAllLogs()); // ✅ Update logs

            if (isAtBottom) {
                logArea.positionCaret(logArea.getText().length()); // ✅ Auto-scroll only if at bottom
            } else {
                setScrollPosition(logArea, scrollPosition); // ✅ Maintain user's scroll position
            }
        });
    }
    private boolean isUserAtBottom(TextArea textArea) {
        ScrollBar scrollBar = (ScrollBar) textArea.lookup(".scroll-bar:vertical");
        return scrollBar != null && scrollBar.getValue() >= 0.98; // ✅ Adjust threshold if needed
    }

    /** ✅ Ensure logs sync with filters */
    private void filterLogs() {
        String selectedFilter = filterComboBox.getValue();
        Platform.runLater(() -> {
            double scrollPosition = getScrollPosition(logArea);
            if ("All".equals(selectedFilter)) {
                isAutoUpdateEnabled = true;
                returnButton.setVisible(false);
                logArea.setText(logSystem.getAllLogs());
            } else {
                isAutoUpdateEnabled = false;
                returnButton.setVisible(true);
                logArea.setText(logSystem.getFilteredLogs(selectedFilter));
            }
            setScrollPosition(logArea, scrollPosition);
        });
    }

    /** ✅ Get current scroll position of TextArea */
    private double getScrollPosition(TextArea textArea) {
        ScrollBar scrollBar = (ScrollBar) textArea.lookup(".scroll-bar:vertical");
        return (scrollBar != null) ? scrollBar.getValue() : 0;
    }

    /** ✅ Restore previous scroll position */
    private void setScrollPosition(TextArea textArea, double position) {
        ScrollBar scrollBar = (ScrollBar) textArea.lookup(".scroll-bar:vertical");
        if (scrollBar != null) {
            scrollBar.setValue(position);
        }
    }

    @FXML
    private void handleClose() {
        logUpdateTimeline.stop(); // ✅ Stop updates when closing window
        logArea.getScene().getWindow().hide();
    }
}