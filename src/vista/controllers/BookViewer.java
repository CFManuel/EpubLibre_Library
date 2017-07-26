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

import daosqlite.GetDatas;
import files.Utils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import modelos.CommonStrings;
import modelos.Libro;

import java.sql.SQLException;

public class BookViewer implements CommonStrings {
    private Libro libro;

    private Stage dialogStage;
    @FXML
    private ImageView nextArrow;
    @FXML
    private ImageView backArrow;
    @FXML
    private Label tfTitulo;
    @FXML
    private Label tfAutor;
    @FXML
    private Label tfColeccion;
    @FXML
    private Label tfVolumen;
    @FXML
    private Label tfGeneros;
    @FXML
    private ChoiceBox cbRevision;
    @FXML
    private Label tfIdioma;
    @FXML
    private Label tfYear;
    @FXML
    private Label tfPages;
    @FXML
    private Label tfValoracion;
    @FXML
    private Label lColeccion;
    @FXML
    private TextArea tfSinopsis;
    @FXML
    private ImageView imgView;
    private int position;

    public BookViewer(Libro libro, int pos) {
        this.libro = libro;
        this.position = pos;
    }

    /**
     * Coge el anterior libro de la lista y lo muestra.
     */
    @FXML
    private void backBook() {
        if (position != 0) {
            position--;
            this.libro = MainTableViewController.libros.get(position);
            drawArrows();
            drawBook();

        }

    }

    /**
     * Coge el siguiente libro de la lista y lo muestra.
     */
    @FXML
    private void nextBook() {
        int maxPos = MainTableViewController.libros.size() - 1;
        if (maxPos != position) {
            position++;
            this.libro = MainTableViewController.libros.get(position);
            drawArrows();
            drawBook();
        }
    }

    /**
     * Modifica la opacidad de las flechas de navegación según su posición.
     */
    private void drawArrows() {
        int maxPos = MainTableViewController.libros.size() - 1;
        double nextOpacity = (maxPos == position) ? 0.5 : 1;
        double backOpacity = (position == 0) ? 0.5 : 1;
        MainTableViewController.focusROW.set(position);
        nextArrow.setOpacity(nextOpacity);
        backArrow.setOpacity(backOpacity);


    }

    /**
     * Carga los datos en sus respectivos componentes.
     */
    @FXML
    private void initialize() {

        cbRevision.setItems(FXCollections.observableArrayList(libro.getRevArray()));
        drawArrows();
        drawBook();
        //Listener para cargar el libro correspondiente a la revisión seleccionada.
        cbRevision.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            //t1 es el index del choiceBox.
            GetDatas getDatas = new GetDatas();
            try {
                libro = getDatas.getLibro(this.libro.getEpl_id(), (Double) cbRevision.getItems().get((Integer) t1));
                libro.setRevArray(this.libro.getRevArray());
                drawBook();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Rellena los campos con la información del libro indicado.
     */

    private void drawBook() {
        tfSinopsis.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tfTitulo.setText(libro.getTitulo());
        tfAutor.setText(libro.getAutor());
        tfColeccion.setText(libro.getColeccion());
        tfVolumen.setText(String.valueOf(libro.getVolumen()));
        tfGeneros.setText(libro.getGeneros());
        tfIdioma.setText(libro.getIdioma());
        cbRevision.setValue(cbRevision.getItems().get(0));
        tfYear.setText(String.valueOf(libro.getFecha_publi()));
        tfPages.setText(String.valueOf(libro.getPaginas()));
        tfValoracion.setText(String.valueOf(libro.getValoracion()));


        tfSinopsis.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        libro.setSinopsis(libro.getSinopsis().replaceAll(PATTERN_FOR_PARAGRAPH, "$1\n\n$2"));
        tfSinopsis.setText(libro.getSinopsis());
        tfSinopsis.setStyle("-fx-background-color: transparent;");

        try {
            Image image = new Image(libro.getImgURI(), true);
            imgView.setImage(image);

        } catch (Exception e) {
            //Lanza error si no existe link.
        }
    }

    /**
     * Cierra el dialogo al pulsar el boton "OK".
     */
    @FXML
    private void ok() {
        dialogStage.close();
    }

    /**
     * Abre el URISchema correspondiente al magnetLink.
     */
    @FXML
    private void download() {
        Utils.launchTorrent(this.libro);
    }

    /**
     * Asigna el libro sobre el que se va a trabajar.
     *
     * @param libro Datos a mostrar.
     */
    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

}
