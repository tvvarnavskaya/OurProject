package com.tasl.client;

/**
 * Created by slaix on 04.09.2016.
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tasl.service.SendLoginService;
import com.tasl.service.SendLoginServiceImpl;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import javax.swing.*;

public class WindowCommunication {

    private static void createAndShowUI() {
        JFrame frame = new JFrame("WindowCommunication");
        MainPanel mainPanel = new MainPanel();
        frame.getContentPane().add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(500, 500));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        mainPanel.openLoginDialog();
    }

    // let's be sure to start Swing on the Swing event thread
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI();
            }
        });
    }
}

class MainPanel extends JPanel {
    private JTextField field = new JTextField(30);
//    private JButton openLoginDialog = new JButton("login");

    // here my main gui has a reference to the JDialog and to the
    // MyDialogPanel which is displayed in the JDialog
    private MyDialogPanel dialogPanel = new MyDialogPanel();
    private JDialog dialog;

    public MainPanel() {
        field.setEditable(false);
        field.setFocusable(false);
        add(field);
    }

    public void openLoginDialog() {
        // lazy creation of the JDialog
        if (dialog == null) {
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win != null) {
                dialog = new JDialog(win, "Login", ModalityType.APPLICATION_MODAL);
                dialog.getContentPane().add(dialogPanel);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
            }
        }
        dialog.setVisible(true); // here the modal dialog takes over

        // this line starts *after* the modal dialog has been disposed
        // **** here's the key where I get the String from JTextField in the GUI held
        // by the JDialog and put it into this GUI's JTextField.
        field.setText(dialogPanel.getLoginText());
    }
}

class MyDialogPanel extends JPanel {
    private JTextField loginField = new JTextField(30);
    private JPasswordField passwordField = new JPasswordField();
    private JButton okButton = new JButton("Login");
    private JLabel loginLabel = new JLabel("Login: ");
    private JLabel passwordLabel = new JLabel("Password: ");

    public MyDialogPanel() {
        okButton.addActionListener(event ->
                okButtonAction()
        );
        okButton.setEnabled(false);
        GridBagLayout gridLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        gridLayout.setConstraints(loginLabel, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        gridLayout.setConstraints(loginField, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        gridLayout.setConstraints(passwordLabel, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 1;
        gridLayout.setConstraints(passwordField, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridwidth = 3;
        constraints.gridy = 2;
        gridLayout.setConstraints(okButton, constraints);
        setLayout(gridLayout);
        add(loginLabel);
        add(loginField);
        add(passwordLabel);
        add(passwordField);
        add(okButton);
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (getLoginText() != null && !getLoginText().equals("")
                        && getPassword() != null && getPassword().length > 0) {
                    okButton.setEnabled(true);
                } else {
                    okButton.setEnabled(false);
                }
                if (getPassword() == null || getPassword().length == 0) {
                    okButton.setEnabled(false);
                }
            }
        });

        loginField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (getLoginText() != null && !getLoginText().equals("")
                        && getPassword() != null && getPassword().length > 0) {
                    okButton.setEnabled(true);
                } else {
                    okButton.setEnabled(false);
                }
                if (getLoginText() == null || getLoginText().equals("")) {
                    okButton.setEnabled(false);
                }
            }
        });
    }

    // to allow outside classes to get the text held by the JTextField
    public String getLoginText() {
        return loginField.getText();
    }

    private char[] getPassword() {
        return passwordField.getPassword();
    }

    // This button's action is simply to dispose of the JDialog.
    private void okButtonAction() {
        //send login and password to server
        String userName = getLoginText();
        char[] password = getPassword();

        System.out.println("userName="+userName);
        System.out.println("password="+ Arrays.toString(password));

        SendLoginService sendLoginService = new SendLoginServiceImpl();
        boolean isValid = false;
        try {
            isValid = sendLoginService.sendLogin(userName, "");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (isValid) {
            // win is here the JDialog that holds this JPanel, but it could be a JFrame or
            // any other top-level container that is holding this JPanel
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win != null) {
                win.dispose();
            }
        }
    }
}
