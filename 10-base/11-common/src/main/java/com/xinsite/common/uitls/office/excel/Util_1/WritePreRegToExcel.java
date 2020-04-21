package com.xinsite.common.uitls.office.excel.Util_1;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WritePreRegToExcel {

	private static BAReportStyle style;

	private static XSSFWorkbook wb;
	
    private static String Font_Arial = "Arial";
    
    private static Map<String, List<PreRegReportDTO>> data = new LinkedHashMap<String, List<PreRegReportDTO>>();
    
    static{
    	String[] regSourceType = new String[]{"Online", "buyer app", "Internal marketing"};
    	
    	String[] regSources = new String[]{"Test site 001", "Test site 002", "Test site 003"};
    	
    	for(String type : regSourceType){
    		for(int i = 0; i < 10; i++){
    			List<PreRegReportDTO> reportDto = new ArrayList<PreRegReportDTO>();
    			for(String s : regSources){
    				PreRegReportDTO dto = new PreRegReportDTO();
        			dto.setRegSource(s);
        			reportDto.add(dto);
    			} 
    			data.put(type, reportDto);
    		}
    	}
    }

//    //创建Excel,自定义样式
//	public static void main(String args[]) {
//		wb = new XSSFWorkbook();
//
//		try { //or new HSSFWorkbook();
//			CreationHelper creationHelper = wb.getCreationHelper();
//			style = new BAReportStyle(wb);
//			Sheet sheet = wb.createSheet("my sheet");
//			sheet.setDisplayGridlines(false);
//			sheet.setColumnWidth(0, 3 * 256);
//			sheet.setColumnWidth(1, 6 * 256);
//			sheet.setColumnWidth(2, 12 * 256);
//			sheet.setColumnWidth(3, 25 * 256);
//
//			for(int i = 4; i <= 7; i++){
//				sheet.setColumnWidth(i, 12 * 256);
//			}
//			for(int i = 8; i <= 13; i++){
//				sheet.setColumnWidth(i, 9 * 256);
//			}
//
//			// title
//			Row row0 = sheet.createRow((short) 0);
//			RichTextString title = creationHelper.createRichTextString("Title here");
//			XSSFFont titleFont = style.getExcelBoldFont(Font_Arial, 12, IndexedColors.BLACK.getIndex());
//			title.applyFont(titleFont);
//			row0.createCell(1).setCellValue(title);
//
//			// title2
//			Row row1 = sheet.createRow(1);
//			Cell cell_1_1 = row1.createCell(11);
//			RichTextString title2 = creationHelper
//			.createRichTextString("Can write something here.");
//			XSSFFont title2Font = style.getExcelBoldFont(Font_Arial, 10, IndexedColors.RED.getIndex());
//			title2.applyFont(title2Font);
//			cell_1_1.setCellValue(title2);
//
//			// Define some common styles.
//			XSSFCellStyle regSourceCellStyle = style.getBoldTitleStyle(Font_Arial, 10, IndexedColors.LIGHT_BLUE.getIndex(), HorizontalAlignment.LEFT);
//			XSSFCellStyle totalCountCellStyle = style.getBoldTitleStyle(Font_Arial, 10, IndexedColors.BLACK.getIndex(), HorizontalAlignment.RIGHT);
//			XSSFCellStyle contentCellStyle = style.getTitleStyle(Font_Arial, 11, IndexedColors.BLACK.getIndex(), HorizontalAlignment.RIGHT);
//			XSSFCellStyle diffCellStyle = style.getTitleStyle(Font_Arial, 10, IndexedColors.GREY_50_PERCENT.getIndex(), HorizontalAlignment.RIGHT);
//			XSSFCellStyle regSourceTypeCellStyle = style.getBoldTitleStyle(Font_Arial, 10, IndexedColors.BLACK.getIndex(), HorizontalAlignment.CENTER);
//			regSourceTypeCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//			regSourceTypeCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//			int currentLine = 5;
//			int totalMergeStartLine = 5 - 3;
//
//			String[] showTypes = new String[]{"Type1", "Type2", "Type3", "Type4"};
//			short[] showColors = new short[]{IndexedColors.ORANGE.getIndex(), IndexedColors.LIGHT_BLUE.getIndex(),
//					IndexedColors.INDIGO.getIndex(), IndexedColors.ROSE.getIndex()};
//
//			for(int t = 0; t < showTypes.length; t++){
//				// writing data to excel.
//				XSSFCellStyle showTypeCellStyle = style.getBoldTitleStyle(Font_Arial, 10, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER);
//				showTypeCellStyle.setFillForegroundColor(showColors[t]);
//				showTypeCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//				if(t == 0){
//					// write title to excel
//					writeTitle(sheet, showTypeCellStyle, showTypes[t]);
//				}
//
//				for(Map.Entry<String, List<PreRegReportDTO>> entry : data.entrySet()){
//					int mergeStartLine = currentLine;
//					for(int i = 0, size = entry.getValue().size(); i < size; i++){
//						PreRegReportDTO dto = entry.getValue().get(i);
//
//						Row contentRow = sheet.createRow(currentLine);
//						Cell contentCell_1 = contentRow.createCell(1);
//						contentCell_1.setCellStyle(showTypeCellStyle);
//						if(t > 0){
//							contentCell_1.setCellValue(showTypes[t]);
//						}
//
//						Cell contentCell_2 = contentRow.createCell(2);
//						contentCell_2.setCellStyle(regSourceTypeCellStyle);
//						if(i == 0){
//							contentCell_2.setCellValue(entry.getKey());
//						}
//
//						Cell contentCell_3 = contentRow.createCell(3);
//						contentCell_3.setCellStyle(regSourceCellStyle);
//						contentCell_3.setCellValue(dto.getRegSource());
//
//						Cell contentCell_4 = contentRow.createCell(4);
//						contentCell_4.setCellStyle(contentCellStyle);
//						contentCell_4.setCellValue(dto.getHKCount());
//
//						Cell contentCell_5 = contentRow.createCell(5);
//						contentCell_5.setCellStyle(contentCellStyle);
//						contentCell_5.setCellValue(dto.getCNCount());
//
//						Cell contentCell_6 = contentRow.createCell(6);
//						contentCell_6.setCellStyle(contentCellStyle);
//						contentCell_6.setCellValue(dto.getNonCNCount());
//
//						Cell contentCell_7 = contentRow.createCell(7);
//						contentCell_7.setCellStyle(totalCountCellStyle);
//						contentCell_7.setCellValue(dto.getTotalCount());
//
//						Cell contentCell_8 = contentRow.createCell(8);
//						contentCell_8.setCellStyle(diffCellStyle);
//						contentCell_8.setCellValue(dto.getPast1ShowRegCntDiff());
//
//						Cell contentCell_9 = contentRow.createCell(9);
//						contentCell_9.setCellStyle(diffCellStyle);
//						contentCell_9.setCellValue(dto.getPast2ShowRegCntDiff());
//
//						Cell contentCell_10 = contentRow.createCell(10);
//						contentCell_10.setCellStyle(diffCellStyle);
//						contentCell_10.setCellValue(dto.getPast3ShowRegCntDiff());
//
//						Cell contentCell_11 = contentRow.createCell(11);
//						contentCell_11.setCellStyle(diffCellStyle);
//						contentCell_11.setCellValue(dto.getPast1ShowRegCnt());
//
//						Cell contentCell_12 = contentRow.createCell(12);
//						contentCell_12.setCellStyle(diffCellStyle);
//						contentCell_12.setCellValue(dto.getPast2ShowRegCnt());
//
//						Cell contentCell_13 = contentRow.createCell(13);
//						contentCell_13.setCellStyle(diffCellStyle);
//						contentCell_13.setCellValue(dto.getPast3ShowRegCnt());
//						currentLine++;
//					}
//					writeTotalRow(new PreRegReportDTO(), sheet, showTypeCellStyle, currentLine);
//					sheet.addMergedRegion(new CellRangeAddress(mergeStartLine,currentLine, 2, 2));
//					currentLine++;
//				}
//				sheet.addMergedRegion(new CellRangeAddress(totalMergeStartLine,currentLine - 1, 1, 1));
//				currentLine++;
//				totalMergeStartLine = currentLine;
//			}
//
//
//			// Write the output to a file
//			FileOutputStream fileOut = new FileOutputStream("D://ooxml-cell.xlsx");
//			wb.write(fileOut);
//			fileOut.flush();
//			fileOut.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	private static void writeTitle(Sheet sheet, XSSFCellStyle showTypeCellStyle, String showType){
		XSSFCellStyle cellStyle_black_title = style.getBoldTitleStyle(Font_Arial, 10, IndexedColors.BLACK.getIndex(),  HorizontalAlignment.CENTER);
		XSSFCellStyle cellStyle_grey_title = style.getBoldTitleStyle(Font_Arial, 10, IndexedColors.GREY_50_PERCENT.getIndex(),  HorizontalAlignment.CENTER);
		Row row2 = sheet.createRow(2);
		Cell cell2_1 = row2.createCell(1);
		cell2_1.setCellStyle(showTypeCellStyle);
		cell2_1.setCellValue(showType);
		
		Cell cell2_2 = row2.createCell(2);
		cell2_2.setCellStyle(cellStyle_black_title);
		
		Cell cell2_3 = row2.createCell(3);
		cell2_3.setCellStyle(cellStyle_black_title);
		cell2_3.setCellValue("Date");
		
		Cell cell2_4 = row2.createCell(4);
		cell2_4.setCellStyle(cellStyle_black_title);
		cell2_4.setCellValue("16-Jul-2017");
		
		for(int i = 5; i <= 13; i++){
			Cell cellTitle = row2.createCell(i);
			cellTitle.setCellStyle(cellStyle_grey_title);				
		}
		
		Row row3 = sheet.createRow(3);
		row3.setHeightInPoints((float) 27.75);
		Cell cell3_1 = row3.createCell(1);
		cell3_1.setCellStyle(showTypeCellStyle);
		
		Cell cell3_2 = row3.createCell(2);
		cell3_2.setCellStyle(cellStyle_black_title);
		
		String[] titles = new String[]{"Sources","Area 001","Area 002","Area 003","Total","YOY diff","SOS diff","17F/16S diff",
				"2016 Fall same period","2017 Spring same period", "2016 Spring same period"};
		for(int i = 0, size = titles.length; i < size; i++){
			Cell cellTitle = row3.createCell(i + 3);
			if(i > 4){
				cellTitle.setCellStyle(cellStyle_grey_title);
			}else{
				cellTitle.setCellStyle(cellStyle_black_title);
			}				
			cellTitle.setCellValue(titles[i]);
		}
		
		Row row4 = sheet.createRow(4);
		row4.setHeightInPoints((float) 27.75);
		Cell cell_4_1 = row4.createCell(1);
		cell_4_1.setCellStyle(showTypeCellStyle);
		
		for(int i = 2; i <= 13; i++){
			Cell cellTitle = row4.createCell(i);
			if(i > 4){
				cellTitle.setCellStyle(cellStyle_grey_title);
			}else{
				cellTitle.setCellStyle(cellStyle_black_title);
			}				
		}
		sheet.addMergedRegion(new CellRangeAddress(2,2, 4, 13));
		for(int j = 3; j <= 13; j++){
			sheet.addMergedRegion(new CellRangeAddress(3, 4, j, j));
		}
		sheet.addMergedRegion(new CellRangeAddress(2,4, 2, 2));
	}
	
	private static void writeTotalRow(PreRegReportDTO totalData, Sheet sheet, XSSFCellStyle showTypeCellStyle, int startLine){
		totalData.setRegSource("Subtotal");
		
		XSSFCellStyle regSourceCellStyle = style.getBoldTitleStyle(Font_Arial, 10, IndexedColors.LIGHT_BLUE.getIndex(), HorizontalAlignment.LEFT);
		
		XSSFCellStyle totalCellStyle = style.getBoldTitleStyle(Font_Arial, 10, IndexedColors.BLACK.getIndex(), HorizontalAlignment.RIGHT);
		totalCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		totalCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		XSSFCellStyle totalDiffCellStyle = style.getBoldTitleStyle(Font_Arial, 10, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.RIGHT);
		totalDiffCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		totalDiffCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		Row totalRow = sheet.createRow(startLine);
		
		Cell totalCell_1 = totalRow.createCell(1);
		totalCell_1.setCellValue("");
		totalCell_1.setCellStyle(showTypeCellStyle);
		
		Cell totalCell_2 = totalRow.createCell(2);
		totalCell_2.setCellValue("");
		totalCell_2.setCellStyle(regSourceCellStyle);
		
		Cell totalCell_3 = totalRow.createCell(3);
		totalCell_3.setCellStyle(totalCellStyle);
		totalCell_3.setCellValue(totalData.getRegSource());
		
		Cell totalCell_4 = totalRow.createCell(4);
		totalCell_4.setCellStyle(totalCellStyle);
		totalCell_4.setCellValue(totalData.getHKCount());
		
		Cell totalCell_5 = totalRow.createCell(5);
		totalCell_5.setCellStyle(totalCellStyle);
		totalCell_5.setCellValue(totalData.getCNCount());
		
		Cell totalCell_6 = totalRow.createCell(6);
		totalCell_6.setCellStyle(totalCellStyle);
		totalCell_6.setCellValue(totalData.getNonCNCount());
		
		Cell totalCell_7 = totalRow.createCell(7);
		totalCell_7.setCellStyle(totalCellStyle);
		totalCell_7.setCellValue(totalData.getTotalCount());
		
		Cell totalCell_8 = totalRow.createCell(8);
		totalCell_8.setCellStyle(totalCellStyle);
		totalCell_8.setCellValue(totalData.getPast1ShowRegCntDiff());
		
		Cell totalCell_9 = totalRow.createCell(9);
		totalCell_9.setCellStyle(totalCellStyle);
		totalCell_9.setCellValue(totalData.getPast2ShowRegCntDiff());
		
		Cell totalCell_10 = totalRow.createCell(10);
		totalCell_10.setCellStyle(totalCellStyle);
		totalCell_10.setCellValue(totalData.getPast3ShowRegCntDiff());
		
		Cell totalCell_11 = totalRow.createCell(11);
		totalCell_11.setCellStyle(totalCellStyle);
		totalCell_11.setCellValue(totalData.getPast1ShowRegCnt());
		
		Cell totalCell_12 = totalRow.createCell(12);
		totalCell_12.setCellStyle(totalCellStyle);
		totalCell_12.setCellValue(totalData.getPast2ShowRegCnt());
		
		Cell totalCell_13 = totalRow.createCell(13);
		totalCell_13.setCellStyle(totalCellStyle);
		totalCell_13.setCellValue(totalData.getPast3ShowRegCnt());
	}
}
