package vista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.stage.Stage;
import modelos.Libro;
import uriSchemeHandler.CouldNotOpenUriSchemeHandler;
import uriSchemeHandler.URISchemeHandler;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
        }catch (Exception e){e.printStackTrace();}
    }

    @FXML
    private void ok() {
        dialogStage.close();
    }

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

    public BookViewer setLibro(Libro libro) {
        this.libro = libro;
        return this;
    }

    public BookViewer setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        return this;
    }
}
