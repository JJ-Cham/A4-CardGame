import java.awt.*;
import java.awt.event.*;
import java.util.ListIterator;

import javax.swing.*;

/**
 *  This class implements a graphical canvas in which card 
 *  piles are placed.  It will also contain a nested listener class
 *  to respond to and handle mouse events.
 *
 *  The canvas is large enough to contain five rows of cards.
 *  Each row has its associated fixed CardPile.  When initialized,
 *  all the cards are in the top pile and the others are empty.
 *
 *  CardTable should implement the following behavior:
 *  - When the user doubleclicks on a card, that card and all those
 *    on top of it on the pile should be flipped over
 *  - When the user drags a card, that card and all those on top of it
 *    on the pile should be removed from the pile they are on and
 *    follow the mouse around.
 *  - When the user releases the mouse while dragging a pile of cards,
 *    the pile should be inserted into some fixed pile according to
 *    where the mouse was released. 
 *  
 *  @author Nicholas R. Howe
 *  @version CSC 112, 8 February 2006
 */
public class CardGame extends JComponent {
    /** The GUI object */
    private static final CardGame GUI = new CardGame();

    /** Gives the number of piles available */
    public static final int NPILE = 5;

    /** gives the width of the canvas */
    public static final int WIDTH = 800;

    /** gives the height of the canvas */
    public static final int HEIGHT = 500;

    /** Storage for each of the piles available */
    CardPile pile[] = new CardPile[NPILE];

    /** Storage for pile that is in motion */
    CardPile movingPile;

    /** Records card under last mouse press */
    Card cardUnderMouse;

    /** Records index of pile under last mouse press */
    CardPile pileUnderMouse;

    /** Initialize a table with a deck of cards in the first slot */
    public CardGame() {
	pile[0] = new CardPile(Card.newDeck(),2,2);
	pile[1] = new CardPile(2,102);
	pile[2] = new CardPile(2,202);
	pile[3] = new CardPile(2,302);
	pile[4] = new CardPile(2,402);

        // Add code here to turn over all the cards
        // FILL IN
        ListIterator<Card> it = pile[0].listIterator(); //go through each card in pile 0, pile 0 is a linked list
        while(it.hasNext()){
            Card c = it.next();
            c.flipCard(); //flip the card
        } //works!

        //Testing Stage 1 methods

        // Test split(null): should move all 52 cards
        CardPile suffix = pile[0].split(null);
        System.out.println("pile[0] size after split(null): " + pile[0].size());  // expect 0
        System.out.println("suffix size: " + suffix.size());                      // expect 52

        // Move cards back for further tests
        pile[0].addAll(suffix);
        suffix.clear();

        // Test split at a specific card
        Card mark = pile[0].get(10);   // pick the 11th card
        CardPile suffix2 = pile[0].split(mark);
        System.out.println("pile[0] size after split(mark): " + pile[0].size());   // expect 10
        System.out.println("suffix2 first card == mark? " + (suffix2.getFirst() == mark));

        // Test insertAfter
        // Make a pile
        CardPile pile = new CardPile(Card.newDeck(), 0, 0);

        // Grab a marker card from the pile
        Card marker = pile.get(5);

        // Make a test card (pull one from a fresh deck so it’s not already in the pile)
        Card testCard = Card.newDeck()[10];

        // Insert testCard after marker
        pile.insertAfter(testCard, marker);

        // Verify
        System.out.println("Inserted " + testCard + " after " + marker);

        Responder responder = new Responder();
        addMouseListener(responder);
        addMouseMotionListener(responder);

        // Test iteratorAfter
        ListIterator<Card> pos = suffix2.iteratorAfter(marker);
        if (pos != null && pos.hasNext()) {
            System.out.println("iteratorAfter result: " + pos.next()); // should be testCard
        }

        // Edge case: split on last card
        Card last = suffix2.getLast();
        CardPile suffix3 = suffix2.split(last);
        System.out.println("suffix3 size (should be 1): " + suffix3.size());

        // Sample card movements. 
        // Uncomment these one at a time to see what they do.
	//pile[0].getLast().flipCard();
        //pile[1].addLast(pile[0].removeLast());
        //pile[1].addLast(pile[0].removeLast());
        //pile[1].addFirst(pile[0].removeFirst());

        // Now add your card movements for stage 1 here.
        // FILL IN

        // Once you have written the split() method in CardPile 
        // you can uncomment and test the line below.
        //pile[2].addAll(pile[0].split(pile[0].get(26)));

        // Next try other uses of split.
        // Then try out the various insert methods.
        // You should test out all the methods of CardGame that move cards
        // and make sure that they all work as intended.
        // FILL IN

    }

    /**
     *  Returns the requested card pile
     *
     *  @param i  The index of the pile requested
     *  @return   The requested pile, or null if the pile is empty
     */
    public CardPile getPile(int i) {
	CardPile pile;
	if ((i >= 0)&&(i < NPILE)) {
	    pile = this.pile[i];
	} else {
	    pile = null;
	}
	return pile;
    }

    /**
     *  Attaches the specified cards to the specified pile.
     *  The location of the pile is set to a fixed location.
     *
     *  @param i  ID of the pile to use
     *  @param pile  Cards to put there
     */
    public void setPile(int i, CardPile pile) {
	if ((i >= 0)&&(i < NPILE)) {
            pile.setX(2);
            pile.setY(2+100*i);
	    this.pile[i] = pile;
	}
    }

