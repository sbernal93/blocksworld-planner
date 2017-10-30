package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
	public static Map<String, Object> loadInput(String filepath) {
		String line;
		try {
			BufferedReader fin=new BufferedReader(new FileReader(filepath));
			Map<String, Object> map = new HashMap<String, Object>();
			List<Block> blockList = new ArrayList<>();
			List<Predicate> initStatePredicates = new ArrayList<>();
			List<Predicate> goalStatePredicates = new ArrayList<>();
			State initialState = new State();
			State goalState = new State();
			
			for (int i=0; i<4; i++) {
				line = fin.readLine();
				int positionEqualSign = line.indexOf("=");
				String section = line.substring(0, positionEqualSign);
				System.out.println("Section: "+section);
				line=line.substring(positionEqualSign+1);
				StringTokenizer tk = new StringTokenizer(line,".");
				
				while (tk.hasMoreTokens()) {

					String tkn = tk.nextToken();
					tkn = tkn.replace(";", "");
					if(section.equals("MaxColumns")) {
						map.put(section, Integer.valueOf(tkn));
					}
					if(section.equals("Blocks")) {
						blockList.add(new Block(tkn.substring(0,1), tkn.length()-1));
					}
					if(section.equals("InitialState")) {
						initStatePredicates.add(getPredicateFromLine(tkn));
					}
					if(section.equals("GoalState")) {
						goalStatePredicates.add(getPredicateFromLine(tkn));
					}
					System.out.println(tkn);
				}
				if(section.equals("Blocks")) {
					map.put(section, blockList);
				}
				if(section.equals("InitialState")) {
					initialState.setPredicates(initStatePredicates);
					map.put("InitialState", initialState);
				}
				if(section.equals("GoalState")) {
					goalState.setPredicates(goalStatePredicates);
					map.put("GoalState", goalState);
				}
				System.out.println("---------------------- \n");
			
			}
			System.out.println("Map is: " + map);
			fin.close();
			return map;
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Input/output error");
		}
		return null;
	}
	
	private static Predicate getPredicateFromLine(String tkn) {
		String[] predicateSplit = tkn.split("\\(");
		String predicateName = predicateSplit[0];
		predicateName = predicateName.replace("-", "_");
		String[] paramSplit = predicateSplit[1].replace(")", "").split(",");
		int c = 0;
		List<Block> blockList = new ArrayList<>();
		ArmType type = ArmType.GENERIC;
		while(c < paramSplit.length) {
			if(paramSplit[c].equals("L") || paramSplit[c].equals("R")) {
				if(paramSplit[c].equals("L")) {
					type = ArmType.LEFT;
				} else {
					type = ArmType.RIGHT;
				}
			} else {
				blockList.add(new Block(paramSplit[c]));
			}
			c++;
		}
		if(blockList.size()>0) {
			if(type.equals(ArmType.GENERIC)){
				return new Predicate(PredicateName.valueOf(predicateName), blockList);
			} else {
				return new Predicate(PredicateName.valueOf(predicateName), blockList, type);
			}
		} else {
			return new Predicate(PredicateName.valueOf(predicateName), type);
		}
	}

}
