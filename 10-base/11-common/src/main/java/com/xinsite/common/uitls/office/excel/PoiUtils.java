package com.xinsite.common.uitls.office.excel;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xinsite.common.uitls.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PoiUtils {
    //当前文件已经存在
    private String excelPath = "D:\\template.xlsx";
    //从第几行插入进去
    private int insertStartPointer = 8;
    //在当前工作薄的那个工作表单  （sheet页名称）
    private String sheetName = "领用单";

//    /**
//     * 总的入口方法
//     */
//    public static void main(String[] args) {
//        String filePath = "D:\\template.xlsx";
//        PoiUtils.insertRows(filePath, "领用单");
//    }

    /**
     * 在已有的Excel文件中插入一行新的数据的入口方法
     */
    public static void insertRows(String excelPath, String sheetName) {
        XSSFWorkbook wb = returnWorkBookGivenFileHandle(excelPath);
        XSSFSheet sheet1 = wb.getSheet(sheetName);
//        XSSFRow row = createRow(sheet1, insertStartPointer);
//        createCell(row);
        //XSSFRow row_0 = sheet1.getRow(insertStartPointer - 1);
        //XSSFRow row = createRow(sheet1, insertStartPointer);

//        PoiUtils.insertRow(sheet1, sheet1.getLastRowNum(), 1);
//        PoiUtils.insertRow(sheet1, sheet1.getLastRowNum(), 1);
//        PoiUtils.insertRow(sheet1, sheet1.getLastRowNum(), 2);

        XSSFCellStyle cellStyle = (XSSFCellStyle) wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        List<ExcelData> datas = new ArrayList<ExcelData>();
        List<ExcelMerged> mergeds = new ArrayList<ExcelMerged>();
        datas.add(new ExcelData("C2", "2019-02-23", cellStyle));
        datas.add(new ExcelData("E2", "aaaa", cellStyle));
        datas.add(new ExcelData("C3", "2019-02-27"));
        datas.add(new ExcelData("A15", "aaaaaaaaaaaaaaaaaaa", cellStyle));
        mergeds.add(new ExcelMerged("A15", "F15", BorderStyle.THIN));
        PoiUtils.writeModel(wb, sheet1, datas);
        PoiUtils.sheetMerged(sheet1, mergeds);
        saveExcel(wb, excelPath);
    }

    /**
     * 工作表插入行，插入行的样式是起始行上一行的样式
     *
     * @param starRow 插入行起始行
     * @param rows    插入行数
     */
    public static void insertRow(XSSFSheet sheet, int starRow, int rows) {
        sheet.shiftRows(starRow, sheet.getLastRowNum(), rows, true, false); //从starRow行向下移动rows行
        for (int i = starRow; i < starRow + rows; i++) {
            CellCopyPolicy copyPolicy = new CellCopyPolicy();
            sheet.copyRows(i - 1, i - 1, i, copyPolicy);
            //sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 4));// 合并单元格,起始行号，终止行号， 起始列号，终止列号
//            XSSFRow sourceRow = sheet.getRow(starRow - 1);
//            XSSFRow targetRow = sheet.createRow(i);
//            targetRow.setHeight(sourceRow.getHeight());
//            for (short m = sourceRow.getFirstCellNum(); m < sourceRow.getLastCellNum(); m++) {
//                XSSFCell sourceCell = sourceRow.getCell(m);
//                XSSFCell targetCell = targetRow.createCell(m);
//                targetCell.setCellStyle(sourceCell.getCellStyle());
//                targetCell.setCellType(sourceCell.getCellType());
//            }
        }
    }

    /**
     * XSSFSheet合并单元格
     */
    public static void sheetMerged(XSSFSheet sheet, List<ExcelMerged> mergeds) {
        try {
            for (ExcelMerged merged : mergeds) {
                CellRangeAddress region = new CellRangeAddress(merged.getFirstRow(), merged.getLastRow(), merged.getFirstCol(), merged.getLastCol());
                sheet.addMergedRegion(region);// 合并单元格,起始行号，终止行号， 起始列号，终止列号

//                if (merged.getBorder() > 0) {
//                    RegionUtil.setBorderBottom(merged.getBorder(), region, sheet); // 下边框
//                    RegionUtil.setBorderLeft(merged.getBorder(), region, sheet); // 左边框
//                    RegionUtil.setBorderRight(merged.getBorder(), region, sheet); // 右边框
//                    RegionUtil.setBorderTop(merged.getBorder(), region, sheet); // 上边框
//                }
                if (merged.getBorderStyle() != null) {
                    RegionUtil.setBorderBottom(merged.getBorderStyle(), region, sheet); // 下边框
                    RegionUtil.setBorderLeft(merged.getBorderStyle(), region, sheet); // 左边框
                    RegionUtil.setBorderRight(merged.getBorderStyle(), region, sheet); // 右边框
                    RegionUtil.setBorderTop(merged.getBorderStyle(), region, sheet); // 上边框
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 替换Excel模板文件内容
     */
    public static boolean writeModel(XSSFWorkbook wb, XSSFSheet sheet, List<ExcelData> datas) {
        boolean bool = true;
        try {
            for (ExcelData data : datas) {
                XSSFRow row = sheet.getRow(data.getRow());
                if (row == null) {
                    row = sheet.createRow(data.getRow());
                    if (row == null) continue;
                }
                if (data.getRowHeight() > 0) {
                    row.setHeightInPoints(data.getRowHeight());
                }
                XSSFCell cell = row.getCell((short) data.getColumn());
                if (cell == null) {
                    cell = row.createCell((short) data.getColumn());
                    if (cell == null) continue;
                }

                if (!StringUtils.isEmpty(data.getKey())) {
                    String str = cell.getStringCellValue();
                    str = str.replace(data.getKey(), data.getValue().toString());  //替换单元格内容
                    cell.setCellValue(str); //写入单元格内容
                } else {
                    CellStyle style = wb.createCellStyle();
                    Object val = data.getValue();
                    if (val == null) {
                        cell.setCellValue("");
                    } else if (val instanceof String) {
                        cell.setCellValue((String) val);
                    } else if (val instanceof Integer) {
                        cell.setCellValue((Integer) val);
                    } else if (val instanceof Long) {
                        cell.setCellValue((Long) val);
                    } else if (val instanceof Double) {
                        cell.setCellValue((Double) val);
                    } else if (val instanceof Float) {
                        cell.setCellValue((Float) val);
                    } else if (val instanceof Date) {
                        DataFormat format = wb.createDataFormat();
                        style.setDataFormat(format.getFormat("yyyy/MM/dd"));
                        cell.setCellValue((Date) val);
                    } else if (val instanceof BigDecimal) {
                        double doubleVal = ((BigDecimal) val).doubleValue();
                        DataFormat format = wb.createDataFormat();
                        style.setDataFormat(format.getFormat("￥#,##0.00"));
                        cell.setCellValue(doubleVal);
                    } else {
                        cell.setCellValue(val.toString());
                    }
                }
                if (data.getCellStyle() != null)
                    cell.setCellStyle(data.getCellStyle());
            }
        } catch (Exception e) {
            bool = false;
            e.printStackTrace();
        }
        return bool;
    }

    /**
     * 替换Excel模板文件内容
     */
    public static boolean writeModel(XSSFSheet sheet, Map<String, String> map) {
        boolean bool = true;
        try {
            if (map != null) {
                for (String atr : map.keySet()) {
                    int rowNum = sheet.getLastRowNum();//该sheet页里最多有几行内容
                    for (int i = 0; i < rowNum; i++) {//循环每一行
                        XSSFRow row = sheet.getRow(i);
                        int colNum = row.getLastCellNum();//该行存在几列
                        for (int j = 0; j < colNum; j++) {//循环每一列
                            XSSFCell cell = row.getCell((short) j);
                            String str = cell.getStringCellValue();//获取单元格内容  （行列定位）
                            if (atr.equals(str)) {
                                cell.setCellValue(map.get(atr)); //替换单元格内容
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            bool = false;
            e.printStackTrace();
        }
        return bool;
    }

    /**
     * 找到需要插入的行数，并新建一个POI的row对象
     */
    public static XSSFRow createRow(XSSFSheet sheet, Integer rowIndex) {
        XSSFRow row = null;
        if (sheet.getRow(rowIndex) != null) {
            int lastRowNo = sheet.getLastRowNum();
            sheet.shiftRows(rowIndex, lastRowNo, 1, true, true);
        }
        row = sheet.createRow(rowIndex);
        return row;
    }

    /**
     * 创建要出入的行中单元格
     */
    public static XSSFCell createCell(XSSFRow row) {
        XSSFCell cell = row.createCell((short) 0);
        cell.setCellValue(999999);
        row.createCell(1).setCellValue(1.2);
        row.createCell(2).setCellValue("This is a string cell");
        return cell;
    }

    /**
     * 保存工作薄
     */
    public static void saveExcel(XSSFWorkbook wb, String excelPath) {
        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(excelPath);
            wb.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到一个已有的工作薄的POI对象
     */
    public static XSSFWorkbook returnWorkBookGivenFileHandle(String excelPath) {
        XSSFWorkbook wb = null;
        FileInputStream fis = null;
        File file = new File(excelPath);
        try {
            if (file != null && !file.exists()) {
                FileOutputStream out = new FileOutputStream(excelPath);
                String fileName = file.getName();
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                com.alibaba.excel.ExcelWriter writer = null;
                if (suffix.equalsIgnoreCase("xls"))
                    writer = new com.alibaba.excel.ExcelWriter(out, ExcelTypeEnum.XLS, true);
                else if (suffix.equalsIgnoreCase("xlsx"))
                    writer = new com.alibaba.excel.ExcelWriter(out, ExcelTypeEnum.XLSX, true);
                else {
                    return null;
                }
                com.alibaba.excel.metadata.Sheet sheet1 = new com.alibaba.excel.metadata.Sheet(1, 0);
                sheet1.setSheetName("Sheet1");
                List<List<String>> head = new ArrayList<List<String>>();
                com.alibaba.excel.metadata.Table table = new com.alibaba.excel.metadata.Table(1);
                table.setHead(head);

                writer.write0(new ArrayList<List<String>>(), sheet1, table);
                writer.finish();
            }
            if (file != null) {
                fis = new FileInputStream(file);
                wb = new XSSFWorkbook(fis);
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return wb;
    }

//    public static void main(String[] args) {
//        try {
//            // 创建Excel表格工作簿
//            XSSFWorkbook wb = new XSSFWorkbook();
//            XSSFSheet sheet = wb.createSheet("表格单元格格式化");
//
//            //============================
//            //       设置单元格的字体
//            //============================
//            XSSFRow ztRow = sheet.createRow((short) 0);
//            XSSFCell ztCell = ztRow.createCell(0);
//            ztCell.setCellValue("中国");
//            // 创建单元格样式对象
//            XSSFCellStyle ztStyle = (XSSFCellStyle) wb.createCellStyle();
//            // 创建字体对象
//            XSSFFont ztFont = wb.createFont();
//            ztFont.setItalic(true);                     // 设置字体为斜体字
//            ztFont.setColor(Font.COLOR_RED);            // 将字体设置为“红色”
//            ztFont.setFontHeightInPoints((short) 22);    // 将字体大小设置为18px
//            ztFont.setFontName("华文行楷");             // 将“华文行楷”字体应用到当前单元格上
//            ztFont.setUnderline(Font.U_DOUBLE);         // 添加（Font.U_SINGLE单条下划线/Font.U_DOUBLE双条下划线）
////          ztFont.setStrikeout(true);                  // 是否添加删除线
//            ztStyle.setFont(ztFont);                    // 将字体应用到样式上面
//            ztCell.setCellStyle(ztStyle);               // 样式应用到该单元格上
//
//            //============================
//            //        设置单元格边框
//            //============================
//            Row borderRow = sheet.createRow(2);
//            Cell borderCell = borderRow.createCell(1);
//            borderCell.setCellValue("中国");
//            // 创建单元格样式对象
//            XSSFCellStyle borderStyle = (XSSFCellStyle) wb.createCellStyle();
//            // 设置单元格边框样式
//            // CellStyle.BORDER_DOUBLE      双边线
//            // CellStyle.BORDER_THIN        细边线
//            // CellStyle.BORDER_MEDIUM      中等边线
//            // CellStyle.BORDER_DASHED      虚线边线
//            // CellStyle.BORDER_HAIR        小圆点虚线边线
//            // CellStyle.BORDER_THICK       粗边线
//            borderStyle.setBorderBottom(CellStyle.BORDER_THICK);
//            borderStyle.setBorderTop(CellStyle.BORDER_DASHED);
//            borderStyle.setBorderLeft(CellStyle.BORDER_DOUBLE);
//            borderStyle.setBorderRight(CellStyle.BORDER_THIN);
//
//            // 设置单元格边框颜色
//            borderStyle.setBottomBorderColor(new XSSFColor(java.awt.Color.RED));
//            borderStyle.setTopBorderColor(new XSSFColor(java.awt.Color.GREEN));
//            borderStyle.setLeftBorderColor(new XSSFColor(java.awt.Color.BLUE));
//
//            borderCell.setCellStyle(borderStyle);
//
//            //============================
//            //      设置单元内容的对齐方式
//            //============================
//            Row alignRow = sheet.createRow(4);
//            Cell alignCell = alignRow.createCell(1);
//            alignCell.setCellValue("中国");
//
//            // 创建单元格样式对象
//            XSSFCellStyle alignStyle = (XSSFCellStyle) wb.createCellStyle();
//
//            // 设置单元格内容水平对其方式
//            // XSSFCellStyle.ALIGN_CENTER       居中对齐
//            // XSSFCellStyle.ALIGN_LEFT         左对齐
//            // XSSFCellStyle.ALIGN_RIGHT        右对齐
//            alignStyle.setAlignment(HorizontalAlignment.CENTER);
//
//            // 设置单元格内容垂直对其方式
//            // XSSFCellStyle.VERTICAL_TOP       上对齐
//            // XSSFCellStyle.VERTICAL_CENTER    中对齐
//            // XSSFCellStyle.VERTICAL_BOTTOM    下对齐
//            alignStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//            alignCell.setCellStyle(alignStyle);
//
//            //============================
//            //      设置单元格的高度和宽度
//            //============================
//            Row sizeRow = sheet.createRow(6);
//            sizeRow.setHeightInPoints(30);                  // 设置行的高度
//
//            Cell sizeCell = sizeRow.createCell(1);
//            String sizeCellValue = "《Java编程思想》";            // 字符串的长度为10，表示该字符串中有10个字符，忽略中英文
//            sizeCell.setCellValue(sizeCellValue);
//            // 设置单元格的长度为sizeCellVlue的长度。而sheet.setColumnWidth使用sizeCellVlue的字节数
//            // sizeCellValue.getBytes().length == 16
//            sheet.setColumnWidth(1, (sizeCellValue.getBytes().length) * 256);
//
//            //============================
//            //      设置单元格自动换行
//            //============================
//            Row wrapRow = sheet.createRow(8);
//            Cell wrapCell = wrapRow.createCell(2);
//            wrapCell.setCellValue("宝剑锋从磨砺出,梅花香自苦寒来");
//
//            // 创建单元格样式对象
//            XSSFCellStyle wrapStyle = (XSSFCellStyle) wb.createCellStyle();
//            wrapStyle.setWrapText(true);                    // 设置单元格内容是否自动换行
//            wrapCell.setCellStyle(wrapStyle);
//
//            //============================
//            //         合并单元格列
//            //============================
//            Row regionRow = sheet.createRow(12);
//            Cell regionCell = regionRow.createCell(0);
//            regionCell.setCellValue("宝剑锋从磨砺出,梅花香自苦寒来");
//
//            // 合并第十三行中的A、B、C三列
//            CellRangeAddress region = new CellRangeAddress(12, 12, 0, 2); // 参数都是从O开始
//            sheet.addMergedRegion(region);
//
//            //============================
//            //         合并单元格行和列
//            //============================
//            Row regionRow2 = sheet.createRow(13);
//            Cell regionCell2 = regionRow2.createCell(3);
//            String region2Value = "宝剑锋从磨砺出,梅花香自苦寒来。"
//                    + "采得百花成蜜后,为谁辛苦为谁甜。"
//                    + "操千曲而后晓声,观千剑而后识器。"
//                    + "察己则可以知人,察今则可以知古。";
//            regionCell2.setCellValue(region2Value);
//
//            // 合并第十三行中的A、B、C三列
//            CellRangeAddress region2 = new CellRangeAddress(13, 17, 3, 7); // 参数都是从O开始
//            sheet.addMergedRegion(region2);
//
//            XSSFCellStyle region2Style = (XSSFCellStyle) wb.createCellStyle();
//            region2Style.setVerticalAlignment(VerticalAlignment.CENTER);
//            region2Style.setWrapText(true);                     // 设置单元格内容是否自动换行
//            regionCell2.setCellStyle(region2Style);
//
//            //============================
//            // 将Excel文件写入到磁盘上
//            //============================
//            FileOutputStream is = new FileOutputStream("document/CellFormatExcel.xlsx");
//            wb.write(is);
//            is.close();
//
//            System.out.println("写入成功，运行结束！");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}