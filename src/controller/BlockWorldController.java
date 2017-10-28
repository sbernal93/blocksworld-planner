package controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import model.Block;
import model.InputLoader;
import model.Operator;
import model.OperatorLoader;
import model.Plan;
import model.Planner;
import model.Predicate;
import model.State;


public class BlockWorldController {

	private List<Block> blocks;
	private int maxColumns;
	private State initialState;
	private State goalState;
	private State currentState;
	private List<Operator> operators;
	private int usedColumns;
	private int numOfSteps;
	private Planner planner;
	
	@SuppressWarnings("unchecked")
	public void loadBlockWorld(String filepath){
		operators = OperatorLoader.loadOperators();
		Map<String, Object> inputMap = InputLoader.loadInput(filepath);
		blocks = (List<Block>) inputMap.get("Blocks");
		maxColumns = (int) inputMap.get("MaxColumns");
		initialState = (State) inputMap.get("InitialState");
		goalState =  (State) inputMap.get("GoalState");
	}
	
	
	//TODO: test 
	public void start(){
		planner = new Planner();
		List< State> newPossibleStates = calculateNewStates(goalState);
		for(State posibleState : newPossibleStates) {
			Plan plan = new Plan(goalState);
			plan.addState(posibleState);
			planner.addPlan(plan);
		}
		for(Plan plan : planner.getPlans()) {
			while(!plan.isCompletedPlan()) {
				//TODO: validate that plan changes inside method
				planner = calculatePlan(planner, plan);
			}
		}
		//at the end of this loop the planner object should have all the possible plans
		//that where created, including failed ones and valid ones
	}
	
	/**
	 * Planner is returned because new plans can result from calculating a plan
	 * this method calculate the next possible states that can be the outcome of a plan
	 * by applying diferent operators.
	 * @param planner
	 * @param plan
	 * @return
	 */
	private Planner calculatePlan(Planner planner, Plan plan) {
		//the last state of the plan is used to validate if we finished
		//or to determine where to continue
		State finalStateInList = plan.getStates().get(plan.getStates().size()-1);
		if(finalStateInList.areEqualStates(initialState)) {
			plan.setCompletedPlan(true);
			plan.setValidPlan(true);
		}
		if(isPossibleState(finalStateInList)) {
			List< State> newPossibleStates = calculateNewStates(goalState);
			boolean firstIt = true;
			for(State possibleState : newPossibleStates) {
				if(firstIt) {
					//the current plan is the first to change, then we generate new plans that may be possible
					plan.addState(possibleState);
					firstIt = false;
				} else {
					//a new plan is generated exactly as the one we are calculating but
					//with a new state resulting from a different operator
					//this new plan is added to the planner to be processed later
					//this plan may not be possible
					Plan newPlan = plan.makeCopyPlan();
					newPlan.addState(possibleState);
					planner.addPlan(newPlan);
				}
			}
		} else {
			//the plan is invalid since we reached an impossible state
			//the plan is completed because we cannot continue this path
			plan.setValidPlan(false);
			plan.setCompletedPlan(true);
		}
		return planner;
	}
	
	
	private boolean isPossibleState(State state) {
		return false;
	}
	
	private List< State> calculateNewStates(State currentState) {
		List<Operator> operatorsFound = new ArrayList<>();
		Map<Predicate, List<Operator>> operatorsFoundForPredicate = new HashMap<>();
		for (Predicate predicate : currentState.getPredicates()) {
			operatorsFound = getOperatorsThatHasPredicateInAddList(predicate);
			operatorsFoundForPredicate.put(predicate, operatorsFound);
		}
		List< State> stateList = new ArrayList<>();
		for(Map.Entry<Predicate, List<Operator>> entry : operatorsFoundForPredicate.entrySet()) {
			for(Operator operator : entry.getValue()) {
				stateList.addAll(stateFoundAfterOperator(currentState, operator, entry.getKey()));
			}
		}
		/*for(Operator operator : operatorsFound) {
			stateList.add(stateFoundAfterOperator(currentState, operator));
		}*/
		return stateList;
	}
	
	private List<Operator> getOperatorsThatHasPredicateInAddList(Predicate predicate) {
		List<Operator> operatorList = new ArrayList<>();
		for(Operator operator : operators) {
			if(operator.getAddList().stream().anyMatch(p -> p.getName().equals(predicate.getName()))) {
				operatorList.add(operator);
			}
		}
		return operatorList;
	}
	
