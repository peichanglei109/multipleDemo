package com.leige.demo1.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.leige.demo1.listener.pdf.PdfEvent;

import java.util.Map;

/**
 * @author peichanglei
 * @date 2018/9/21 14:50
 */
public class PdfUtils {
    /**
     * 设置字体
     *
     * @param fontsize
     * @param color
     * @param isBold
     * @return
     */
    public static Font setFont(Float fontsize, BaseColor color, Boolean isBold) {
        Font font = new Font();
        try {
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            font = new Font(baseFont, fontsize, isBold ? Font.BOLD : Font.NORMAL, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return font;
    }

    //创建表格
    public static PdfPTable createTable(int colume, int width, int border) {
        PdfPTable table = new PdfPTable(colume);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        float[] totalWidth = {width / 4, width - width / 4};
        try {
//            table.setTotalWidth(width);
            //设置各列的宽度
            table.setTotalWidth(totalWidth);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //设置各列的宽度,得锁定宽度,设为true
        table.setLockedWidth(true);
        table.getDefaultCell().setBorder(0);
        return table;
    }

    //创建单元格
    public static PdfPCell createCell(Element element) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.addElement(element);
        return cell;
    }

    public static PdfPCell createCell(Element element, int border) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderWidth(0);
        cell.addElement(element);
        return cell;
    }

    public static CMYKColor getColor(String color) {
        switch (color) {
            case "black":
                return new CMYKColor(0, 0, 0, 1);
            case "red":
                return new CMYKColor(251, 0, 0, 1);
            case "yellow":
                return new CMYKColor(251, 245, 0, 1);
            case "green":
                return new CMYKColor(25, 251, 0, 1);
            case "blue":
                return new CMYKColor(0, 17, 251, 1);
            default:
                return new CMYKColor(0, 0, 0, 1);
        }
    }

    public static String upperCaseNumber(int number) {
        int a, b = 1, t = 0;
        String s = "";
        String[] shu = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] wei = {"十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千", "万"};
        while (number != 0) {
            a = number % 10;
            if (a == 0) {
                if (t != 0 && b != 0) {
                    s = shu[0] + s;
                }
            } else if (a > 0 && a < 10) {
                s = shu[a] + s;
            }

            if (number / 10 != 0 && (number / 10 % 10 != 0 || t == 3)) {
                s = wei[t] + s;
            }

            t++;
            number = number / 10;
            b = a;
        }
        return s.replaceAll("零万", "万零");
    }

    public static String subStringName(String name) {
        int lenth = 35;
        if (name == null) {
            return null;
        }
        String[] split = name.split("");
        int first = 0;
        int last;
        for (int i = 0; i < split.length; i++) {
            if (!" ".equals(split[i])) {
                first = i;
                break;
            }
        }

        String[] split1 = name.split(",");
        String[] split2 = name.split(".");
        String[] split3 = name.split("。");
        String[] split4 = name.split("，");
        //有英文逗号
        if (split1.length > 1) {
            String sub = split1[0].substring(first);
            if (sub.length() <= lenth) {
                name = sub;
            } else {
                last = lenth;
                name = sub.substring(0, last);
            }
        } else if (split2.length > 1) {
            //包含英文句号:即点点.
            String sub = split2[0].substring(first);
            if (sub.length() <= lenth) {
                name = sub;
            } else {
                last = lenth;
                name = sub.substring(0, last);
            }

            //包含中文句号:即。
        } else if (split3.length > 1) {
            String sub = split3[0].substring(first);
            if (sub.length() <= lenth) {
                name = sub;
            } else {
                last = lenth;
                name = sub.substring(0, last);
            }
            //包含中文逗号,
        } else if (split4.length > 1) {
            String sub = split3[0].substring(first);
            if (sub.length() <= lenth) {
                name = sub;
            } else {
                last = lenth;
                name = sub.substring(0, last);
            }
        } else {
            int length = name.substring(first, name.length()).length();
            last = length > lenth ? lenth : length;
            name = name.substring(first, last);
        }
        return name;
    }


    /**
     * 临时把点点转化成,
     *
     * @param name
     * @return
     */
    public static String converPointToSign2(String name) {
        if (name == null) {
            return null;
        }
        String replace = name.replace(".", ":");
        return replace;
    }

    public static void setCatalogueLevel(PdfEvent event, Chapter indexChapter) {
        for (Map.Entry<String, Integer> index : event.getIndex().entrySet()) {
            String key = index.getKey();
            String keyValue = key;
            float size = 11f;
            //小标题缩进判断, 根据级别缩进
            StringBuilder space = new StringBuilder("   ");
            for (int i = 2; i < 12; i++) {
                if (countInString(key, ".") == i) {
                    for (int j = 1; j < i; j++) {
                        space.append("   ");
                    }
                    //                key = converSignToPoint(key);
                    keyValue = space + key;
                }
            }
            //去除目录最后一个点"."
            //keyValue = keyValue.substring(0,keyValue.lastIndexOf("."));
            Paragraph paragraph = new Paragraph(keyValue, setFont(size, BaseColor.BLACK, false));

            Chunk chunk0101 = new Chunk(new DottedLineSeparator());

            Chunk pageno = new Chunk(index.getValue() + "");

            Chunk chunk0102 = new Chunk(pageno);

            //加入点点
            paragraph.add(chunk0101);
            //加入页码
            paragraph.add(chunk0102);

            indexChapter.add(paragraph);
        }
    }

    public static int countInString(String str1, String str2) {
        //根据目录编号的长度判断菜单的等级   例：1.1.1.  长度为3
        int total = 0;
        for (String tmp = str1; tmp != null && tmp.length() >= str2.length(); ) {
            if (tmp.indexOf(str2) == 0) {
                total++;
                tmp = tmp.substring(str2.length());
            } else {
                tmp = tmp.substring(1);
            }
        }
        return total;
    }
}
