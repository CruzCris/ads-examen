package cajero_ex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author crist
 */
public class movimientos {

    Scanner sc = new Scanner(System.in);
    Connect connection = new Connect();
    validaciones v = new validaciones();
    funciones f = new funciones();
    boolean flag = false;
    double saldo_ac = 0, retirar = 0;
    String pin = "";

    boolean retiro(String num_tar) {
        // primero validamos que la tarjeta sea de débito
        if (v.isDebit(num_tar)) {
            // mostramos el saldo que tiene en la tarjeta
            try {
                Connection cx = connection.connect();
                String sql = "select sald_deb from debito where num_tar = ?";
                PreparedStatement pst = cx.prepareStatement(sql);
                pst.setString(1, num_tar);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    saldo_ac = rs.getDouble("sald_deb");
                }

                if (saldo_ac < 100) {
                    System.out.println("Para poder realizar un retiro debe tener mínimo $100 en su cuenta");
                } else {
                    System.out.print("Su saldo actual es de: $" + saldo_ac + "\n");
                    do {
                        System.out.print("Ingrese la cantidad a retirar (solo múltiplos de 100 y menor a 2000): ");
                        retirar = sc.nextDouble();
                        sc.nextLine();
                        flag = v.retiro(retirar);
                    } while (flag == false);
                    if (retirar > saldo_ac) {
                        System.out.println("No se puede retirar más dinero del que tiene en su cuenta.");
                    } else {
                        flag = false;
                        do {
                            System.out.print("Para continuar con la operación, ingrese su pin: ");
                            pin = sc.nextLine();
                            flag = v.validarPin(pin, num_tar);
                        } while (flag == false);
                        
                        sql = "update debito set sald_deb = ? where num_tar = ?";
                        pst = cx.prepareStatement(sql);
                        pst.setDouble(1, saldo_ac - retirar);
                        pst.setString(2, num_tar);
                        pst.executeUpdate();

                        System.out.println("Pin correcto, favor de tomar su dinero...");
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                        LocalDateTime fechaActual = LocalDateTime.now();
                        String idMov = f.generarCadena();
                        sql = "insert into movimiento (id_mov,tipo_mov,mont_mov,fch_mov,num_tar) values (?,?,?,?,?)";
                        pst = cx.prepareStatement(sql);
                        pst.setString(1, idMov);
                        pst.setString(2, "r");
                        pst.setDouble(3, retirar);
                        pst.setTimestamp(4, Timestamp.valueOf(fechaActual));
                        pst.setString(5, num_tar);
                        pst.executeUpdate();
                        System.out.println("Se registró el retiro exitosamente.");
                    }
                }
            } catch (SQLException e) {
                Logger.getLogger(validaciones.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            System.out.println("Para poder realizar un retiro se debe usar una tarjeta de débito");
        }
        return true;
    }

    boolean deposito(String num_tar) {
        return true;
    }

    boolean pago_tar(String num_tar) {
        return true;
    }

    boolean pago_servicio(String num_tar) {
        String ref = "", monto = "", pago = "";
        int m = 0, p = 0, idServ = 0, dinero = 0;
        System.out.print("Este cajero solo acepta pagos a servicios con efectivo, si desea continuar ingrese Y, si desea salir ingrese N: ");
        String conf = sc.nextLine();
        if (conf.equals("Y")) {
            System.out.println("Ingrese el nombre del servicio del cual desea hacer un pago");

            try {
                Connection cx = connection.connect();
                PreparedStatement pst = cx.prepareStatement("select nom_serv from servicio");
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String value = rs.getString("nom_serv");
                    System.out.println("- " + value);
                }
            } catch (SQLException ex) {
                Logger.getLogger(validaciones.class.getName()).log(Level.SEVERE, null, ex);
            }

            String serv = sc.nextLine();

            do {
                System.out.print("Ingrese la referencia del pago: ");
                ref = sc.nextLine();
                flag = v.ref_serv(ref);
            } while (flag == false);
            //System.out.println("");
            do {
                System.out.print("Ingrese el monto a pagar (únicamente múltiplos de 100): ");
                monto = sc.nextLine();
                flag = v.monto(monto);
            } while (flag == false);
            //System.out.println("");
            do {
                System.out.print("¿Cuánto dinero va a ingresar? (múltiplos de 100): ");
                pago = sc.nextLine();
                flag = v.pago(pago, monto);
            } while (flag == false);
            //System.out.println("");

            System.out.println("Por favor, ingrese el dinero...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Contando dinero...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            m = Integer.parseInt(monto);
            p = Integer.parseInt(pago);
            LocalDateTime fechaActual = LocalDateTime.now();
            String idMov = f.generarCadena();

            if (m == p) {
                // No hay cambio y se registra el pago
                try {
                    Connection cx = connection.connect();
                    String sql = "insert into movimiento (id_mov,tipo_mov,mont_mov,fch_mov,num_tar) values (?,?,?,?,?)";
                    PreparedStatement pst = cx.prepareStatement(sql);
                    pst.setString(1, idMov);
                    pst.setString(2, "s");
                    pst.setInt(3, m);
                    pst.setTimestamp(4, Timestamp.valueOf(fechaActual));
                    pst.setString(5, num_tar);
                    pst.executeUpdate();
                    //System.out.println("Se actualizó la tabla movimiento corretamente");

                    sql = "select id_serv from servicio where nom_serv = ?";
                    pst = cx.prepareStatement(sql);
                    pst.setString(1, serv);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        idServ = rs.getInt("id_serv");
                    }

                    sql = "insert into pag_servicio (id_mov,id_serv,ref_pg) values (?,?,?)";
                    pst = cx.prepareStatement(sql);
                    pst.setString(1, idMov);
                    pst.setInt(2, idServ);
                    pst.setString(3, ref);
                    pst.executeUpdate();

                    System.out.println("Se registró el pago correctamente");
                } catch (SQLException e) {
                    //Logger.getLogger(validaciones.class.getName()).log(Level.SEVERE, null, e);
                    System.out.println("No se registró el pago correctamente");
                }
            } else if (p > m) {
                // Hay que devolver el cambio
                int cambio = p - m;
                try {
                    Connection cx = connection.connect();
                    String sql = "insert into movimiento (id_mov,tipo_mov,mont_mov,fch_mov,num_tar) values (?,?,?,?,?)";
                    PreparedStatement pst = cx.prepareStatement(sql);
                    pst.setString(1, idMov);
                    pst.setString(2, "s");
                    pst.setInt(3, m);
                    pst.setTimestamp(4, Timestamp.valueOf(fechaActual));
                    pst.setString(5, num_tar);
                    pst.executeUpdate();
                    //System.out.println("Se actualizó la tabla movimiento corretamente");

                    sql = "select id_serv from servicio where nom_serv = ?";
                    pst = cx.prepareStatement(sql);
                    pst.setString(1, serv);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        idServ = rs.getInt("id_serv");
                    }

                    sql = "insert into pag_servicio (id_mov,id_serv,ref_pg) values (?,?,?)";
                    pst = cx.prepareStatement(sql);
                    pst.setString(1, idMov);
                    pst.setInt(2, idServ);
                    pst.setString(3, ref);
                    pst.executeUpdate();

                    sql = "select fond_term from terminal";
                    pst = cx.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        dinero = rs.getInt("fond_term");
                    }

                    sql = "update terminal set fond_term = ?";
                    pst = cx.prepareStatement(sql);
                    pst.setInt(1, dinero - cambio);
                    pst.executeUpdate();

                    System.out.println("Favor de tomar su cambio... $" + cambio);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Se registró el pago correctamente");

                } catch (SQLException e) {
                    //Logger.getLogger(validaciones.class.getName()).log(Level.SEVERE, null, e);
                    System.out.println("No se registró el pago correctamente");
                }
            }

            return true;
        } else {
            //System.out.println("Quiero con tarjeta");
            return true;
        }
    }

    boolean consulta(String num_tar) {
        return true;
    }

}
