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
@RequestMapping("/api/absence-requests")
@RestController
public class AbsenceRequestController {

    AuthService authService;
    JwtService jwtService;

    AbsenceRequestService absenceRequestService;

    @PreAuthorize("@ClassPermission.everyoneInClass(#classId)")
    @PostMapping("/classes/{classId}")
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
    @PostMapping("/classes/{classId}/requests/{requestId}/approve")
    public ResponseDTO<AbsenceRequestIdResponseDto> approve(
            @PathVariable Long classId,
            @PathVariable Long requestId
    ) {
        AbsenceRequestIdResponseDto response = absenceRequestService.approve(classId, requestId);

        return ResponseDTO.<AbsenceRequestIdResponseDto>builder()
                .success(true)
                .message("Approve absence request successful")
                .data(response)
                .build();
    }

    @PreAuthorize("@ClassPermission.manageAbsenceRequest(#classId)")
    @PostMapping("/classes/{classId}/requests/{requestId}/reject")
    public ResponseDTO<AbsenceRequestIdResponseDto> reject(
            @PathVariable Long classId,
            @PathVariable Long requestId
    ) {
        AbsenceRequestIdResponseDto response = absenceRequestService.reject(classId, requestId);

        return ResponseDTO.<AbsenceRequestIdResponseDto>builder()
                .success(true)
                .message("Reject absence request successful")
                .data(response)
                .build();
    }

    @PreAuthorize("@ClassPermission.exceptAdmin(#classId)")
    @PatchMapping("/classes/{classId}/requests/{requestId}/cancel")
    public ResponseDTO<AbsenceRequestIdResponseDto> cancel(
            @PathVariable Long classId,
            @PathVariable Long requestId
    ) {
        Authentication authentication = authService.getAuthentication();
        Long userId = jwtService.extractUserId(authentication);

        AbsenceRequestIdResponseDto response = absenceRequestService.cancel(classId, userId, requestId);

        return ResponseDTO.<AbsenceRequestIdResponseDto>builder()
                .success(true)
                .message("Cancel absence request successful")
                .data(response)
                .build();
    }

    @GetMapping("/classes/{classId}/requests")
    public ResponseDTO<List<AbsenceRequestResponseDto>> getByClass(@PathVariable Long classId) {
        return ResponseDTO.<List<AbsenceRequestResponseDto>>builder()
                .success(true)
                .message("Get list successful")
                .data(absenceRequestService.getByClass(classId))
                .build();
    }

    @GetMapping("/users/{userId}/requests")
    public ResponseDTO<List<AbsenceRequestResponseDto>> getByUser(@PathVariable Long userId) {
        return ResponseDTO.<List<AbsenceRequestResponseDto>>builder()
                .success(true)
                .message("Get list successful")
                .data(absenceRequestService.getByUser(userId))
                .build();
    }

//    @DeleteMapping("/{id}")
//    public ResponseDTO<Void> delete(@PathVariable Long id) {
//        absenceRequestService.delete(id);
//        return ResponseDTO.<Void>builder()
//                .success(true)
//                .message("Deleted")
//                .build();
//    }

//    @GetMapping("/{id}")
//    public ResponseDTO<AbsenceRequestResponseDto> get(@PathVariable Long id) {
//        return ResponseDTO.<AbsenceRequestResponseDto>builder()
//                .success(true)
//                .message("Get absence request successful")
//                .data(absenceRequestService.get(id))
//                .build();
//    }

}
