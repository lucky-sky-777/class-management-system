package com.mezon.classmanagement.backend.domain_document.component.parser.impl;

import com.mezon.classmanagement.backend.common.util.TextUtils;
import com.mezon.classmanagement.backend.domain_document.component.parser.CustomDocumentParser;
import dev.langchain4j.data.document.BlankDocumentException;
import dev.langchain4j.data.document.Document;
import org.apache.tika.exception.ZeroByteFileException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TikaV4XDocumentParser implements CustomDocumentParser {

	private boolean includeMetadata = false;
	private final Metadata metadata;

	public TikaV4XDocumentParser() {
		this.metadata = new Metadata();
	}

	public TikaV4XDocumentParser(Metadata metadata) {
		includeMetadata = true;
		this.metadata = metadata;
	}

	@Override
	public Document parse(InputStream inputStream) {
		try (TikaInputStream tikaInputStream = TikaInputStream.get(inputStream)) {
			Parser parser = new AutoDetectParser();
			ContentHandler handler = new BodyContentHandler(-1);
			ParseContext context = new ParseContext();

			parser.parse(tikaInputStream, handler, metadata, context);
			String text = TextUtils.clean(handler.toString());

			if (text == null || text.trim().isEmpty()) {
				throw new BlankDocumentException();
			}

			return includeMetadata ? Document.from(text, convert(metadata)) : Document.from(text);
		} catch (BlankDocumentException e) {
			throw e;
		} catch (ZeroByteFileException e) {
			throw new BlankDocumentException();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

//	@Override
//	public String extractToFile(
//			InputStream inputStream,
//			String outputFileName
//	) throws Exception {
//		Path outputDir = Paths.get(System.getProperty("java.io.tmpdir"), "extracted_docs");
//		if (!Files.exists(outputDir)) {
//			Files.createDirectories(outputDir);
//		}
//
//		Path outputPath = outputDir.resolve(outputFileName + ".txt");
//
//		try (
//				Writer writer = new BufferedWriter(
//						new OutputStreamWriter(
//								new FileOutputStream(
//										outputPath.toFile()
//								),
//								StandardCharsets.UTF_8
//						)
//				)
//		) {
//
//			AutoDetectParser parser = new AutoDetectParser();
//			Metadata metadata = new Metadata();
//			ParseContext context = new ParseContext();
//
//			BodyContentHandler handler = new BodyContentHandler(writer);
//
//			parser.parse(TikaInputStream.get(inputStream), handler, metadata, context);
//
//			System.out.println(handler);
//		}
//
//		return outputPath.toAbsolutePath().toString();
//	}

	private dev.langchain4j.data.document.Metadata convert(Metadata tikaMetadata) {

		final Map<String, String> tikaMetaData = new HashMap<>();

		for (String name : tikaMetadata.names()) {
			tikaMetaData.put(name, String.join(";", tikaMetadata.getValues(name)));
		}

		return new dev.langchain4j.data.document.Metadata(tikaMetaData);
	}

}