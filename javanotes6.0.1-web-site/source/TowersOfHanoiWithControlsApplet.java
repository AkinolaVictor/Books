import javax.swing.JApplet;


/**
 * This applet just displays a TowersOfHanoiWithControls panel.
 * Should be 430 pixels wide and maybe 175 high.
 */
public class TowersOfHanoiWithControlsApplet extends JApplet {
   public void init() {
      setContentPane(new TowersOfHanoiWithControls());
   }
}
