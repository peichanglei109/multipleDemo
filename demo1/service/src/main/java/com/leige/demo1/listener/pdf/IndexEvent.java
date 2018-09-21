package com.leige.demo1.listener.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * 设置页面
 *
 * @author peichanglei
 * @date 2018/9/21 14:49
 */
public class IndexEvent extends PdfPageEventHelper {
    private int page;
    private boolean body;

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        if (body) {
            page++;
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(page + ""),
                    (document.left() + document.right()) / 2, document.bottom() - 40, 0);
        }
    }

    public int getPage() {
        return page;
    }

    public boolean isBody() {
        return body;
    }

    public void setBody(boolean body) {
        this.body = body;
    }

    public void setPage(int page) {

        this.page = page;
    }
}
