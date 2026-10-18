package com.mezon.classmanagement.backend.common.util;

import com.mezon.classmanagement.backend.common.constant.FileTypeUtils;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.config.s3.S3Config;
import com.mezon.classmanagement.backend.domain_document.component.parser.impl.TikaV4XDocumentParser;
import com.mezon.classmanagement.backend.domain_document.component.s3.service.S3Service;
import dev.langchain4j.data.document.Document;
//import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
//import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
//import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.apache.tika.Tika;
import org.apache.tika.config.loader.TikaLoader;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.ParseContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

public final class FileUtils {

	private static final Tika TIKA = new Tika();
	//private static final ApacheTikaDocumentParser APACHE_TIKA_DOCUMENT_PARSER = new ApacheTikaDocumentParser();

	public static Path createTempFile(MultipartFile multipartFile) throws IOException {
		Path tmp = Files.createTempFile("upload-", ".tmp");

		multipartFile.transferTo(tmp);

		return tmp;
	}

//	public static String createS3TempFile(
//			S3Service s3Service,
//			MultipartFile multipartFile
//	) throws Exception {
//		@SuppressWarnings({})
//		String directorySlashName = s3Service.uploadFileToPath(
//				multipartFile,
//				S3Config.Bucket.ROOT,
//				buildPath(
//						S3Config.Directory.UPLOAD_TEMP
//				),
//				UUID.randomUUID()
//						+ "."
//						+ FileTypeUtils.getExtension(detectMimeTypeFromMultipartFile(multipartFile))
//		);
//
//		return directorySlashName;
//	}

	public static Metadata setOriginalFileName(MultipartFile multipartFile) {
		Metadata metadata = new Metadata();
		String originalFileName = getName(multipartFile);

		if (!originalFileName.isBlank()) {
			metadata.set(
					TikaCoreProperties.RESOURCE_NAME_KEY,
					originalFileName
			);

			return metadata;
		}

		return metadata;
	}

	public static Metadata setOriginalFileName(String originalFileName) {
		Metadata metadata = new Metadata();

		if (originalFileName != null && !originalFileName.isBlank()) {
			metadata.set(
					TikaCoreProperties.RESOURCE_NAME_KEY,
					originalFileName
			);

			return metadata;
		}

		return metadata;
	}

	@Deprecated
	public static String detectMimeType2(MultipartFile file) throws IOException {
		Detector detector = TIKA.getDetector();

		Metadata metadata = setOriginalFileName(file);

		try (TikaInputStream tikaInputStream = TikaInputStream.get(file.getInputStream())) {
			MediaType mediaType = detector.detect(
					tikaInputStream,
					metadata,
					new ParseContext()
			);

			return mediaType.toString();
		}
	}

	public static String detectMimeTypeFromInputStream(
			InputStream inputStream,
			String originalFileName
	) throws Exception {
		Metadata metadata = setOriginalFileName(originalFileName);
		return detectMimeType(inputStream, metadata);
	}

	public static String detectMimeTypeFromMultipartFile(MultipartFile file) throws Exception {
		Metadata metadata = setOriginalFileName(file);
		return detectMimeType(file.getInputStream(), metadata);
	}

//	public static String detectMimeType(String extension) {
//		if (!FileTypeUtils.isValidExtension(extension.toLowerCase(Locale.ROOT))) {
//			//FileTypeUtils.invalidExtension();
//			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, "detect mime type");
//		}
//		try {
//			return new Tika().detect("file." + extension);
//		} catch (Exception e) {
//			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, e.getMessage());
//		}
//	}

	public static String detectMimeType(String extension) {
		Metadata metadata = new Metadata();
		metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, "file." + extension);

		try {
			return MimeTypes.getDefaultMimeTypes()
					.detect(null, metadata, new ParseContext())
					.toString();
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					"detect mime type"
			);
		}
	}

	private static String detectMimeType(
			InputStream inputStream,
			Metadata metadata
	) throws Exception {
		Detector detector = TikaLoader.loadDefault().loadDetectors();

		try (TikaInputStream tikaInputStream = TikaInputStream.get(inputStream)) {
			MediaType type = detector.detect(
					tikaInputStream,
					metadata,
					new ParseContext()
			);

			return type.toString();
		}
	}

	public static String getExtension(MultipartFile multipartFile) {
		String fileName = getName(multipartFile);

		if (fileName.isBlank()) {
			return "";
		}

		int dotIndex = fileName.lastIndexOf(".");

		if (dotIndex == 0) {
			return "";
		}

		return fileName
				.substring(dotIndex + 1)
				.toLowerCase(Locale.ROOT);
	}

	public static String getName(MultipartFile multipartFile) {
		String fileName = multipartFile.getOriginalFilename();

		if (fileName == null || fileName.isBlank()) {
			return "";
		}

		return fileName;
	}

	public static String getName(String path) {
		return new File(path).getName();
	}

	public static String getNameWithoutExtension(String path) {
		String name = getName(path);
		int dotIndex = name.lastIndexOf('.');

		return dotIndex > 0 ? name.substring(0, dotIndex) : name;
	}

	public static String createFileNameFromTimestamp() {
		return "file_" + System.currentTimeMillis();
	}

	public static String getContent(String path) {
		try {
			TIKA.setMaxStringLength(1000000);
			return TIKA.parseToString(new ClassPathResource(path).getInputStream());
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public static Document toDocument(MultipartFile multipartFile) throws Exception {
//		return APACHE_TIKA_DOCUMENT_PARSER.parse(
//				multipartFile.getInputStream()
//		);
		return new TikaV4XDocumentParser(
				setOriginalFileName(multipartFile)
		).parse(
				multipartFile.getInputStream()
		);
	}

	public static Document toDocument(InputStream inputStream) throws Exception {
//		return APACHE_TIKA_DOCUMENT_PARSER.parse(
//				TikaInputStream.get(inputStream)
//		);
		return new TikaV4XDocumentParser().parse(inputStream);
	}

	public static Document toDocument(File file) throws Exception {
//		return APACHE_TIKA_DOCUMENT_PARSER.parse(
//				new FileInputStream(file)
//		);
		return new TikaV4XDocumentParser().parse(new FileInputStream(file));
	}

//	public static Document toDocument(String tempFilePath) throws Exception {
//		try {
//			return FileSystemDocumentLoader.loadDocument(
//					Paths.get(tempFilePath),
//					new TikaImageMarkerParser()
//			);
//		} catch (Exception e) {
//			throw new Exception(e);
//		}
//	}
//
//	public static Document toDocumentWithParser(
//			String tempFilePath,
//			DocumentParser documentParser
//	) throws Exception {
//		try {
//			return FileSystemDocumentLoader.loadDocument(
//					Paths.get(tempFilePath),
//					documentParser
//			);
//		} catch (Exception e) {
//			throw new Exception(e);
//		}
//	}

	public static String getFileNameFromMetadata(dev.langchain4j.data.document.Metadata metadata) {
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

	public static String buildPath(String... args) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < args.length; i++) {
			if (i == 0) {
				stringBuilder.append(args[i]);
			} else {
				stringBuilder.append("/").append(args[i]);
			}
		}

		return stringBuilder.toString();
	}

}