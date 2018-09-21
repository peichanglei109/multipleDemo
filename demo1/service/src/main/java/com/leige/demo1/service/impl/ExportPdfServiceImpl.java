package com.leige.demo1.service.impl;

import com.google.common.collect.Lists;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.leige.demo1.ModelVo.ClauseVo;
import com.leige.demo1.constants.CatalogueAndFontEnum;
import com.leige.demo1.listener.pdf.IndexEvent;
import com.leige.demo1.listener.pdf.PdfEvent;
import com.leige.demo1.model.ControlRequirement;
import com.leige.demo1.service.ExportPdfService;
import com.leige.demo1.utils.DateUtils;
import com.leige.demo1.utils.PdfUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 导出PDF
 *
 * @author peichanglei
 * @date 2018/9/21 14:33
 */
@Service
public class ExportPdfServiceImpl implements ExportPdfService {


    /**
     * 导出PDF
     */
    @Override
    public void exportPdf() {
        System.out.println("开始导出=-----------");
        List<ClauseVo> clauseVoList = initData();
        pdfAllClause(clauseVoList, "测试 - " + DateUtils.getTimeStringToDay(System.currentTimeMillis()));
    }

    private void pdfAllClause(List<ClauseVo> clauseList, String pdfName) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        Document doc = new Document(PageSize.A4, 48, 48, 60, 65);
        PdfWriter contentWriter = null;
        try {
//                contentWriter = PdfWriter.getInstance(doc, response.getOutputStream());
            contentWriter = PdfWriter.getInstance(doc, new ByteArrayOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PdfEvent event = new PdfEvent();
        contentWriter.setPageEvent(event);
        doc.open();
        List<Chapter> chapterList = new ArrayList<>();
        //递归生成PDF
        try {
            producePDF(clauseList, doc, chapterList);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        doc.close();

        Document document = new Document(PageSize.A4, 48, 48, 60, 65);

        //    设置页面编码格式
        response.setContentType("application/vnd.ms-pdf");
        response.setContentType("text/plain;charaset=utf-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((pdfName + DateUtils.getTimeStringToDay(System.currentTimeMillis())).getBytes(), "ISO-8859-1") + ".pdf");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            ServletOutputStream os = response.getOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, os);
            IndexEvent indexEvent = new IndexEvent();
            writer.setPageEvent(indexEvent);
            document.open();

            //添加章节目录
            Paragraph paragraph1 = new Paragraph("目录", PdfUtils.setFont(16f, BaseColor.BLACK, false));
            paragraph1.setAlignment(Element.ALIGN_CENTER);
            Chapter indexChapter = new Chapter(paragraph1, 0);
            // 设置数字深度
            indexChapter.setNumberDepth(-1);
            //设置目录的层级缩进
            PdfUtils.setCatalogueLevel(event, indexChapter);
            //添加文档首页信息
            addDocumentInfo(document, pdfName);
            document.add(indexChapter);

            //添加内容
            document.newPage();
            for (Chapter c : chapterList) {
                indexEvent.setBody(true);
                document.add(c);
            }
            document.close();
            os.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private void producePDF(List<ClauseVo> clauseList, Document doc, List<Chapter> chapterList) throws DocumentException {
        //根据chapter章节分页,第一层条款
        for (int i = 0; i < clauseList.size(); i++) {
            //点点转化
            //String clauseContent = converPointToSign2(clauseList.get(i).getClauseContent());
            String clauseContent = clauseList.get(i).getContent();
            Chapter chapter = new Chapter(new Paragraph(PdfUtils.subStringName(clauseContent), PdfUtils.setFont(CatalogueAndFontEnum.ONE.getMessage(), BaseColor.BLACK, true)), i + 1);
            //增加条款正文
            addClauseTable(chapter, null, clauseList.get(i), PdfUtils.setFont(CatalogueAndFontEnum.CONTENT.getMessage(), BaseColor.BLACK, true));
            List<ClauseVo> clauseList2 = clauseList.get(i).getClauseList();
            //包含的多层条款
            if (clauseList2 != null && clauseList2.size() > 0) {
                addNumberTow(clauseList2, chapter, 2);
            }
            //每条控制要求
            List<ControlRequirement> controlRequirementList = clauseList.get(i).getControlRequirementList();
            addControlRequirement(controlRequirementList, chapter, null, 2, clauseList.get(i));
            doc.add(chapter);
            chapterList.add(chapter);
        }

    }

    /**
     * 增加第二层,若有子条款
     */
    private void addNumberTow(List<ClauseVo> clauseList, Section chapter, int lever) {
        //lever 记录是第几级循环 lever,即lever=目录级别  controlLever:控制要求的级别
        int controlLever = lever;
        for (int i = 0; i < clauseList.size(); i++) {
            float fontSize = 11f;
            for (CatalogueAndFontEnum catalogueAndFontEnum : CatalogueAndFontEnum.values()) {
                if (catalogueAndFontEnum.getName() == lever) {
                    fontSize = catalogueAndFontEnum.getMessage();
                }
            }
            //!!!去掉目录中带的回车,会导致目录展示错误
            String clauseContent = clauseList.get(i).getContent();
            if (clauseContent != null && clauseContent.contains("\n")) {
                clauseContent = clauseContent.replace("\n", "");
            }
            //点点转化
            clauseContent = PdfUtils.converPointToSign2(clauseContent);
            Section section = chapter.addSection(new Paragraph(PdfUtils.subStringName(clauseContent), PdfUtils.setFont(fontSize, BaseColor.BLACK, true)));
            List<ClauseVo> clauseList2 = clauseList.get(i).getClauseList();
            section.setIndentationLeft(15);
            section.add(new Paragraph("\n"));
            //增加条款正文
            addClauseTable(null, section, clauseList.get(i), PdfUtils.setFont(CatalogueAndFontEnum.CONTENT.getMessage(), BaseColor.BLACK, true));
            if (clauseList2 != null && clauseList2.size() > 0) {
                addNumberTow(clauseList2, section, ++lever);
            }
            List<ControlRequirement> controlRequirementList = clauseList.get(i).getControlRequirementList();
            addControlRequirement(controlRequirementList, null, section, ++controlLever, clauseList.get(i));
        }
    }

    /**
     * 增加控制要求
     *
     * @param controlRequirementList
     * @param chapter
     * @param section
     * @param lever
     * @param customClause
     */
    private void addControlRequirement(List<ControlRequirement> controlRequirementList, Chapter chapter, Section section, int lever, ClauseVo customClause) {
        if (controlRequirementList != null) {
            float fontSize = 11f;
            for (CatalogueAndFontEnum catalogueAndFontEnum : CatalogueAndFontEnum.values()) {
                if (catalogueAndFontEnum.getName() == lever) {
                    fontSize = catalogueAndFontEnum.getMessage();
                }
            }
            for (int j = 0; j < controlRequirementList.size(); j++) {
                //每一块控制要求
                Section controlRequirementNumber;
                if (chapter != null) {
                    controlRequirementNumber = chapter;
                } else {
                    controlRequirementNumber = section;
                }
                ControlRequirement customControlRequirement = controlRequirementList.get(j);
                Section controlRequestSection = controlRequirementNumber.addSection(new Paragraph("控制要求", PdfUtils.setFont(fontSize, BaseColor.BLACK, true)));
                addControlRequirementTable(controlRequestSection, customControlRequirement, PdfUtils.setFont(11f, BaseColor.BLACK, true));
            }
        }
    }

    /**
     * 增加PDF首页封面
     *
     * @param document
     * @param produceRecord
     * @throws DocumentException
     */
    private static void addDocumentInfo(Document document, String produceRecord) throws DocumentException {
        Paragraph paragraph = new Paragraph("\n\n\n" + produceRecord + "\n\n\n\n\n\n\n\n\n\n\n", PdfUtils.setFont(24f, BaseColor.BLACK, true));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(0);
        PdfPCell cell1 = new PdfPCell();
        cell1.addElement(new Paragraph("编者", PdfUtils.setFont(11f, BaseColor.BLACK, false)));
        cell1.setPaddingBottom(10);
        cell1.setBorderWidthBottom(2);
        cell1.setBorderWidthTop(2);
        cell1.setBorderWidthLeft(0);
        cell1.setBorderWidthRight(0);
        PdfPCell cell2 = new PdfPCell();
        cell2.addElement(new Paragraph("雷哥", PdfUtils.setFont(11f, BaseColor.BLACK, false)));
        cell2.setBorderWidthBottom(2);
        cell2.setBorderWidthTop(2);
        cell2.setBorderWidthLeft(0);
        cell2.setBorderWidthRight(0);
        PdfPCell cell3 = new PdfPCell();
        cell3.addElement(new Paragraph("日期", PdfUtils.setFont(11f, BaseColor.BLACK, false)));
        cell3.setPaddingBottom(10);
        cell3.setBorderWidthBottom(2);
        cell3.setBorderWidthLeft(0);
        cell3.setBorderWidthRight(0);
        PdfPCell cell4 = new PdfPCell();
        cell4.addElement(new Paragraph(DateUtils.getTimeStringToDay(System.currentTimeMillis()), PdfUtils.setFont(11f, BaseColor.BLACK, false)));
        cell3.setPaddingBottom(10);
        cell4.setBorderWidthBottom(2);
        cell4.setBorderWidthLeft(0);
        cell4.setBorderWidthRight(0);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        document.add(paragraph);
        document.add(table);
    }

    /**
     * 条款信息以表格展示,
     *
     * @param chapter
     * @param section
     * @param clause
     * @param font
     */
    private void addClauseTable(Chapter chapter, Section section, ClauseVo clause, Font font) {
        PdfPTable table = PdfUtils.createTable(2, 400, 1);
        table.addCell(PdfUtils.createCell(new Paragraph("条款编号", font)));
        table.addCell(PdfUtils.createCell(new Paragraph(clause.getCode(), font)));
        table.addCell(PdfUtils.createCell(new Paragraph("章节", font)));
        table.addCell(PdfUtils.createCell(new Paragraph("chapter", font)));
        table.addCell(PdfUtils.createCell(new Paragraph("条款原文", font)));
        table.addCell(PdfUtils.createCell(new Paragraph(clause.getContent(), font)));
        table.setSpacingBefore(10);
        if (chapter != null) {
            chapter.add(table);
        } else {
            section.add(table);
        }
    }

    /**
     * 控制要求信息以表格展示
     *
     * @param section
     * @param controlRequirement
     * @param font
     */
    private void addControlRequirementTable(Section section, ControlRequirement controlRequirement, Font font) {
        PdfPTable table = PdfUtils.createTable(2, 400, 1);
        table.addCell(PdfUtils.createCell(new Paragraph("编号", font)));
        table.addCell(PdfUtils.createCell(new Paragraph(controlRequirement.getCode(), font)));
        table.addCell(PdfUtils.createCell(new Paragraph("内容", font)));
        table.addCell(PdfUtils.createCell(new Paragraph(controlRequirement.getContent(), font)));
        table.setSpacingBefore(10);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        section.add(table);
    }

    /**
     * 初始化数据,本demo的数据格式是先全部从查出来,封装好,之后递归生成目录和正文
     * 缺点:当文档过大时,有可能造成内存溢出,若有更好的解决方法请告知,共同交流优化
     *
     * @return
     */
    private List<ClauseVo> initData() {
        List<ClauseVo> clauseVoList = Lists.newArrayListWithExpectedSize(12);
        ClauseVo clauseVo = new ClauseVo();
        for (int i = 0; i < 12; i++) {
            clauseVo.setName("一级条款name " + PdfUtils.upperCaseNumber(i));
            clauseVo.setCode(PdfUtils.upperCaseNumber(i));
            clauseVo.setContent("一级条款content " + PdfUtils.upperCaseNumber(i));
            initControlRequirement(clauseVo);
            List<ClauseVo> clauseVoList2 = Lists.newArrayListWithExpectedSize(4);
            ClauseVo clauseVo2 = new ClauseVo();
            if (i < 4) {
                for (int j = 0; j < 4; j++) {
                    clauseVo2.setName("二级条款name " + PdfUtils.upperCaseNumber(i));
                    clauseVo2.setCode(PdfUtils.upperCaseNumber(j));
                    clauseVo2.setContent("二级条款content " + PdfUtils.upperCaseNumber(i));
                    initControlRequirement(clauseVo2);
                    clauseVoList2.add(clauseVo2);
                    clauseVo.setClauseList(clauseVoList2);
                    List<ClauseVo> clauseVoList3 = Lists.newArrayListWithExpectedSize(4);
                    ClauseVo clauseVo3 = new ClauseVo();
                    if (j < 2) {
                        for (int k = 0; k < 4; k++) {
                            clauseVo3.setName("三级条款name " + PdfUtils.upperCaseNumber(i));
                            clauseVo3.setCode(PdfUtils.upperCaseNumber(k));
                            clauseVo3.setContent("三级条款content " + PdfUtils.upperCaseNumber(i));
                            initControlRequirement(clauseVo3);
                            clauseVoList3.add(clauseVo3);
                            clauseVo2.setClauseList(clauseVoList3);
                        }
                    }
                }
            }
            clauseVoList.add(clauseVo);
        }
        return clauseVoList;
    }

    private void initControlRequirement(ClauseVo clauseVo) {
        List<ControlRequirement> list = Lists.newArrayListWithExpectedSize(1);
        ControlRequirement controlRequirement = new ControlRequirement();
        controlRequirement.setCode(clauseVo.getCode() + 1);
        controlRequirement.setContent(clauseVo.getContent() + "控制要求");
        list.add(controlRequirement);
        clauseVo.setControlRequirementList(list);
    }
}
