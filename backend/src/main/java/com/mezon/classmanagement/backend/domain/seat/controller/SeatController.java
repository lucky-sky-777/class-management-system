package com.mezon.classmanagement.backend.domain.seat.controller;

import com.mezon.classmanagement.backend.domain.groupuser.dto.request.CreateGroupUserSeatRequestDto;
import com.mezon.classmanagement.backend.domain.groupuser.dto.request.UpdateGroupUserSeatRequestDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.seat.dto.ClassSeatResponseDto;
import com.mezon.classmanagement.backend.domain.seat.service.SeatService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/seats/classes/{classId}")
@RestController
public class SeatController {

	SeatService seatService;

	@GetMapping
	public ResponseDTO<ClassSeatResponseDto> get(
			@PathVariable Long classId
	) {
		ClassSeatResponseDto response = seatService.get(classId);

		return ResponseDTO.<ClassSeatResponseDto>builder()
				.success(true)
				.message("Get class seats successful")
				.data(response)
				.build();
	}

	@PatchMapping("/seating")
	public ResponseDTO<ClassSeatResponseDto> create(
			@PathVariable Long classId,
			@RequestBody CreateGroupUserSeatRequestDto request
	) {
		ClassSeatResponseDto response = seatService.create(classId, request);

		return ResponseDTO.<ClassSeatResponseDto>builder()
				.success(true)
				.message("Create seat successful")
				.data(response)
				.build();
	}

	//@PreAuthorize("@ClassPermission.manageGroup(#classId)")
	@PatchMapping
	public ResponseDTO<ClassSeatResponseDto> update(
			@PathVariable Long classId,
			@RequestBody UpdateGroupUserSeatRequestDto request
	) {
		ClassSeatResponseDto response = seatService.update(classId, request);

		return ResponseDTO.<ClassSeatResponseDto>builder()
				.success(true)
				.message("Update group user seat successful")
				.data(response)
				.build();
	}

	@GetMapping("/shuffle")
	public ResponseDTO<ClassSeatResponseDto> shuffle(
			@PathVariable Long classId
	) {
		ClassSeatResponseDto response = seatService.shuffle(classId);

		return ResponseDTO.<ClassSeatResponseDto>builder()
				.success(true)
				.message("Shuffle class seats successful")
				.data(response)
				.build();
	}

}