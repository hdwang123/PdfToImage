package org.example.pdftoimage;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PDFToImageConverter {

    private JLabel progressLabel;
    private JProgressBar progressBar;

    /**
     * 是否UI程序在调用
     */
    private boolean ui = false;

    PDFToImageConverter() {
    }

    PDFToImageConverter(JLabel progressLabel, JProgressBar progressBar) {
        this.progressLabel = progressLabel;
        this.progressBar = progressBar;
        this.ui = true;
    }

    /**
     * 入口方法，用于处理 PDF 转换的操作
     *
     * @param pdfPath     输入的 PDF 文件路径
     * @param outputDir   输出目录
     * @param isLongImage 是否生成单张长图
     * @param dpi         图像的DPI分辨率
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void convertPDFToImage(String pdfPath, String outputDir, boolean isLongImage, int dpi) throws Exception {
        // 更新进度
        updateProgress("转换开始", null);

        // 调用 PDF 转换为图像的方法
        List<BufferedImage> images = convertPdfToImages(pdfPath, dpi);

        // 根据是否是长图的选项进行处理
        String fileName = pdfPath.substring(pdfPath.lastIndexOf(File.separator) + 1, pdfPath.lastIndexOf("."));
        processImages(images, outputDir, isLongImage, fileName);

        // 更新进度
        updateProgress("转换完成", null);
    }

    /**
     * 处理 PDF 转换为图像
     *
     * @param images      要处理的图片列表
     * @param outputDir   输出目录
     * @param isLongImage 是否生成单张长图
     * @param fileName    文件名
     * @throws IOException
     */
    private void processImages(List<BufferedImage> images, String outputDir, boolean isLongImage, String fileName) throws IOException {
        // 判断是否需要拼接成单张长图
        if (isLongImage) {
            BufferedImage longImage = createLongImage(images);
            String outFileName = fileName + ".png";
            saveImage(longImage, outputDir, outFileName);
        } else {
            // 否则保存为多张图
            for (int i = 0; i < images.size(); i++) {
                String outFileName = fileName + "_" + (i + 1) + ".png";
                saveImage(images.get(i), outputDir, outFileName);
            }
        }
    }

    /**
     * 将 PDF 转换为图像
     *
     * @param pdfPath PDF 文件路径
     * @param dpi     图像的DPI分辨率
     * @return 返回每页的 BufferedImage 列表
     * @throws IOException          如果读取 PDF 文件时发生错误
     * @throws InterruptedException 如果线程执行被中断
     * @throws ExecutionException   如果任务执行失败
     */
    private List<BufferedImage> convertPdfToImages(String pdfPath, int dpi) throws IOException, InterruptedException, ExecutionException {
        try (PDDocument document = Loader.loadPDF(new File(pdfPath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            // 获取页面总数
            int totalPages = document.getNumberOfPages();

            // 设置进度条最大值为总页数
            if (ui) {
                progressBar.setMaximum(totalPages);
                progressBar.setValue(0);
            }

            // 每页生成一张图片
            List<BufferedImage> images = new ArrayList<>();
            for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
                // 渲染每一页为图片
                int page = pageIndex + 1;

                // 更新进度
                updateProgress("正在转换第" + page + "页", page);

                BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, dpi);
                images.add(image);
            }
            return images;
        }
    }

    /**
     * 将多张图片拼接成单张长图
     *
     * @param images 要拼接的图片列表
     * @return 拼接后的长图
     */
    private BufferedImage createLongImage(List<BufferedImage> images) {
        // 更新进度
        updateProgress("正在拼图", null);

        // 计算拼接后的长图宽度和高度
        int totalWidth = images.get(0).getWidth();
        int totalHeight = images.stream().mapToInt(BufferedImage::getHeight).sum();

        // 创建一张新的图像用于拼接
        BufferedImage longImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        int yOffset = 0;

        // 按照顺序将每一张图像拼接到新的长图中
        for (BufferedImage image : images) {
            longImage.getGraphics().drawImage(image, 0, yOffset, null);
            yOffset += image.getHeight();  // 更新y偏移量
        }

        // 更新进度
        updateProgress("拼图完成", null);
        return longImage;
    }

    /**
     * 保存图片到指定目录
     *
     * @param image       要保存的图片
     * @param outputPath  输出路径
     * @param outFileName 输出文件名
     * @throws IOException 如果保存图片时发生错误
     */
    private void saveImage(BufferedImage image, String outputPath, String outFileName) throws IOException {
        updateProgress("正在保存图片:" + outFileName, null);
        String targetFilePath = outputPath + File.separator + outFileName;
        ImageIO.write(image, "PNG", new File(targetFilePath));
        updateProgress("保存图片完成:" + outFileName, null);
    }

    /**
     * 更新进度
     *
     * @param text     进度提示
     * @param progress 进度值
     */
    private void updateProgress(String text, Integer progress) {
        if (ui) {
            SwingUtilities.invokeLater(() -> {
                progressLabel.setText(text);
                if (progress != null) {
                    progressBar.setValue(progress);
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        PDFToImageConverter converter = new PDFToImageConverter();
        converter.convertPDFToImage("src\\main\\resources\\input.pdf", "output", false, 150);
    }
}
