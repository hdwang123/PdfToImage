package org.example.pdftoimage;

public enum DpiLevelEnum {
    LOW(72),       // 低质量打印
    MIDDLE(150),  // 标准打印质量
    HIGH(300);      // 高质量打印

    private final int dpi;
    DpiLevelEnum(int dpi) {
        this.dpi = dpi;
    }

    public int getDpi() {
        return dpi;
    }

}
