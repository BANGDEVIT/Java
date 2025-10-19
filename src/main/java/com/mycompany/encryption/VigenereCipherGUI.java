package com.mycompany.encryption;

import javax.swing.*;
import java.awt.*;

public class VigenereCipherGUI extends JFrame {

    private JTextArea plaintextArea, ciphertextArea;
    private JTextField keyField1, keyField2;
    private JTextField keyGenField1, keyGenField2;
    private JButton btnEncrypt, btnDecrypt;
    private VigenereCipher cipher;

    public VigenereCipherGUI() {
        cipher = new VigenereCipher();

        setTitle("Vigenère Cipher");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 400);
        setLocationRelativeTo(null);

        // ======= Tạo giao diện =======
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(230, 232, 235));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.BOTH;

        // Ô nhập Plaintext
        plaintextArea = new JTextArea(6, 30);
        plaintextArea.setLineWrap(true);
        plaintextArea.setWrapStyleWord(true);
        JScrollPane scrollPlain = new JScrollPane(plaintextArea);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollPlain, gbc);

        // Nhãn và ô nhập Key cho mã hóa
        JLabel lblKey1 = new JLabel("Key");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lblKey1, gbc);

        keyField1 = new JTextField(15);
        keyField1.setBackground(Color.WHITE);
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(keyField1, gbc);

        // Ô hiển thị chuỗi khóa sinh ra khi mã hóa
        JLabel lblKeyGen1 = new JLabel("Key generation");
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(lblKeyGen1, gbc);

        keyGenField1 = new JTextField(15);
        keyGenField1.setEditable(false);
        keyGenField1.setBackground(Color.WHITE);
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(keyGenField1, gbc);

        // Nút mã hóa
        btnEncrypt = new JButton("Encrypt");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(btnEncrypt, gbc);

        // Ô hiển thị Ciphertext
        ciphertextArea = new JTextArea(6, 30);
        ciphertextArea.setLineWrap(true);
        ciphertextArea.setWrapStyleWord(true);
        JScrollPane scrollCipher = new JScrollPane(ciphertextArea);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollCipher, gbc);

        // Nhãn và ô nhập Key cho giải mã
        JLabel lblKey2 = new JLabel("Key");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lblKey2, gbc);

        keyField2 = new JTextField(15);
        keyField2.setBackground(Color.WHITE);
        gbc.gridx = 2;
        gbc.gridy = 3;
        panel.add(keyField2, gbc);

        // Ô hiển thị key sinh ra khi giải mã
        JLabel lblKeyGen2 = new JLabel("Key generation");
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(lblKeyGen2, gbc);

        keyGenField2 = new JTextField(15);
        keyGenField2.setEditable(false);
        keyGenField2.setBackground(Color.WHITE);
        gbc.gridx = 2;
        gbc.gridy = 4;
        panel.add(keyGenField2, gbc);

        // Nút giải mã
        btnDecrypt = new JButton("Decrypt");
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(btnDecrypt, gbc);

        setupEventListeners();
        add(panel);
    }

    // ======= Sự kiện cho nút Encrypt / Decrypt =======
    private void setupEventListeners() {
        btnEncrypt.addActionListener(e -> {
            try {
                String plaintext = plaintextArea.getText();
                String key = keyField1.getText();

                // Sinh chuỗi key lặp tương ứng độ dài văn bản
                String keyGen = cipher.generateKeyString(plaintext, key);
                keyGenField1.setText(keyGen);

                // Mã hóa
                String encrypted = cipher.encrypt(plaintext, key);
                ciphertextArea.setText(encrypted);

                keyField2.setText(key);

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDecrypt.addActionListener(e -> {
            try {
                String ciphertext = ciphertextArea.getText();
                String key = keyField2.getText();

                // Sinh chuỗi key tương ứng ciphertext
                String keyGen = cipher.generateKeyString(ciphertext, key);
                keyGenField2.setText(keyGen);

                // Giải mã
                String decrypted = cipher.decrypt(ciphertext, key);
                plaintextArea.setText(decrypted);

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // ======= Lớp xử lý mã hóa Vigenère =======
    public class VigenereCipher {

        // Sinh chuỗi khóa lặp theo độ dài văn bản
        public String generateKeyString(String text, String key) {
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("Key không được để trống!");
            }

            // Chỉ lấy ký tự chữ cái, chuyển về thường
            String cleanKey = key.replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (cleanKey.isEmpty()) {
                throw new IllegalArgumentException("Key phải chứa ít nhất một chữ cái!");
            }

            StringBuilder keyString = new StringBuilder();
            int keyIndex = 0;
            int keyLen = cleanKey.length();

            // Tạo chuỗi khóa theo số lượng ký tự chữ và khoảng trắng
            for (char c : text.toCharArray()) {
                if (Character.isLetter(c) || c == ' ') {
                    keyString.append(cleanKey.charAt(keyIndex % keyLen));
                    keyIndex++;
                }
            }

            return keyString.toString();
        }

        // ======= Hàm mã hóa =======
        public String encrypt(String plaintext, String key) {
            if (plaintext == null || plaintext.trim().isEmpty()) {
                throw new IllegalArgumentException("Plaintext không được để trống!");
            }

            String keyString = generateKeyString(plaintext, key);
            StringBuilder ciphertext = new StringBuilder();
            int keyIndex = 0;

            for (char c : plaintext.toCharArray()) {
                if (Character.isLetter(c)) {
                    // Mã hóa ký tự chữ cái (chuẩn Vigenère)
                    boolean isUpper = Character.isUpperCase(c);
                    char base = isUpper ? 'A' : 'a';
                    int plainValue = Character.toLowerCase(c) - 'a';
                    int keyValue = keyString.charAt(keyIndex) - 'a';
                    int cipherValue = (plainValue + keyValue) % 26;
                    char encryptedChar = (char) (cipherValue + base);
                    ciphertext.append(encryptedChar);
                    keyIndex++;
                } else if (c == ' ') {
                    // Mã hóa khoảng trắng — coi ' ' là giá trị 26 (thêm vào bảng chữ cái 27 ký tự)
                    int plainValue = 26;
                    int keyValue = keyString.charAt(keyIndex) - 'a';
                    int cipherValue = (plainValue + keyValue) % 27;

                    // Nếu kết quả vẫn là 26 → giữ nguyên khoảng trắng
                    if (cipherValue == 26) {
                        ciphertext.append(' ');
                    } else {
                        // Ngược lại, chuyển về chữ cái
                        ciphertext.append((char) ('a' + cipherValue));
                    }
                    keyIndex++;
                } else {
                    // Ký tự đặc biệt (số, dấu câu...) giữ nguyên
                    ciphertext.append(c);
                }
            }

            return ciphertext.toString();
        }

        // ======= Hàm giải mã =======
        public String decrypt(String ciphertext, String key) {
            if (ciphertext == null || ciphertext.trim().isEmpty()) {
                throw new IllegalArgumentException("Ciphertext không được để trống!");
            }

            String keyString = generateKeyString(ciphertext, key);
            StringBuilder plaintext = new StringBuilder();
            int keyIndex = 0;

            for (char c : ciphertext.toCharArray()) {
                boolean isUpper = Character.isUpperCase(c);
                char base = isUpper ? 'A' : 'a';

                // Chuyển ký tự về giá trị số (a = 0)
                int cipherValue = Character.toLowerCase(c) - 'a';
                int keyValue = keyString.charAt(keyIndex) - 'a';

                // Giải mã modulo 26
                int plainValue = (cipherValue - keyValue + 26) % 26;
                char decryptedChar = (char) (plainValue + base);
                plaintext.append(decryptedChar);
                keyIndex++;
            }
            return plaintext.toString();
        }
    }

    // ======= Hàm main =======
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new VigenereCipherGUI().setVisible(true);
        });
    }
}
