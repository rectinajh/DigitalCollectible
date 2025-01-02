package com.nft.collection.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class AdjustComposeActivityTimeParam {

	@NotBlank
	private String id;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date activityTimeStart;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date activityTimeEnd;

}
