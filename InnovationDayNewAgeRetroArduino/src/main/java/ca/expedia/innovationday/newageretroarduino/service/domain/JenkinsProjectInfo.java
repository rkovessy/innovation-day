package ca.expedia.innovationday.newageretroarduino.service.domain;

import lombok.Data;

@Data
public class JenkinsProjectInfo {

	private Long currentBuildNumber;
	private Long successBuildNumber;
	private Long failBuildNumber;

}
