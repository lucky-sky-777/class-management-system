package com.mezon.classmanagement.backend.domain_document.main.interest.controller;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.main.interest.dto.request.CreateInterestRequest;
import com.mezon.classmanagement.backend.domain_document.main.interest.dto.request.UpdateInterestRequest;
import com.mezon.classmanagement.backend.domain_document.main.interest.dto.response.InterestResponse;
import com.mezon.classmanagement.backend.domain_document.main.interest.service.InterestService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings({WarningConstant.UNUSED})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {
    InterestService interestService;

    @PutMapping("/users/{userId}")
    public ResponseEntity<InterestResponse> registerOrUpdateInterests(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UpdateInterestRequest request
    ) {
        InterestResponse response = interestService.registerOrUpdateInterests(userId, request);
        return ResponseEntity.ok(response);
    }
}
