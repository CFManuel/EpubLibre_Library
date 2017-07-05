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
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import modelos.Libro;
import uriSchemeHandler.CouldNotOpenUriSchemeHandler;
import uriSchemeHandler.URISchemeHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

/**
 * Created by david on 02/07/2017.
 */
public class BookViewer {
    private Libro libro;
    private Stage dialogStage;
    @FXML
    private Label tfTitulo;
    @FXML
    private Label tfAutor;
    @FXML
    private Label tfColeccion;
    @FXML
    private Label tfGeneros;
    @FXML
    private ChoiceBox cbRevision;
    @FXML
    private Label tfIdioma;
    @FXML
    private Label tfSinopsis;
    @FXML
    private ImageView imgView;

    public BookViewer(Libro libro) {
        this.libro = libro;
    }

    /**
     * Carga los datos en sus respectivos componentes.
     */
    @FXML
    private void initialize() {
        cbRevision.setItems(FXCollections.observableArrayList(libro.getRevArray()));
        drawBook(this.libro);
        cbRevision.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            //t1 es el index del choiceBox.
            GetDatas getDatas = new GetDatas();
            try {
                Libro libro = getDatas.getLibro(this.libro.getEpl_id(), (Double) cbRevision.getItems().get((Integer) t1));
                System.out.println(libro.getTitulo() + " -> " + libro.getRevision());
                drawBook(libro);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private void drawBook(Libro libro) {
        tfSinopsis.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tfTitulo.setText(libro.getTitulo());
        tfAutor.setText(libro.getAutor());
        tfColeccion.setText(libro.getColeccion());
        tfGeneros.setText(libro.getGeneros());

        cbRevision.setValue(cbRevision.getItems().get(0));

        //tfRevision.setText(String.valueOf(libro.getRevision()));
        tfIdioma.setText(libro.getIdioma());
        tfSinopsis.setText(libro.getSinopsis());
        try{
            Image image = new Image(libro.getImgURI(),true);
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
        try {
            URI magnetLink = new URI(libro.getEnlaces());
            URISchemeHandler uriSchemeHandler = new URISchemeHandler();
            uriSchemeHandler.open(magnetLink);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CouldNotOpenUriSchemeHandler couldNotOpenUriSchemeHandler) {
            couldNotOpenUriSchemeHandler.printStackTrace();
        }
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
