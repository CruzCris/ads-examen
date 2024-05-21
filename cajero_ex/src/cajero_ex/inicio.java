

package cajero_ex;

import java.util.Scanner;

/**
 *
 * @author crist
 */
public class inicio {
    public static void main(String[] args){
        boolean flag = false;
        String num_tar = "";
        int movimiento = 0;
        movimientos m = new movimientos();
        Scanner sc = new Scanner(System.in);
        
        
        System.out.println("Bienvenido a tu cajero YE de confianza");
        validaciones v = new validaciones();
        do{
            System.out.print("Para realizar algún movimiento, ingresa tu número de tarjeta a continuación: ");
            num_tar = sc.next();
            flag = v.num_tarjeta(num_tar);
        }while(flag == false);
        
        do{
            System.out.println("¿Qué movimiento desea hacer?");
            System.out.println("1.- Retiro de efectivo");
            System.out.println("2.- Depósito de efectivo");
            System.out.println("3.- Pago de tarjeta de crédito");
            System.out.println("4.- Pago de servicios");
            System.out.println("5.- Consulta de saldo/movimientos");
            System.out.println("0.- Salir");
            movimiento = sc.nextInt();
            switch(movimiento){
                case 1:
                    // Retiro
                    flag = m.retiro(num_tar);
                    break;
                case 2:
                    // Depósito
                    flag = m.deposito(num_tar);
                    break;
                case 3:
                    // Pago de tarjeta
                    flag = m.pago_tar(num_tar);
                    break;
                case 4:
                    // Pago se servicios
                    flag = m.pago_servicio(num_tar);
                    break;
                case 5:
                    // Consulta
                    flag = m.consulta(num_tar);
                    break;
                default:
                    flag = false;
                    break;
            }
        }while(flag == true);
        
        //System.out.println(""+num_tar);
    }
}
