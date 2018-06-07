import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.ArrayList;
import javafx.collections.MapChangeListener;
import java.lang.Runnable;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.Node;
import javafx.util.Callback;

/**
 * [Enter application description here]
 *
 * @author Griffin Elliott Goggans
 * @version 0.0.1
 * Last modified: 6/7/2018
 */


class App extends Application {
    private File directory;
    private File[] mp3Directory;
    private ArrayList<File> mp3Files = new ArrayList<File>();
    private MediaPlayer mediaPlayer;

    public class Song {
        private File file;
        private Media media;
        private String fileName;
        private String artist;
        private String title;
        private String album;
        /**
         * Overloaded constructor, creates a File object and puts it into
         * song.
         *
         * @param songPath The path of the file to be loaded.
         */
        public Song(String songPath) {
            this.file = new File(songPath);
            this.fileName = file.getName();
            this.media = new Media(songPath);
            createMeta(media);
            System.out.println("Song created.");

        }
        /**
         * Returns an actual File object.
         *
         * @return song, a File object.
         */
        public void createMeta(Media media) {
            media.getMetadata().addListener(new MapChangeListener<String, Object>() {
                @Override
                public void onChanged(Change<? extends String, ? extends Object> ch) {
                    if (ch.wasAdded()) {
                        handleMetadata(ch.getKey(), ch.getValueAdded());
                    }
                }
            });
            System.out.println("Metadata created.");
        }
        private void handleMetadata(String key, Object value) {
            if (key.equals("album")) {
                this.album = value.toString();
            } else if (key.equals("artist")) {
                this.artist = value.toString();
            } if (key.equals("title")) {
                this.title = value.toString();
            }
            System.out.println("Metadata handled.");
        }
        public File getFile() {
            return file;
        }
        public Media getMedia() {
            return media;
        }
        public String getFileName() {
            return fileName;
        }
        public String getTitle() {
            return title;
        }
        public String getArtist() {
            return artist;
        }
        public String getAlbum() {
            return album;
        }
    }

    @Override public void start(Stage stage) {
        directory = new File(".");
        mp3Directory = directory.listFiles();
        for (File file: mp3Directory) {
            if (file.getName().contains(".mp3")) {
                mp3Files.add(file);
            }
        }
        System.out.println(mp3Files);

        ObservableList<Song> songList =
            FXCollections.observableArrayList();
        for (File file: mp3Files) {
            String source = file.toURI().toString();
            Song song = new Song(source);
            songList.add(song);
        }

        TableView<Song> table = createTable(songList);

        Button playButton = new Button("Play");
        Button pauseButton = new Button("Pause");
        Button searchButton = new Button("Search Songs");
        Button showAllButton = new Button("Show all Songs");

        playButton.setOnAction(e -> {
                Song song = table.getSelectionModel().getSelectedItem();
                if (!(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))) {
                    playSong(song);
                    playButton.setDisable(true);
                    pauseButton.setDisable(false);
                }
            });
        playButton.disableProperty()
            .bind(Bindings.isNull(table.getSelectionModel().selectedItemProperty()));

        pauseButton.setOnAction(e -> {
                Song song = table.getSelectionModel().getSelectedItem();
                if (!(mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED))) {
                    pauseSong(song);
                    pauseButton.setDisable(true);
                    playButton.setDisable(false);
                }
            });

        searchButton.setOnAction(e -> searchMedia());
        showAllButton.setOnAction(e -> showAllMedia());
        showAllButton.disableProperty()
            .bind(Bindings.isNull(table.getSelectionModel().selectedItemProperty()));

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(playButton, pauseButton, searchButton,
            showAllButton);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(table, buttonBox);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.setTitle("Music Player");
        stage.show();
    }

    private TableView<Song> createTable(ObservableList<Song> songList) {
        TableView<Song> table = new TableView<Song>();
        table.setItems(songList);

        TableColumn<Song, String> fileNameCol =
            new TableColumn<Song, String>("File Name");
        fileNameCol.setCellValueFactory(new PropertyValueFactory<Song, String>("fileName"));

        TableColumn<Song, String> artistCol =
            new TableColumn<Song, String>("Artist");
        artistCol.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));

        TableColumn<Song, String> titleCol =
            new TableColumn<Song, String>("Subject");
        titleCol.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));

        TableColumn<Song, String> albumCol =
            new TableColumn<Song, String>("Album");
        albumCol.setCellValueFactory(new PropertyValueFactory<Song, String>("album"));

        TableColumn attributes = new TableColumn("Attributes");
        attributes.getColumns().addAll(artistCol, titleCol, albumCol);
        table.getColumns().setAll(fileNameCol, attributes);
        // table.setColumnResizePolicy(new Callback<TableView.ResizeFeatures, Boolean>() {
        //     @Override
        //     public Boolean call(ResizeFeatures p) {
        //         return true;
        //     }
        // });

        return table;
    }

}
