package com.mezon.classmanagement.backend_document.common.util;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.apache.tika.Tika;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public final class FileUtils {

	private static final Tika TIKA = new Tika();
	private static final ApacheTikaDocumentParser APACHE_TIKA_DOCUMENT_PARSER = new ApacheTikaDocumentParser();

	public static String getMimeType(byte[] fileBytes) {
		return TIKA.detect(fileBytes);
	}

	public static String getContent(String path) {
		try {
			return TIKA.parseToString(new ClassPathResource(path).getInputStream());
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public static Document toDocument(MultipartFile multipartFile) throws Exception {
		return APACHE_TIKA_DOCUMENT_PARSER.parse(
				multipartFile.getInputStream()
		);
	}

	public static Document toDocument(File file) throws Exception {
		return APACHE_TIKA_DOCUMENT_PARSER.parse(
				new FileInputStream(file)
		);
	}

	public static Document toDocument(String filePath) throws Exception {
		try {
			return FileSystemDocumentLoader.loadDocument(
					Paths.get(filePath),
					new ApacheTikaDocumentParser()
			);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public static String getFileNameFromMetadata(Metadata metadata) {
		return metadata.getString("file_name");
	}

	public static String readResourceFile(String path) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read resource: " + path, e);
		}
	}

}