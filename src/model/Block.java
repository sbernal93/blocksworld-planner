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

	@Override
	public String toString() {
		return "Block [name=" + name + ", weight=" + weight + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}
	
	
}
