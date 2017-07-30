package editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.Iterator;
import javafx.util.Duration;
import java.util.ArrayList;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;
import javafx.geometry.Orientation;
import javafx.event.EventType;
import javafx.scene.effect.BlendMode;


public class Editor extends Application {

    private String fontName = "Verdana";
    public static String[] args;
    private static Group root = new Group();
    private static Rectangle cursor = new Rectangle();
    private static LinkedListDeque text = new LinkedListDeque();
    public static ScrollBar scrollBar = new ScrollBar();
    private Group textRoot = new Group();
    
    public class MouseClickEventHandler implements EventHandler<MouseEvent> {

        MouseClickEventHandler(Group root) {
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            int mousePressedX = (int) Math.round(mouseEvent.getX());
            int mousePressedY = (int) Math.round(mouseEvent.getY());
            text.mouseClick(mousePressedX, mousePressedY, cursor);
        }

    }

    private class MouseEventHandler implements EventHandler<MouseEvent> {
        int lastPositionX;
        int lastPositionY;
        int firstPositionX;
        int firstPositionY;
        ArrayList pathLines = new ArrayList<Rectangle>();
        int a;
        int b;
        
        MouseEventHandler() {
            pathLines = new ArrayList<Rectangle>();
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();
            EventType eventType = mouseEvent.getEventType();

            if (eventType == MouseEvent.MOUSE_PRESSED) {
                firstPositionX = (int) Math.round(mousePressedX);
                firstPositionY = (int) Math.round(mousePressedY);
                text.mouseClick(firstPositionX, firstPositionY, cursor);
                a = text.getCursorX();
                b = text.getCursorY();

            } else if (eventType == MouseEvent.MOUSE_DRAGGED) {
                lastPositionX = (int) Math.round(mousePressedX);
                lastPositionY = (int) Math.round(mousePressedY);
                text.mouseClick(lastPositionX, lastPositionY, cursor);
                Rectangle path = new Rectangle(a, b, lastPositionX - firstPositionX, text.getFontSize());
                path.setFill(Color.LIGHTBLUE);
                path.setBlendMode(BlendMode.DARKEN);
                root.getChildren().add(path);
                pathLines.add(path);

            } else if (eventType == MouseEvent.MOUSE_RELEASED) {
                //root.getChildren().removeAll(pathLines);

            }

        }

    }

