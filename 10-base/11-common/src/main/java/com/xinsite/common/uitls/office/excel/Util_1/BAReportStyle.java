package com.xinsite.common.uitls.office.excel.Util_1;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BAReportStyle {

	private XSSFWorkbook wb = new XSSFWorkbook();

	private XSSFFont steelBlueTotalFont;

	private static String Font_Arial = "Arial";

	private static String Font_Calibri = "Calibri";

	public BAReportStyle(XSSFWorkbook wb) {
		super();
		this.wb = wb;
	}

	/**
	 * @return the buleTitleFont
	 */
	public XSSFFont getExcelFont(String fontName, int size, short color) {
		XSSFFont blueTitleFont = wb.createFont();
		blueTitleFont.setFontName(fontName);
		blueTitleFont.setFontHeightInPoints((short) size);
		blueTitleFont.setColor(color);
		return blueTitleFont;
	}

	/**
	 * @return the buleTotalFont
	 */
	public XSSFFont getExcelBoldFont(String fontName, int size, short color) {
		XSSFFont blueTotalFont = wb.createFont();
		blueTotalFont.setBold(true);
		blueTotalFont.setFontName(fontName);
		blueTotalFont.setFontHeightInPoints((short) size);
		blueTotalFont.setColor(color);
		return blueTotalFont;
	}

	/**
	 * @return the buleTotalFont
	 */
	public XSSFFont getSteelBlueTotalFont() {
		steelBlueTotalFont = wb.createFont();
		steelBlueTotalFont.setBold(true);
		steelBlueTotalFont.setFontName("Arial");
		steelBlueTotalFont.setFontHeightInPoints((short) 10);
		steelBlueTotalFont.setColor(new XSSFColor(new java.awt.Color(70, 130, 180)));
		return steelBlueTotalFont;
	}

	/**
	 * get total style
	 * @param align
	 * @param color
	 * @return
	 */
	public XSSFCellStyle getTotalStyle(HorizontalAlignment align, short color) {
		XSSFCellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(align);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setFont(getExcelBoldFont(Font_Arial, 10, color));
		return style;
	}
	
	/**
	 * get VM total style
	 * @param align
	 * @param color
	 * @return
	 */
	public XSSFCellStyle getVMTotalStyle(HorizontalAlignment align, short color) {
		XSSFCellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(align);
		style.setBorderTop(BorderStyle.DASHED);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setFont(getExcelBoldFont(Font_Arial, 10, color));
		return style;
	}

	/**
	 * get title style
	 * @param align
	 * @return
	 */
	public XSSFCellStyle getTitleStyle(HorizontalAlignment align, String fontName) {
		XSSFCellStyle titleStyle = wb.createCellStyle();
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		titleStyle.setAlignment(align);
		titleStyle.setWrapText(true); // Set up line wrapping
		titleStyle.setBorderTop(BorderStyle.THIN);
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderLeft(BorderStyle.THIN);
		titleStyle.setBorderRight(BorderStyle.THIN);
		if (Font_Arial.equals(fontName)) {
			titleStyle.setFont(getExcelFont(Font_Arial, 8, IndexedColors.BLUE.getIndex()));
		} else {
			titleStyle.setFont(getExcelFont(Font_Calibri, 11, IndexedColors.BLACK.getIndex()));
		}
		return titleStyle;
	}

	/**
	 * get content style
	 * @param align
	 * @param color
	 * @return
	 */
	public XSSFCellStyle getContentStyle(HorizontalAlignment align, short color) {
		XSSFCellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(align);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setFont(getExcelFont(Font_Arial, 10, color));
		return style;
	}

	/**
	 * get total content style
	 * @return
	 */
	public XSSFCellStyle getTotalContentStyle() {
		XSSFCellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setFont(getExcelFont(Font_Arial, 11, IndexedColors.BLACK.getIndex()));
		return style;
	}
	
	/**
	 * get VM total content style
	 * @return
	 */
	public XSSFCellStyle getVMTotalContentStyle() {
		XSSFCellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.DASHED);
		style.setFont(getExcelFont(Font_Arial, 11, IndexedColors.DARK_RED.getIndex()));
		return style;
	}
	
	/**
	 * get the title style by type
	 * @param align
	 * @return
	 */
	public XSSFCellStyle getTotalTitleStyle(HorizontalAlignment align, short color) {
		XSSFCellStyle titleStyle = wb.createCellStyle();
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		titleStyle.setAlignment(align);
		titleStyle.setWrapText(true); // Set up line wrapping
		titleStyle.setBorderTop(BorderStyle.THIN);
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderLeft(BorderStyle.THIN);
		titleStyle.setBorderRight(BorderStyle.THIN);
		titleStyle.setFont(getExcelBoldFont(Font_Arial, 11, color));
		return titleStyle;
	}

	/**
	 * get the title style by type
	 * @param align
	 * @return
	 */
	public XSSFCellStyle getTotalStyle(HorizontalAlignment align) {
		return getTotalStyle(align, IndexedColors.BLACK.getIndex());
	}
	
	/**
	 * get a bold title style
	 * @param fontName
	 * @param size, font size
	 * @param color, font color
	 * @param align
	 * @return XSSFCellStyle
	 */
	public XSSFCellStyle getBoldTitleStyle(String fontName, int size, short color, HorizontalAlignment align){
		XSSFCellStyle titleStyle = wb.createCellStyle();
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		titleStyle.setAlignment(align);
		titleStyle.setWrapText(true); // Set up line wrapping
		titleStyle.setBorderTop(BorderStyle.THIN);
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderLeft(BorderStyle.THIN);
		titleStyle.setBorderRight(BorderStyle.THIN);
		titleStyle.setFont(getExcelBoldFont(fontName, size, color));
		return titleStyle;
	}
	
	/**
	 * get a title style
	 * @param fontName
	 * @param size, font size
	 * @param color, font color
	 * @param align
	 * @return XSSFCellStyle
	 */
	public XSSFCellStyle getTitleStyle(String fontName, int size, short color, HorizontalAlignment align){
		XSSFCellStyle titleStyle = wb.createCellStyle();
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		titleStyle.setAlignment(align);
		titleStyle.setWrapText(true); // Set up line wrapping
		titleStyle.setBorderTop(BorderStyle.THIN);
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderLeft(BorderStyle.THIN);
		titleStyle.setBorderRight(BorderStyle.THIN);
		titleStyle.setFont(getExcelFont(fontName, size, color));
		return titleStyle;
	}
	
	

}