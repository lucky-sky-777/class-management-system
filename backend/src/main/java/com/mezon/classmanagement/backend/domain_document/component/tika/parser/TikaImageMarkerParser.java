package com.mezon.classmanagement.backend.domain_document.component.tika.parser;

import com.mezon.classmanagement.backend.domain_document.component.tika.handler.ImageMarkerContentHandler;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;

import java.io.InputStream;

public class TikaImageMarkerParser implements DocumentParser {
	@Override
	public Document parse(InputStream inputStream) {
		try {
			AutoDetectParser parser = new AutoDetectParser();
			org.apache.tika.metadata.Metadata tikaMetadata = new org.apache.tika.metadata.Metadata();
			ParseContext context = new ParseContext();

			// Sử dụng Handler chúng ta vừa viết
			ImageMarkerContentHandler handler = new ImageMarkerContentHandler();

			// Tika bắt đầu đọc file và kích hoạt các sự kiện cho Handler xử lý
			parser.parse(inputStream, handler, tikaMetadata, context);

			// Lấy nội dung đã được chèn thẻ [[IMAGE_ID:...]]
			String finalContent = handler.getParsedText();

			// Lấy Metadata (tuỳ chọn)
			Metadata metadata = new Metadata();
			String fileName = metadata.getString("file_name");//tikaMetadata.get(TikaCoreProperties.RESOURCE_NAME_KEY);
			if (fileName != null) {
				metadata.put("file_name", fileName);
			}

			return Document.from(finalContent, metadata);
		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi parse tài liệu qua Tika", e);
		}
	}
}