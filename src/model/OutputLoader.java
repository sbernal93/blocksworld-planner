package model;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringJoiner;

public class OutputLoader {

	public static void generateOutput(Planner planner) {
		try {
			StringJoiner operatorJoiner = new StringJoiner(",");
			for(State state : planner.getCompletedPlan().getStates()) {
				operatorJoiner.add(state.getOperatorUsedToReachState().toString());
			}
			List<String> lines = new ArrayList<>();
			lines.add(String.valueOf(planner.getCompletedPlan().getStates().size()));
			lines.add(String.valueOf(planner.getCompletedPlan().getStates().size()));
			lines.add(operatorJoiner.toString());
			lines.add("------------------------------------------------------------");
			for (Plan plan : planner.getPlans()) {
				State lastState = plan.getStates().get(plan.getStates().size()-1);
				//added this validation to make sure we dont add the finished plan
				if(!lastState.isValid()) {
					StringJoiner predicateJoiner = new StringJoiner(",");
					for(Predicate predicate : lastState.getPredicates()) {
						predicateJoiner.add(predicate.toString());
					}
					lines.add(predicateJoiner.toString());
					lines.add(lastState.getReasonForInvalidState());
					lines.add("------------------------------------------------------------");
 				}
			}
			String fileName = "par-activity-output" + Calendar.getInstance().getTimeInMillis() + ".txt";
			Path file = Paths.get(fileName);
			Files.write(file, lines, Charset.forName("UTF-8"));
			System.out.println("Output file is: " + fileName);
		}catch (Exception e) {
			System.out.println("There was an error creating output file");
			e.printStackTrace();
		}
	}
}
