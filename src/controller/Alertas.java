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

package controller;

import javafx.scene.control.Alert;
import modelos.CommonStrings;

public class Alertas implements CommonStrings {
    /**
     * Alerta de fin de carga de datos.
     */
    public static void alertUpdateOK() {
        Alert fin = new Alert(Alert.AlertType.INFORMATION);
        fin.setHeaderText("Se han cargado los libros con exito.");
        fin.setContentText("Actualizacion finalizada.");
        fin.show();
    }

    /**
     * Alerta de fin de carga de datos.
     */
    public static void alertUpdateFail() {
        Alert fin = new Alert(Alert.AlertType.INFORMATION);
        fin.setHeaderText("No se han podido actualizar los libros.");
        fin.setContentText("Me avergüenza decir, que algo no ha salido bien...");
        fin.show();
    }

    public static void alertDBInformation(String fecha, int libros) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Información de actualización de la base de datos:");
        info.setContentText(String.format("La base de datos se actualizo por ultima vez en %s. %n" +
                "Actualmente contiene %d libros.", fecha, libros));
        info.show();
    }

    public static void alertDBError() {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setHeaderText("Ha habido un error al realizar consultar los datos");
        error.setContentText("Me avergüenza decir, que algo no ha salido bien...");
        error.show();
    }

    public static void aplicationInfo() {
        Alert informacion = new Alert(Alert.AlertType.INFORMATION);
        informacion.setTitle("EpubLibrary " + VERSION);
        informacion.setHeaderText("Created by and for ePubLibre.");
        informacion.setContentText("Created by ladaga on 02/07/17.\n" +
                "Distributed under GNU GPL v3.");
        informacion.show();
    }
}
