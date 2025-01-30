package org.example.view;

import org.example.controller.Controller;
import org.example.model.Model;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;

public class Viewer extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Canvas canvas;
    private final Controller controller;
    private JTextField jTextField;

    public Viewer() {
        controller = new Controller(this);
        Model model = controller.getModel();
        canvas = new Canvas(model);

        controller.attachCanvasMouseEvents(canvas);

        setTitle("Google Chrome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        createSearchField();
        JScrollPane scrollPane = new JScrollPane(canvas);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    public void update() {
        canvas.repaint();
    }

    private void createSearchField() {
        jTextField = new JTextField();

        jTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        jTextField.setForeground(Color.DARK_GRAY);
        jTextField.setBackground(Color.LIGHT_GRAY);
        jTextField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jTextField.setActionCommand("Search");
        jTextField.addActionListener(controller);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(Color.DARK_GRAY);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setActionCommand("Search");
        searchButton.addActionListener(controller);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout(5, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        searchPanel.add(jTextField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
    }

    public String getSiteUrl() {
        return jTextField.getText();
    }

    public Controller getController() {
        return controller;
    }
}