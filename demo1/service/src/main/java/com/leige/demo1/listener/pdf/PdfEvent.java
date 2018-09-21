package com.leige.demo1.listener.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 页码监听
 *
 * @author peichanglei
 * @date 2018/9/21 14:46
 */
public class PdfEvent extends PdfPageEventHelper {
    private int page;
    private Map<String, Integer> index = new LinkedHashMap<>();

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        page++;
    }

    @Override
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
        index.put(title.getContent(), page);
    }

    @Override
    public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {
        onChapter(writer, document, paragraphPosition, title);
    }

    public int getPage() {
        return page;
    }

    public Map<String, Integer> getIndex() {
        return index;
    }
}
