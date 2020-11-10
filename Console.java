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

@SuppressWarnings("unused")
public class Console implements KeyListener, MouseListener {
    private String title;

    private String icon = null;

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int width = (int) (this.screenSize.getWidth() * 0.75);
    private int height = (int) (this.screenSize.getHeight() * 0.75);

    private boolean resizable = true;

    private String fontFile = "/Fonts/CONSOLA.TTF";
    private String fontName = null;
    private int fontSize = 12;
    private int fontStyle = Font.PLAIN;
    private Color fontColor = Color.WHITE;

    private Color backgroundColor = Color.BLACK;

    private final int[] keysWithoutInterests = {KeyEvent.VK_ACCEPT, KeyEvent.VK_ADD, KeyEvent.VK_AGAIN,
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

    private JFrame frame;
    private JTextArea textArea;

    private final PipedOutputStream pipedOutputStream = new PipedOutputStream();

    private int initialCaretPosition = 0;
    private int curCaretPosition = 0;

    private int curCmdHistoryIndex = 0;
    private ArrayList<String> cmdHistory = new ArrayList<>();

    public Console(String title) {
        this(title, -1, -1);
    }

    public Console(String title, int requestWidth, int requestHeight) {
        this.title = title;
        this.setSize(requestWidth, requestHeight);
    }


    public void show() {
        //
        this.textArea = this.textArea();

        // Setup Listener
        this.textArea.addKeyListener(this);
        this.textArea.addMouseListener(this);

        // Redirecting output
        this.setSystemOutput(this.textArea);

        // Redirecting input
        this.setSystemInput(this.pipedOutputStream);

        //
        this.frame = this.frame();
        this.frame.add(this.scrolls());
        this.frame.setVisible(true);
    }


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setSize(int requestWidth, int requestHeight) {
        this.setWidth(requestWidth);
        this.setHeight(requestHeight);
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int requestWidth) {
        int maxWidth = (int) this.screenSize.getWidth() - 10;

        if (requestWidth > 500 && requestWidth < maxWidth) {
            this.width = requestWidth;
        } else if (requestWidth > 0) {
            this.width = maxWidth;
        }
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int requestHeight) {
        int maxHeight = (int) this.screenSize.getHeight() - 10;

        if (requestHeight > 500 && requestHeight < maxHeight) {
            this.height = requestHeight;
        } else if (requestHeight > 0) {
            this.height = maxHeight;
        }
    }

    public boolean isResizable() {
        return this.resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public String getFontFile() {
        return this.fontFile;
    }

    public void setFontFile(String fontFile) {
        this.fontFile = fontFile;
    }

    public String getFontName() {
        return this.fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontStyle() {
        return this.fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public Color getFontColor() {
        return this.fontColor;
    }

    public void setFontColor(Color color) {
        this.fontColor = color;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public String[] getCmdHistory() {
        return this.cmdHistory.toArray(new String[0]);
    }

    public void setCmdHistory(String[] cmdHistory) {
        this.cmdHistory = new ArrayList<>(Arrays.asList(cmdHistory));;
    }


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

        for (int code : keysWithoutInterests) {
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


    private boolean hasSelection() {
        return this.textArea.getSelectedText() != null;
    }


    private int getCaretPosition() {
        return this.textArea.getCaretPosition();
    }

    private void setCaretPosition(int position) {
        this.textArea.setCaretPosition(position);
    }


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
        this.textArea.setSelectionStart(this.getInputStart());
        this.textArea.setSelectionEnd(this.getInputEnd());
        this.textArea.replaceSelection(string);
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


    private JFrame frame() {
        JFrame jFrame = new JFrame(this.title);

        jFrame.setSize(this.width, this.height);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setResizable(this.resizable);

        if (this.icon != null) {
            URL iconURL = getClass().getResource(this.icon);
            jFrame.setIconImage(new ImageIcon(iconURL).getImage());
        }

        return jFrame;
    }

    private JScrollPane scrolls() {
        JScrollPane jScrollPane = new JScrollPane(this.textArea);

        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        return jScrollPane;
    }

    private JTextArea textArea() {
        JTextArea jTextArea = new JTextArea();

        jTextArea.setEditable(true);
        jTextArea.setLineWrap(false);

        jTextArea.setForeground(this.fontColor);
        jTextArea.setBackground(this.backgroundColor);

        try {
            Font font;

            if (this.fontName == null) {
                InputStream inputStream = Console.class.getResourceAsStream(this.fontFile);

                font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(this.fontStyle, this.fontSize);

                // Register the font
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

            } else {
                font = new Font(this.fontName, this.fontStyle, this.fontSize);
            }

            // Set the font
            jTextArea.setFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        // Automatically scroll
        DefaultCaret caret = (DefaultCaret) jTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        return jTextArea;
    }


    private void write(String text) {
        try {
            this.pipedOutputStream.write((text + "\n").getBytes());
            this.pipedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setSystemInput(PipedOutputStream pipedOutputStream) {
        try {
            System.setIn(new PipedInputStream(pipedOutputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSystemOutput(JTextArea textArea) {
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

    public void destroy() {
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }
}
