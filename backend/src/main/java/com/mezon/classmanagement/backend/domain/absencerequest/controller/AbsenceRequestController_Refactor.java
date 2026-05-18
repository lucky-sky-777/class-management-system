package com.mezon.classmanagement.backend.domain.absencerequest.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.request.CreateAbsenceRequestRequestDto;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestIdResponseDto;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto;
import com.mezon.classmanagement.backend.domain.absencerequest.service.AbsenceRequestService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/classes/{classId}/absence-requests")
@RestController
public class AbsenceRequestController_Refactor {

    AuthService authService;
    JwtService jwtService;

    AbsenceRequestService absenceRequestService;

    @PreAuthorize("@ClassPermission.everyoneInClass(#classId)")
    @PostMapping
    public ResponseDTO<AbsenceRequestResponseDto> create(
            @PathVariable Long classId,
            @RequestBody CreateAbsenceRequestRequestDto request
    ) {
        Authentication authentication = authService.getAuthentication();
        Long userId = jwtService.extractUserId(authentication);

        AbsenceRequestResponseDto response = absenceRequestService.create(classId, userId, request);

        return ResponseDTO.<AbsenceRequestResponseDto>builder()
                .success(true)
                .message("Create absence request successful")
                .data(response)
                .build();
    }

    @PreAuthorize("@ClassPermission.manageAbsenceRequest(#classId)")
    @PatchMapping("/{absenceRequestId}/approve")
    public ResponseDTO<AbsenceRequestIdResponseDto> approve(
            @PathVariable Long classId,
            @PathVariable Long absenceRequestId
    ) {
        AbsenceRequestIdResponseDto response = absenceRequestService.approve(classId, absenceRequestId);

        return ResponseDTO.<AbsenceRequestIdResponseDto>builder()
                .success(true)
                .message("Approve absence request successful")
                .data(response)
                .build();
    }

    @PreAuthorize("@ClassPermission.manageAbsenceRequest(#classId)")
    @PatchMapping("/{absenceRequestId}/reject")
    public ResponseDTO<AbsenceRequestIdResponseDto> reject(
            @PathVariable Long classId,
            @PathVariable Long absenceRequestId
    ) {
        AbsenceRequestIdResponseDto response = absenceRequestService.reject(classId, absenceRequestId);

        return ResponseDTO.<AbsenceRequestIdResponseDto>builder()
                .success(true)
                .message("Reject absence request successful")
                .data(response)
                .build();
    }

    @PreAuthorize("@ClassPermission.exceptAdmin(#classId)")
    @PatchMapping("/{absenceRequestId}/cancel")
    public ResponseDTO<AbsenceRequestIdResponseDto> cancel(
            @PathVariable Long classId,
            @PathVariable Long absenceRequestId
    ) {
        Authentication authentication = authService.getAuthentication();
        Long userId = jwtService.extractUserId(authentication);

        AbsenceRequestIdResponseDto response = absenceRequestService.cancel(classId, userId, absenceRequestId);

        return ResponseDTO.<AbsenceRequestIdResponseDto>builder()
                .success(true)
                .message("Cancel absence request successful")
                .data(response)
                .build();
    }

    @GetMapping
    public ResponseDTO<List<AbsenceRequestResponseDto>> getByClass(
            @PathVariable Long classId
    ) {
        List<AbsenceRequestResponseDto> response = absenceRequestService.getByClass(classId);

        return ResponseDTO.ok(
                "Get absence requests by class successful",
                response
        );
    }

    @GetMapping("/users/{userId}")
    public ResponseDTO<List<AbsenceRequestResponseDto>> getByUser(
            @PathVariable Long classId,
            @PathVariable Long userId
    ) {
        List<AbsenceRequestResponseDto> response = absenceRequestService.getByClassAndUser(classId, userId);

        return ResponseDTO.ok(
                "Get absence requests by user successful",
                response
        );
    }

}
