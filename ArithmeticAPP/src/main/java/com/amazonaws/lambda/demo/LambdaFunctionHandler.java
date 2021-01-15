package com.amazonaws.lambda.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LambdaFunctionHandler implements RequestStreamHandler {
	JSONParser parser = new JSONParser();

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		
		LambdaLogger logger = context.getLogger();
		
		logger.log("Start -- lambda function execution");
		String path = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		JSONObject responseJson = new JSONObject();
		JSONObject event = null;
		String[] pathVariables = null;
		
		try {
			event = (JSONObject) parser.parse(reader);
			
			//reading the path parameters from the lambda events
			if (event.get("pathParameters") != null) {
				JSONObject pathParameters = (JSONObject) event.get("pathParameters");
				if (pathParameters.get("proxy") != null) {
					path = (String) pathParameters.get("proxy");
					//Splitting  the path and storing in the array
					pathVariables = path.split("/", 0);
             }
			}
		} catch (Exception ex) {
			responseJson.put("statusCode", "400");
			responseJson.put("exception", ex);
		}
		
		int output = 0;
		if (pathVariables.length == 1) {
			output = addTen(Integer.parseInt(pathVariables[0]));
		} else {
			output = sum(pathVariables);
		}
		
		//constructing the response.
		JSONObject responseBody = new JSONObject();
		responseBody.put("sum", output);
		responseJson.put("body", responseBody.toString());
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writer.write(responseJson.toJSONString());
		writer.close();
		
		logger.log("End -- lambda function execution");

	}
	
	public int addTen(int a) {
		return a + 10;

	}
	
	public int sum(String[] pathVariables)
	{
		int count = 0;
		for (int i = 0; i < pathVariables.length; i++) {
			int variable = Integer.parseInt(pathVariables[i]);
			count = count + variable;
		}
		return count;
	}
}
