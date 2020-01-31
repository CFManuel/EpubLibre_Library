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

import com.dmmop.daosqlite.GetDatas;
import com.dmmop.files.Utils;
import com.dmmop.modelos.CommonStrings;
import com.dmmop.modelos.Libro;
import com.dmmop.vista.Main;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
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
    private Thread loadImg;

    public BookViewer(Libro libro, int pos) {
        this.libro = libro;
        this.position = pos;
    }

    /**
     * Coge el anterior libro de la lista y lo muestra.
     */
    @FXML
    private void backBook() {
        interruptThread();
        if (position != 0) {
            position--;
            this.libro = MainTableViewController.libros.get(position);
            configComboBox();
            drawArrows();
            drawBook();
        }
    }

    /**
     * Coge el siguiente libro de la lista y lo muestra.
     */
    @FXML
    private void nextBook() {
        interruptThread();
        int maxPos = MainTableViewController.libros.size() - 1;
        if (maxPos != position) {
            position++;
            this.libro = MainTableViewController.libros.get(position);
            configComboBox();
            drawArrows();
            drawBook();
        }
    }

    private void interruptThread() {
        if (this.loadImg.isAlive()) this.loadImg.interrupt();
    }

    /**
     * Modifica la opacidad de las flechas de navegación según su posición.
     */
    private void drawArrows() {
        int maxPos = MainTableViewController.libros.size() - 1;
        double nextOpacity, backOpacity;
        if (maxPos == position) {
            nextOpacity = 0.5;
            nextArrow.setStyle("-fx-cursor: default");
        } else {
            nextOpacity = 1;
            nextArrow.setStyle("-fx-cursor: hand");
        }
        if (0 == position) {
            backOpacity = 0.5;
            backArrow.setStyle("-fx-cursor: default");
        } else {
            backOpacity = 1;
            backArrow.setStyle("-fx-cursor: hand");
        }
        MainTableViewController.focusROW.set(position);
        nextArrow.setOpacity(nextOpacity);
        backArrow.setOpacity(backOpacity);
    }

    private void configComboBox() {
        cbRevision.setItems(FXCollections.observableArrayList(libro.getRevArray()));
        cbRevision.getSelectionModel().selectFirst();
    }

    /**
     * Carga los datos en sus respectivos componentes.
     */
    @FXML
    private void initialize() {
        drawArrows();
        drawBook();
        configComboBox();
        //Listener para cargar el libro correspondiente a la revisión seleccionada.
        cbRevision
                .getSelectionModel()
                .selectedIndexProperty()
                .addListener(
                        (observableValue, number, t1) -> {
                            //t1 es el index del choiceBox.
                            GetDatas getDatas = new GetDatas();
                            try {

                                libro =
                                        getDatas.getLibro(
                                                this.libro.getEpl_id(),
                                                (Double) cbRevision.getItems().get((Integer) t1));
                                libro.setRevArray(this.libro.getRevArray());
                                drawBook();

                            } catch (SQLException
                                    | ClassNotFoundException
                                    | ArrayIndexOutOfBoundsException e) {
                                //Error de mierda.
                            }
                        });
    }

    @FXML
    private void openePLWeb() {
        String web = String.format("https://www.epublibre.org/libro/detalle/%d", libro.getEpl_id());
        try {
            Desktop.getDesktop().browse(new URI(web));
        } catch (URISyntaxException | IOException e) {
            Alertas.stackTraceAlert(e);
        }
    }

    @FXML
    private void searchAutor() {
        dialogStage.close();
        Main.getMainTableViewController().searchAutor(tfAutor.getText());
    }

    @FXML
    private void searchCollection() {
        dialogStage.close();
        Main.getMainTableViewController().searchCollection(tfColeccion.getText());
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
        tfYear.setText(String.valueOf(libro.getFecha_publi()));
        tfPages.setText(String.valueOf(libro.getPaginas()));
        tfValoracion.setText(String.valueOf(libro.getValoracion()));

        tfSinopsis.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        libro.setSinopsis(libro.getSinopsis().replaceAll(PATTERN_FOR_PARAGRAPH, "$1\n\n$2"));
        tfSinopsis.setText(libro.getSinopsis());
        tfSinopsis.setStyle("-fx-background-color: transparent;");

        try {
            Image image = null;
            Task loadImage =
                    new Task() {
                        @Override
                        protected Image call() throws Exception {
                            URL url = new URL(libro.getImgURI());
                            URLConnection conn = Utils.getConnection(url);
                            conn.setRequestProperty("User-Agent", USER_AGENT);

                            try (InputStream stream = conn.getInputStream()) {
                                return new Image(stream);
                            }
                        }
                    };
            loadImage.setOnSucceeded(e -> imgView.setImage((Image) loadImage.getValue()));
            loadImage.setOnFailed(
                    e -> imgView.setImage(new Image("images/no-image.png")));
            this.loadImg = new Thread(loadImage);
            this.loadImg.start();
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

    @FXML
    private void copyToClipboard() {
        final ClipboardContent content = new ClipboardContent();
        content.putString(
                String.format(
                        "%s&dn=ePL_[%d]_%s",
                        libro.getEnlaces(),
                        libro.getEpl_id(),
                        libro.getTitulo().replaceAll("\\s", "_").replaceAll("[´`\"\']", "")));
        Clipboard.getSystemClipboard().setContent(content);
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
        this.dialogStage.setMinHeight(635);
        this.dialogStage.setMinWidth(448);
        this.dialogStage
                .getScene()
                .addEventHandler(
                        KeyEvent.KEY_PRESSED,
                        keyEvent -> {
                            if (keyEvent.getCode() == KeyCode.RIGHT) {
                                nextBook();
                            } else if (keyEvent.getCode() == KeyCode.LEFT) {
                                backBook();
                            }
                        });
    }
}
