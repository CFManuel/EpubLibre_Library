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
import daoSqLite.InsertDatas;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.*;
import modelos.CommonStrings;
import modelos.Libro;
import vista.Main;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainTableViewController implements CommonStrings {
    private static String OPT_TITLE = "Título";
    private static String OPT_AUTHOR = "Autor";
    private static String OPT_COLLECTIONS = "Colecciones";
    private static String OPT_GENDER = "Géneros";
    private static String OPT_LANGUAGE = "Idioma";
    //ArrayList con los datos que se muestran en la tabla.
    private final ObservableList<Libro> libros = FXCollections.observableArrayList();
    private Main main;
    @FXML
    private TextField tfSearch;


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
    private TableColumn<Libro, Number> publiYearColumn;
    @FXML
    private TableColumn<Libro, String> publiDateColumn;
    @FXML
    private TableColumn<Libro, Number> n_votosColumn;
    @FXML
    private Label labelBookFound;
    @FXML
    private CheckBox cbSearchOnTable;
    @FXML
    private MenuButton menuButton;
    @FXML
    private CheckMenuItem cbTitulo;
    @FXML
    private CheckMenuItem cbAutor;
    @FXML
    private CheckMenuItem cbColeccion;
    @FXML
    private CheckMenuItem cbGenero;
    @FXML
    private CheckMenuItem cbIdioma;
    @FXML
    private CheckMenuItem cbSinopsis;

    private ArrayList<Integer> visible_rows = new ArrayList<>();

    @FXML
    private void initialize() {
        configTable();
        cbSearchOnTable.selectedProperty().addListener((observableValue, aBoolean, t1) -> menuButton.setDisable(t1));
    }

    /**
     * Acción en el botón de busqueda.
     * Recoge los datos del TextField y realiza la consulta a la db.
     */
    @FXML
    private void searchBtn() {
        main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
        if (cbSearchOnTable.isSelected()) {
            searchOnTable();
        } else {
            searchOnDB();
        }
        writeNumberBooks();
        main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);

    }

    private void searchOnTable() {
        java.util.function.BiFunction<Libro, String, Boolean> match = (libro, s) ->
                (libro.getTitulo().toLowerCase().contains(s) || libro.getAutor().toLowerCase().contains(s) || libro.getColeccion().toLowerCase().contains(s));
        String search = tfSearch.getText().toLowerCase();
        Iterator<Libro> i = this.libros.iterator();
        i.forEachRemaining(libro -> {
            if (!match.apply(libro, search)) i.remove();
        });
    }

    private void searchOnDB() {
        if (cbColeccion.isSelected()) { //evita que salgan resultados si no está seleccionado.
            String search = tfSearch.getText();
            search = String.format("%%%s%%", search);
            search = search.replaceAll("\\s", "%");
            GetDatas getDatas = new GetDatas();
            ArrayList<Libro> libros;
            String[] busqueda = new String[6];
            busqueda[0] = cbTitulo.isSelected() ? search : "";
            busqueda[1] = cbAutor.isSelected() ? search : "";
            busqueda[2] = cbColeccion.isSelected() ? search : "";
            busqueda[3] = cbGenero.isSelected() ? search : "";
            busqueda[4] = cbIdioma.isSelected() ? search : "";
            busqueda[5] = cbSinopsis.isSelected() ? search : "";

            try {
                libros = getDatas.getLibros(busqueda);
                this.libros.clear();
                this.libros.addAll(libros);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
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
        publiYearColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getFecha_publi()));
        n_votosColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getN_votos()));
        publiDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPublicado()));

        bookTableView.setEditable(false);
        bookTableView.setItems(this.libros);
        bookTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        publiDateColumn.setComparator(new DateComparator());
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
        final KeyCombination searchOnTableKeyCombination = new KeyCodeCombination(KeyCode.ENTER,
                KeyCombination.CONTROL_DOWN);
        tfSearch.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (searchOnTableKeyCombination.match(keyEvent)) {
                searchOnTable();
                writeNumberBooks();
            }
        });
        saveANDrestoreTableState();
    }

    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Convierte una cadena tipo [1, 2, 3, 4] en array de int[]
     *
     * @param intArray Cadena que contiene el array en formato texto.
     * @return Array de enteros.
     */
    private int[] stringToArray(String intArray) {
        String[] array = intArray.replaceAll("[\\s\\[\\]]+", "").split(",");
        int[] ints = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            try {
                ints[i] = Integer.parseInt(array[i]);
            } catch (NumberFormatException nfe) {
                //Not an integer
            }
        }
        return ints;
    }

    /**
     * Restaura el estado anterior de la tabla.
     * Añade los listener para cambio de orden y de visibilidad de una columna.
     * Guarda estos datos en la DB.
     */
    private void saveANDrestoreTableState() {
        //listeners de orden y visibilidad.
        ObservableList<TableColumn<Libro, ?>> columns = bookTableView.getColumns();
        final List<TableColumn<Libro, ?>> unchangedColumns = Collections.unmodifiableList(new ArrayList<>(columns));
        //Registra el orden de las columnas y lo actualiza en la base de datos.
        columns.addListener((ListChangeListener<TableColumn<Libro, ?>>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    int[] colOrder = new int[columns.size()];

                    for (int i = 0; i < columns.size(); ++i) {
                        colOrder[i] = unchangedColumns.indexOf(columns.get(i));
                    }
                    // colOrder will now contain current order (e.g. 1, 2, 0, 5, 4)

                    InsertDatas insertDatas = new InsertDatas();
                    try {
                        insertDatas.insertConfig(CommonStrings.ORDER_ROWS, Arrays.toString(colOrder).replaceAll("\\s", ""));
                    } catch (Exception e) {
                        //empty
                    }
                }
            }
        });
        //Obtiene la última configuración de la tabla.
        Boolean[] vista = {};
        int[] order = {};

        GetDatas getDatas = new GetDatas();
        try {
            order = stringToArray(getDatas.getConfig(CommonStrings.ORDER_ROWS));
            vista = Arrays.stream(getDatas.getConfig(CommonStrings.VISIBLE_ROWS).replaceAll("[\\s\\[\\]]+", "")
                    .split(","))
                    .map(Boolean::parseBoolean)
                    .toArray(Boolean[]::new);
        } catch (Exception e) {
            //empty
        }
        //Listener que avisa de un cambio en la visibilidad de las columnas.
        for (TableColumn<Libro, ?> column : columns) {
            column.visibleProperty().addListener((observableValue, oldVisibility, newVisibility) -> {
                Boolean[] vista1 = new Boolean[columns.size()];
                for (int i = 0; i < vista1.length; i++) {
                    vista1[i] = unchangedColumns.get(i).visibleProperty().getValue();
                }
                InsertDatas insertDatas = new InsertDatas();
                try {
                    insertDatas.insertConfig(CommonStrings.VISIBLE_ROWS, Arrays.toString(vista1));
                } catch (Exception e) {
                    //empty
                }
            });
        }
        //Restaura la visibilidad de las columnas.
        if (vista.length == columns.size()) {
            for (int i = 0; i < columns.size(); i++) {
                columns.get(i).setVisible(vista[i]);
            }
        }
        //Reordena las columnas.
        if (order.length == columns.size()) {
            columns.clear();
            for (int anOrder : order) {
                columns.add(unchangedColumns.get(anOrder));
            }
        }
    }

    private void writeNumberBooks() {
        labelBookFound.setText(String.valueOf(this.libros.size()) + " Libros");
    }

    public static class DateComparator implements Comparator<String> {
        Pattern fecha = Pattern.compile("(\\w):(\\d+)-(\\d+)-(\\d+)");
        private int MAYOR = 1;
        private int MENOR = -1;

        //1: p, 2: dia, 3: mes, 4:año
        @Override
        public int compare(String o1, String o2) {
            int result = 0;
            Matcher fecha1 = fecha.matcher(o1);
            Matcher fecha2 = fecha.matcher(o2);
            if (fecha1.find() && fecha2.find()) {
                int compareYear = fecha1.group(4).compareTo(fecha2.group(4));
                int compareMonth = fecha1.group(3).compareTo(fecha2.group(3));
                int compareDay = fecha1.group(2).compareTo(fecha2.group(2));
                int compareLetter = fecha1.group(1).compareTo(fecha2.group(1));
                if (compareYear == 0) {
                    if (compareMonth == 0) {
                        if (compareDay == 0) {
                            if (compareLetter < 0) {
                                result = MAYOR;
                            } else {
                                result = MENOR;
                            }
                        } else if (compareDay > 0) {
                            result = MAYOR;
                        } else {
                            result = MENOR;
                        }
                    } else if (compareMonth > 0) {
                        result = MAYOR;
                    } else {
                        result = MENOR;
                    }
                } else if (compareYear > 0) {
                    result = MAYOR;
                } else {
                    result = MENOR;
                }
            }
            return result;
        }
    }
}
