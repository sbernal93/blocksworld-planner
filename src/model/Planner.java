package model;

import java.util.ArrayList;
import java.util.List;

public class Planner {
	
	private List<Plan> plans;
	private List<Plan> failedPlans;
	private Plan completedPlan;

	public Planner(){
		this.plans = new ArrayList<>();
		this.failedPlans = new ArrayList<>();
	}
	
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

	public List<Plan> getFailedPlans() {
		return failedPlans;
	}

	public void setFailedPlans(List<Plan> failedPlans) {
		this.failedPlans = failedPlans;
	}

	public void addPlan(Plan plan) {
		if(this.plans == null) {
			this.plans = new ArrayList<>();
		}
		this.plans.add(plan);
	}
	
	public void addFailedPlan(Plan plan) {
		if(this.failedPlans == null) {
			this.failedPlans = new ArrayList<>();
		}
		this.failedPlans.add(plan);
	}
}
