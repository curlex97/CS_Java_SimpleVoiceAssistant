package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //  primaryStage.setTitle("Hello World");
        //   Scene scene = new Scene(root, 300, 275, Color.BLACK);
        //  primaryStage.setScene(scene);
        //  primaryStage.show();

        primaryStage.setTitle("Overvoice");

        final Group root = new Group();
        final Scene scene = new Scene(root, 400, 400, Color.BLACK);


        GridPane gridpane = new GridPane();
        gridpane.setLayoutX(80);
        gridpane.setLayoutY(80);
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);

        final ImageView imv = new ImageView();
        final Image image2 = new Image(Main.class.getResourceAsStream("br.png"));
        imv.setImage(image2);
        imv.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                final Image image2 = new Image(Main.class.getResourceAsStream("bp.png"));
                imv.setImage(image2);
            }
        });

        imv.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                final Image image2 = new Image(Main.class.getResourceAsStream("bs.png"));
                imv.setImage(image2);
                controller.handleSubmitButtonAction();
            }
        });

        final HBox pictureRegion = new HBox();

        pictureRegion.getChildren().add(imv);
        gridpane.add(pictureRegion, 1, 1);

        root.getChildren().add(gridpane);


        final Text text1 = new Text(104, 70, "");

        text1.setFill(Color.LIGHTBLUE);
        text1.setFont(Font.font("Terminator Two", 32));
        root.getChildren().add(text1);
        controller = new Controller(text1);


        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
