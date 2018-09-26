package javanotepad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class JavaNotepad extends JFrame {
    
    static JTextArea mainArea;
    JMenuBar mnuBar;
    JMenu mnuFile, mnuEdit, mnuFormat, mnuHelp;
    JMenuItem itmNew, itmOpen, itmSave, itmSaveas, itmExit,
            itmCut, itmCopy, itmPaste, itmFontColor, itmFind,
            itmReplace, itmFont;
    String filename;
    JFileChooser fc;
    String fileContent;
    UndoManager undo;
    UndoAction undoAction;
    RedoAction redoAction;
    JCheckBoxMenuItem wordWrap;
    FontHelper font;
    //public static JavaNotepad fromMain = new JavaNotepad();

    public JavaNotepad() {
        initComponent();
        itmSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        itmSaveas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        });
        itmOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                open();
            }
        });
        itmNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                open_new();
            }
        });
        itmExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        itmCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainArea.cut();
            }
        });
        itmCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainArea.copy();
            }
        });
        itmPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainArea.paste();
            }
        });
        mainArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
                undoAction.update();
                redoAction.update();
            }
        });
        wordWrap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (wordWrap.isSelected()) {
                    mainArea.setLineWrap(true);
                    mainArea.setWrapStyleWord(true);
                } else {
                    mainArea.setLineWrap(false);
                    mainArea.setWrapStyleWord(false);
                }
            }
        });
        itmFontColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(rootPane, "Choose Font Color", Color.BLUE);
                mainArea.setForeground(c);
            }
        });
        itmFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FindAndReplace(null, false);
            }
        });
        itmReplace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FindAndReplace(null, true);
            }
        });
        itmFont.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                font.setVisible(true);
            }
        });
        font.getOk().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainArea.setFont(font.font());
                font.setVisible(false);
            }
        });
        font.getCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                font.setVisible(false);
            }
        });
        
    }
    
    void initComponent() {
        fc = new JFileChooser();
        mainArea = new JTextArea();
        undo = new UndoManager();
        font = new FontHelper();
        ImageIcon iconUndo = new ImageIcon(getClass().getResource("/img/undo.png"));
        ImageIcon iconRedo = new ImageIcon(getClass().getResource("/img/redo.png"));
        undoAction = new UndoAction(iconUndo);
        redoAction = new RedoAction(iconRedo);
        getContentPane().add(mainArea);
        getContentPane().add(new JScrollPane(mainArea), BorderLayout.CENTER);
        setTitle("Untitled Notepad");
        setSize(800, 600);

        //menu bar
        mnuBar = new JMenuBar();
        //menu
        mnuFile = new JMenu("File");
        mnuEdit = new JMenu("Edit");
        mnuFormat = new JMenu("Format");
        mnuHelp = new JMenu("Help");
        //add icon
        ImageIcon iconNew = new ImageIcon(getClass().getResource("/img/new.png"));
        ImageIcon iconOpen = new ImageIcon(getClass().getResource("/img/open.png"));
        ImageIcon iconSave = new ImageIcon(getClass().getResource("/img/save.png"));
        ImageIcon iconSaveAs = new ImageIcon(getClass().getResource("/img/saveAs.png"));
        ImageIcon iconExit = new ImageIcon(getClass().getResource("/img/exit.png"));
        ImageIcon iconFind = new ImageIcon(getClass().getResource("/img/find.png"));
        ImageIcon iconReplace = new ImageIcon(getClass().getResource("/img/replace.png"));

        //ImageIcon iconCut = new ImageIcon(getClass().getResource("/img/cut.png"));
        ImageIcon iconCopy = new ImageIcon(getClass().getResource("/img/copy.png"));
        ImageIcon iconPaste = new ImageIcon(getClass().getResource("/img/paste.png"));
        //menu item
        itmNew = new JMenuItem("New", iconNew);
        itmOpen = new JMenuItem("Open", iconOpen);
        itmSave = new JMenuItem("Save", iconSave);
        itmSaveas = new JMenuItem("Save As...", iconSaveAs);
        itmExit = new JMenuItem("Exit", iconExit);
        
        itmCut = new JMenuItem("Cut");
        itmCopy = new JMenuItem("Copy", iconCopy);
        itmPaste = new JMenuItem("paste", iconPaste);
        itmFind = new JMenuItem("Find", iconFind);
        itmReplace = new JMenuItem("Replace", iconReplace);
        itmFont = new JMenuItem("Font");
        
        wordWrap = new JCheckBoxMenuItem("Word Wrap");
        itmFontColor = new JMenuItem("Font Color");
        //adding shortcut
        itmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        itmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        //add menu item to menu
        mnuFile.add(itmNew);
        mnuFile.add(itmOpen);
        mnuFile.add(itmSave);
        mnuFile.add(itmSaveas);
        mnuFile.addSeparator();
        mnuFile.add(itmExit);
        
        mnuEdit.add(undoAction);
        mnuEdit.add(redoAction);
        mnuEdit.add(itmCut);
        mnuEdit.add(itmCopy);
        mnuEdit.add(itmPaste);
        mnuEdit.add(itmFind);
        mnuEdit.add(itmReplace);
        mnuEdit.add(itmFont);
        
        mnuFormat.add(wordWrap);
        mnuFormat.add(itmFontColor);
        //add menu to menu bar
        mnuBar.add(mnuFile);
        mnuBar.add(mnuEdit);
        mnuBar.add(mnuFormat);
        mnuBar.add(mnuHelp);
        //menu bar add to frame
        setJMenuBar(mnuBar);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void save() {
        PrintWriter fout = null;
        try {
            if (filename == null) {
                saveAs();
            } else {
                fout = new PrintWriter(new FileWriter(filename));
                String s = mainArea.getText();
                StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                while (st.hasMoreElements()) {
                    fout.println(st.nextToken());
                }
                JOptionPane.showMessageDialog(rootPane, "File Saved");
                fileContent = mainArea.getText();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                fout.close();
            }
        }
    }
    
    private void saveAs() {
        PrintWriter fout = null;
        int retval = -1;
        try {
            retval = fc.showSaveDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                
                if (fc.getSelectedFile().exists()) {
                    int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to replace this file",
                            "Confirmation", JOptionPane.OK_CANCEL_OPTION);
                    
                    if (option == 0) {
                        fout = new PrintWriter(new FileWriter(fc.getSelectedFile()));
                        String s = mainArea.getText();
                        StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                        while (st.hasMoreElements()) {
                            fout.println(st.nextToken());
                        }
                        JOptionPane.showMessageDialog(rootPane, "File Saved");
                        fileContent = mainArea.getText();
                        filename = fc.getSelectedFile().getName();
                        setTitle(filename = fc.getSelectedFile().getName());
                    } else {
                        saveAs();
                    }
                } else {
                    fout = new PrintWriter(new FileWriter(fc.getSelectedFile()));
                    
                    String s = mainArea.getText();
                    StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                    while (st.hasMoreElements()) {
                        fout.println(st.nextToken());
                    }
                    JOptionPane.showMessageDialog(rootPane, "File Saved");
                    fileContent = mainArea.getText();
                    filename = fc.getSelectedFile().getName();
                    setTitle(filename = fc.getSelectedFile().getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                fout.close();
            }
        }
    }
    
    private void open() {
        try {
            int retval = fc.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                mainArea.setText(null);
                Reader in = new FileReader(fc.getSelectedFile());
                char[] buffer = new char[1000];
                int n;
                while ((n = in.read(buffer, 0, buffer.length)) != -1) {
                    mainArea.append(new String(buffer, 0, n));
                }
                filename = fc.getSelectedFile().getName();
                setTitle(filename = fc.getSelectedFile().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void open_new() {
        if (!mainArea.getText().equals("") && !mainArea.getText().equals(fileContent)) {
            if (filename == null) {
                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to Change.?");
                if (option == 0) {
                    saveAs();
                    clear();
                } else if (option == 2) {
                    
                } else {
                    clear();
                }
            } else {
                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to Change.?");
                if (option == 0) {
                    save();
                    clear();
                } else if (option == 2) {
                } else {
                    clear();
                }
            }
        } else {
            clear();
        }
    }
    
    private void clear() {
        filename = null;
        mainArea.setText(null);
        setTitle("Untitle Notepade");
        fileContent = null;
    }
    
    class UndoAction extends AbstractAction {
        
        public UndoAction(ImageIcon undoIcon) {
            super("Undo", undoIcon);
            setEnabled(false);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                ex.printStackTrace();
            }
            update();
            redoAction.update();
        }
        
        protected void update() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, "Undo");
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }
    
    class RedoAction extends AbstractAction {
        
        RedoAction(ImageIcon redoIcon) {
            super("Redo", redoIcon);
            setEnabled(false);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                ex.printStackTrace();
            }
            update();
            undoAction.update();
        }
        
        protected void update() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, "Redo");
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }
    
    public static void main(String[] args) {
        JavaNotepad jn = new JavaNotepad();
        jn.setIconImage(new ImageIcon("qsn.png").getImage());
    }
    
    public static JTextArea getArea() {
        return mainArea;
    }
}