    /**
     *  Draws the table and the cards upon it
     *
     *  @param g The graphics object to draw into
     */
    public void paintComponent(Graphics g) {
	g.setColor(Color.green.darker().darker());
	g.fillRect(0,0,WIDTH,HEIGHT);
	g.setColor(Color.black);
	for (int i = 0; i < pile.length; i++) {
	    g.drawRect(2,2+100*i,72,96);
	    pile[i].draw(g);
	}
	if (movingPile != null) {
	    movingPile.draw(g);
	}
    }

    /**
     *  The component will look bad if it is sized smaller than this
     *
     *  @return The minimum dimension
     */
    public Dimension getMinimumSize() {
	return new Dimension(WIDTH,HEIGHT);
    }

    /**
     *  The component will look best at this size
     *
     *  @return The preferred dimension
     */
    public Dimension getPreferredSize() {
	return new Dimension(WIDTH,HEIGHT);
    }

    /**
     *  For debugging.  Runs validation tests on all piles.
     */
    public void validatePiles() {
	for (int i = 0; i < NPILE; i++) {
	    System.out.print("Pile "+i+":  ");
            System.out.print("Location:  ("+pile[i].getX()+","+
                             pile[i].getY()+");  Length:  ");
            System.out.print(pile[i].size()+";  Status:  ");
            System.out.println("Valid.");
	}
	System.out.print("Moving pile:  ");
        System.out.print("Location:  ("+movingPile.getX()+","+
                         movingPile.getY()+");  Length:  ");
        System.out.print(movingPile.size()+";  Status:  ");
        System.out.println("Valid.");
    }

    /**
     *  Locates the pile clicked on, if any.
     *
     *  @param x,y  Coordinates of mouse click
     *  @return  CardPile  holding clicked card
     */
    private CardPile locatePile(int x, int y) {
        int index = y/100;
        if (index < 0) {
            index = 0;
        } else if (index>=NPILE) {
            index = NPILE-1;
        }
	return pile[index];
    }

    /**
     *  Locates the card clicked on, if any.
     *
     *  @param x,y  Coordinates of mouse click
     *  @return  Card  holding clicked card
     */
    public Card locateCard(int x, int y) {
	return locatePile(x,y).locateCard(x,y);
    }

    
   ///////////////////////////////////////////////
   // Methods below this point are for stage 2. //
   ///////////////////////////////////////////////

    
    /** Listener for relevant mouse events */
    private class Responder implements MouseListener, MouseMotionListener {
        /** Click event handler */
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
		System.out.println("Mouse double click event at ("+e.getX()+","+e.getY()+").");
                // FILL IN
                // Locate the pile and card under the mouse
            pileUnderMouse = locatePile(e.getX(), e.getY());
            if (pileUnderMouse == null) return;

            cardUnderMouse = pileUnderMouse.locateCard(e.getX(), e.getY());
            if (cardUnderMouse == null) return;

            // Flip this card and all cards above it
            ListIterator<Card> it = pileUnderMouse.listIterator();
            boolean found = false;
            while (it.hasNext()) {
                Card c = it.next();
                if (c == cardUnderMouse) found = true;
                if (found) c.flipCard();
            }
		// What happens here when a pile is double clicked?
		
            repaint();
            }
        }

        /**
         * Press event handler stores card currently under mouse,
         * but doesn't move any data until we have a drag event
         */
        public void mousePressed(MouseEvent e) {
	    // FILL IN
	    // What happens here when the mouse is pressed?
        pileUnderMouse = locatePile(e.getX(), e.getY());
        //if (pileUnderMouse == null) return;
        cardUnderMouse = pileUnderMouse.locateCard(e.getX(), e.getY());
        }

        /** Release event handler */
        public void mouseReleased(MouseEvent e) {
            if (movingPile != null) {
		// FILL IN
                // We have a pile coming to rest -- where? what happens?
                CardPile targetPile = locatePile(e.getX(), e.getY());
                Card targetCard = targetPile.locateCard(e.getX(), e.getY());

                if (targetPile != null) {
                 // Insert moving pile after that card
                    targetPile.insertAfter(targetCard, movingPile.getFirst());
                }       
                else {
                    // Otherwise, just add all to the end
                    targetPile.addAll(movingPile);
                }

                movingPile = null;  // Done dragging

            }
            repaint();
        }

        /** Enter event handler */
        public void mouseEntered(MouseEvent e) {
        }

        /** Exit event handler */
        public void mouseExited(MouseEvent e) {
        }

        /** Drag event handler moves piles around */
        public void mouseDragged(MouseEvent e) {
	    // FILL IN
	    // What happens when the mouse is dragged?
	    // What if it is the first drag after a mouse down?
            // If no pile is currently being moved, start a new drag
            if (movingPile == null && pileUnderMouse != null) {
                movingPile = pileUnderMouse.split(cardUnderMouse);
            }

            // Move pile with mouse
            if (movingPile != null) {
                movingPile.setX(e.getX() - 36);  // Adjust so pile centers under cursor
                movingPile.setY(e.getY() - 48);
                repaint();
            }
        }

        /** Move event handler */
        public void mouseMoved(MouseEvent e) {
        }
    }

    ///////////////////////////////////////////////
    // Methods below this point handle GUI setup //
    ///////////////////////////////////////////////

    /**
     * This method is called by the application version.
     */
    public void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("Sample GUI Application");
        try {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
        }

        // Add components
        createComponents(frame.getContentPane());

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Call this to set up the GUI contents.
     *
     * @param pane The pane of the JFrame of JApplet
     */
    public void createComponents(Container pane) {
        // set up layout
        pane.add(GUI);
    }

    /**
     * This is the entry point for the application version
     */
    public static void main(String[] args) {
        // Load card images
        Card.loadImages(GUI);

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI.createAndShowGUI();
            }
        });
    }
} // end of CardGame
