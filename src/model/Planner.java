package model;

import java.util.ArrayList;
import java.util.List;

public class Planner {
	
	private List<Plan> plans;
	private Plan completedPlan;

	public Plan getCompletedPlan() {
		return completedPlan;
	}

	public void setCompletedPlan(Plan completedPlan) {
		this.completedPlan = completedPlan;
	}

	public List<Plan> getPlans() {
		return plans;
	}

	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}

	public void addPlan(Plan plan) {
		if(this.plans == null) {
			this.plans = new ArrayList<>();
		}
		this.plans.add(plan);
	}
}
