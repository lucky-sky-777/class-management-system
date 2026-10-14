package com.mezon.classmanagement.backend;

import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.ChunkStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.MultipartFileDocumentSource;
import com.mezon.classmanagement.domain_document.main.rag.GeminiRagService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest
public class GeminiRagServiceTest {

    @Autowired
    GeminiEnv geminiEnv;

    // Inject các Service chia chunk tương tự như VnCoreNLPTest
    @Autowired
    ChunkService chunkService;

    @Autowired
    ChunkStrategy chunkByParagraphWithoutOverlapStrategy;

    @Test
    public void askGeminiRagWithRealFileTest() throws Exception {
        // 1. Khởi tạo service Gemini API
        GeminiRagService geminiRagService = new GeminiRagService(geminiEnv.apiKey);

        // 2. Đọc file thật từ resources
        String fileName = "triethocmaclenin.docx";
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        MockMultipartFile multipartFile = new MockMultipartFile(fileName, classPathResource.getInputStream());
        DocumentSource documentSource = new MultipartFileDocumentSource(multipartFile);

        // 3. Dùng hệ thống của bạn để cắt file thành chunks
        List<String> chunkList = chunkService.getChunkList(
                chunkByParagraphWithoutOverlapStrategy,
                documentSource
        );

        System.out.println("Đã bóc tách được " + chunkList.size() + " đoạn (chunks) từ tài liệu.");

        // LƯU Ý QUAN TRỌNG:
        // Nếu file quá dài, việc nhồi toàn bộ chunkList vào LLM có thể gây lỗi vượt quá giới hạn token (Context Window).
        // Trong hệ thống RAG thực tế, bước này ta sẽ dùng Vector Search để lọc ra Top 3-5 chunks.
        // Để test API nhanh ở đây, ta tạm cắt lấy 10 chunks đầu tiên (hoặc nếu file ngắn thì lấy hết).
        List<String> contextChunks = chunkList.size() > 10 ? chunkList.subList(0, 10) : chunkList;

        // 4. Đặt câu hỏi dựa trên nội dung bạn biết chắc chắn có trong file
        String question = "Danh sách tác giả của cuốn giáo trình này là ai?";

        // 5. Gọi thực thi
        System.out.println("Đang gửi câu hỏi và ngữ cảnh (context) tới Gemini API...");
        String response = geminiRagService.askGeminiRag(question, contextChunks);

        // 6. In kết quả
        System.out.println("==========\n");
        System.out.println("CÂU HỎI: " + question);
        System.out.println("\nTRẢ LỜI TỪ GEMINI:");
        System.out.println(response);
        System.out.println("\n==========");
    }
}