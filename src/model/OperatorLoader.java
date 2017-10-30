package model;

import java.util.ArrayList;
import java.util.List;

public class OperatorLoader {
	
	public static Operator load(OperatorName name, Block x, Block y, ArmType type) {
		switch (name) {
		case LEAVE:
			return loadLeave(x, type);
		case PICK_UP:
			return loadPickUp(x);
		case PICKUP_LEFT:
			return loadPickUpLeft(x);
		case STACK:
			return loadStack(x, y, type);
		case UNSTACK:
			return loadUnStack(x, y);
		case UNSTACK_LEFT:
			return loadUnStackLeft(x, y);
		}
		return null;
	}
	
	public static List<Operator> loadOperators(){
		List<Operator> operators = new ArrayList<>();
		
		operators.add(loadStack(new Block("X"), new Block("Y"), ArmType.GENERIC));
		operators.add(loadUnStack(new Block("X"), new Block("Y")));
		operators.add(loadUnStackLeft(new Block("X"), new Block("Y")));
		operators.add(loadPickUp(new Block("X")));
		operators.add(loadPickUpLeft(new Block("X")));
		operators.add(loadLeave(new Block("X"), ArmType.GENERIC));
		return operators;
	}
	
	public static Operator loadStack(Block x, Block y, ArmType type) {
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		
		Block blockY = y;
		Block blockX = x;

		blockList.add(blockY);
		preconditions.add(new Predicate(PredicateName.CLEAR, blockList));
		delList.add(new Predicate(PredicateName.CLEAR, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.HOLDING, blockList, type));
		delList.add(new Predicate(PredicateName.HOLDING, blockList, type));
		addList.add(new Predicate(PredicateName.CLEAR, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockY);
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.HEAVIER, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		blockList.add(blockY);
		addList.add(new Predicate(PredicateName.ON, blockList));
		
		addList.add(new Predicate(PredicateName.EMPTY_ARM, type));
		
		return new Operator(OperatorName.STACK, preconditions, addList, delList, blockList, type);
		
	}
	
	private static Operator loadBasicUnstack(Block x, Block y, ArmType type){ 
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();

		Block blockX = x;
		Block blockY = y;
		
		preconditions.add(new Predicate(PredicateName.EMPTY_ARM, type));
		delList.add(new Predicate(PredicateName.EMPTY_ARM, type));

		blockList.add(blockY);
		addList.add(new Predicate(PredicateName.CLEAR, blockList));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.CLEAR, blockList));
		addList.add(new Predicate(PredicateName.CLEAR, blockList));
		addList.add(new Predicate(PredicateName.HOLDING, blockList, type));
		
		blockList = new ArrayList<>();
		blockList.add(blockX);
		blockList.add(blockY);
		preconditions.add(new Predicate(PredicateName.ON, blockList));
		
		
		return new Operator(OperatorName.UNSTACK, preconditions, addList, delList, blockList, type);
	}
	
	public static Operator loadUnStack(Block x, Block y) {
		return loadBasicUnstack(x,y, ArmType.RIGHT);
	}
	
	
	public static Operator loadUnStackLeft(Block x, Block y) {
		Operator operator = loadBasicUnstack(x,y, ArmType.LEFT);
		operator.setName(OperatorName.UNSTACK_LEFT);
		
		List<Block> blockList = new ArrayList<>();
		Block blockX = x;
		blockList.add(blockX);
		operator.addPredicateToPreconditions(new Predicate(PredicateName.LIGHT_BLOCK,blockList));
		return operator;
	}
	
	private static Operator loadBasicPickUp(Block x, ArmType type){
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		
		Block blockX = x;
	
		preconditions.add(new Predicate(PredicateName.EMPTY_ARM, type));

		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.CLEAR, blockList));
		preconditions.add(new Predicate(PredicateName.ON_TABLE, blockList));
		addList.add(new Predicate(PredicateName.HOLDING, blockList, type));
		delList.add(new Predicate(PredicateName.ON_TABLE, blockList));
		
		return new Operator(OperatorName.PICK_UP, preconditions, addList, delList, blockList, type);
	}
	
	public static Operator loadPickUp(Block x) {
		return loadBasicPickUp(x, ArmType.RIGHT);
	}
	
	public static Operator loadPickUpLeft(Block x){
		Operator operator = loadBasicPickUp(x, ArmType.LEFT);
		operator.setName(OperatorName.PICKUP_LEFT);
		
		List<Block> blockList = new ArrayList<>();
		Block blockX = x;
		blockList.add(blockX);
		operator.addPredicateToPreconditions(new Predicate(PredicateName.LIGHT_BLOCK,blockList));
		return operator;
	}
	
	public static Operator loadLeave(Block x, ArmType type) {
		List<Predicate> preconditions = new ArrayList<>();
		List<Predicate> addList = new ArrayList<>();
		List<Predicate> delList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		
		Block blockX = x;

		blockList = new ArrayList<>();
		blockList.add(blockX);
		preconditions.add(new Predicate(PredicateName.HOLDING, blockList, type));
		delList.add(new Predicate(PredicateName.HOLDING, blockList, type));
		addList.add(new Predicate(PredicateName.ON_TABLE, blockList));
		addList.add(new Predicate(PredicateName.CLEAR, blockList));
		
		addList.add(new Predicate(PredicateName.EMPTY_ARM, type));
		
		return new Operator(OperatorName.LEAVE, preconditions, addList, delList, blockList, type);
		
	}

}
