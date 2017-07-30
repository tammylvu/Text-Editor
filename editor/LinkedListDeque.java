package editor;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Stack;

public class LinkedListDeque {

    private Node sentinel;
    private Node cursorPointer;
    private int cursorX;
    private int cursorY;
    private int fontSize = 12;
    private String fontName = "Verdana";
    private int currentX;
    private int currentY;
    private int textWidth;
    private int windowWidth = 500;
    private int windowHeight = 500;
    private ArrayList<Node> arr = new ArrayList();
    private Stack<Sn> undos = new Stack();
    private Stack<Sn> redos = new Stack();
    private int stackCount = 0;
    private int undoCount = 0;
    private int redoCount = 0;

    private class Node {

        private Text item;
        private Node next;
        private Node prev;

        private Node(Node p, Text i, Node n) {
            prev = p;
            item = i;
            next = n;
        }

    }

    public LinkedListDeque() {
        Text sent = new Text(0, 0, "sentinel");
        sentinel = new Node(null, sent, null);
        cursorPointer = sentinel;
        cursorX = 5;
        cursorY = 0;

    }

    public int getDocSize() {
        if ((arr.size() - 1) * fontSize > windowWidth) {
            return (arr.size() - 1) * fontSize;
        } 
        return windowWidth;
    }

    public void setWindowWidth(int w) {
        windowWidth = w;
    }

