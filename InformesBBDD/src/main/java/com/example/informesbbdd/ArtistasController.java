package com.example.informesbbdd;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ArtistasController {

    @FXML
    private final String urlDB = "jdbc:sqlite:datos/chinook.db"; //Ruta para la bdd

    //Declaracion de elementos
    public ListView listView;

    private int idArtista;

    //Metodo initialize donde se obtendrá el ID del artista
    @FXML
    public void initialize() {
        //Metodo para cargar el nombre de los artistas
        cargarArtistas();

        //Listener para obtener el nombre del artista seleccionado
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String nombreArtista = (String) newValue;
                idArtista = obtenerId(nombreArtista);
            }
        });
    }

    //Metodo para obtener el Id del artista seleccionado
    private int obtenerId(String nombreArtista) {
        int idArtista = 0;
        //Consulta para obtener el id de artistas
        String query = "SELECT ArtistId FROM Artists WHERE Name = ?";

        //Intentamos la conexion de la base de datos
        try (Connection conn = DriverManager.getConnection(urlDB);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombreArtista);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idArtista = rs.getInt("ArtistId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idArtista;
    }

    private void cargarArtistas() {

        //Declaramos la lista de artistas
        ObservableList<String> ListaArtistas = FXCollections.observableArrayList();

        //Consulta sql para obtener el nombre
        String sql = "SELECT  Name from artists";

        //Intentamos realizar la conexion con la bdd
        try (Connection conn = DriverManager.getConnection(urlDB);
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()){
            while (rs.next()){
                ListaArtistas.add(rs.getString("Name"));
            }
            listView.setItems(ListaArtistas);
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void handlerInforme(ActionEvent actionEvent){

        try{
            //Ruta del informe
            String jasperFilePath = "Artistas.jrxml";
            InputStream in = MainApplication.class.getResourceAsStream(jasperFilePath);

            //Compilamos el informe JRXML a un archivo Jasper
            JasperReport jasperReport = JasperCompileManager.compileReport(in);

            //Accedemos a la base de datos
            Connection conn = DriverManager.getConnection(urlDB);

            //Creamos los parametros
            Map<String, Object> params = new HashMap<>();
            params.put("ArtistId", idArtista);
            params.put("ArtistName", listView.getSelectionModel().getSelectedItem());

            //Cargamos los parámetros (el id del artista seleccionado) y la
            //conexión de la base de datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

            //Mostramos el informe
            JasperViewer.viewReport(jasperPrint, true);

        }catch (JRException | SQLException ex){
            ex.printStackTrace();
        }
    }

    //Funcionalidad para el boton cancelar (no muestra ventana de alerta)
    public void handlerCancelar(ActionEvent actionEvent) {

        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.close();
    }
}

