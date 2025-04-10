package com.aihb.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.utils.PdfMerger;
//import org.apache.commons.collections.CollectionUtils;
import cn.hutool.core.io.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author tanzsay
 * @date 2023/10/7 16:45
 * @desc
 */
public class PdfTool {

    public static void main(String[] args) {
        merge("D:\\Desktop\\test");
    }

    public static void merge(String filepath) {
        List<File> dirFiles = Arrays.asList(FileUtil.ls(filepath));    /*FileTool.getDirFiles(filepath)*/
        if (dirFiles.size() == 0/*CollectionUtils.isEmpty(dirFiles)*/) {
            return;
        }
        try {
            mergerPdf(dirFiles, filepath + ".pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 敲黑板，重点在这里 */
    private static void mergerPdf(List<File> files, String outputName) throws IOException {
        // 创建要合并的pdf
        PdfDocument mergedPdfDoc = new PdfDocument(new PdfWriter(outputName));
        // 使用 PdfMerger 合并
        PdfMerger pdfMerger = new PdfMerger(mergedPdfDoc);
        // 添加大纲
        PdfOutline outline = mergedPdfDoc.getOutlines(false);
        int i = 1;
        for (File file : files) {
            // 读取要合并的pdf文件
            PdfDocument sourcePdfDoc = new PdfDocument(new PdfReader(file));
            // 合并
            pdfMerger.merge(sourcePdfDoc, 1, sourcePdfDoc.getNumberOfPages());

            // 将文件名设置为章节，在合并pdf的第一页添加跳转动作
            outline.addOutline(file.getName())
                    .addAction(PdfAction.createGoTo(PdfExplicitDestination.createFit(mergedPdfDoc.getPage(i))));
            // 保证添加大纲的位置正确
            i = i + sourcePdfDoc.getNumberOfPages();

            sourcePdfDoc.close();
        }
        pdfMerger.close();
    }
}