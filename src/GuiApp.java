import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GuiApp extends Application {

    private Stage primaryStage;
    private ObservableList<FolderItem> recentFoldersList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Smart File Organizer");

        // --- Header ---
        SVGPath backArrow = new SVGPath();
        backArrow.setContent("M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z");
        backArrow.setFill(Color.WHITE);
        Label title = new Label("Smart File Organizer");
        title.getStyleClass().add("header-title");
        HBox header = new HBox(15, backArrow, title);
        header.getStyleClass().add("header");

        // --- Top Section (Icon & Button) ---
        SVGPath folderIcon = new SVGPath();
        folderIcon.setContent("M20 6h-8l-2-2H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm-2 6h-2v2h-2v-2h-2v-2h2v-2h2v2h2v2z");
        folderIcon.setFill(Color.web("#3B82F6"));
        folderIcon.setScaleX(3.0);
        folderIcon.setScaleY(3.0);

        Button selectFolderButton = new Button("Select Folder to Organize");
        selectFolderButton.setOnAction(e -> chooseAndOrganizeFolder());

        VBox topSection = new VBox(25, folderIcon, selectFolderButton);
        topSection.getStyleClass().add("top-section");

        // --- "Recently Organized" List ---
        Label listHeader = new Label("Recently Organized");
        listHeader.getStyleClass().add("list-header-label");
        
        ListView<FolderItem> listView = new ListView<>(recentFoldersList);
        listView.setCellFactory(param -> new RecentFolderCell());
        listView.getStyleClass().add("list-view");
        listView.setPlaceholder(new Label("No recent activity. Organize a folder to begin!"));

        VBox listSection = new VBox(10, listHeader, listView);

        // --- Main Layout ---
        VBox mainContent = new VBox(30, topSection, listSection);
        mainContent.getStyleClass().add("main-content");
        VBox root = new VBox(header, mainContent);

        // --- Scene and Stage ---
        Scene scene = new Scene(root, 420, 750);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void chooseAndOrganizeFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Folder to Organize");
        File sourceDirectory = directoryChooser.showDialog(primaryStage);

        if (sourceDirectory != null) {
            showProgressDialog(sourceDirectory);
        }
    }

    private void showProgressDialog(File sourceDirectory) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Organizing...");

        Label progressLabel = new Label("Processing files, please wait...");
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(-1.0);

        VBox dialogVBox = new VBox(20, progressLabel, progressBar);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setPadding(new Insets(20));
        Scene dialogScene = new Scene(dialogVBox);
        dialogStage.setScene(dialogScene);
        dialogStage.show();

        Task<Integer> organizationTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return organizeFiles(sourceDirectory);
            }
        };

        organizationTask.setOnSucceeded(e -> {
            dialogStage.close();
            int filesMoved = organizationTask.getValue();
            String date = new SimpleDateFormat("MMM dd, yyyy").format(new Date());
            recentFoldersList.add(0, new FolderItem(sourceDirectory.getName(), "Organized " + date, String.valueOf(filesMoved), true));
            
            try {
                Desktop.getDesktop().open(sourceDirectory);
            } catch (IOException ioException) {
                System.err.println("Could not open the organized folder.");
            }
        });

        new Thread(organizationTask).start();
    }

    private int organizeFiles(File sourceFolder) {
        String basePath = sourceFolder.getAbsolutePath();
        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule(".pdf", basePath + "/PDFs"));
        rules.add(new Rule(".jpg", basePath + "/Images"));
        rules.add(new Rule(".png", basePath + "/Images"));
        rules.add(new Rule(".zip", basePath + "/Archives"));
        rules.add(new Rule(".exe", basePath + "/Installers"));
        rules.add(new Rule(".docx", basePath + "/Documents"));
        rules.add(new Rule(".mp4", basePath + "/Videos"));
        rules.add(new Rule(".pptx", basePath + "/Presentations"));

        File[] files = sourceFolder.listFiles();
        if (files == null) return 0;

        int filesMoved = 0;
        for (File file : files) {
            if (file.isFile()) {
                for (Rule rule : rules) {
                    if (file.getName().toLowerCase().endsWith(rule.fileExtension)) {
                        try {
                            Path destDir = Paths.get(rule.destinationFolder);
                            Files.createDirectories(destDir);
                            Files.move(file.toPath(), destDir.resolve(file.getName()));
                            filesMoved++;
                        } catch (IOException ex) {
                            // Error handling can be improved here
                        }
                        break;
                    }
                }
            }
        }
        return filesMoved;
    }

    // --- Helper classes for the list view ---
    private static class FolderItem {
        String title, subtitle, count;
        boolean isGreen;
        FolderItem(String title, String subtitle, String count, boolean isGreen) {
            this.title = title; this.subtitle = subtitle; this.count = count; this.isGreen = isGreen;
        }
    }

    static class RecentFolderCell extends ListCell<FolderItem> {
        @Override
        protected void updateItem(FolderItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                SVGPath countIcon = new SVGPath();
                countIcon.setContent("M4 15h16v-2H4v2zm0-4h16v-2H4v2zm0-4h16V5H4v2z");
                countIcon.setFill(Color.web("#22C55E"));
                SVGPath folderIcon = new SVGPath();
                folderIcon.setContent("M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z");
                folderIcon.setFill(item.isGreen ? Color.web("#22C55E") : Color.web("#A0A0A0"));
                Label title = new Label(item.title);
                title.getStyleClass().add("list-item-title");
                Label subtitle = new Label(item.subtitle);
                subtitle.getStyleClass().add("list-item-subtitle");
                VBox textVBox = new VBox(title, subtitle);
                Label countText = new Label(item.count);
                countText.getStyleClass().add("list-item-count-text");
                Label arrow = new Label(">");
                arrow.setTextFill(Color.web("#A0A0A0"));
                HBox countBox = new HBox(5, countIcon, countText, new Pane(), arrow);
                countBox.setAlignment(Pos.CENTER_LEFT);
                Pane spacer = new Pane();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                HBox itemBox = new HBox(15, folderIcon, textVBox, spacer, countBox);
                itemBox.setAlignment(Pos.CENTER_LEFT);
                setGraphic(itemBox);
            }
        }
    }

    private static class Rule {
        final String fileExtension;
        final String destinationFolder;
        Rule(String fileExtension, String destinationFolder) {
            this.fileExtension = fileExtension;
            this.destinationFolder = destinationFolder;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
