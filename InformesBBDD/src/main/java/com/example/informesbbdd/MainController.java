package com.example.informesbbdd;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

public class MainController {

    @FXML
    private final String urlDB = "jdbc:sqlite:datos/chinook.db";

    public void handlerArtistas(ActionEvent event) {

        //Creo el objeto mediante la vista de artistas
        Scene escena = null;
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/com/example/informesbbdd/artistas-view.fxml"));

        //Cargo la escena
        try {
            escena = new Scene(fxmlLoader.load());
            ArtistasController artistasController = fxmlLoader.getController();
        }catch (IOException ex){
            System.out.println("Error al leer el archivo");
            ex.printStackTrace();
        }
        Stage stage = new Stage();

        //Archivo css
        assert escena != null;
        escena.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/informesbbdd/artistas.css")).toExternalForm());

        //Titulo de la ventana
        stage.setTitle("Artistas");
        stage.setScene(escena);

        //Muestra la escena y espera
        stage.showAndWait();

    }

    public void handlerClientes(ActionEvent event) {
        try{
            //Ruta del informe
            String jasperFilePath = "Clientes.jrxml";
            InputStream in = MainApplication.class.getResourceAsStream(jasperFilePath);

            //Compilamos el informe JRXML a un archivo Jasper
            JasperReport jasperReport = JasperCompileManager.compileReport(in);

            //Accedemos a la base de datos
            Connection conn = DriverManager.getConnection(urlDB);

            //Cargamos los parámetros (en este caso ninguno) y la
            //conexión de la base de datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), conn);

            //Mostramos el informe
            JasperViewer.viewReport(jasperPrint, true);

        }catch (JRException | SQLException ex){
            ex.printStackTrace();
        }
    }

    //Funcionalidad para el boton cerrar (no muestra mensaje de alerta)
    public void handlerCerrar(ActionEvent actionEvent) {
        System.exit(0);
    }

}