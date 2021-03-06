/*
 * Copyright (c) 2019. Ladaga.
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

package com.dmmop.vista.controllers;

import com.dmmop.controller.UpdateDB;
import com.dmmop.controller.UpdateTask;
import com.dmmop.daosqlite.GetDatas;
import com.dmmop.daosqlite.InsertDatas;
import com.dmmop.modelos.CommonStrings;
import com.dmmop.modelos.Libro;
import com.dmmop.modelos.RecursiveSearchByContent;
import com.dmmop.modelos.RecursiveSearchByName;
import com.dmmop.parser.Csv;
import com.dmmop.vista.Main;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.dmmop.vista.controllers.Alertas.*;

/**
 * Controlador de la com.dmmop.vista principal y el MenuBar asignado
 */
public class RootLayoutController implements CommonStrings {
    private static Main main;

    /**
     * Recibe la instancia del archivo main para poder consultar datos de la misma.
     *
     * @param main Instancia del objeto Main.
     */
    public static void setMain(Main main) {
        RootLayoutController.main = main;
    }

    @FXML
    private void getReadedBooks() {
        GetDatas.getLibrosLeidos();
    }

    @FXML
    private void checkByContent() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Carpeta con ePubs.");
        final Path root =
                Paths.get(directoryChooser.showDialog(main.getPrimaryStage()).getAbsolutePath());
        main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
        final String[] busqueda = new String[6];
        ArrayList<Libro> librosDownload;

