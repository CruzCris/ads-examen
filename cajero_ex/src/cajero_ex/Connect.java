
package cajero_ex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author crist
 */
public class Connect {
    
    String bd = "cajero_examen";
    String url = "jdbc:mysql://localhost:3306/";
    String user = "root";
    String password = "cruzcrisx";
    String driver = "com.mysql.cj.jdbc.Driver";
    Connection cx;

    public Connect() {
        
    }
    
    public Connection connect(){
        try {
            Class.forName(driver);
            cx = DriverManager.getConnection(url+bd, user, password);
            //System.out.println("La conexión a la bd fue exitosa");
        } catch (SQLException |ClassNotFoundException ex) {
            System.out.println("No se pudo lograr la conexión a la bd");
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cx;
    }
    
    public void disconnect(){
        try {
            cx.close();
        } catch (SQLException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*public static void main(String[] args){
        Connect connection = new Connect();
        connection.connect();
    }*/
}
