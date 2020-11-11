/*
 * Copyright (C) 2020 Henrik Ankersø
 *  Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * https://www.sdu.dk/ - Software Engineering 2020 - Group T2-3; SDU Overflow
 *
 * @author Alican Erten, Henrik Ankersø, Simon Krüger Tagge, Stefan Profft Larsen and Thomas Christensen
 * @version 2020.11.10
 */

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings({"unused"})
public class Console implements KeyListener, MouseListener {
    private final Color FONT_COLOR = Color.WHITE;
    private final Color BACKGROUND_COLOR = Color.BLACK;

    private final int[] KEYS_WITHOUT_INTERESTS = {KeyEvent.VK_ACCEPT, KeyEvent.VK_ADD, KeyEvent.VK_AGAIN,
            KeyEvent.VK_ALL_CANDIDATES, KeyEvent.VK_ALPHANUMERIC, KeyEvent.VK_ALT, KeyEvent.VK_ALT_GRAPH,
            KeyEvent.VK_AMPERSAND, KeyEvent.VK_BEGIN, KeyEvent.VK_CANCEL, KeyEvent.VK_CAPS_LOCK, KeyEvent.VK_CODE_INPUT,
            KeyEvent.VK_COMPOSE, KeyEvent.VK_CONTEXT_MENU, KeyEvent.VK_CONTROL, KeyEvent.VK_CONVERT, KeyEvent.VK_F1,
            KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4, KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7,
            KeyEvent.VK_F8, KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11, KeyEvent.VK_F12, KeyEvent.VK_F13,
            KeyEvent.VK_F14, KeyEvent.VK_F15, KeyEvent.VK_F16, KeyEvent.VK_F17, KeyEvent.VK_F18, KeyEvent.VK_F19,
            KeyEvent.VK_F20, KeyEvent.VK_F21, KeyEvent.VK_F22, KeyEvent.VK_F23, KeyEvent.VK_F24, KeyEvent.VK_DECIMAL,
            KeyEvent.VK_FINAL, KeyEvent.VK_FIND, KeyEvent.VK_FULL_WIDTH, KeyEvent.VK_HALF_WIDTH, KeyEvent.VK_HELP,
            KeyEvent.VK_HIRAGANA, KeyEvent.VK_INPUT_METHOD_ON_OFF, KeyEvent.VK_JAPANESE_HIRAGANA,
            KeyEvent.VK_JAPANESE_KATAKANA, KeyEvent.VK_JAPANESE_ROMAN, KeyEvent.VK_KANA, KeyEvent.VK_KANA_LOCK,
            KeyEvent.VK_KANJI, KeyEvent.VK_KATAKANA, KeyEvent.VK_META, KeyEvent.VK_MODECHANGE, KeyEvent.VK_NONCONVERT,
            KeyEvent.VK_NUM_LOCK, KeyEvent.VK_PAUSE, KeyEvent.VK_PREVIOUS_CANDIDATE, KeyEvent.VK_PRINTSCREEN,
            KeyEvent.VK_PROPS, KeyEvent.VK_QUOTE, KeyEvent.VK_QUOTEDBL, KeyEvent.VK_ROMAN_CHARACTERS,
            KeyEvent.VK_SCROLL_LOCK, KeyEvent.VK_SHIFT, KeyEvent.VK_STOP, KeyEvent.VK_TAB, KeyEvent.VK_UNDEFINED,
            KeyEvent.VK_UNDO, KeyEvent.VK_WINDOWS};

    private JTextArea textArea;
    private JFrame frame;
    private final PipedOutputStream outputToInputStream = new PipedOutputStream();

    private int initialCaretPosition = 0;
    private int curCaretPosition = 0;

    private int curCmdHistoryIndex = 0;
    private ArrayList<String> cmdHistory = new ArrayList<>();


    public Console() {
        this("Console", -1, -1, null);
    }

    public Console(String title) {
        this(title, -1, -1, null);
    }

