package com.tasl; /**
 * Created by tvarnavskaya on 05.09.2016.
 */
import java.awt.Component;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class SpringDemo {
    public static void main(String args[]) {
        JFrame frame = new JFrame("SpringLayout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane();

        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);

        Component loginLabel = new JLabel("Login");
        Component passwordLabel = new JLabel("Password");
        Component loginTextField = new JTextField(15);
        Component passwordTextField = new JTextField(15);

        contentPane.add(loginLabel);
        contentPane.add(loginTextField);
        contentPane.add(passwordLabel);
        contentPane.add(passwordTextField);

        layout.putConstraint(SpringLayout.WEST, loginLabel, 35, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.NORTH, loginLabel, 28, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.NORTH, loginTextField, 0, SpringLayout.NORTH, loginLabel);
        layout.putConstraint(SpringLayout.WEST, loginTextField, 35, SpringLayout.EAST, loginLabel);

        layout.putConstraint(SpringLayout.WEST, passwordLabel, 0, SpringLayout.WEST, loginLabel);
        layout.putConstraint(SpringLayout.NORTH, passwordLabel, 30, SpringLayout.NORTH, loginLabel);
        layout.putConstraint(SpringLayout.NORTH, passwordTextField, 0, SpringLayout.NORTH, passwordLabel);
        layout.putConstraint(SpringLayout.WEST, passwordTextField, 0, SpringLayout.WEST, loginTextField);

        frame.setSize(300, 140);
        frame.setVisible(true);
    }
}
