import java.net.*;
import java.util.Enumeration;

/**
 * This short program lists information about available network interfaces
 * on the computer on which it is run.  The name of each interface is
 * output along with a list of one or more IP addresses for that
 * interface.  The names are arbitrary names assigned by the operating
 * system to the interfaces.  The addresses can include both IPv4 and
 * IPv6 addresses.  The list should include the local loopback interface
 * (usually referred to as "localhost") as well as the interface
 * corresponding to any network card that has been installed and configured.
 */
public class ShowMyNetwork {

   public static void main(String[] args) {

      Enumeration netInterfaces;

      System.out.println();

      try {
         netInterfaces = NetworkInterface.getNetworkInterfaces();
      }
      catch (Exception e){
         System.out.println();
         System.out.println("Sorry, an error occurred while looking for network");
         System.out.println("interfaces.  The error was:");
         System.out.println(e);
         return;
      }

      if (! netInterfaces.hasMoreElements() ) {
         System.out.println("No network interfaces found.");
         return;
      }
      
      System.out.println("Network interfaces found on this computer:");
      
      while (netInterfaces.hasMoreElements()) {
         NetworkInterface net = (NetworkInterface)netInterfaces.nextElement();
         String name = net.getName();
         System.out.print("   " + name + " :  ");
         Enumeration inetAddresses = net.getInetAddresses();
         while (inetAddresses.hasMoreElements()) {
            InetAddress address = (InetAddress)inetAddresses.nextElement();
            System.out.print(address + "  ");
         }
         System.out.println();
      }
      
      System.out.println();
      
   } // end main()
   

}
