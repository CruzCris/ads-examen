package cajero_ex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author crist
 */
public class validaciones {

    Connect connection = new Connect();

    boolean isDebit(String num_tar) {
        Connection cx = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean flag = false;
        try {
            cx = connection.connect();
            pst = cx.prepareStatement("select * from debito where num_tar='" + num_tar + "'");
            rs = pst.executeQuery();
            if (rs.next()) {
                // Si es de debito
                flag = true;
            } else {
                // No es de debito
                flag = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(validaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return flag;
    }

    boolean num_tarjeta(String num_tar) {
        if (isNumeric(num_tar)) {
            //System.out.println("La cadena solo contiene números");
            if (num_tar.length() != 16) {
                System.out.println("El número de la tarjeta debe de contener 16 dígitos exactamente.");
                return false;
            } else {
                Connection cx = null;
                PreparedStatement pst = null;
                ResultSet rs = null;
                try {
                    cx = connection.connect();
                    pst = cx.prepareStatement("select num_tar from tarjeta where num_tar='" + num_tar + "'");
                    rs = pst.executeQuery();
                    if (rs.next()) {
                        //System.out.println("El número de tarjeta ingresado si está registrado en la base de datos.");
                        if (isDebit(num_tar)) {
                            return true;
                        } else {
                            System.out.println("La tarjeta bancaria debe de ser únicamente de débito para realizar operaciones.");
                            return false;
                        }
                    } else {
                        System.out.println("El número de tarjeta ingresado no está registrado en la base de datos.");
                        return false;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(validaciones.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            }
        } else {
            System.out.println("El número de la tarjeta solo debe contener números.");
            return false;
        }

        //return true;
    }

    boolean ref_serv(String ref) {
        if (isNumeric(ref)) {
            //System.out.println("La cadena solo contiene números");
            if (ref.length() < 10 || ref.length() > 15) {
                System.out.println("La referencia únicamente debe contener entre 10 a 15 dígitos.");
                return false;
            } else {
                return true;
            }
        } else {
            System.out.println("La referencia solo debe contener números.");
            return false;
        }
    }

    boolean monto(String monto) {
        if (isNumeric(monto)) {
            //System.out.println("La cadena solo contiene números");
            int m = Integer.parseInt(monto);
            if (m % 100 != 0) {
                System.out.println("El monto debe ser múltiplo de 100.");
                return false;
            } else {
                return true;
            }
        } else {
            System.out.println("El monto a pagar solo debe contener números.");
            return false;
        }
    }

    boolean deposito(double monto) {
        if (monto % 100 != 0) {
            System.out.println("La cantidad a depositar debe ser múltiplo de 100.");
            return false;
        } else {
            if(monto > 2000){
                System.out.println("La cantidad a depositar debe ser menor a $2000");
                return false;
            }else{
                return true;
            }
        }
    }

    boolean retiro(double cant) {
        if (cant % 100 != 0) {
            System.out.println("La cantidad a retirar debe ser múltiplo de 100.");
            return false;
        } else {
            if (cant > 2000) {
                System.out.println("La cantidad debe ser menor a $2000");
                return false;
            } else {
                return true;
            }
        }
    }

    boolean pago(String pago, String monto) {
        if (isNumeric(pago)) {
            //System.out.println("La cadena solo contiene números");
            int p = Integer.parseInt(pago);
            int m = Integer.parseInt(monto);
            if (p % 100 != 0) {
                System.out.println("El pago debe ser múltiplo de 100.");
                return false;
            } else if (p < m) {
                System.out.println("El pago debe ser mayor o igual al monto.");
                return false;
            } else {
                return true;
            }
        } else {
            System.out.println("El monto a pagar solo debe contener números.");
            return false;
        }
    }

    public static boolean isNumeric(String cadena) {
        String exp_reg = "^[0-9]+$";
        Pattern patron = Pattern.compile(exp_reg);
        Matcher matcher = patron.matcher(cadena);
        return matcher.matches();
    }

    boolean validarPin(String pin, String tar) {
        if (isNumeric(pin)) {
            //System.out.println("La cadena solo contiene números");
            if (pin.length() != 4) {
                System.out.println("El pin consta de únicamente 4 dígitos.");
                return false;
            } else {
                try {
                    Connection cx = connection.connect();
                    String sql = "select * from tarjeta where num_tar = ? and pin_tar = ?";
                    PreparedStatement pst = cx.prepareStatement(sql);
                    pst.setString(1, tar);
                    pst.setString(2, pin);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        return true;
                    } else {
                        System.out.println("El pin no coincide con la tarjeta.");
                        return false;
                    }
                } catch (SQLException e) {
                    Logger.getLogger(validaciones.class.getName()).log(Level.SEVERE, null, e);
                    return false;
                }
            }
        } else {
            System.out.println("El pin solo debe contener números.");
            return false;
        }
    }

}