    public void setWindowHeight(int h) {
        windowHeight = h;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setCursorDefault(Rectangle c) {
        c.setHeight(fontSize);
        c.setWidth(1);
    }

    public void updateCursor(Rectangle c) {
        currentX = (int) Math.round(cursorPointer.item.getX());
        textWidth = (int) Math.round(cursorPointer.item.getLayoutBounds().getWidth());
        currentY = (int) Math.round(cursorPointer.item.getY());
        c.setY(currentY);

        if (cursorPointer == sentinel) {
            c.setX(5);
        }
        else {
            c.setX(currentX + textWidth);
        }
    }

    public void makeFontBigger(Rectangle c, Group root) {
        fontSize += 4;
        render(root, c);
        updateCursor(c);
        setCursorDefault(c);
    }

    public void makeFontSmaller(Rectangle c, Group root) {
        fontSize -= 4;
        render(root, c);
        updateCursor(c);
        setCursorDefault(c);
    }

    public int getCursorX() {
        return (int) Math.round(cursorPointer.item.getX());
    }

    public int getCursorY() {
        return (int) Math.round(cursorPointer.item.getY());
    }

    public void mouseClick(int x, int y, Rectangle c) {
        Node bookmark = sentinel.next;
        int temp = (int) (y / fontSize);
        if (bookmark == null) {
            return;
        }
        if (temp < arr.size()) {
            bookmark = arr.get(temp);
        }
        
        if (bookmark.next != null) {
            if (bookmark.next.item.getY() != bookmark.item.getY()) {
                bookmark = bookmark.next;
            } else if (bookmark.next != null) {
                bookmark = bookmark.next;
            }

        }

        if (temp >= arr.size() && y > arr.get(arr.size() - 1).item.getY() + fontSize) {
            Node bookmark2 = arr.get(arr.size() - 1);
            int size = 0;
            textWidth = textWidth = (int) Math.round(bookmark2.item.getLayoutBounds().getWidth());

            if (bookmark2.next != null) {
                bookmark2 = bookmark2.next;
            }

            while (!(bookmark2.item.getText().equals("\n")) &&
                    size + textWidth < windowWidth - 10 &&
                    bookmark2.next != null) {

                textWidth = (int) Math.round(bookmark2.item.getLayoutBounds().getWidth());
                size += textWidth;
                bookmark2 = bookmark2.next;
            }

            cursorPointer = bookmark2;
            updateCursor(c);
            
        } else if (bookmark != null) {

            while (bookmark.next != null && !(bookmark.item.getText().equals("\n"))) {
                textWidth = (int) Math.round(bookmark.item.getLayoutBounds().getWidth());
                int getX = (int) Math.round(bookmark.item.getX());

                if (x <= (getX + textWidth / 2)) {
                    cursorPointer = bookmark.prev;
                    updateCursor(c);
                    break;
                }

                bookmark = bookmark.next;
            }

            if (bookmark.next == null) {
                cursorPointer = bookmark;
            } else {
                cursorPointer = bookmark.prev;
            }
            updateCursor(c);

            
        }

    }

    public void moveCursorUp(Rectangle c) {
        if (cursorPointer.prev == sentinel) {
            cursorPointer = sentinel;
            updateCursor(c);
            return;
        }
        int temp = (int) (c.getY() / fontSize);
        int size = 0;
        if (temp > 0) {
            Node bookmark = arr.get(temp - 1);

            if (bookmark.item.getText() == "\n" && bookmark.next != null &&
                    bookmark.next.item.getY() == bookmark.item.getY()) {
                bookmark = bookmark.next;
            }

            while (!(bookmark.item.getText().equals("\n")) &&
                    size + textWidth < windowWidth - 10 &&
                    bookmark.next != null) {
                int getX = (int) Math.round(bookmark.item.getX());
                textWidth = (int) Math.round(bookmark.item.getLayoutBounds().getWidth());
                size += textWidth;

                if (c.getX() <= (getX + textWidth / 2)) {
                    cursorPointer = bookmark.prev;
                    updateCursor(c);
                    break;
                }

                bookmark = bookmark.next;
            }

            if (c.getX() > size) {

                if (size == 0 && bookmark.item.getText() == "\n") {
                    cursorPointer = bookmark;
                } else if (bookmark.item.getText() == "\n") {
                    cursorPointer = bookmark.prev;
                } else {
                    cursorPointer = bookmark;
                }

                updateCursor(c);
            }

        }
        
    }

    public void moveCursorDown(Rectangle c) {
        
        int temp = (int) (c.getY() / fontSize);
        int size = 0;

        if (temp < arr.size() - 1) {
            Node bookmark = arr.get(temp + 1);

            if (bookmark.next != null || bookmark.next.item.getY() != bookmark.item.getY()) {
                bookmark = bookmark.next;
            }

            while (!(bookmark.item.getText().equals("\n")) &&
                    size + textWidth < windowWidth - 10 &&
                    bookmark.next != null) {
                int getX = (int) Math.round(bookmark.item.getX());
                textWidth = (int) Math.round(bookmark.item.getLayoutBounds().getWidth());
                size += textWidth;

                if (c.getX() <= (getX + textWidth / 2)) {
                    cursorPointer = bookmark.prev;
                    updateCursor(c);
                    break;
                }

                bookmark = bookmark.next;
            }

            if (c.getX() > size) {
                
                if (bookmark == null || bookmark.item.getText() == "\n") {
                    cursorPointer = bookmark.prev;
                } else {
                    cursorPointer = bookmark;
                }

                updateCursor(c);
            }

        }

    }

    public void insert(Text t, Rectangle c, Group root) {
        regulate(t);
        cursorPointer.next = new Node(cursorPointer, t, cursorPointer.next);
        cursorPointer = cursorPointer.next;
        root.getChildren().add(t);

        if (cursorPointer.next != null) {
            cursorPointer.next.prev = cursorPointer;
        }

        render(root, c);
        setCursorDefault(c);
        Print.print("inserted: " + t.getText());
        updateCursor(c);
        currentX += textWidth;
        undoCount++;
        undos.push(new Sn(t, false, undoCount, cursorPointer));
        redos.clear();
    }

    public Node delete(Rectangle c, Group root) {

        if (cursorPointer != sentinel) {
            Node temp = cursorPointer;
            cursorPointer = cursorPointer.prev;
            cursorPointer.next = temp.next;

            if (cursorPointer.next != null) {
                temp.next.prev = cursorPointer;
            }

            root.getChildren().remove(temp.item);
            render(root, c);
            setCursorDefault(c);
            updateCursor(c);
            undoCount++;
            undos.push(new Sn(temp.item, true, undoCount, cursorPointer));
            redos.clear();
            return temp;
        }

        return null;
    }

    private class Sn {
        private Text t;
        private boolean in;
        private int index;
        private Node cursor;

        public Sn(Text t, boolean in, int index, Node cursor) {
            this.t = t;
            this.in = in;
            this.index = index;
            this.cursor = cursor;
        }

        public boolean getIn() {
            return in;
        }

        public Text getT() {
            return t;
        }

        public int getCount() {
            return index;
        }

        public Node getCursor() {
            return cursor;
        }

    }

    public void callUndo(Rectangle c, Group root) {
        if (undos.size() > 100) {
            undos.remove(0);
        }
        if (!undos.empty()) {
            redoCount++;
            redos.push(new Sn(undos.peek().getT(), !undos.peek().getIn(), redoCount, cursorPointer)); 

            if (undos.peek().getIn()) {
                cursorPointer = undos.peek().getCursor();
                insert(undos.pop().getT(), c, root);
                undos.pop();
                
            } else {
                cursorPointer = undos.peek().getCursor();
                delete(c, root);
                undos.pop();
                undos.pop();
            }

            undoCount--;
        }
    }

    public void callRedo(Rectangle c, Group root) {
        if (!redos.empty()) {
            undos.push(new Sn(redos.peek().getT(), !redos.peek().getIn(), undoCount, cursorPointer));
            undoCount++;

            if (redos.peek().getIn()) {
                cursorPointer = redos.peek().getCursor();
                insert(redos.pop().getT(), c, root);
                undos.pop();
            } else {
                cursorPointer = redos.peek().getCursor();
                delete(c, root);
                undos.pop();
            }

        }
        
    }

    public int getFontSize() {
        return fontSize;
    }

    public void render(Group root, Rectangle c) {

        Node bookmark = sentinel.next;
        int curX = 5;
        int curY = 0;
        arr.clear();
        arr.add(bookmark.prev);

        while (bookmark != null) {
            regulate(bookmark.item);

            if (bookmark.item.getText().equals("\n")) {
                curY += fontSize;
                curX = 5;
                bookmark.item.setX(curX);
                bookmark.item.setY(curY);
                arr.add(bookmark);
                    

            } else if (curX + (int) Math.round(bookmark.item.getLayoutBounds().getWidth()) > windowWidth - 5) {
                curY += fontSize;
                curX = 5;
                Node wordStart = bookmark;
                int size = 0;

                while (!(wordStart.item.getText().equals(" ")) && wordStart != sentinel) {
                    size += (int) Math.round(wordStart.item.getLayoutBounds().getWidth());
                    wordStart = wordStart.prev;
                }

                if (size < windowWidth - 10) {
                    wordStart = wordStart.next;
                    arr.add(wordStart);

                    while (wordStart != bookmark) {
                        wordStart.item.setX(curX);
                        wordStart.item.setY(curY);
                        int textWidth = (int) Math.round(wordStart.item.getLayoutBounds().getWidth());
                        curX += textWidth;
                        wordStart = wordStart.next;
                    }

                    if (wordStart == bookmark) {
                        wordStart.item.setX(curX);
                        wordStart.item.setY(curY);
                    }

                } else {
                    arr.add(bookmark);
                }

            }

            bookmark.item.setX(curX);
            bookmark.item.setY(curY);
            /*if (bookmark.item.getY() > windowHeight) {
                Editor.scrollBar.setMax(arr.size() * fontSize - windowHeight);
            }*/
            int textWidth = (int) Math.round(bookmark.item.getLayoutBounds().getWidth());
            curX += textWidth;
            bookmark = bookmark.next;
        }

    }

    public void regulate(Text t) {
        t.setTextOrigin(VPos.TOP);
        t.setFont(Font.font(fontName, fontSize));
    }

    public void resetCursor(Rectangle c) {
        cursorPointer = sentinel;
        updateCursor(c);
    }

    public void moveCursorLeft(Rectangle c) {

        if (cursorPointer != sentinel) {

            if (cursorPointer.item.getText() == "\n") {
                cursorPointer = cursorPointer.prev;
                updateCursor(c);
            } else {
                currentX = (int) Math.round(cursorPointer.item.getX());
                currentY = (int) Math.round(cursorPointer.item.getY());
                textWidth = (int) Math.round(cursorPointer.item.getLayoutBounds().getWidth());
                cursorPointer = cursorPointer.prev;
                c.setX(currentX);
                c.setY(currentY);
                currentX -= textWidth;
            }

        }

    }

    public void moveCursorRight(Rectangle c) {

        if (cursorPointer.next != null) {
            cursorPointer = cursorPointer.next;
            updateCursor(c);
        }

    }

    private class Iter implements Iterator<Text> {
        private Node bookmark;

        public Iter() {
            bookmark = sentinel.next;
        }

        public boolean hasNext() {
            if (bookmark == null) {
                return false;
            }
            
            return bookmark.next != null;
        }

        public Text next() {
            Node t = bookmark;
            bookmark = bookmark.next;
            return t.item;
        }

    }

    public Iterator<Text> iterator() {
        return new Iter();
    }

}
