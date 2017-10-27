package model;

import java.util.ArrayList;
import java.util.List;

public class Planner {
	
	private List<Plan> plans;

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
