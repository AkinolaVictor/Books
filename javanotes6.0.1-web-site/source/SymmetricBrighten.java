
/**
 *  This is a simple extension of the RandomBrighten class.
 *  It shows an animation similar to the one in RandomBrighten
 *  except that the pattern is horizontally and vertically
 *  symmetric.  The applet depends on RandomBrighten and
 *  on MosaicCanvas.
 */

public class SymmetricBrighten extends RandomBrighten {

   void brighten(int row, int col) {
         // Brighten the specified square and its horizontal and vertical
         // reflections.  This overrides the brighten method from the
         // RandomBrighten class, which just brightens one square.
      super.brighten(row, col);
      super.brighten(ROWS - 1 - row, col);
      super.brighten(row, COLUMNS - 1 - col);
      super.brighten(ROWS - 1 - row, COLUMNS - 1 - col);
   }

}
