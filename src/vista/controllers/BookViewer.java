package vista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import modelos.Libro;
import uriSchemeHandler.CouldNotOpenUriSchemeHandler;
import uriSchemeHandler.URISchemeHandler;

import java.net.URI;
import java.net.URISyntaxException;

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
    private Label tfRevision;
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
        tfSinopsis.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tfTitulo.setText(libro.getTitulo());
        tfAutor.setText(libro.getAutor());
        tfColeccion.setText(libro.getColeccion());
        tfGeneros.setText(libro.getGeneros());
        tfRevision.setText(String.valueOf(libro.getRevision()));
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
