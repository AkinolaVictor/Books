package netgame.chat;

import java.io.IOException;
import netgame.common.Hub;

/**
 * This class contains just a small main class that creates a Hub
 * and starts it listening on port 37829.  This port is used
 * by the ChatRoomWindow application.  This program should be run
 * on the computer that "hosts" the chat room.  See the ChatRoomWindow
 * class for more details.  Once the server starts listening, it
 * will listen for connection requests from clients until the
 * ChatRoomServer program is terminated (for example by a 
 * Control-C).
 * <p>Note that the ChatRoom application uses a basic, generic
 * Hub, which simply forwards any message that it received from
 * a client to all connected clients (including the one that
 * sent it), wrapped in an object of type ForwardedMessage.
 */
public class ChatRoomServer {

   private final static int PORT = 37829;
   
   public static void main(String[] args) {
      try {
         new Hub(PORT);
      }
      catch (IOException e) {
         System.out.println("Can't create listening socket.  Shutting down.");
      }
   }
   
}
