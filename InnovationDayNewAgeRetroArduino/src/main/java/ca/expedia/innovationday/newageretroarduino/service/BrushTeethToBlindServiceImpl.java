package ca.expedia.innovationday.newageretroarduino.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import ca.expedia.innovationday.newageretroarduino.service.domain.JenkinsProjectInfo;

public class BrushTeethToBlindServiceImpl implements BrushTeethToBlindService {

	@Autowired private NastySound nastySound;
	JenkinsProjectInfo projectInfo;
	
	public void brushTeeth() {
		List<String> response = getJenkinsResponse("http://chellssbuild002:8080/jenkins/view/Lodging%20Services/job/lodging.services_content_score/api/json");
		projectInfo = buildProjectInfo(response.get(0));
	
		if (projectInfo.getCurrentBuildNumber() != projectInfo.getSuccessBuildNumber()) {
			System.out.println("loud noises!");
			System.out.println("Build breaker: "+getNameOfHeathen(projectInfo.getCurrentBuildNumber()));
			nastySound.on(1000);
		} else {
			System.out.println("All good");
		}
	}
	
	private String getNameOfHeathen(long projectID) {
		String url = "http://chellssbuild002:8080/jenkins/view/Lodging%20Services/job/lodging.services_content_score/"+Long.toString(projectID)+"/api/json";
		System.out.println(url);
		List<String> response = getJenkinsResponse(url);
		JSONParser parser = new JSONParser();
		String heathen = "";
		try {
			Object obj = parser.parse(response.get(0));
			JSONObject jsonObject = (JSONObject) obj;
			
			JSONArray culprits = (JSONArray) jsonObject.get("culprits");
			JSONObject culprit = (JSONObject) culprits.get(0);
			heathen = (String) culprit.get("fullName");
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		
		return heathen;
	}
	
	private JenkinsProjectInfo buildProjectInfo(String input) {
		JSONParser parser = new JSONParser();
		JenkinsProjectInfo projectInfo = new JenkinsProjectInfo();
		
		try {
			Object obj = parser.parse(input);
			JSONObject jsonObject = (JSONObject) obj;
			
			JSONObject lastStableBuild = (JSONObject) jsonObject.get("lastStableBuild");
			projectInfo.setSuccessBuildNumber((Long) lastStableBuild.get("number"));
			
			JSONObject lastFailedBuild = (JSONObject) jsonObject.get("lastFailedBuild");
			projectInfo.setFailBuildNumber((Long) lastFailedBuild.get("number"));
			
			JSONObject currentBuild = (JSONObject) jsonObject.get("lastBuild");
			projectInfo.setCurrentBuildNumber((Long) currentBuild.get("number"));
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		return projectInfo;
	}
	
	private List<String> getJenkinsResponse(String url) {
		List<String> output = new ArrayList<String>();
		
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(url);
			
			HttpResponse response = httpClient.execute(getRequest);
			
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("HTTP error: "+ response.getStatusLine().getStatusCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			
			
			String line;
			while ((line = br.readLine()) != null) {
				output.add(line);
			}
			
			if (output.size() > 1) {
				System.out.println("Problem from Jenkins: Got more than one project in response.");
			} 
			
			httpClient.getConnectionManager().shutdown();
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e ) { 
			e.printStackTrace();
		}
		
		return output;
	}
	

}
