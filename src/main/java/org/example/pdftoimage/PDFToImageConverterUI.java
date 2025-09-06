package org.example.pdftoimage;

import org.apache.commons.lang3.StringUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class PDFToImageConverterUI extends JFrame {

    private JTextField pdfFilePathField;
    private JTextField outputDirField;
    private JRadioButton multipleImagesRadioButton;
    private JRadioButton singleLongImageRadioButton;
    private ButtonGroup qualityRadioGroup;
    private JButton convertButton;

    private JLabel progressLabel;
    private JProgressBar progressBar;

    private PDFToImageConverter converter;

    public PDFToImageConverterUI() {

        // 设置窗口标题
        setTitle("PDF转换为图片");

        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 使用 BoxLayout 垂直排列组件
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // 创建字体对象，设置较大的字体
        Font largeFont = new Font("微软雅黑", Font.PLAIN, 16);

        // 增加一个高度
        add(Box.createVerticalStrut(10));

        // 第一行：PDF 文件选择区域
        JPanel pdfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel pdfLabel = new JLabel("PDF文件：");
        pdfLabel.setFont(largeFont);
        pdfPanel.add(pdfLabel);
        pdfFilePathField = new JTextField(30);
        pdfFilePathField.setFont(largeFont);
        pdfPanel.add(pdfFilePathField);
        JButton selectPdfButton = new JButton("选择");
        selectPdfButton.setFont(largeFont);
        selectPdfButton.setPreferredSize(new Dimension(70, 30)); // 设置按钮大小
        selectPdfButton.addActionListener(this::onSelectPdfFile);
        pdfPanel.add(selectPdfButton);
        add(pdfPanel);
        add(Box.createVerticalStrut(2)); // 增加行之间的间距

        // 第二行：输出目录选择区域
        JPanel outputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel outputLabel = new JLabel("输出目录：");
        outputLabel.setFont(largeFont);
        outputPanel.add(outputLabel);
        outputDirField = new JTextField(30);
        outputDirField.setFont(largeFont);
        outputPanel.add(outputDirField);
        JButton selectOutputButton = new JButton("选择");
        selectOutputButton.setFont(largeFont);
        selectOutputButton.setPreferredSize(new Dimension(70, 30)); // 设置按钮大小
        selectOutputButton.addActionListener(this::onSelectOutputDir);
        outputPanel.add(selectOutputButton);
        add(outputPanel);
        add(Box.createVerticalStrut(2)); // 增加行之间的间距

        // 第三行：是否合成长图选择区域: 选择“多图”或“单张长图”
        JPanel imageTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel imageTypeLabel = new JLabel("输出方式：");
        imageTypeLabel.setFont(largeFont);
        imageTypePanel.add(imageTypeLabel);

        // 创建单选按钮
        multipleImagesRadioButton = new JRadioButton("多张图片");
        multipleImagesRadioButton.setFont(largeFont);

        singleLongImageRadioButton = new JRadioButton("单张长图");
        singleLongImageRadioButton.setFont(largeFont);
        singleLongImageRadioButton.setSelected(true); // 默认选择

        // 将这两个单选按钮放入ButtonGroup中，确保只能选择一个
        ButtonGroup group = new ButtonGroup();
        group.add(multipleImagesRadioButton);
        group.add(singleLongImageRadioButton);

        imageTypePanel.add(multipleImagesRadioButton);
        imageTypePanel.add(singleLongImageRadioButton);
        add(imageTypePanel);
        add(Box.createVerticalStrut(10)); // 增加行之间的间距

        // 第三行：选择图片质量
        JPanel imageQualityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel imageQualityLabel = new JLabel("图片质量：");
        imageQualityLabel.setFont(largeFont);
        imageQualityPanel.add(imageQualityLabel);

        // 创建单选按钮
        JRadioButton lowQualityRadioButton = new JRadioButton("低(72DPI)");
        lowQualityRadioButton.setFont(largeFont);
        lowQualityRadioButton.setActionCommand("LOW");

        JRadioButton middleQualityRadioButton = new JRadioButton("中(150DPI)");
        middleQualityRadioButton.setFont(largeFont);
        middleQualityRadioButton.setActionCommand("MIDDLE");
        middleQualityRadioButton.setSelected(true); // 默认选择


        JRadioButton highQualityRadioButton = new JRadioButton("高(300DPI)");
        highQualityRadioButton.setFont(largeFont);
        highQualityRadioButton.setActionCommand("HIGH");

        // 将这两个单选按钮放入ButtonGroup中，确保只能选择一个
        qualityRadioGroup = new ButtonGroup();
        qualityRadioGroup.add(lowQualityRadioButton);
        qualityRadioGroup.add(middleQualityRadioButton);
        qualityRadioGroup.add(highQualityRadioButton);

        imageQualityPanel.add(lowQualityRadioButton);
        imageQualityPanel.add(middleQualityRadioButton);
        imageQualityPanel.add(highQualityRadioButton);
        add(imageQualityPanel);
        add(Box.createVerticalStrut(10)); // 增加行之间的间距

        // 第五行：配置进度条
        JPanel progressLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        progressLabel = new JLabel("转换进度");
        progressLabel.setForeground(Color.RED);
        progressLabel.setFont(largeFont);
        progressLabelPanel.add(progressLabel);
        add(progressLabelPanel);
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        add(progressBar);

        // 第六行：转换按钮
        JPanel convertPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        convertButton = new JButton("转换");
        convertButton.setFont(largeFont);
        convertButton.setPreferredSize(new Dimension(180, 50)); // 设置按钮大小
        convertButton.addActionListener(this::onConvertButtonClicked);
        convertPanel.add(convertButton);
        add(convertPanel);

        // 设置窗口大小并显示
        setSize(680, 400);
        setLocationRelativeTo(null); // 窗口居中显示
        setResizable(false);  // 不允许调整大小
        setVisible(true);

        // 初始化转换器对象
        converter = new PDFToImageConverter(progressLabel, progressBar);
    }

    // 选择 PDF 文件
    private void onSelectPdfFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // 设置默认目录为桌面
        String userHome = System.getProperty("user.home");
        File desktopDir = new File(userHome, "Desktop");
        fileChooser.setCurrentDirectory(desktopDir);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            pdfFilePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    // 选择输出目录
    private void onSelectOutputDir(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // 设置默认目录为桌面
        String userHome = System.getProperty("user.home");
        File desktopDir = new File(userHome, "Desktop");
        fileChooser.setCurrentDirectory(desktopDir);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();
            outputDirField.setText(selectedDir.getAbsolutePath());
        }
    }

    // 转换按钮点击事件
    private void onConvertButtonClicked(ActionEvent e) {
        String pdfFilePath = pdfFilePathField.getText();
        String outputDirPath = outputDirField.getText();
        if (StringUtils.isBlank(pdfFilePath)) {
            JOptionPane.showMessageDialog(this, "PDF文件不能为空!");
            return;
        }
        if (StringUtils.isBlank(outputDirPath)) {
            JOptionPane.showMessageDialog(this, "输出目录不能为空!");
            return;
        }

        //输出方式
        boolean outputSingleLongImage = singleLongImageRadioButton.isSelected();
        //图片质量
        String qualityCommand = qualityRadioGroup.getSelection().getActionCommand();
        int dpi = DpiLevelEnum.valueOf(qualityCommand).getDpi();

        // 执行转换:异步线程处理，防止UI卡死
        new Thread(() -> {
            try {
                // 调用转换器进行转换
                converter.convertPDFToImage(pdfFilePath, outputDirPath, outputSingleLongImage, dpi);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "转换失败：" + ex.getMessage());
            }
        }).start();

    }
}
