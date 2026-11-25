package com.mezon.classmanagement.backend.domain_document.main.search.controller;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.main.search.dto.response.SearchResultDtoProjection;
import com.mezon.classmanagement.backend.domain_document.main.search.service.SearchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SearchController {

    AuthService authService;
    JwtService jwtService;

    SearchService searchService;

    @GetMapping("/search")
    public ResponseDTO<List<SearchResultDtoProjection>> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "classId", required = false) Long classId,
            @RequestParam(value = "directoryId", required = false) Long directoryId,
            @RequestParam(value = "documentId", required = false) Long documentId,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "minSimilarity", required = false) Double minSimilarity
    ) {
        Authentication authencation = authService.getAuthentication();
        Long userId = jwtService.extractUserIdFromAuthentication(authencation);

        List<SearchResultDtoProjection> data = searchService.searchForUser(
                userId,
                q,
                classId,
                directoryId,
                documentId,
                limit,
                minSimilarity
        );
        return ResponseDTO.ok("Tìm kiếm thành công", data);
    }

}
