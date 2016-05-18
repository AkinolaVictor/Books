package netgame.fivecarddraw;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 *  A main class for the network Five Card Draw Poker game.  Main routine
 *  shows a dialog where the user can choose to be a server or
 *  a client.  If the user chooses to be a server, then a PokerHub
 *  is created to manage the game; the game will not start until a
 *  second player has connected as a client.  To act as a client,
 *  the user must know the host name or IP address of the computer
 *  and the port number where the server is waiting for a connection.
 *  When run as a client, this program does not create a hub;
 *  rather, it connects to the hub that was created by the server.
 *  In either case, a PokerWindow is created where the game will 
 *  be played.
 */
public class Main {

   private static final int DEFAULT_PORT = 32058;
   
   public static void main(String[] args) {
      
      // First, construct a panel that will be placed into a JOptionPane confirm dialog.
      
      JLabel message = new JLabel("Welcome to NetPoker!", JLabel.CENTER);
      message.setFont(new Font("Serif", Font.BOLD, 16));
      
      final JTextField listeningPortInput = new JTextField("" + DEFAULT_PORT, 5);
      final JTextField hostInput = new JTextField(30);
      final JTextField connectPortInput = new JTextField("" + DEFAULT_PORT, 5);
      
      final JRadioButton selectServerMode = new JRadioButton("Start a new game");
      final JRadioButton selectClientMode = new JRadioButton("Connect to existing game");
      
      ButtonGroup group = new ButtonGroup();
      group.add(selectServerMode);
      group.add(selectClientMode);
      ActionListener radioListener = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (e.getSource() == selectServerMode) {
               listeningPortInput.setEnabled(true);
               hostInput.setEnabled(false);
               connectPortInput.setEnabled(false);
               listeningPortInput.setEditable(true);
               hostInput.setEditable(false);
               connectPortInput.setEditable(false);
            }
            else {
               listeningPortInput.setEnabled(false);
               hostInput.setEnabled(true);
               connectPortInput.setEnabled(true);
               listeningPortInput.setEditable(false);
               hostInput.setEditable(true);
               connectPortInput.setEditable(true);
            }
         }
      };
      selectServerMode.addActionListener(radioListener);
      selectClientMode.addActionListener(radioListener);
      selectServerMode.setSelected(true);
      hostInput.setEnabled(false);
      connectPortInput.setEnabled(false);
      hostInput.setEditable(false);
      connectPortInput.setEditable(false);
      
      
      JPanel inputPanel = new JPanel();
      inputPanel.setLayout(new GridLayout(0,1,5,5));
      inputPanel.setBorder(BorderFactory.createCompoundBorder(
                 BorderFactory.createLineBorder(Color.BLACK, 2),
                 BorderFactory.createEmptyBorder(6,6,6,6) ));
      
      inputPanel.add(message);
      
      JPanel row;
      
      inputPanel.add(selectServerMode);
      
      row = new JPanel();
      row.setLayout(new FlowLayout(FlowLayout.LEFT));
      row.add(Box.createHorizontalStrut(40));
      row.add(new JLabel("Listen on port: "));
      row.add(listeningPortInput);
      inputPanel.add(row);
      
      inputPanel.add(selectClientMode);
      
      row = new JPanel();
      row.setLayout(new FlowLayout(FlowLayout.LEFT));      
      row.add(Box.createHorizontalStrut(40));
      row.add(new JLabel("Computer: "));
      row.add(hostInput);
      inputPanel.add(row);

      row = new JPanel();
      row.setLayout(new FlowLayout(FlowLayout.LEFT));
      row.add(Box.createHorizontalStrut(40));
      row.add(new JLabel("Port Number: "));
      row.add(connectPortInput);
      inputPanel.add(row);
      
      // Show the dialog, get the user's response and -- if the user doesn't
      // cancel -- start a game.  If the user chooses to run as the server
      // then a PokerHub (server) is created and after that a PokerWindow
      // is created that connects to the server running on  localhost, which was
      // just created.  In that case, the game will wait for a second connection. 
      // If the user chooses to connect to an existing server, then only
      // a PokerWindow is created, that will connect to the specified
      // host where the server is running.
      
      while (true) {  // Repeats until a game is started or the user cancels.

         int action = JOptionPane.showConfirmDialog(null, inputPanel, "Net Poker", 
               JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      
         if (action != JOptionPane.OK_OPTION)
            return;
         
         if (selectServerMode.isSelected()) {
            int port;
            try {
               port = Integer.parseInt(listeningPortInput.getText().trim());
               if (port <= 0)
                  throw new Exception();
            }
            catch (Exception e) {
               message.setText("Illegal port number!");
               listeningPortInput.selectAll();
               listeningPortInput.requestFocus();
               continue;
            }
            try {
               new PokerHub(port);
            }
            catch (Exception e) {
               message.setText("Error: Can't listen on port " + port);
               listeningPortInput.selectAll();
               listeningPortInput.requestFocus();
               continue;
            }
            new PokerWindow("localhost", port);
            break;
         }
         else {
            String host;
            int port;
            host = hostInput.getText().trim();
            if (host.length() == 0) {
               message.setText("You must enter a computer name!");
               hostInput.requestFocus();
               continue;
            }
            try {
               port = Integer.parseInt(connectPortInput.getText().trim());
               if (port <= 0)
                  throw new Exception();
            }
            catch (Exception e) {
               message.setText("Illegal port number!");
               connectPortInput.selectAll();
               connectPortInput.requestFocus();
               continue;
            }
            new PokerWindow(host,port);
            break;
         }
      }
      
   }
}
