package com.example;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainWindow extends JFrame {

    private JTextField inputField;
    private JButton addButton;
    private JButton removeButton;
    private JButton saveChartButton;
    private JButton saveCsvButton;

    private final List<String> symbols = new ArrayList<>();
    private final Map<String, List<Double>> priceHistory = new ConcurrentHashMap<>();
    private final Map<String, String> displayNames = new ConcurrentHashMap<>();

    private final ChartUtil chartUtil = new ChartUtil();

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> symbolList = new JList<>(listModel);

    public MainWindow() {

        setTitle("Realtime Market Viewer");
        setSize(1000, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ----------------- 상단 입력 -----------------
        JPanel top = new JPanel();
        inputField = new JTextField(20);
        addButton = new JButton("종목 추가");
        removeButton = new JButton("종목 삭제");

        top.add(new JLabel("종목명/코드 입력:"));
        top.add(inputField);
        top.add(addButton);
        top.add(removeButton);

        add(top, BorderLayout.NORTH);

        addButton.addActionListener(e -> addSymbol());
        inputField.addActionListener(e -> addSymbol());
        removeButton.addActionListener(e -> removeSymbol());

        // ----------------- 좌측 리스트 -----------------
        JScrollPane scroll = new JScrollPane(symbolList);
        scroll.setPreferredSize(new java.awt.Dimension(180, 0));
        add(scroll, BorderLayout.WEST);

        // ----------------- 중앙 차트 -----------------
        add(chartUtil.getChartPanel(), BorderLayout.CENTER);

        // ----------------- 하단 저장 버튼 -----------------
        JPanel bottom = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        saveChartButton = new JButton("차트 이미지 저장");
        saveCsvButton = new JButton("CSV 저장");

        bottom.add(saveChartButton);
        bottom.add(saveCsvButton);

        saveChartButton.addActionListener(e -> chartUtil.saveChartImage());
        saveCsvButton.addActionListener(e -> CsvWriter.saveAll(priceHistory));

        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addSymbol() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "추가에 실패하였습니다. 다시 입력해주세요.",
                    "추가 실패",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String symbol = SymbolSearch.searchSymbol(input);

        if (symbol == null) {
            JOptionPane.showMessageDialog(this,
                    "추가에 실패하였습니다. 다시 입력해주세요.",
                    "추가 실패",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (symbols.contains(symbol)) {
            JOptionPane.showMessageDialog(this,
                    "이미 추가된 종목입니다.",
                    "중복 종목",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 정상 추가 처리
        symbols.add(symbol);
        priceHistory.put(symbol, new ArrayList<>());
        displayNames.put(symbol, input);
        listModel.addElement(input + " (" + symbol + ")");

        chartUtil.addSeries(symbol, input);
        inputField.setText("");

        //성공 메시지 팝업
        JOptionPane.showMessageDialog(this,
                "종목이 추가되었습니다.",
                "추가 성공",
                JOptionPane.INFORMATION_MESSAGE);
    }


    private void removeSymbol() {

        String selected = symbolList.getSelectedValue();

        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "삭제할 종목을 선택해주세요.",
                    "삭제 실패",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String symbol = null;
        for (String s : symbols) {
            if (selected.contains(s)) {
                symbol = s;
                break;
            }
        }

        if (symbol == null) {
            JOptionPane.showMessageDialog(this,
                    "삭제에 실패하였습니다.",
                    "삭제 실패",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        symbols.remove(symbol);
        priceHistory.remove(symbol);
        displayNames.remove(symbol);
        listModel.removeElement(selected);
        chartUtil.removeSeries(symbol);

        //삭제 성공 메세지 팝업
        JOptionPane.showMessageDialog(this,
                "종목이 삭제되었습니다.",
                "삭제 성공",
                JOptionPane.INFORMATION_MESSAGE);
    }


    public void startFetching() {
        FetcherThread th = new FetcherThread(symbols, priceHistory, displayNames, chartUtil);
        th.setDaemon(true);
        th.start();
    }
}
