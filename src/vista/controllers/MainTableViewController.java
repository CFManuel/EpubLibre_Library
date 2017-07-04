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

package vista.controllers;

import daoSqLite.GetDatas;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import modelos.Libro;
import vista.Main;

import java.sql.SQLException;
import java.util.ArrayList;

import static controller.Alertas.alertLibrosFound;

/**
 * Created by david on 02/07/2017.
 */
public class MainTableViewController {
    private static String OPT_TITLE = "Título";
    private static String OPT_AUTHOR = "Autor";
    private static String OPT_GENDER = "Géneros";
    private static String OPT_LANGUAGE = "Idioma";

    private Main main;
    @FXML
    private TextField tfSearch;
    @FXML
    private ChoiceBox<String> choiceBoxSearch;

    @FXML
    private TableView<Libro> bookTableView;
    @FXML
    private TableColumn<Libro, String> titleColumn;
    @FXML
    private TableColumn<Libro, String> autorColumn;
    @FXML
    private TableColumn<Libro, String> colColumn;
    @FXML
    private TableColumn<Libro, Number> volColumn;
    @FXML
    private TableColumn<Libro, Number> revColumn;
    @FXML
    private TableColumn<Libro, String> idiomaColumn;
    @FXML
    private TableColumn<Libro, Number> pagColumn;
    @FXML
    private TableColumn<Libro, Number> valColumn;
    @FXML
    private TableColumn<Libro, String> generosColumn;
    @FXML
    private VBox vBox;

    @FXML
    private void initialize() {
        configTable();
        configChoiceBox();

    }

    /**
     * Acción en el botón de busqueda.
     * Recoge los datos del TextField y realiza la consulta a la db.
     */
    @FXML
    private void searchBtn() {
        main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
        String search = tfSearch.getText();
        String option = choiceBoxSearch.getValue();
        GetDatas getDatas = new GetDatas();
        ArrayList<Libro> libros = new ArrayList<>();
        try {
            if (option.equalsIgnoreCase(OPT_TITLE)) {
                libros = getDatas.getLibros(search, GetDatas.TITLE);
            } else if (option.equalsIgnoreCase(OPT_AUTHOR)) {
                libros = getDatas.getLibros(search, GetDatas.AUTHOR);
            } else if (option.equalsIgnoreCase(OPT_GENDER)) {
                libros = getDatas.getLibros(search, GetDatas.GENDER);
            } else if (option.equalsIgnoreCase(OPT_LANGUAGE)) {
                libros = getDatas.getLibros(search, GetDatas.LANGUAGE);
            }
            main.setLibros(libros);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
        alertLibrosFound(libros.size());
    }

    /**
     * Configuración de los campos de la tabla.
     */
    private void configTable() {
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));
        autorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAutor()));
        colColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColeccion()));
        volColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getVolumen()));
        generosColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGeneros()));
        revColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getRevision()));
        idiomaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdioma()));
        pagColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPaginas()));
        valColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getValoracion()));

        bookTableView.setEditable(false);
        bookTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //Abrir ficha del libro con doble click o enter.
        bookTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() > 1) {
                main.launchBook(bookTableView.getSelectionModel().getSelectedItem());
            }
        });
        bookTableView.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                main.launchBook(bookTableView.getSelectionModel().getSelectedItem());
            }
        });
    }

    /**
     * Configura las opciones del ChoiceBox de busquedas.
     */
    private void configChoiceBox() {
        choiceBoxSearch.setItems(FXCollections.observableArrayList(OPT_TITLE, OPT_AUTHOR, OPT_GENDER, OPT_LANGUAGE));
        choiceBoxSearch.setValue(OPT_TITLE);
    }

    public void setMain(Main main) {
        this.main = main;
        bookTableView.setItems(main.getLibros());
    }

}
