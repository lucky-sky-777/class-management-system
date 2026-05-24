package com.mezon.classmanagement.backend.domain.activity.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.activity.dto.request.CreateAndUpdateActivityRequestDto;
import com.mezon.classmanagement.backend.domain.activity.dto.response.ActivityIdResponseDto;
import com.mezon.classmanagement.backend.domain.activity.dto.response.ActivityResponseDto;
import com.mezon.classmanagement.backend.domain.activity.dto.response.ActivitySummaryResponseDto;
import com.mezon.classmanagement.backend.domain.activity.service.ActivityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/classes/{classId}/activities")
@RestController
public class ActivityController {

	ActivityService activityService;

	@PreAuthorize("@ClassSecurity.manageActivity(#classId)")
	@PostMapping
	public ResponseDTO<ActivityResponseDto> create(
			@PathVariable Long classId,
			@RequestBody CreateAndUpdateActivityRequestDto request
	) {
		ActivityResponseDto response = activityService.create(classId, request);

		return ResponseDTO.ok(
				"Create activity successful",
				response
		);
	}

	@PreAuthorize("@ClassSecurity.manageActivity(#classId)")
	@PatchMapping("/{activityId}")
	public ResponseDTO<ActivityResponseDto> update(
			@PathVariable Long classId,
			@PathVariable Long activityId,
			@RequestBody CreateAndUpdateActivityRequestDto request
	) {
		ActivityResponseDto response = activityService.update(classId, activityId, request);

		return ResponseDTO.ok(
				"Update activity successful",
				response
		);
	}

	@PreAuthorize("@ClassSecurity.manageActivity(#classId)")
	@DeleteMapping("/{activityId}")
	public ResponseDTO<ActivityIdResponseDto> delete(
			@PathVariable Long classId,
			@PathVariable Long activityId
	) {
		ActivityIdResponseDto response = activityService.delete(classId, activityId);

		return ResponseDTO.ok(
				"Delete activity successful",
				response
		);
	}

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@GetMapping
	public ResponseDTO<List<ActivityResponseDto>> getByClass(
			@PathVariable Long classId
	) {
		List<ActivityResponseDto> response = activityService.getByClass(classId);

		return ResponseDTO.ok(
				"Get activities by class successful",
				response
		);
	}

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@GetMapping("/summaries")
	public ResponseDTO<List<ActivitySummaryResponseDto>> getSummaries(
			@PathVariable Long classId
	) {
		List<ActivitySummaryResponseDto> response = activityService.getSummaries(classId);

		return ResponseDTO.ok(
				"Get activity summaries successful",
				response
		);
	}

}