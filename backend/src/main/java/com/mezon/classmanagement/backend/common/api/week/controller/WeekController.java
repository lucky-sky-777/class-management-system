package com.mezon.classmanagement.backend.common.api.week.controller;

import com.mezon.classmanagement.backend.common.api.week.dto.WeekResponseDto;
import com.mezon.classmanagement.backend.common.api.week.service.WeekService;
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
@RequestMapping("/api/public/weeks")
@RestController
public class WeekController {

	WeekService weekService;

	@GetMapping
	public List<WeekResponseDto> get(
			@RequestParam int year
	) {
		return weekService.getWeeks(year);
	}

}