        for (int i = 0; i < 6; i++) busqueda[i] = "%%";
        try {
            final GetDatas getDatas = new GetDatas();
            final ArrayList<Libro> librosDB = getDatas.getLibros(busqueda, true);
            final RecursiveSearchByContent epubSearch = new RecursiveSearchByContent();

            Files.walkFileTree(root, epubSearch);
            librosDownload = epubSearch.getEpubs();
            BiFunction<Libro, ArrayList<Libro>, Integer> cmp =
                    (l1, l2) -> {
                        for (int i = 0; i < l2.size(); i++) {
                            if (l1.getTitulo().equalsIgnoreCase(l2.get(i).getTitulo())
                                    && l1.getAutor().equalsIgnoreCase(l2.get(i).getAutor())
                                    && l1.getRevision().equals(l2.get(i).getRevision())) {
                                return i;
                            }
                        }
                        return -1;
                    };
            // Comprueba que hay libros descargados.
            if (librosDownload.size() > 0) {
                librosDownload.forEach(
                        libro -> {
                            int num;
                            if ((num = cmp.apply(libro, librosDB)) != -1) librosDB.remove(num);
                        });
                MainTableViewController.libros.clear();
                MainTableViewController.libros.addAll(librosDB);
            }
        } catch (SQLException | ClassNotFoundException | IOException e) {
            main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
            e.printStackTrace();
        }
        main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
    }

    @FXML
    private void checkByName() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final String[] busqueda = new String[6];
        HashMap<Integer, String> librosDownload;
        directoryChooser.setTitle("Carpeta con ePubs.");

        final Path root =
                Paths.get(directoryChooser.showDialog(main.getPrimaryStage()).getAbsolutePath());
        main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);

        for (int i = 0; i < 6; i++) busqueda[i] = "%%";
        try {
            final GetDatas getDatas = new GetDatas();
            final ArrayList<Libro> librosDB = getDatas.getLibros(busqueda, true);
            final RecursiveSearchByName epubSearch = new RecursiveSearchByName();
            Files.walkFileTree(root, epubSearch);
            librosDownload = epubSearch.getEpubs();
            // Comprueba que hay libros descargados.
            if (librosDownload.size() > 0) {
                for (int i = 0; i < librosDB.size(); i++) {
                    if (librosDownload.containsKey(librosDB.get(i).getEpl_id())) librosDB.remove(i);
                }

                MainTableViewController.libros.clear();
                MainTableViewController.libros.addAll(librosDB);
            }
        } catch (SQLException | ClassNotFoundException | IOException e) {
            main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
            e.printStackTrace();
        }
        main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
    }

    /**
     * Abre la pestaña de importación de ficheros. Lanza un FileChooser para archivos .csv y .rss.
     * Según el tipo de fichero abre su com.dmmop.parser correspondiente.
     */
    @FXML
    @Deprecated
    private void importDataSource() {
        FileChooser fileChooser = new FileChooser();
        UpdateTask importar = null;
        fileChooser.setTitle("Open source file.");
        fileChooser
                .getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("EpubLibrary", "*.csv"));
        final File datos = fileChooser.showOpenDialog(main.getPrimaryStage());
        UpdateDB.ProgressForm progressForm = new UpdateDB.ProgressForm();

        if (datos != null) {
            if (datos.getName().endsWith("csv")) {
                importar =
                        new UpdateTask() {
                            @Override
                            protected Object call() throws Exception {
                                progressForm.getDialogStage().getScene().setCursor(Cursor.WAIT);
                                updateMessage("Iniciando actualización");
                                updateProgress(1, 4);
                                main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
                                updateMessage("Importando CSV...");
                                updateProgress(2, 4);
                                new Csv().importCSV(datos,null);
                                updateMessage("CSV importado.");
                                updateProgress(3, 4);
                                main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
                                progressForm.getDialogStage().getScene().setCursor(Cursor.DEFAULT);
                                updateMessage("Finalizando actualización..,");
                                updateProgress(4, 4);
                                return null;
                            }
                        };
            }
            assert importar != null;
            UpdateTask finalImportar = importar;
            importar.setOnSucceeded(
                    e -> {
                        InsertDatas insertDatas = new InsertDatas();
                        try {
                            insertDatas.updateDate();
                        } catch (ClassNotFoundException | SQLException e1) {
                            e1.printStackTrace();
                        }
                        progressForm.getDialogStage().close();
                        alertUpdateOK(finalImportar.getNumLibrosImportados());
                    });
            importar.setOnFailed(
                    e -> {
                        alertUpdateFail();
                        progressForm.getDialogStage().close();
                    });
            progressForm.activateProgressBar(importar);
            new Thread(importar).start();
        }
    }

    /**
     * Confirma si el usuario desea actualizar.
     */
    @FXML
    private void epubLibreImport() throws InterruptedException {
        Optional<ButtonType> boton = updateConfirmation();
        if (boton.isPresent() && boton.get() == ButtonType.OK) {
            UpdateDB.updateDataBase();
        }
    }

    /**
     * Muestra la información de la base de datos.
     */
    @FXML
    private void getLastUpdate() {
        try {
            GetDatas getDatas = new GetDatas();
            String fecha = getDatas.getLastUpdate();
            int libros = getDatas.countBooks();
            alertDBInformation(fecha, libros);
        } catch (Exception e) {
            alertDBError();
        }
    }

    /**
     * Información sobre la aplicación.
     */
    @FXML
    private void help() {
        aplicationInfo();
    }

    /**
     * Función propia JafaFX que se ejecuta al iniciar el componente
     */
    @FXML
    private void initialize() {

        // do Something.
    }

    @FXML
    private void getMultiRev() {
        GetDatas.getLibrosMultiRev();
    }

    @FXML
    private void resetGUI()
            throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        Optional<ButtonType> borrar = panicButton();
        if (borrar.isPresent() && borrar.get() == ButtonType.OK) {
            InsertDatas insertDatas = new InsertDatas();
            insertDatas.deleteConfig(CommonStrings.VISIBLE_COLUMNS);
            insertDatas.deleteConfig(CommonStrings.ORDER_COLUMNS);
            insertDatas.deleteConfig(CommonStrings.WIDTH_COLUMNS);
            insertDatas.deleteConfig(CommonStrings.HEIGHT_WINDOW);
            insertDatas.deleteConfig(CommonStrings.WIDTH_WINDOW);
        }
    }

    //@FXML
    public void launchConfig() {
        try {
            FXMLLoader loader = new FXMLLoader();
            ConfigLayout controller = new ConfigLayout();
            loader.setController(controller);
            loader.setLocation(getClass().getClassLoader().getResource("ConfigLayout.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);
            Stage dialogStage = new Stage();
            dialogStage.setWidth(580);
            dialogStage.setHeight(450);
            dialogStage.setScene(scene);
            dialogStage.setTitle("Configuración");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.getIcons().add(new Image("images/EPL_Portadas_NEGRO.png"));
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alertas.stackTraceAlert(e);
            throw new RuntimeException(e);
        }
    }
}
