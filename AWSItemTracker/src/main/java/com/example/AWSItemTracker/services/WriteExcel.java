package com.example.AWSItemTracker.services;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.AWSItemTracker.entities.WorkItem;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class WriteExcel {
	
	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	private Logger logger = LoggerFactory.getLogger(WriteExcel.class);

	// Returns an InputStream that represents the Excel report
	public java.io.InputStream exportExcel( List<WorkItem> list) {

		try {
			java.io.InputStream is = write( list);
			return is ;
		} catch(WriteException | IOException e) {
			logger.error("Could not export spread sheet: " + e.getMessage());
		}
		return null;
	}

	// Generates the report and returns an inputstream
	public java.io.InputStream write( List<WorkItem> list) throws IOException, WriteException {
		java.io.OutputStream os = new java.io.ByteArrayOutputStream() ;
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		// Create a Workbook - pass the OutputStream
		WritableWorkbook workbook = Workbook.createWorkbook(os, wbSettings);
		workbook.createSheet("Work Item Report", 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		createLabel(excelSheet) ;
		int size = createContent(excelSheet, list);

		// Close the workbook
		workbook.write();
		workbook.close();

		// Get an inputStream that represents the Report
		java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		stream = (java.io.ByteArrayOutputStream)os;
		byte[] myBytes = stream.toByteArray();
		java.io.InputStream is = new java.io.ByteArrayInputStream(myBytes) ;

		return is ;
	}

	// Create Headings in the Excel spreadsheet
	private void createLabel(WritableSheet sheet)
			throws WriteException {
		// Create a times font
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		// Define the cell format
		times = new WritableCellFormat(times10pt);
		// Lets automatically wrap the cells
		times.setWrap(true);

		// Create a bold font with underlining
		WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
				UnderlineStyle.SINGLE);
		timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
		// Automatically wrap the cells
		timesBoldUnderline.setWrap(true);

		CellView cv = new CellView();
		cv.setFormat(times);
		cv.setFormat(timesBoldUnderline);
		cv.setAutosize(true);

		// Write a few headers
		addCaption(sheet, 0, 0, "Writer");
		addCaption(sheet, 1, 0, "Date");
		addCaption(sheet, 2, 0, "Guide");
		addCaption(sheet, 3, 0, "Description");
		addCaption(sheet, 4, 0, "Status");
	}

	// Write the Work Item Data to the Excel report
	private int createContent(WritableSheet sheet, List<WorkItem> list) throws WriteException {

		int size = list.size() ;

		// Add customer data to the Excel report
		for (int i = 0; i < size; i++) {

			WorkItem wi = list.get(i);

			//Get tne work item values
			String name = wi.getName();
			String guide = wi.getGuide();
			String date = wi.getDate();
			String des = wi.getDescription();
			String status = wi.getStatus();

			// First column
			addLabel(sheet, 0, i+2, name);
			// Second column
			addLabel(sheet, 1, i+2, date);

			// Third column
			addLabel(sheet, 2, i+2,guide);

			// Forth column
			addLabel(sheet, 3, i+2, des);

			// Fifth column
			addLabel(sheet, 4, i+2, status);

		}
		return size;
	}

	private void addCaption(WritableSheet sheet, int column, int row, String s)
			throws WriteException {
		Label label;
		label = new Label(column, row, s, timesBoldUnderline);

		int cc = countString(s);
		sheet.setColumnView(column, cc);
		sheet.addCell(label);
	}

	private void addNumber(WritableSheet sheet, int column, int row,
			Integer integer) throws WriteException {
		Number number;
		number = new Number(column, row, integer, times);
		sheet.addCell(number);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException {
		Label label;
		label = new Label(column, row, s, times);
		int cc = countString(s);
		if (cc > 200)
			sheet.setColumnView(column, 150);
		else
			sheet.setColumnView(column, cc+6);

		sheet.addCell(label);

	}

	private int countString (String ss) {
		int count = 0;
		//Counts each character except space
		for(int i = 0; i < ss.length(); i++) {
			if(ss.charAt(i) != ' ')
				count++;
		}
		return count;
	}
}
