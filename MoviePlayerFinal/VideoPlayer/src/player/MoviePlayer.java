package player;

import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.misc.GC;

import java.awt.geom.Rectangle2D;
import java.io.File;

public class MoviePlayer extends Application {

    public static void main(String[] args) {

        launch(args);
    }
    //********************************************Variables**********************************************************
    MediaPlayer mediaPlayer;
    private Label time;
    Duration duration;
    Button fullScreenButton;
    Scene scene;
    Media media;
    double width;
    double height;
    MediaView mediaView;
    private Slider slider;
    private Slider volumeSlider;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;


    //*******************************************updateValues method***************************************************

    //*******************************************Method for adding format of Time************************************
    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    //*******************************************set Scene Method****************************************************
    public Scene setScene(double width, double height, Stage primaryStage) {
        StackPane stackPane = new StackPane();
        this.height = height;
        this.width = width;
        //Add your own path of the vidio that you want to play
        String path = "C:/Users/MANJEET SINGH/IdeaProjects/MoviePlayer3/Trailers/T.mp4";

        media = new Media(new File(path).toURI().toString());

        mediaPlayer = new MediaPlayer(media);
        //mediaPlayer.setAutoPlay(false);
        mediaView = new MediaView(mediaPlayer);

        final Timeline Slideout = new Timeline();
        final Timeline Slidein = new Timeline();
        final Timeline show = new Timeline();

        // DropShadow effect
        //DropShadow dropshadow = new DropShadow();
        //dropshadow.setColor(Color.WHITE);
        //mediaView.setEffect(dropshadow);


        stackPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                show.play();
            }
        });

        stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Slideout.play();
            }

        });


        stackPane.setOnMouseEntered (new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Slidein.play();
            }
        });



        stackPane.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Slideout.play();

            }
        });




        //********************************** Adding Toolbar *********************************************************

        //********************************** Volume Button *********************************************************

        final Duration FADE_DURATION = Duration.seconds(0.1);
        volumeSlider = new Slider(0, 1, 0);
        volumeSlider.setValue(0.5);
        //volumeSlider.setOrientation(Orientation.VERTICAL);
        mediaPlayer.volumeProperty().bindBidirectional(volumeSlider.valueProperty());

        final Timeline fadeInTimeline = new Timeline(
                new KeyFrame(FADE_DURATION,new KeyValue(mediaPlayer.volumeProperty(), 1.0)
                )
        );

        final Timeline fadeOutTimeline = new Timeline(
                new KeyFrame(FADE_DURATION,new KeyValue(mediaPlayer.volumeProperty(), 0.0)
                )
        );


        //*****************************Mute Button*******************************************************************
        Image muteButtonImage = new Image("file:///C:/Users/MANJEET SINGH/IdeaProjects/MoviePlayer3/buttonImages/mute.png");
        ImageView ivmute = new ImageView(muteButtonImage);
        ivmute.setFitHeight(25);
        ivmute.setFitWidth(25);
        final Button muteButton  = new Button();
        muteButton.setGraphic(ivmute);
        muteButton.setStyle("-fx-background-color: Black");
        muteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                fadeOutTimeline.play();
            }
        });

        muteButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            muteButton.setStyle("-fx-background-color: Black");
            muteButton.setStyle("-fx-body-color: Black");
        });
        muteButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            muteButton.setStyle("-fx-background-color: Black");
        });
        muteButton.setMaxWidth(Double.MAX_VALUE);

        //*****************************************************Full Volume*******************************************
        Image volButtonImage = new Image("file:///C:/Users/MANJEET SINGH/IdeaProjects/MoviePlayer3/buttonImages/vol.png");
        ImageView ivvol = new ImageView(volButtonImage);
        ivvol.setFitHeight(25);
        ivvol.setFitWidth(25);
        final Button volButton  = new Button();
        volButton.setGraphic(ivvol);
        volButton.setStyle("-fx-background-color: Black");
        volButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                fadeInTimeline.play();
            }
        });
        volButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            volButton.setStyle("-fx-background-color: Black");
            volButton.setStyle("-fx-body-color: Black");
        });
        volButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            volButton.setStyle("-fx-background-color: Black");
        });
        volButton.setMaxWidth(Double.MAX_VALUE);


        HBox controls = new HBox(2);
        controls.getChildren().setAll(muteButton,volumeSlider,volButton);
        controls.setAlignment(Pos.CENTER_RIGHT);
        VBox.setVgrow(volumeSlider, Priority.ALWAYS);

        controls.disableProperty().bind(
                Bindings.or(
                        Bindings.equal(Timeline.Status.RUNNING, fadeInTimeline.statusProperty()),
                        Bindings.equal(Timeline.Status.RUNNING, fadeOutTimeline.statusProperty())
                )
        );


        //*************************************Play/pause Button*******************************************************


        Image playButtonImage = new Image("file:///C:/Users/MANJEET SINGH/IdeaProjects/MoviePlayer3/buttonImages/play.png");
        ImageView iv1 = new ImageView(playButtonImage);
        iv1.setFitHeight(25);
        iv1.setFitWidth(25);
        Image pauseButtonImage = new Image("file:///C:/Users/MANJEET SINGH/IdeaProjects/MoviePlayer3/buttonImages/pause.png");
        ImageView iv2 = new ImageView(pauseButtonImage);
        iv2.setFitHeight(25);
        iv2.setFitWidth(25);
        final Button playButton = new Button();
        playButton.setGraphic(iv1);
        playButton.setStyle("-fx-background-color: Black");
        //playButton.setAlignment(Pos.BOTTOM_CENTER);
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MediaPlayer.Status status = mediaPlayer.getStatus();
                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                    // don't do anything in these states
                    return;
                }
                if (status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED) {
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    }

                    mediaPlayer.play();

                }
                else {
                    mediaPlayer.pause();
                }
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                if (stopRequested) {
                    mediaPlayer.pause();
                    stopRequested = false;
                } else {
                    playButton.setGraphic(iv2);
                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            @Override
                public void run() {
                    System.out.println("onPaused");
                    playButton.setGraphic(iv1);
            }
        });

        mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                if (!repeat) {
                    playButton.setGraphic(iv1);
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
        });

        playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            playButton.setStyle("-fx-background-color: Black");
            playButton.setStyle("-fx-body-color: Black");
        });
        playButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            playButton.setStyle("-fx-background-color: Black");
        });


        //**************************************************Forward Button*******************************************

        Image forwardButtonImage = new Image("file:///C:/Users/MANJEET SINGH/IdeaProjects/MoviePlayer3/buttonImages/forwardbutton.png");
        ImageView ivforward = new ImageView(forwardButtonImage);
        ivforward.setFitHeight(25);
        ivforward.setFitWidth(25);

        final Button forwardButton = new Button();
        forwardButton.setGraphic(ivforward);
        forwardButton.setStyle("-fx-background-color: Black");

        forwardButton.setOnAction((ActionEvent e) -> {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().multiply(1.5));
        });

        forwardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            forwardButton.setStyle("-fx-background-color: Black");
            forwardButton.setStyle("-fx-body-color: Black");
        });
        forwardButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            forwardButton.setStyle("-fx-background-color: Black");
        });




        //******************************************Backward Button*************************************************

        Image backwardButtonImage = new Image("file:///C:/Users/MANJEET SINGH/IdeaProjects/MoviePlayer3/buttonImages/backwardbutton.png");
        ImageView ivbackward = new ImageView(backwardButtonImage);
        ivbackward.setFitHeight(25);
        ivbackward.setFitWidth(25);

        final Button backwardButton = new Button();
        backwardButton.setGraphic(ivbackward);
        backwardButton.setStyle("-fx-background-color: Black");

        backwardButton.setOnAction((ActionEvent e) -> {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().multiply(0.5));
        });

        backwardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            backwardButton.setStyle("-fx-background-color: Black");
            backwardButton.setStyle("-fx-body-color: Black");
        });
        backwardButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            backwardButton.setStyle("-fx-background-color: Black");
        });


        //***************************************************Reload Button********************************************

        Image reloadButtonImage = new Image("file:///C:/Users/MANJEET SINGH/IdeaProjects/MoviePlayer3/buttonImages/refreshbutton.png");
        ImageView ivreload = new ImageView(reloadButtonImage);
        ivreload.setFitHeight(25);
        ivreload.setFitWidth(25);

        final Button reloadButton = new Button();
        reloadButton.setGraphic(ivreload);
        reloadButton.setStyle("-fx-background-color: Black");

        reloadButton.setOnAction((ActionEvent e) -> {
            mediaPlayer.seek(mediaPlayer.getStartTime());
        });

        reloadButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            reloadButton.setStyle("-fx-background-color: Black");
            reloadButton.setStyle("-fx-body-color: Black");
        });
        reloadButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            reloadButton.setStyle("-fx-background-color: Black");
        });



        //*************************************************Fullscreen Button******************************************

        Image fullscreenButtonImage = new Image("file:///C:/Users/MANJEET SINGH/IdeaProjects/MoviePlayer3/buttonImages/fullscreen.png");
        ImageView ivfullscreen = new ImageView(fullscreenButtonImage);
        ivfullscreen.setFitHeight(25);
        ivfullscreen.setFitWidth(25);

        final Button fullscreenButton = new Button();
        fullscreenButton.setGraphic(ivfullscreen);
        fullscreenButton.setStyle("-fx-background-color: Black");

        fullscreenButton.setOnAction((ActionEvent e) -> {
            if (primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false);
            } else {
                primaryStage.setFullScreen(true);
            }
        });

        fullscreenButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            fullscreenButton.setStyle("-fx-background-color: Black");
            fullscreenButton.setStyle("-fx-body-color: Black");
        });
        fullscreenButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            fullscreenButton.setStyle("-fx-background-color: Black");
        });



        //***************************************************FileChooser Button********************************************

        Image fileChooserImage = new Image("file:///C:/Users/MANJEET SINGH/IdeaProjects/MoviePlayer3/buttonImages/filechooser.png");
        ImageView ivfileChooser = new ImageView(fileChooserImage);
        ivfileChooser.setFitHeight(25);
        ivfileChooser.setFitWidth(25);

        final Button fileChooserButton = new Button("File");
        fileChooserButton.setTextFill(Color.WHITE);
        fileChooserButton.setGraphic(ivfileChooser);
        fileChooserButton.setStyle("-fx-background-color: Black");
       // fileChooserButton.setStyle("-fx-text-color: White");

        fileChooserButton.setOnAction((ActionEvent e) -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.flv", "*.mp4", "*.mpeg", "*.mp3", ".wav"));
            File file = fc.showOpenDialog(null);
            String path2 = file.getAbsolutePath();
            path2 = path2.replace("\\", "/");
            media = new Media(new File(path2).toURI().toString());
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.volumeProperty().bindBidirectional(volumeSlider.valueProperty());
            mediaPlayer.setAutoPlay(true);

            mediaPlayer.setOnPlaying(new Runnable() {
                @Override
                public void run() {
                    if (stopRequested) {
                        mediaPlayer.pause();
                        stopRequested = false;
                    } else {
                        playButton.setGraphic(iv2);
                    }
                }
            });

            mediaPlayer.setOnPaused(new Runnable() {
                @Override
                public void run() {
                    System.out.println("onPaused");
                    playButton.setGraphic(iv1);
                }
            });

            mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    if (!repeat) {
                        playButton.setGraphic(iv1);
                        stopRequested = true;
                        atEndOfMedia = true;
                    }
                }
            });
            mediaView.setMediaPlayer(mediaPlayer);
        });

        fileChooserButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            fileChooserButton.setStyle("-fx-background-color: Black");
            fileChooserButton.setStyle("-fx-body-color: Black");
        });
        fileChooserButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            fileChooserButton.setStyle("-fx-background-color: Black");
        });


        //******************************** Adding all components to Stackpane*******************************************

        final HBox hbox3 = new HBox();
        //hbox3.setTranslateY(height - 100);
        final HBox hbox2 = new HBox();
        final HBox hbox = new HBox();
        final int bands = mediaPlayer.getAudioSpectrumNumBands();
        final Rectangle[] rects = new Rectangle[bands];
        for (int i=0;i<rects.length;i++){
            rects[i] = new Rectangle();
            rects[i].setFill(Color.GREENYELLOW);
            hbox.getChildren().add(rects[i]);
        }
        hbox2.setPadding(new Insets(0, 0, 0, 20));
        hbox3.setPadding(new Insets(0, 0, 0, 30));
        hbox2.setSpacing(2);
        //hbox2.setAlignment(Pos.CENTER);

        final  VBox vbox = new VBox();
        slider = new Slider();
        slider.setPadding(new Insets(0,20,0,20));
        vbox.getChildren().add(slider);

        hbox2.getChildren().addAll(backwardButton,playButton,forwardButton,reloadButton,fullscreenButton);
        hbox2.getChildren().add(controls);
        controls.setTranslateX(895);
        hbox3.getChildren().addAll(fileChooserButton);

        vbox.getChildren().add(hbox2);
        stackPane.getChildren().add(mediaView);
        stackPane.getChildren().add(hbox3);
        stackPane.getChildren().add(vbox);
        //stackPane.getChildren().add(createVolumeControls(mediaPlayer));

        stackPane.setStyle("-fx-background-color: Black");
        Scene scene = new Scene(stackPane, 600, 600, Color.BLACK);

        //***********************************************Setting Properties of MediaPlayer*******************************

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                int w = mediaPlayer.getMedia().getWidth();
                int h = mediaPlayer.getMedia().getHeight();

                hbox.setMinWidth(w);
                int bandwith = w/rects.length;
                for (Rectangle r:rects){
                    r.setWidth(bandwith);
                    r.setHeight(2);
                }

                primaryStage.setMinWidth(w);
                primaryStage.setMinHeight(h);

                vbox.setMinSize(w, 100);
                double h2 = stackPane.getHeight();
                vbox.setTranslateY(h2+50);


                slider.setMin(0.0);
                slider.setValue(0.0);
                slider.setMax(mediaPlayer.getTotalDuration().toSeconds());


                Slideout.getKeyFrames().addAll(
                        new KeyFrame(new Duration(0),
                                new KeyValue(vbox.translateYProperty(),h2+50),
                                new KeyValue(vbox.opacityProperty(), 0.9),
                                new KeyValue(hbox3.translateYProperty(),20),
                                new KeyValue(hbox3.opacityProperty(),0.9)
                        ),
                        new KeyFrame(new Duration(300),
                                new KeyValue(vbox.translateYProperty(),h2+200),
                                new KeyValue(vbox.opacityProperty(), 0.0),
                                new KeyValue(hbox3.translateYProperty(),0),
                                new KeyValue(hbox3.opacityProperty(),0)
                        )

                );

                Slidein.getKeyFrames().addAll(
                        new KeyFrame(new Duration(0),
                                new KeyValue(vbox.translateYProperty(),h2+200),
                                new KeyValue(vbox.opacityProperty(), 0.0),
                                new KeyValue(hbox3.translateYProperty(),0),
                                new KeyValue(hbox3.opacityProperty(),0)
                        ),
                        new KeyFrame(new Duration(300),
                                new KeyValue(vbox.translateYProperty(),h2+50),
                                new KeyValue(vbox.opacityProperty(), 0.9),
                                new KeyValue(hbox3.translateYProperty(), 20),
                                new KeyValue(hbox3.opacityProperty(),0.9)
                        )

                );
                show.getKeyFrames().addAll(
                        new KeyFrame(new Duration(0),
                                new KeyValue(vbox.translateYProperty(),h2+50),
                                new KeyValue(vbox.opacityProperty(), 0.9),
                                new KeyValue(hbox3.translateYProperty(), 20),
                                new KeyValue(hbox3.opacityProperty(),0.9)

                                ),
                        new KeyFrame(new Duration(300),
                                new KeyValue(vbox.translateYProperty(),h2+50),
                                new KeyValue(vbox.opacityProperty(), 0.9),
                                new KeyValue(hbox3.translateYProperty(), 20),
                                new KeyValue(hbox3.opacityProperty(),0.9)

                )
                );

            }
        });

        //********************************************* Controllers *****************************************************

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration duration, Duration current) {
                slider.setValue(current.toSeconds());
            }
        });


        //******************************************Slider Click Property************************************************

        slider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(slider.getValue()));
            }
        });


        //*********************************************Spectrum Listener*************************************************

        mediaPlayer.setAudioSpectrumListener(new AudioSpectrumListener() {
            @Override
            public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
                for (int i=0; i < rects.length; i++) {
                    double h = magnitudes[i] + 60 ;
                    if(h>2){
                        rects[i].setHeight(h);
                    }
                }
            }
        });
        return scene;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = setScene(this.width, this.height, primaryStage);
        primaryStage.setTitle("Media Player!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
