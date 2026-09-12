package com.mezon.classmanagement.backend.domain_document.component.tika.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ImageMarkerContentHandler extends DefaultHandler {
	private StringBuilder textBuffer;
	private int imageCounter;

	public ImageMarkerContentHandler() {
		this.textBuffer = new StringBuilder();
		this.imageCounter = 1;
	}

	// Bắt sự kiện khi Tika đọc được chữ (Text)
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		textBuffer.append(ch, start, length);
	}

	// Bắt sự kiện khi Tika đọc được một thẻ cấu trúc (p, table, img,...)
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// Nếu thẻ đó là hình ảnh (<img>)
		if ("img".equalsIgnoreCase(qName) || "img".equalsIgnoreCase(localName)) {
			// Lấy tên file ảnh (nếu có)
			String src = attributes.getValue("src");
			if (src == null || src.isEmpty()) {
				src = "image_" + imageCounter + ".jpg";
			}

			// Chèn thẻ đánh dấu vào đúng vị trí hiện tại của văn bản
			textBuffer.append("\n[[IMAGE_ID: ").append(src).append("]]\n");

			imageCounter++;
		}
		// Để ngắt dòng tốt hơn cho các đoạn văn (p) hoặc thẻ div
		else if ("p".equalsIgnoreCase(localName) || "div".equalsIgnoreCase(localName)) {
			textBuffer.append("\n");
		}
	}

	public String getParsedText() {
		return textBuffer.toString().trim();
	}
}