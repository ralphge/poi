/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.xssf.extractor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.poi.POITextExtractor;
import org.apache.poi.hssf.HSSFTestDataSamples;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.xssf.XSSFTestDataSamples;

/**
 * Tests for {@link XSSFExcelExtractor}
 */
public class TestXSSFExcelExtractor extends TestCase {
	protected XSSFExcelExtractor getExtractor(String sampleName) {
		return new XSSFExcelExtractor(XSSFTestDataSamples.openSampleWorkbook(sampleName));
	}

	/**
	 * Get text out of the simple file
	 * @throws IOException 
	 */
	public void testGetSimpleText() throws IOException {
		// a very simple file
		XSSFExcelExtractor extractor = getExtractor("sample.xlsx");
		extractor.getText();
		
		String text = extractor.getText();
		assertTrue(text.length() > 0);
		
		// Check sheet names
		assertTrue(text.startsWith("Sheet1"));
		assertTrue(text.endsWith("Sheet3\n"));
		
		// Now without, will have text
		extractor.setIncludeSheetNames(false);
		text = extractor.getText();
		String CHUNK1 =
			"Lorem\t111\n" + 
    		"ipsum\t222\n" + 
    		"dolor\t333\n" + 
    		"sit\t444\n" + 
    		"amet\t555\n" + 
    		"consectetuer\t666\n" + 
    		"adipiscing\t777\n" + 
    		"elit\t888\n" + 
    		"Nunc\t999\n";
		String CHUNK2 =
			"The quick brown fox jumps over the lazy dog\n" +
			"hello, xssf	hello, xssf\n" +
			"hello, xssf	hello, xssf\n" +
			"hello, xssf	hello, xssf\n" +
			"hello, xssf	hello, xssf\n";
		assertEquals(
				CHUNK1 + 
				"at\t4995\n" + 
				CHUNK2
				, text);
		
		// Now get formulas not their values
		extractor.setFormulasNotResults(true);
		text = extractor.getText();
		assertEquals(
				CHUNK1 +
				"at\tSUM(B1:B9)\n" + 
				CHUNK2, text);
		
		// With sheet names too
		extractor.setIncludeSheetNames(true);
		text = extractor.getText();
		assertEquals(
				"Sheet1\n" +
				CHUNK1 +
				"at\tSUM(B1:B9)\n" + 
				"rich test\n" +
				CHUNK2 +
				"Sheet3\n"
				, text);
		
		extractor.close();
	}
	
	public void testGetComplexText() throws IOException {
		// A fairly complex file
		XSSFExcelExtractor extractor = getExtractor("AverageTaxRates.xlsx");
		extractor.getText();
		
		String text = extractor.getText();
		assertTrue(text.length() > 0);
		
		// Might not have all formatting it should do!
		// TODO decide if we should really have the "null" in there
		assertTrue(text.startsWith(
						"Avgtxfull\n" +
						"null\t(iii) AVERAGE TAX RATES ON ANNUAL"	
		));
		
		extractor.close();
	}
	
	/**
	 * Test that we return pretty much the same as
	 *  ExcelExtractor does, when we're both passed
	 *  the same file, just saved as xls and xlsx
	 * @throws IOException 
	 */
	public void testComparedToOLE2() throws IOException {
		// A fairly simple file - ooxml
		XSSFExcelExtractor ooxmlExtractor = getExtractor("SampleSS.xlsx");

		ExcelExtractor ole2Extractor =
			new ExcelExtractor(HSSFTestDataSamples.openSampleWorkbook("SampleSS.xls"));
		
		POITextExtractor[] extractors =
			new POITextExtractor[] { ooxmlExtractor, ole2Extractor };
		for (int i = 0; i < extractors.length; i++) {
			@SuppressWarnings("resource")
            POITextExtractor extractor = extractors[i];
			
			String text = extractor.getText().replaceAll("[\r\t]", "");
			assertTrue(text.startsWith("First Sheet\nTest spreadsheet\n2nd row2nd row 2nd column\n"));
			Pattern pattern = Pattern.compile(".*13(\\.0+)?\\s+Sheet3.*", Pattern.DOTALL);
			Matcher m = pattern.matcher(text);
			assertTrue(m.matches());			
		}

		ole2Extractor.close();
		ooxmlExtractor.close();
	}
	
	/**
	 * From bug #45540
	 * @throws IOException 
	 */
	public void testHeaderFooter() throws IOException {
		String[] files = new String[] {
			"45540_classic_Header.xlsx", "45540_form_Header.xlsx",
			"45540_classic_Footer.xlsx", "45540_form_Footer.xlsx",
		};
		for(String sampleName : files) {
			XSSFExcelExtractor extractor = getExtractor(sampleName);
			String text = extractor.getText();
			
			assertTrue("Unable to find expected word in text from " + sampleName + "\n" + text, text.contains("testdoc"));
			assertTrue("Unable to find expected word in text\n" + text, text.contains("test phrase"));
			
			extractor.close();
		}
	}

	/**
	 * From bug #45544
	 * @throws IOException 
	 */
	public void testComments() throws IOException {
		XSSFExcelExtractor extractor = getExtractor("45544.xlsx");
		String text = extractor.getText();

		// No comments there yet
		assertFalse("Unable to find expected word in text\n" + text, text.contains("testdoc"));
		assertFalse("Unable to find expected word in text\n" + text, text.contains("test phrase"));

		// Turn on comment extraction, will then be
		extractor.setIncludeCellComments(true);
		text = extractor.getText();
		assertTrue("Unable to find expected word in text\n" + text, text.contains("testdoc"));
		assertTrue("Unable to find expected word in text\n" + text, text.contains("test phrase"));
		
		extractor.close();
	}
	
	public void testInlineStrings() throws IOException {
      XSSFExcelExtractor extractor = getExtractor("InlineStrings.xlsx");
      extractor.setFormulasNotResults(true);
      String text = extractor.getText();

      // Numbers
      assertTrue("Unable to find expected word in text\n" + text, text.contains("43"));
      assertTrue("Unable to find expected word in text\n" + text, text.contains("22"));
      
      // Strings
      assertTrue("Unable to find expected word in text\n" + text, text.contains("ABCDE"));
      assertTrue("Unable to find expected word in text\n" + text, text.contains("Long Text"));
      
      // Inline Strings
      assertTrue("Unable to find expected word in text\n" + text, text.contains("1st Inline String"));
      assertTrue("Unable to find expected word in text\n" + text, text.contains("And More"));
      
      // Formulas
      assertTrue("Unable to find expected word in text\n" + text, text.contains("A2"));
      assertTrue("Unable to find expected word in text\n" + text, text.contains("A5-A$2"));
      
      extractor.close();
	}
	/**
	 * Simple test for text box text
	 * @throws IOException
	 */
	public void testTextBoxes() throws IOException {
	    XSSFExcelExtractor extractor = getExtractor("WithTextBox.xlsx");
	    try {
    	    extractor.setFormulasNotResults(true);
    	    String text = extractor.getText();
    	    assertTrue(text.indexOf("Line 1") > -1);
    	    assertTrue(text.indexOf("Line 2") > -1);
    	    assertTrue(text.indexOf("Line 3") > -1);
	    } finally {
	        extractor.close();
	    }
	}
}
