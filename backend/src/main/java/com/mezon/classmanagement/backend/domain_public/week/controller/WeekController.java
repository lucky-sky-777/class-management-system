package com.mezon.classmanagement.backend.domain_public.week.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.annotation.Public;
import com.mezon.classmanagement.backend.domain_public.week.dto.WeekResponseDto;
import com.mezon.classmanagement.backend.domain_public.week.service.WeekService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Public
@RequestMapping("/api/public/weeks")
@RestController
public class WeekController {

	WeekService weekService;

	@GetMapping
	public ResponseDTO<List<WeekResponseDto>> get(
			@RequestParam int year
	) {
		List<WeekResponseDto> response = weekService.getWeekList(year);

		return ResponseDTO.<List<WeekResponseDto>>builder()
				.success(true)
				.message("Get weeks successful")
				.data(response)
				.build();
	}

}