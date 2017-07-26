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

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import modelos.CommonStrings;
import vista.Main;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class Alertas implements CommonStrings {
    private Alertas() {
    }

    /**
     * Alerta de fin de carga de datos correcta.
     */
    public static void alertUpdateOK() {
        Alert ok = new Alert(Alert.AlertType.INFORMATION);
        ((Stage) ok.getDialogPane().getScene().getWindow()).getIcons().add(new Image("vista/resources/images/EPL_Portadas_NEGRO.png"));
        ok.setHeaderText("Se han cargado los libros con éxito.");
        ok.setContentText("Actualización finalizada.");
        ok.show();
    }

    public static void alertNewAppUpdate() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("vista/resources/images/EPL_Portadas_NEGRO.png"));
        alert.setGraphic(new ImageView(new Image("vista/resources/images/icono_alertas.png")));
        alert.setHeaderText("Hay una nueva versión disponible.");
        alert.setContentText("¿Desea abrir el navegador para ver la nueva versión?");
        ButtonType epl = new ButtonType("ePL");
        ButtonType dropbox = new ButtonType("Dropbox");
        ButtonType cancel = new ButtonType("Cancelar");
        alert.getButtonTypes().setAll(epl, dropbox, cancel);

        Label label = new javafx.scene.control.Label("Cambios en la última versión:");
        TextArea textArea = new TextArea(Main.getConfiguracion().get(NEWS));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);


        GridPane expandContent = new GridPane();
        expandContent.setMaxWidth(Double.MAX_VALUE);
        expandContent.add(label, 0, 0);
        expandContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expandContent);


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            try {
                if (result.get() == epl) {
                    Desktop.getDesktop().browse(new URI(Main.getConfiguracion().get(EPL_FORO)));
                } else if (result.get() == dropbox) {
                    Desktop.getDesktop().browse(new URI(Main.getConfiguracion().get(DONWLOAD_LINK)));
                }

            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Alerta de error en carga de datos.
     */
    public static void alertUpdateFail() {
        Alert fail = new Alert(Alert.AlertType.ERROR);
        ((Stage) fail.getDialogPane().getScene().getWindow()).getIcons().add(new Image("vista/resources/images/EPL_Portadas_NEGRO.png"));
        fail.setHeaderText("No se han podido actualizar los libros.");
        fail.setContentText("Me avergüenza decirlo, pero algo no ha salido bien...");
        fail.show();
    }

    /**
     * Alerta de información de la base de datos.
     *
     * @param fecha  Fecha de la última actualización.
     * @param libros Número de libros en la base de datos.
     */
    public static void alertDBInformation(String fecha, int libros) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        ((Stage) info.getDialogPane().getScene().getWindow()).getIcons().add(new Image("vista/resources/images/EPL_Portadas_NEGRO.png"));
        info.setGraphic(new ImageView(new Image("vista/resources/images/icono_alertas.png")));
        info.setHeaderText("Información de actualización de la base de datos:");
        info.setContentText(
                String.format(
                        "La base de datos se actualizo por ultima vez en %s. %n"
                                + "Actualmente contiene %d libros.",
                        fecha, libros));
        info.show();
    }

    /**
     * Información de la aplicación.
     */
    public static void aplicationInfo() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        ((Stage) info.getDialogPane().getScene().getWindow()).getIcons().add(new Image("vista/resources/images/EPL_Portadas_NEGRO.png"));
        info.setGraphic(new ImageView(new Image("vista/resources/images/icono_alertas.png")));
        info.setTitle("EpubLibrary " + VERSION);
        info.setHeaderText("Created by and for ePubLibre.");
        info.setContentText(
                "Created by ladaga on 02/07/17.\n" + "Distributed under GNU GPL v3.");
        info.show();
    }

    /**
     * Error al realizar búsqueda en la db.
     */
    public static void alertDBError() {
        Alert error = new Alert(Alert.AlertType.ERROR);
        ((Stage) error.getDialogPane().getScene().getWindow()).getIcons().add(new Image("vista/resources/images/EPL_Portadas_NEGRO.png"));
        error.setHeaderText("Ha habido un error al realizar consultar los datos");
        error.setContentText("Me avergüenza decirlo, pero algo no ha salido bien...");
        error.show();
    }

    /**
     * Pide confirmación para continuar la actualización.
     *
     * @return El tipo de botón que el usuario ha pulsado.
     */
    public static Optional<ButtonType> updateConfirmation() {
        Alert sure = new Alert(Alert.AlertType.CONFIRMATION);
        ((Stage) sure.getDialogPane().getScene().getWindow()).getIcons().add(new Image("vista/resources/images/EPL_Portadas_NEGRO.png"));
        sure.setHeaderText(
                "La actualización puede tardar unos minutos, dependiendo de tu conexión a internet.");
        sure.setContentText("¿Está seguro que desea continuar?");
        return sure.showAndWait();
    }

    /**
     * Solicita confirmación antes del borrado de la configuración.
     *
     * @return El tipo de botón que el usuario ha pulsado.
     */
    public static Optional<ButtonType> panicButton() {
        Alert sure = new Alert(Alert.AlertType.CONFIRMATION);
        ((Stage) sure.getDialogPane().getScene().getWindow()).getIcons().add(new Image("vista/resources/images/EPL_Portadas_NEGRO.png"));
        sure.setHeaderText("¿Desea restablecer los valores por defecto?");
        sure.setContentText("Deberá reiniciar la aplicación para que los cambios tengan efecto.");
        return sure.showAndWait();
    }
}
