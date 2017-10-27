package model;

import java.util.Map;

public class InputLoader {
	
	/**
	 * Loads input in a map like:
	 * ["InitialState"={ON-TABLE(C),ON(B,C),ON(A,B),CLEAR(A);ON-TABLE(D),ON(F,D)},
	 * "MaxColumns"=3,Blocks={{A,2}, {B,1}, {C, 3}, {D, 4}, {F,5}} "
	 * 
	 * The map should have:
	 * "Blocks": List of {@link Block} objects with name and weight
	 * "MaxColumns": int with the number of max columns allowed
	 * "InitialState": a {@link State} object describing the initial state ONLY with the
	 * 					List of {@link Predicate}, the rest are calculated by the algorithm
	 * "GoalState": a {@link State} object describing the goal state ONLY with the
	 * 					List of {@link Predicate}, the rest are calculated by the algorithm
	 * @return
	 */
	public static Map<String, Object> loadInput() {
		//TODO
		return null;
	}

}
