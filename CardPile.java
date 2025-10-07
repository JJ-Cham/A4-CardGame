import java.awt.*;
import java.awt.List;
import java.util.*;

/**
 * This class stores a collection of playing cards
 * in a linked list format.
 *
 * @author Nicholas R. Howe
 * @version CSC 112, 22 February 2006
 */
public class CardPile extends LinkedList<Card> {
    /** Location of the pile of cards on the table */
    private int x, y;

    /** Offset between cards in the pile */
    private int offsetX = 12, offsetY = 0;

    /** Constructor initializes location of empty pile */
    public CardPile(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    /** Constructor puts array of cards into pile */
    public CardPile(Card[] cards, int x, int y) {
        super(Arrays.asList(cards));
        this.x = x;
        this.y = y;
    }

    /** Copy constructor */
    public CardPile(CardPile pile) {
        super(pile);
        this.x = pile.x;
        this.y = pile.y;
    }

    /** Accessor for x coordinate of pile */
    public int getX() {
        return x;
    }

    /** Accessor for y coordinate of pile */
    public int getY() {
        return y;
    }

    /** Manipulator for x coordinate of pile */
    public void setX(int x) {
        this.x = x;
    }

    /** Manipulator for y coordinate of pile */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Insert a card node before the specified marker
     *
     * @param card The card to insert
     * @param mark New card goes before this one
     */
    public void insertBefore(Card card, Card mark) {
        ListIterator<Card> position = listIterator(size());
        while (position.hasPrevious() && (position.previous() != mark)) {
            // keep going until we find our card
        }
        position.add(card); // add it
    }

    /**
     * Insert a card node after the specified marker
     *
     * @param card The card to insert
     * @param mark New card goes after this one
     */
    public void insertAfter(Card card, Card mark) {
        // FILL IN
        ListIterator<Card> it = listIterator();
        while (it.hasNext()){
            Card c = it.next();
            if(c == mark){
                it.add(card);
                return;
            }
        }

    }

    /**
     * Inserts a one pile into another, leaving the inserted pile empty
     *
     * @param insert list to insert
     * @param mark   insert before this card
     */
    public void insertBefore(CardPile insert, Card mark) {
        ListIterator<Card> position = listIterator(size());
        // empty loop to find the position to insert at
        while (position.hasPrevious() && (position.previous() != mark)) {
            // nothing to do here
        }
        // now move cards one at a time
        while (insert.size() > 0) {
            position.add(insert.removeFirst());
        }
    }

    /**
     * Inserts a one pile into another, leaving the inserted pile empty
     *
     * @param insert list to insert
     * @param mark   insert after this point
     */
    public void insertAfter(CardPile insert, Card mark) {
        // FILL IN
        ListIterator<Card> it = listIterator();
        while (it.hasNext()){
            Card c = it.next();
            if(c == mark){
                while(insert.size() > 0){
                    it.add(insert.removeFirst());
                }
                return;
            }
        }
    }

    //adding code that was missing 
    /**
     * Find an iterator just before the mark
     *
     * @param mark New card goes before this one
     */
    public ListIterator<Card> iteratorBefore(Card mark) {
        ListIterator<Card> position = listIterator(size());
        while (position.hasPrevious() && (position.previous() != mark)) {
            // keep going until we find our card
        }
        return position;
    }

    /**
    * Return a ListIterator positioned just after the specified marker card.
    *
    * @param mark The card to search for
    * @return iterator positioned after mark, or null if not found
    */
    public ListIterator<Card> iteratorAfter(Card mark) {
        ListIterator<Card> it = listIterator();
        while (it.hasNext()) {
            Card current = it.next();
            if (current == mark) { //found the marker
                return it; //return the iterator positioned after the marker
            }
        }
        
        return null; //didn't find the marker
    }

    /**
     * Moves every element after the mark into a new pile.
     * If mark is null, entire pile is moved.
     * The location of the new pile will be (0,0).
     *
     * @param mark elements including and after this are moved
     * @return the suffix pile
     */
    public CardPile split(Card mark) {
        // FILL IN -- return value below is temporary, for clean compile
        CardPile suffix = new CardPile(0, 0); // new empty pile
        ListIterator<Card> it = this.listIterator(); 

        //boolean = (mark == null);

        if(mark == null){
            while(it.hasNext()){
                suffix.addLast(it.next());
                it.remove();
            }
            return suffix;
        }

        //if mark isn't full, find it 
        while(it.hasNext()){
            Card c = it.next();
            if(c == mark){
                it.previous();
                while(it.hasNext()){
                    suffix.addLast(it.next());
                    it.remove();
                }
                return suffix;
            }
        }
        return null;
    }

    /**
     * Appends the provided suffix onto this list.
     * If the suffix list is empty, nothing happens.
     * If this list is empty, the suffix list takes its place.
     *
     * @param suffix list to append and empty
     */
    public void append(CardPile suffix) {
        addAll(size(), suffix);
    }

    /**
     * Draws the pile at its location on the table.
     *
     * @param g Graphics object to draw into
     */
    public void draw(Graphics g) {
        int cx = this.x, cy = this.y;
        for (Card card : this) {
            if (card.getIsFaceUp()) {
                g.drawImage(card.getFrontSide(), cx, cy, 72, 96, null);
            } else {
                g.drawImage(Card.getBackSide(), cx, cy, 72, 96, null);
            }
            cx += offsetX;
            cy += offsetY;
        }
    }

    /**
     * Determine if the specified click falls upon a card in this pile.
     * If so, return the node holding that card.
     *
     * @param x Coordinate of mouse click
     * @param y Coordinate of mouse click
     * @return Clicked Card, or null
     */
    public Card locateCard(int x, int y) {
        Card result = null;
        int cx = this.x, cy = this.y;
        for (Card card : this) {
            if ((x >= cx) && (x <= cx + 72) && (y >= cy) && (y < cy + 96)) {
                result = card;
            }
            cx += offsetX;
            cy += offsetY;
        }
        return result;
    }

    /**
     * Prints a representation of a CardPile
     */
    public void print() {
        if (size() == 0) {
            System.out.println("Empty pile.");
        } else {
            for (Card card : this) {
                System.out.print(card + ", ");
            }
            System.out.println("");
        }
    }
}
