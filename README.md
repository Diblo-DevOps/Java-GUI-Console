# Java GUI Console
This is a standalone Java GUI console that is simple to implement in any console based Java code.

## How to implement the console in my code
1. Download Console.java and place the file in your project
2. Initialize the Console class as an object
3. Run your code

### Examples of Use

_Example 1_

    public class Foo {
        public static void main(String[] args) {
            Console console = new Console();
            console.show();

            new Bar().metohod();
        }
    }

_Example 2_

    public class Foo {
        public static void main(String[] args) {
            Console console = new Console();
            console.show();

            new Bar().metohod();

            console.destroy();
        }
    }

_Example 3_

    public class Foo {
        public static void main(String[] args) {
            Console console = new Console();
            console.show();

            Bar bar = new Bar();

            boolean finished = false;
            while (finished) {
                finished = bar.metohod();
            }

            console.destroy();
        }
    }

## Code Description

**Constructor Summary**

    Console()
    Console(String title)
    Console(String title, String iconFile)
    Console(String title, int requestWidth, int requestHeight)
    Console(String title, int requestWidth, int requestHeight, String iconFile)

**Method Summary**

| Modifier and Type | Method | Description |
|-------------------|--------|-------------|
| public void | show() | Show the console window. |
| public void | destroy() | Destroy the console window. |
| public String | getTitle() | Get the window title. |
| public void | setTitle(String title) | Set the window title. |
| public void | requestSize(int width, int height) | Request a window size. The size is handled as a request not as the absolute size. |
| public void | setSize(int width, int height) | Set the window size. |
| public Dimension | getSize() | Get the window size. |
| public void | loadIconFile() | Load icon file or resource. |
| public Image | getIcon() | Get the window icon. |
| public void | setIcon(String icon) | Set the window icon. |
| public boolean | isResizable | Indicates whether the window is resizable by the user. |
| public void | setResizable | Sets whether the window is resizable by the user. |
| public void | loadFontFile(String file)<br>loadFontFile(String file, int fontFormat)<br>loadFontFile(String file, int fontFormat, int size)<br>loadFontFile(InputStream is)<br>loadFontFile(InputStream is, int fontFormat)<br>loadFontFile(InputStream is, int fontFormat, int size) | Load font from file or resource. |
| public Font | getFont() | Get the font. |
| public void | setFont(Font font)<br>setFont(String string)<br>setFont(String string, int size) | Set the font. |
| public String | getFontName() | Get the font name. |
| public int | getFontStyle() | Get the font style. |
| public void | setFontStyle(int fontStyle) | Set the font style. |
| public int | getFontSize() | Get the font size. |
| public void | setFontSize(int fontSize) | Set the font size. |
| public Color | getFontColor() | Get the font color. |
| public void | setFontColor(Color color) | Set the font color. |
| public int | getTabSize | Gets the number of characters used to expand tabs. |
| public void | setTabSize | Sets the number of characters to expand tabs to. |
| public Color | getBackgroundColor() | Get the background color. |
| public void | setBackgroundColor(Color color) | Set the background color. |
| public String[] | getCmdHistory() | Get the entire commando history. |
| public void | setCmdHistory(String[] cmdHistory) | Add or restore commando history. |
