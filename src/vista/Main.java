/*
 * Copyright (c) 2017. Ladaga.
 * This file is part of EpubLibre_Library.
 *
 *     EpubLibre_Library is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     EpubLibre_Library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package vista;

import controller.UpdateDB;
import daosqlite.ConnectorHelper;
import daosqlite.GetDatas;
import daosqlite.InsertDatas;
import files.Utils;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelos.CommonStrings;
import modelos.Libro;
import vista.controllers.Alertas;
import vista.controllers.BookViewer;
import vista.controllers.MainTableViewController;
import vista.controllers.RootLayoutController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Main extends Application implements CommonStrings {
    public static String appLocation;
    private static HashMap<String, String> configuracion = new HashMap<>();
    private static MainTableViewController mainTableViewController;

    @SuppressWarnings("FieldCanBeLocal")
    private Main main;

    private Stage primaryStage;
    private BorderPane rootLayout;

    /**
     * Modo sin GUI para actualizar la db en segundo plano y lanzamiento sin Args para uso de GUI.
     * El lanzamiento normal incluye la actualización automática de la db.
     *
     * @param args Argumentos recibidos por linea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static String getLocation() {
        return Main.appLocation;
    }

    public static HashMap<String, String> getConfiguracion() {
        return configuracion;
    }

    /**
     * Lanzamiento de aplicación JavaFX.
     *
     * @param primaryStage Stage de la aplicación.
     * @throws Exception Cualquier error que pueda dar.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        appLocation =
                new File(
                        Main.class
                                .getProtectionDomain()
                                .getCodeSource()
                                .getLocation()
                                .toURI()
                                .getPath())
                        .getParent()
                        + "/epl/";
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ePubLibre Library " + VERSION);
        this.primaryStage.getIcons().add(new Image("vista/resources/images/EPL_Portadas_NEGRO.png"));
        //Restaurar último tamaño
        try {
            this.primaryStage.setWidth(
                    Double.parseDouble(GetDatas.getConfig(CommonStrings.WIDTH_WINDOW)));
            this.primaryStage.setHeight(
                    Double.parseDouble(GetDatas.getConfig(CommonStrings.HEIGHT_WINDOW)));
        } catch (Exception e) {
            //Empty value
        }
        //Listeners para hacer persitente el tamaño de la ventana.
        this.primaryStage
                .widthProperty()
                .addListener(
                        (observableValue, number, t1) -> {

                            try {
                                InsertDatas.insertConfig(
                                        CommonStrings.WIDTH_WINDOW, String.valueOf(t1));
                            } catch (Exception e) { //empty]
                            }
                        });
        this.primaryStage
                .heightProperty()
                .addListener(
                        (observableValue, number, t1) -> {
                            try {
                                InsertDatas.insertConfig(
                                        CommonStrings.HEIGHT_WINDOW, String.valueOf(t1));
                            } catch (Exception e) { //empty]
                            }
                        });
        this.main = this;
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                if (UpdateDB.checkDBage()) configuracion = Utils.getConfig();
                return null;
            }
        };
        task.setOnSucceeded(e -> UpdateDB.timeToUpdate());
        new Thread(task).start();
        initRootLayout();
        initMainTableView();
        this.primaryStage.getScene().setCursor(Cursor.WAIT);
        Utils.crearEPL();
        new ConnectorHelper().crearTabla();
        //todo: realizar última búsqueda
        this.primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    /**
     * Inicia el contenedor principal con MenuBar.
     */
    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("resources/RootLayout.fxml"));
            this.rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            RootLayoutController.setMain(this);
            primaryStage.setResizable(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alertas.stackTraceAlert(e);
            throw new RuntimeException(e);
        }
    }

    public static MainTableViewController getMainTableViewController() {
        return mainTableViewController;
    }

    /**
     * Abre el BookCard con la información del libro correspondiente.
     *
     * @param libro Libro que se va a mostrar en el BookCard.
     */
    public void launchBook(Libro libro, int pos) {
        try {
            FXMLLoader loader = new FXMLLoader();
            BookViewer controller = new BookViewer(libro, pos);
            loader.setController(controller);
            loader.setLocation(Main.class.getResource("resources/BookViewer.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);
            Stage dialogStage = new Stage();
            dialogStage.setScene(scene);
            dialogStage.setTitle(libro.getTitulo());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.primaryStage);
            dialogStage.getIcons().add(new Image("vista/resources/images/EPL_Portadas_NEGRO.png"));
            controller.setDialogStage(dialogStage);
            controller.setLibro(libro);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alertas.stackTraceAlert(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Inicia la tabla de vista y busqueda de libros.
     */
    private void initMainTableView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            // Se indica la situación del xml gráfico.
            loader.setLocation(Main.class.getResource("resources/MainTableView.fxml"));
            // Se carga el contenedor, en este caso un AnchorPane.
            AnchorPane pane = loader.load();
            // Se integra en la parte central del RootLayout.
            pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            rootLayout.setCenter(pane);
            // Se indica el tamaño del root para que se ajuste a la vista de datos.
            rootLayout.setPrefSize(pane.getWidth(), pane.getHeight());
            // Se relaciona el controlador.
            mainTableViewController = loader.getController();
            // Se le entrega la instancia del Main al controlador.
            mainTableViewController.setMain(this);
        } catch (IOException e) {
            e.printStackTrace();
            Alertas.stackTraceAlert(e);
            throw new RuntimeException(e);
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