    public Console(String title, String iconFile) {
        this(title, -1, -1, iconFile);
    }

    public Console(String title, int requestWidth, int requestHeight) {
        this(title, requestWidth, requestHeight, null);
    }

    public Console(String title, int requestWidth, int requestHeight, String iconFile) {
        this.createWindow(title, requestWidth, requestHeight, iconFile);

        // Redirecting input && output
        this.setSystemInput();
        this.setSystemOutput();
    }


    public void show() {
        this.frame.setVisible(true);
    }


    public String getTitle(String title) {
        return this.frame.getTitle();
    }

    public void setTitle(String title) {
        this.frame.setTitle(title);
    }

    public void requestSize(int width, int height) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int maxWidth = (int) screenSize.getWidth() - 10;
        if (width > maxWidth) {
            width = maxWidth;
        } else if (width < 0) {
            width = (int) (screenSize.getWidth() * 0.75);
        }

        int maxHeight = (int) screenSize.getWidth() - 10;
        if (height > maxHeight) {
            height = maxHeight;
        } else if (height < 0) {
            height = (int) (screenSize.getHeight() * 0.75);
        }

        this.frame.setSize(width, height);
    }

    public void setSize(int width, int height) {
        this.frame.setSize(width, height);
    }

    public Dimension getSize() {
        return this.frame.getSize();
    }

    public void loadIconFile(String file) {
        ImageIcon imageIcon;

        URL iconURL = Console.class.getResource(file);
        if (iconURL == null) {
            imageIcon = new ImageIcon(file);
        } else {
            imageIcon = new ImageIcon(iconURL);
        }

        this.frame.setIconImage(imageIcon.getImage());
    }

    public Image getIcon() {
        return this.frame.getIconImage();
    }

    public void setIcon(Image image) {
        this.frame.setIconImage(image);
    }

    public boolean isResizable() {
        return this.frame.isResizable();
    }

    public void setResizable(Boolean resizable) {
        this.frame.setResizable(resizable);
    }


    public void loadFontFile(String file) {
        this.loadFontFile(file, Font.TRUETYPE_FONT, this.getFont().getSize());
    }

    public void loadFontFile(String file, int fontFormat) {
        this.loadFontFile(file, fontFormat, this.getFont().getSize());
    }

    public void loadFontFile(String file, int fontFormat, int size) {
        InputStream inputStream;
        try {
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                inputStream = Console.class.getResourceAsStream(file);
                if (inputStream == null) {
                    throw new FileNotFoundException("Could not find the font file: " + file);
                }
            }

            this.loadFontFile(inputStream, fontFormat, size);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadFontFile(InputStream is) {
        this.loadFontFile(is, Font.TRUETYPE_FONT, this.getFont().getSize());
    }

    public void loadFontFile(InputStream is, int fontFormat) {
        this.loadFontFile(is, fontFormat, this.getFont().getSize());
    }

    public void loadFontFile(InputStream is, int fontFormat, int size) {
        try {
            Font font = Font.createFont(fontFormat, is).deriveFont(this.getFont().getStyle(), size);

            // Register the font
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

            this.setFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public Font getFont() {
        return this.textArea.getFont();
    }

    public void setFont(Font font) {
        this.textArea.setFont(font);
    }

    public void setFont(String string) {
        Font font = this.getFont();
        this.setFont(new Font(string, font.getStyle(), font.getSize()));
    }

    public void setFont(String string, int size) {
        this.setFont(new Font(string, this.getFont().getStyle(), size));
    }

    public void setFont(String string, int style, int size) {
        this.setFont(new Font(string, style, size));
    }

    public String getFontName() {
        return this.getFont().getName();
    }

    public int getFontStyle() {
        return this.getFont().getStyle();
    }

    public void setFontStyle(int style) {
        this.textArea.setFont(this.getFont().deriveFont(style));
    }

    public int getFontSize(float size) {
        return this.getFont().getSize();
    }

    public void setFontSize(float size) {
        this.textArea.setFont(this.getFont().deriveFont(size));
    }

    public Color getFontColor() {
        return this.textArea.getForeground();
    }

    public void setFontColor(Color color) {
        this.textArea.setForeground(color);
    }

    public int getTabSize() {
        return this.textArea.getTabSize();
    }

    public void setTabSize(int font) {
        this.textArea.setTabSize(font);
    }


    public Color getBackgroundColor() {
        return this.textArea.getBackground();
    }

    public void setBackgroundColor(Color color) {
        this.textArea.setBackground(color);
    }


    public String[] getCmdHistory() {
        return this.cmdHistory.toArray(new String[0]);
    }

    public void setCmdHistory(String[] cmdHistory) {
        this.cmdHistory = new ArrayList<>(Arrays.asList(cmdHistory));
    }


    public void destroy() {
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }


    /* Events */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            if (this.hasSelection()) {
                this.textArea.copy();

                this.setCaretPosition(this.curCaretPosition);
            } else if (this.isInInput()) {
                this.textArea.paste();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.isInInput()) {
            this.curCaretPosition = this.getCaretPosition();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }


    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        if (this.isInInput() && !(ke.getKeyCode() == KeyEvent.VK_A && this.isControlOrMetaDown(ke))) {
            this.curCaretPosition = this.getCaretPosition();
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int keyCode = ke.getKeyCode();

        for (int code : KEYS_WITHOUT_INTERESTS) {
            if (code == keyCode) {
                return;
            }
        }

        if (keyCode == KeyEvent.VK_A && this.isControlOrMetaDown(ke)) {
            int start = this.getInputStart();
            int end = this.getInputEnd();

            if (this.textArea.getSelectionStart() == start && this.textArea.getSelectionEnd() == end) {
                start = 0;
            }

            this.textArea.setSelectionStart(start);
            this.textArea.setSelectionEnd(end);

        } else if (keyCode == KeyEvent.VK_HOME) {
            this.setCaretPosition(this.getInputStart());

        } else if (keyCode == KeyEvent.VK_END) {
            this.setCaretPosition(this.getInputEnd());

        } else if (keyCode == KeyEvent.VK_COPY || keyCode == KeyEvent.VK_CUT ||
                ((keyCode == KeyEvent.VK_C || keyCode == KeyEvent.VK_X) && this.isControlOrMetaDown(ke))) {
            if (this.hasSelection()) {
                this.textArea.copy();
            }

            this.setCaretPosition(this.curCaretPosition);

        } else if (keyCode == KeyEvent.VK_PAGE_UP) {
            this.replaceInput(this.getFirstCmdFromHistory());

        } else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
            this.replaceInput(this.getLastCmdFromHistory());

        } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_KP_UP) {
            this.replaceInput(this.getPrevCmdFromHistory());

        } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_KP_DOWN) {
            this.replaceInput(this.getNextCmdFromHistory());

        } else {
            if (keyCode == KeyEvent.VK_ENTER) {
                int start = this.getInputStart();
                int end = this.getInputEnd();

                this.setCaretPosition(end);

                try {
                    String cmd = this.textArea.getText(start, end - start);

                    this.addCmdToHistory(cmd);

                    this.write(cmd);

                    this.resetInputPosition();
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            } else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_KP_LEFT ||
                    keyCode == KeyEvent.VK_BACK_SPACE || (keyCode == KeyEvent.VK_H && this.isControlOrMetaDown(ke))) {
                if (!this.isInInput()) {
                    this.setCaretPosition(this.curCaretPosition);
                }

                if (this.getCaretPosition() == this.getInputStart()) {
                    ke.consume();
                }
            } else if (!this.isInInput()) {
                this.setCaretPosition(this.curCaretPosition);
            }

            return;
        }

        ke.consume();
    }


    private boolean isControlOrMetaDown(KeyEvent ke) {
        return ke.isControlDown() || ke.isMetaDown();
    }


    /* Cmd history */
    private void addCmdToHistory(String cmd) {
        this.cmdHistory.add(cmd);
        this.curCmdHistoryIndex = this.cmdHistory.size();
    }

    private String getPrevCmdFromHistory() {
        this.curCmdHistoryIndex--;
        if (this.curCmdHistoryIndex < 0) {
            this.curCmdHistoryIndex = this.cmdHistory.size() - 1;
        }

        return this.getCmdFromHistory(this.curCmdHistoryIndex);
    }

    private String getNextCmdFromHistory() {
        this.curCmdHistoryIndex++;
        if (this.curCmdHistoryIndex >= this.cmdHistory.size()) {
            this.curCmdHistoryIndex = 0;
        }

        return this.getCmdFromHistory(this.curCmdHistoryIndex);
    }

    private String getCmdFromHistory(int index) {
        if (this.cmdHistory.size() > 0) {
            return this.cmdHistory.get(index);
        }
        return "";
    }

    private String getLastCmdFromHistory() {
        this.curCmdHistoryIndex = this.cmdHistory.size() - 1;
        return this.getCmdFromHistory(this.curCmdHistoryIndex);
    }

    private String getFirstCmdFromHistory() {
        this.curCmdHistoryIndex = 0;
        return this.getCmdFromHistory(0);
    }


    /* Selection */
    private boolean hasSelection() {
        return this.textArea.getSelectedText() != null;
    }

    /* Caret */
    private int getCaretPosition() {
        return this.textArea.getCaretPosition();
    }

    private void setCaretPosition(int position) {
        this.textArea.setCaretPosition(position);
    }


    /* Input */
    private boolean isInInput() {
        int selStart = this.textArea.getSelectionStart();
        int selEnd = this.textArea.getSelectionEnd();

        if (selStart < selEnd) {
            return selStart >= this.getInputStart() && selEnd >= this.getInputEnd();
        }

        int caretPos = this.getCaretPosition();
        return caretPos >= this.getInputStart() && caretPos <= this.getInputEnd();
    }

    private void replaceInput(String string) {
        this.textArea.replaceRange(string, this.getInputStart(), this.getInputEnd());
    }

    private int getInputStart() {
        return this.initialCaretPosition;
    }

    private int getInputEnd() {
        return this.textArea.getDocument().getLength();
    }

    private void resetInputPosition() {
        this.initialCaretPosition = this.curCaretPosition = this.textArea.getDocument().getLength();
    }


    private void write(String text) {
        try {
            this.outputToInputStream.write((text + "\n").getBytes());
            this.outputToInputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setSystemInput() {
        try {
            System.setIn(new PipedInputStream(this.outputToInputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* Output */
    private void setSystemOutput() {
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                byte[] bytes = new byte[1];
                bytes[0] = (byte) (b & 0xff);

                textArea.append(new String(bytes));
                resetInputPosition();
            }
        }));
    }


    /* GUI */
    private void createWindow(String title, int requestWidth, int requestHeight, String iconFile) {
        // Create text area
        this.textArea = this.createConsoleArea();
        this.textArea.addKeyListener(this);
        this.textArea.addMouseListener(this);

        this.frame = new JFrame(title);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.requestSize(requestWidth, requestHeight);
        if (iconFile != null) {
            this.loadIconFile(iconFile);
        }
        this.frame.add(this.createScrolls());
    }

    private JScrollPane createScrolls() {
        JScrollPane jScrollPane = new JScrollPane(this.textArea);

        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        return jScrollPane;
    }

    private JTextArea createConsoleArea() {
        JTextArea jTextArea = new JTextArea();

        jTextArea.setEditable(true);
        jTextArea.setLineWrap(false);
        jTextArea.setForeground(FONT_COLOR);
        jTextArea.setBackground(BACKGROUND_COLOR);

        // Automatically scroll
        DefaultCaret caret = (DefaultCaret) jTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        return jTextArea;
    }
}
