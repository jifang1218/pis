package com.pms.entities;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.pms.constants.PMSAuthType;

import lombok.Data;

@Data
public class PMSLoginInfo {
	@NotNull(message="email cannot be null")
	@NotEmpty(message="email cannot be empty")
	@Email
	private String email;
	
	@NotNull(message="password cannot be null")
	@NotEmpty(message="password cannot be empty")
	private String password;

	private PMSAuthType authType = PMSAuthType.system;
}
