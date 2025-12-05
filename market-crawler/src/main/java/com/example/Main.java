package com.example;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            MainWindow stock = new MainWindow();
            stock.startFetching();

            ExchangeWindow fx = new ExchangeWindow();
            fx.startFetching();
        });
    }
}
