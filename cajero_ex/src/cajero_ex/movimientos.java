package cajero_ex;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
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
    double saldo_ac = 0, retirar = 0, dinero = 0;
    String pin = "";

    boolean retiro(String num_tar) {
        // mostramos el saldo que tiene en la tarjeta
        try {
            do {
                System.out.print("Para continuar con la operación, ingrese su pin: ");
                pin = sc.nextLine();
                flag = v.validarPin(pin, num_tar);
            } while (flag == false);
            flag = false;
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
                    System.out.print("Ingrese la cantidad a retirar (solo múltiplos de 100 y menor a $2000): $");
                    retirar = sc.nextDouble();
                    sc.nextLine();
                    flag = v.retiro(retirar);
                } while (flag == false);
                if (retirar > saldo_ac) {
                    System.out.println("No se puede retirar más dinero del que tiene en su cuenta.");
                } else {
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

                    sql = "select fond_term from terminal";
                    pst = cx.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        dinero = rs.getDouble("fond_term");
                    }

                    sql = "update terminal set fond_term = ?";
                    pst = cx.prepareStatement(sql);
                    pst.setDouble(1, dinero - retirar);
                    pst.executeUpdate();

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
        return true;
    }

    boolean deposito(String num_tar) {
        double saldo_ac = 0;
        double depositar;
        String pin;

        try {
            // Solicitar el PIN del usuario y validarlo
            do {
                System.out.print("Para continuar con la operación, ingrese su pin: ");
                pin = sc.nextLine();
                flag = v.validarPin(pin, num_tar);
            } while (!flag);
            flag = false;

            // Conectar a la base de datos
            Connection cx = connection.connect();

            // Obtener el saldo actual de la tarjeta
            String sql = "select sald_deb from debito where num_tar = ?";
            PreparedStatement pst = cx.prepareStatement(sql);
            pst.setString(1, num_tar);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                saldo_ac = rs.getDouble("sald_deb");
            }

            // Mostrar el saldo actual
            System.out.print("Su saldo actual es de: $" + saldo_ac + "\n");

            // Solicitar la cantidad a depositar
            do {
                System.out.print("Ingrese la cantidad a depositar (solo múltiplos de 100 y menor a $2000): $");
                depositar = sc.nextDouble();
                sc.nextLine();
                flag = v.deposito(depositar);
            } while (flag == false);

            // Actualizar el saldo de la tarjeta en la base de datos
            sql = "update debito set sald_deb = ? where num_tar = ?";
            pst = cx.prepareStatement(sql);
            pst.setDouble(1, saldo_ac + depositar);
            pst.setString(2, num_tar);
            pst.executeUpdate();

            System.out.println("Favor de depositar su dinero...");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Actualizar el fondo de la terminal
            sql = "select fond_term from terminal";
            pst = cx.prepareStatement(sql);
            rs = pst.executeQuery();
            double dinero = 0;
            if (rs.next()) {
                dinero = rs.getDouble("fond_term");
            }

            sql = "update terminal set fond_term = ?";
            pst = cx.prepareStatement(sql);
            pst.setDouble(1, dinero + depositar);
            pst.executeUpdate();

            // Registrar el depósito en la tabla de movimientos
            LocalDateTime fechaActual = LocalDateTime.now();
            String idMov = f.generarCadena();
            sql = "insert into movimiento (id_mov,tipo_mov,mont_mov,fch_mov,num_tar) values (?,?,?,?,?)";
            pst = cx.prepareStatement(sql);
            pst.setString(1, idMov);
            pst.setString(2, "d"); // 'd' para depósito
            pst.setDouble(3, depositar);
            pst.setTimestamp(4, Timestamp.valueOf(fechaActual));
            pst.setString(5, num_tar);
            pst.executeUpdate();

            // Obtener el saldo actual de la tarjeta
            sql = "select sald_deb from debito where num_tar = ?";
            pst = cx.prepareStatement(sql);
            pst.setString(1, num_tar);
            rs = pst.executeQuery();
            if (rs.next()) {
                saldo_ac = rs.getDouble("sald_deb");
            }

            // Mostrar el saldo actual
            System.out.print("Su saldo actual es de: $" + saldo_ac + "\n");

            System.out.println("Se registró el depósito exitosamente.");
            return true;
        } catch (SQLException e) {
            Logger.getLogger(validaciones.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    boolean pago_tar(String num_tar) {
        String tar_cre = "";
        int id_cre = 0;
        double saldo_ac = 0, pago = 0;

        // Validamos que la tarjeta de crédito a pagar este registrada en la base de datos
        do {
            System.out.print("Ingresa el número de la tarjeta de crédito que deseas pagar: ");
            tar_cre = sc.nextLine();
            flag = v.isCredit(tar_cre);
        } while (flag == false);

        try {
            // Recuperamos el id de la tarjeta de crédito
            Connection cx = connection.connect();
            String sql = "select id_cred from credito where num_tar = ?";
            PreparedStatement pst = cx.prepareStatement(sql);
            pst.setString(1, tar_cre);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                id_cre = rs.getInt("id_cred");
            }

            // Mostramos el saldo actual de la tarjeta de débito
            sql = "select sald_deb from debito where num_tar = ?";
            pst = cx.prepareStatement(sql);
            pst.setString(1, num_tar);
            rs = pst.executeQuery();
            if (rs.next()) {
                saldo_ac = rs.getDouble("sald_deb");
            }
            System.out.print("Su saldo actual es de: $" + saldo_ac + "\n");

            // Preguntamos cuánto se desea pagar en la tarjeta de crédito
            System.out.println("Ingrese la cantidad a pagar: $");
            pago = sc.nextDouble();

            // Validamos que si se pueda pagar esa cantidad
            if (saldo_ac < pago) {
                System.out.println("No puedes pagar una cantidad de dinero mayor a la almacenada en tu cuenta.");
                return false;
            } else {
                // Validamos el PIN para seguir con la operación
                do {
                    System.out.print("Para continuar con la operación, ingrese su pin: ");
                    pin = sc.nextLine();
                    flag = v.validarPin(pin, num_tar);
                } while (!flag);

                // Realizamos el pago a la tarjeta de crédito
                sql = "update credito set saldo_cred = saldo_cred - ? where id_cred = ?";
                pst = cx.prepareStatement(sql);
                pst.setDouble(1, pago);
                pst.setInt(2, id_cre);

                // Acutualizamos el saldo de la tarjeta de débito
                sql = "update debito set sald_deb = ? where num_tar = ?";
                pst = cx.prepareStatement(sql);
                pst.setDouble(1, saldo_ac - pago);
                pst.setString(2, num_tar);
                pst.executeUpdate();

                System.out.println("Se actualizó el saldo de tu tarjeta de débito.");

                // Generamos el reporte del movimiento en la base de datos
                LocalDateTime fechaActual = LocalDateTime.now();
                String idMov = f.generarCadena();
                sql = "insert into movimiento (id_mov,tipo_mov,mont_mov,fch_mov,num_tar) values (?,?,?,?,?)";
                pst = cx.prepareStatement(sql);
                pst.setString(1, idMov);
                pst.setString(2, "p"); // 'p' para pago de tarjeta de crédito
                pst.setDouble(3, pago);
                pst.setTimestamp(4, Timestamp.valueOf(fechaActual));
                pst.setString(5, num_tar);
                pst.executeUpdate();

                // Generamos el reporte del movimiento en el gestor de tarjetas de crédito
                sql = "insert into pag_credito (id_mov) values (?)";
                pst = cx.prepareStatement(sql);
                pst.setString(1, idMov);
                pst.executeUpdate();

                System.out.println("Se realizó correctamente el pago a la tarjeta de crédito indicada.");

                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(validaciones.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }

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
                System.out.print("Ingrese el monto a pagar (únicamente múltiplos de 100): $");
                monto = sc.nextLine();
                flag = v.monto(monto);
            } while (flag == false);
            //System.out.println("");
            do {
                System.out.print("¿Cuánto dinero va a ingresar? (múltiplos de 100): $");
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
        //s servicio p tarjeta d deposito e recibo deposito r retiro
        System.out.println("Movimientos:");
        String idmov;
        String tipo;
        Date fchmov;
        String adicional = "";
        String tarAdd = "****************";

        DecimalFormat dineros = new DecimalFormat("#0.00");

        try {
            Connection cx = connection.connect();
            String sql1 = "select * from movimiento where num_tar = ?";
            PreparedStatement pst = cx.prepareStatement(sql1);
            pst.setString(1, num_tar);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                idmov = rs.getString("id_mov");
                tipo = rs.getString("tipo_mov");
                fchmov = rs.getDate("fch_mov");
                System.out.print(idmov + " " + fchmov + "");

                switch (tipo) {
                    case "s"://pago de servicio
                        System.out.print("\tpago del servicio \n\t-$");
                        sql1 = "select nom_serv, ref_pg from movimiento natural join pag_servicio natural join servicio where id_mov= ?";
                        pst = cx.prepareStatement(sql1);
                        pst.setString(1, idmov);
                        ResultSet rs2 = pst.executeQuery();
                        while (rs2.next()) {
                            adicional = rs2.getString("nom_serv");
                            adicional = adicional + " " + rs2.getString("ref_pg");
                        }
                        break;
                    case "t"://t pago de tarjeta 
                        System.out.print("\tpago de tarjeta \n\t-$");
                        sql1 = "select num_tar, fchco_cc from credito natural join cred_corte natural join pag_credito where id_mov = ?";
                        pst = cx.prepareStatement(sql1);
                        pst.setString(1, idmov);
                        ResultSet rst = pst.executeQuery();
                        while (rst.next()) {
                            tarAdd = rst.getString("num_tar");
                            adicional = "\t****" + tarAdd.substring(tarAdd.length() - 4);
                            LocalDate fchs = rst.getDate("fchco_cc").toLocalDate();
                            adicional = adicional + " " + fchs.getMonth() + "/" + fchs.getYear();
                        }
                        break;
                    case "d"://d deposito
                        System.out.print("\tDeposito \n\t-$");
                        sql1 = "select num_tar from deposito a cross join movimiento b on a.id_movR = b.id_mov where id_movE=?";
                        pst = cx.prepareStatement(sql1);
                        pst.setString(1, idmov);
                        ResultSet rsd = pst.executeQuery();
                        while (rsd.next()) {
                            tarAdd = rsd.getString("num_tar");
                            adicional = "****" + tarAdd.substring(tarAdd.length() - 4);
                        }
                        break;
                    case "e"://e recibo deposito
                        System.out.print("\tTransferencia \n\t+$");
                        sql1 = "select num_tar from deposito a cross join movimiento b on a.id_movE = b.id_mov where id_movR=?";
                        pst = cx.prepareStatement(sql1);
                        pst.setString(1, idmov);
                        ResultSet rse = pst.executeQuery();
                        while (rse.next()) {
                            tarAdd = rse.getString("num_tar");
                            adicional = (tarAdd != null) ? "****" + tarAdd.substring(tarAdd.length() - 4) : "cajero";
                        }
                        break;
                    case "r"://r retiro 
                        System.out.println("\tRetiro\n\t-$");
                        adicional = "cajero único";
                        break;

                }
                System.out.print(dineros.format(rs.getDouble("mont_mov")));
                System.out.print("\t" + adicional);
                System.out.println("\n");
            }

            sql1 = "select tip_tar from tarjeta where num_tar = ?";
            pst = cx.prepareStatement(sql1);
            pst.setString(1, num_tar);
            rs = pst.executeQuery();
            while (rs.next()) {
                String tipot = rs.getString("tip_tar");
                if (tipot.equalsIgnoreCase("d")) {
                    String sql2 = "select sald_deb from tarjeta natural join debito where num_tar = ?";
                    pst = cx.prepareStatement(sql2);
                    pst.setString(1, num_tar);
                    ResultSet rs3 = pst.executeQuery();
                    while (rs3.next()) {
                        double saldo = rs3.getDouble("sald_deb");
                        System.out.print("\nSaldo actual:\t $");
                        System.out.println(dineros.format(saldo));
                    }
                } else if (tipot.equalsIgnoreCase("c")) {
                    String sql2 = "select crds_cred, sald_cred, fchco_cc, min_cc from credito natural join tarjeta natural join cred_corte where num_tar = ?";
                    pst = cx.prepareStatement(sql2);
                    pst.setString(1, num_tar);
                    ResultSet rs3 = pst.executeQuery();
                    while (rs3.next()) {
                        double creditoDis = rs3.getDouble("crds_cred");
                        double saldo = rs3.getDouble("sald_cred");
                        double saldDis = creditoDis - saldo;
                        LocalDate fchCort = rs3.getDate("fchco_cc").toLocalDate();
                        double minPag = rs3.getDouble("min_cc");

                        System.out.print("\n\t Crédito disponible: $");
                        System.out.print(dineros.format(saldDis));
                        System.out.print("\n\t Mínimo a pagar: ");
                        System.out.print(minPag);
                        System.out.print("\tFecha de corte: ");
                        System.out.print(fchCort.getMonth() + "/" + fchCort.getYear());
                        System.out.println("\n");
                    }
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(validaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

}
