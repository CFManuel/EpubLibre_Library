package vista.controllers;

import daoSqLite.GetLibros;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import modelos.Libro;
import vista.Main;

import java.sql.SQLException;

/**
 * Created by david on 02/07/2017.
 */
public class MainTableViewController {
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
    private TableColumn<Libro, String> linkColumn;
    @FXML
    private VBox vBox;

    @FXML
    private void initialize() {
        configTable();
    }

    /**
     * Acción en el botón de busqueda.
     * Recoge los datos del TextField y realiza la consulta a la db.
     */
    @FXML
    private void searchBtn() {
        main.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
        GetLibros getLibros = new GetLibros();
        try {
            main.setLibros(getLibros.getLibros(tfSearch.getText()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        main.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
    }

    /**
     * Configuración de los campos de la tabla.
     */
    private void configTable() {
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));
        autorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAutor()));
        linkColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImgURI()));
        bookTableView.setEditable(false);
        bookTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getClickCount() > 1){
                main.launchBook(bookTableView.getSelectionModel().getSelectedItem());
            }
        });
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
        bookTableView.setItems(main.getLibros());
    }
}