	/**
	 * This method applies the operator to the state, even if the output is an impossible state,
	 * it will be validated later
	 * @param state
	 * @param operator
	 * @return
	 */
	private List<State> stateFoundAfterOperator(State state, Operator operator, Predicate predicate) {
		List<State> statesFound = new ArrayList<>();
		//stateFound.setOperatorUsedToReachState(operator);
		List<Predicate> predicates = state.getPredicates();
		List<Operator> operatorsWithBlocks = getOperatorsWithBlocks(operator, predicate);
		for(Operator operatorWithBlocks: operatorsWithBlocks) {
			State stateFound = new State();
			//Java 8 magic, finds if any of the predicates in the del list of the operator
			//matches any of the predicates in the current state (if so this state is invalid)
			List<Predicate> found = operatorWithBlocks.getDelList().parallelStream()
					 .filter( operatorDelPredicate-> state.getPredicates()
	                   .parallelStream()
	                   .anyMatch(operatorDelPredicate::equalPredicate)
					 )
					  .collect(Collectors.toList());
			/*return state.getPredicates()
								 .parallelStream()
								 .anyMatch(operatorDelPredicate::equalPredicate);*/
			//.anyMatch( statePredicate->operatorDelPredicate.equalPredicate(statePredicate) );
			if(found.size()>0) {
				stateFound.setValid(false);
				stateFound.setReasonForInvalidState("After applying operator: [" + operator.toString() + "] "
						+ " predicate: [" + found.get(0).toString() + "] would have been removed");
			}
			predicates.remove(predicate);
			predicates.addAll(operatorWithBlocks.getPreconditions());
			stateFound.setOperatorUsedToReachState(operatorWithBlocks);
			stateFound.setPredicates(predicates);
			stateFound = removeRepeatedPredicates(stateFound);
			statesFound.add(stateFound);
		}
		
		return statesFound;
	}
	
	/**
	 * Removes any repeated predicate a state may have
	 * @param state
	 * @return
	 */
	private State removeRepeatedPredicates(State state) {
		List<Predicate> predicates = state.getPredicates().stream().distinct().collect(Collectors.toList());
		state.setPredicates(predicates);
		return state;
	}

	/**
	 * Method that gets the operator with the correct blocks assigned so it matches the predicate
	 * @param operator
	 * @param predicate
     * @return
     */
	private List<Operator> getOperatorsWithBlocks(Operator operator, Predicate predicate) {
		Predicate addPredicateFromOperator;
		List<Operator> operatorsWithBlocks = new ArrayList<>();
		Operator operatorWithBlocks = new Operator();
		if(operator.getParamList().size() == predicate.getVariables().size()) {
			//Tries to find the operator so that the add list element matches the predicate blocks
			if(operator.getParamList().size() == 2) {
				operatorWithBlocks = OperatorLoader.load(operator.getName(), predicate.getVariables().get(0).getName(), predicate.getVariables().get(1).getName());
				addPredicateFromOperator = operatorWithBlocks.getAddList().stream().filter(p -> p.getName().equals(predicate.getName())).findAny().orElse(null);
				if(!addPredicateFromOperator.equalPredicate(predicate)) {
					operatorWithBlocks = OperatorLoader.load(operator.getName(), predicate.getVariables().get(1).getName(), predicate.getVariables().get(0).getName());
					//addPredicateFromOperator = operatorWithBlocks.getAddList().stream().filter(p -> p.getName().equals(predicate.getName())).findAny().orElse(null);
				}
			} else {
				operatorWithBlocks = OperatorLoader.load(operator.getName(), predicate.getVariables().get(0).getName(), "");
			}
			operatorsWithBlocks.add(operatorWithBlocks);
		} else {
			if(operator.getParamList().size() == 2) {
				operatorWithBlocks = OperatorLoader.load(operator.getName(), predicate.getVariables().get(0).getName(), "X");
				addPredicateFromOperator = operatorWithBlocks.getAddList().stream().filter(p -> p.getName().equals(predicate.getName())).findAny().orElse(null);
				if(!addPredicateFromOperator.equalPredicate(predicate)) {
					operatorWithBlocks = OperatorLoader.load(operator.getName(), "X", predicate.getVariables().get(0).getName());
					//addPredicateFromOperator = operatorWithBlocks.getAddList().stream().filter(p -> p.getName().equals(predicate.getName())).findAny().orElse(null);
					operatorsWithBlocks = calculateValueForParam(operatorWithBlocks, 0);
				} else {
					operatorsWithBlocks = calculateValueForParam(operatorWithBlocks, 1);
				}
			}
		}
		return operatorsWithBlocks;
	}

	/**
	 * This method calculates all the possible values for the para in an operator,
	 * it does not validate if the param is valid
	 * @param operator
	 * @param posOfParam
	 * @return
	 */
	private List<Operator> calculateValueForParam(Operator operator, int posOfParam) {
		List<Operator> possibleOperators = new ArrayList<>();
		for (Block block : blocks) {
			Operator opParam = new Operator();
			if(posOfParam == 0) {
				opParam = OperatorLoader.load(operator.getName(), block.getName(), operator.getParamList().get(1).getName());
			} else {
				opParam = OperatorLoader.load(operator.getName(), operator.getParamList().get(0).getName(), block.getName());
			}
			possibleOperators.add(opParam);
		}
		return possibleOperators;
	}
}
