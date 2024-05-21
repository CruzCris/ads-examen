
package cajero_ex;

import java.util.Random;

/**
 *
 * @author crist
 */
public class funciones {
    
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generarCadena() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        
        // Generar una cadena con longitud máxima de 5 caracteres
        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(CARACTERES.length());
            sb.append(CARACTERES.charAt(index));
        }
        
        return sb.toString();
    }
    
}
