package model;

public class Block {

	private String name;
	private int weight;

	public Block(String name) {
		super();
		this.name = name;
	}

	public Block(String name, int weight) {
		super();
		this.name = name;
		this.weight = weight;
	}
	

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