    public class CursorBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] cursorColors = {Color.BLACK, Color.WHITE};

        public CursorBlinkEventHandler() {
            blink();
        }

        public void blink() {
            cursor.setFill(cursorColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % cursorColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            blink();
        }

    }

    public void makeCursorBlink() {
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        CursorBlinkEventHandler cursorChange = new CursorBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public class KeyEventHandler implements EventHandler<KeyEvent> {
        public KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
            cursor.setX(5);
            cursor.setY(0);
            text.setCursorDefault(cursor);
            root.getChildren().add(cursor);
            makeCursorBlink();
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            
            if (keyEvent.isShortcutDown()) {
                KeyCode code = keyEvent.getCode();

                if (code == KeyCode.P) {
                    System.out.println((int) Math.round(cursor.getX()) + ", " + (int) Math.round(cursor.getY()));
                    keyEvent.consume();
                } else if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                    text.makeFontBigger(cursor, root);
                    keyEvent.consume();
                } else if (code == KeyCode.MINUS) {
                    text.makeFontSmaller(cursor, root);
                    keyEvent.consume();
                } else if (code == KeyCode.S) {
                    String inputFilename = Editor.args[0];
                    try {
                        File inputFile = new File(inputFilename);
                        FileWriter writer = new FileWriter(inputFile);
                        Iterator textIter = text.iterator();
                        writer.flush();
                        while (textIter.hasNext()) {
                            Text next = (Text) textIter.next();

                            if (next.equals("\r\n") || next.equals("\n")) {
                                writer.write("\n");
                            } else {
                                writer.write(next.getText());
                            }
                            
                        }
                        System.out.println("Successfully saved file " + inputFilename);
                        writer.close();
                    } catch (FileNotFoundException fileNotFoundException) {
                        System.out.println("File not found! Exception was: " + fileNotFoundException);
                    } catch (IOException ioException) {
                        System.out.println("Error when saving; exception was: " + ioException);
                    }

                    keyEvent.consume();
                } else if (code == KeyCode.Z) {
                    text.callUndo(cursor, root);
                } else if (code == KeyCode.Y) {
                    text.callRedo(cursor, root);
                }

            }
                
            else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                String characterTyped = keyEvent.getCharacter();
                
                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {

                    if (characterTyped.equals("\r")) {
                        characterTyped = "\n";
                    }

                    Text t = new Text(0, 0, characterTyped);
                    text.insert(t, cursor, root);
                    keyEvent.consume();
                }

            } 

            else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                KeyCode code = keyEvent.getCode();

                if (code == KeyCode.BACK_SPACE) {
                    text.delete(cursor, root);
                    keyEvent.consume();
                } else if (code == KeyCode.LEFT) {
                    text.moveCursorLeft(cursor);
                    keyEvent.consume();
                } else if (code == KeyCode.RIGHT) {
                    text.moveCursorRight(cursor);
                    keyEvent.consume();
                } else if (code == KeyCode.UP) {
                    text.moveCursorUp(cursor);
                    keyEvent.consume();
                } else if (code == KeyCode.DOWN) {
                    text.moveCursorDown(cursor);
                    keyEvent.consume();
                }

            }

        }

    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(root, text.getWindowWidth(), text.getWindowHeight(), Color.WHITE);
        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root, text.getWindowWidth(), text.getWindowHeight());
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnMouseClicked(new MouseClickEventHandler(root));

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                text.setWindowWidth(newScreenWidth.intValue());
                text.render(root, cursor);
                scrollBar.setLayoutX(newScreenWidth.intValue() - scrollBar.getLayoutBounds().getWidth());
            }
        });

        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                text.setWindowHeight(newScreenHeight.intValue());
                text.render(root, cursor);
                scrollBar.setPrefHeight(newScreenHeight.intValue());
            }
        });

        Iterator textIter = text.iterator();
        root.getChildren().add(textRoot);

        while (textIter.hasNext()) {
            Text next = (Text) textIter.next();
            text.insert(next, cursor, textRoot);
        }

        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.setPrefHeight(text.getWindowHeight());
        scrollBar.setPrefWidth(5);
        scrollBar.setMin(0);
        scrollBar.setMax(1000);
        double usableScreenWidth = text.getWindowWidth() - scrollBar.getLayoutBounds().getWidth();
        scrollBar.setLayoutX(usableScreenWidth);

        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {

            }
        });

        root.getChildren().add(scrollBar);
        MouseEventHandler mouseEventHandler = new MouseEventHandler();
        scene.setOnMousePressed(mouseEventHandler);
        scene.setOnMouseDragged(mouseEventHandler);
        scene.setOnMouseReleased(mouseEventHandler);
        primaryStage.setTitle("Text Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Editor.args = args;

        if (args.length < 1) {
            System.out.println("Expected usage: Open File <source filename> (Optional Debug)");
            System.exit(1);
            
        } else if (args.length == 2) {

            if (!(args[1].equals("debug"))) {
                System.out.println("Second argument must be debug");
                System.exit(1);
            }

        }

        String inputFilename = Editor.args[0];

        try {
            File inputFile = new File(inputFilename);

            if (!inputFile.exists()) {
                inputFile.createNewFile();
                launch(args);
                System.out.println("Successfully created new File named " + inputFilename);
                return;
            } else {
                FileReader reader = new FileReader(inputFile);
                BufferedReader bufferedReader = new BufferedReader(reader);
                int intRead = -1;

                while ((intRead = bufferedReader.read()) != -1) {
                    char charRead = (char) intRead;
                    Text a = new Text(0, 0, String.valueOf(charRead));
                    text.insert(a, cursor, root);
                }

                System.out.println("Successfully opened file " + inputFilename);
                bufferedReader.close();
            }

            text.resetCursor(cursor);
            launch(args);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when opening; exception was: " + ioException);
        }

        
    }
}
