package ncs;

import java.util.LinkedList;
import java.util.ListIterator;

public class LatticeNetwork extends Network {

	private int size;	// number of vertices
	private int gridSize;	// side length of square lattice
	private double ro; // prob. for unconditional imitation rule
	private double p;	// prob. for agent to have defective strategy
	private Vertex[] vertices;
	private DilemmaGame game; // the game that the agents in the 
	// network play

	private double pCopy;
	/*
	 * each agent plays a dilemma
	 * game against all its neighbors
	 */
	@Override
	public void playRound() {
		DoublePair payoffs = null;
		for(int i = 0; i < this.size; i++) {
			Agent currentAgent = this.vertices[i].getAgent();
			ListIterator<Vertex> iter = this.vertices[i].getneighbors().listIterator();
			while(iter.hasNext()) {
				Vertex n = iter.next();
				if(!n.matched(this.vertices[i]) && !this.vertices[i].matched(n)) {
					Agent newAgent = n.getAgent();
					payoffs = this.game.gamble(currentAgent, newAgent);
					currentAgent.adaptSuccess((double)payoffs.getA());
					newAgent.adaptSuccess((double)payoffs.getB());
					n.addMatched(this.vertices[i]);
					this.vertices[i].addMatched(n);
				}
			}
		}
		for(int i = 0; i < this.size; i++) {
			vertices[i].resetMatched();
		}
	}

	/*
	 * get the fraction of agents that
	 * have a cooperative strategy at the moment
	 */
	@Override
	public double getCoopFrac() {
		int cNumb = 0;
		for(int i = 0; i < this.size; i++)
			if(this.vertices[i].getAgent().getStrategy().toString().equals("COOP"))
				cNumb++;
		return (double)cNumb / this.size;
	}

	@Override
	public void populate() {
		for(int i = 0; i < this.size; i++) 
			this.vertices[i].populate(new Agent(this.p));
	}

	/*
	 * each agent updates its strategy
	 * depending on some probability for the update rule
	 * used and the success of his neighbors.
	 */
	@Override
	public void updateStrategy() {
		for(int i = 0; i < this.size; i++) {
			if(Math.random() < this.ro) {
				// unconditional imitation --> imitate the most successful neighbor
				Agent msNeighbor = this.vertices[i].getAgent();
				ListIterator<Vertex> iter = this.vertices[i].getneighbors().listIterator();
				while(iter.hasNext()) {
					Agent nextAgent = iter.next().getAgent();
					if(nextAgent.getSuccess() > msNeighbor.getSuccess())
						msNeighbor = nextAgent;
				}
				this.vertices[i].getAgent().setNextStrategy(msNeighbor.getStrategy());
			} else {
				LinkedList<Vertex> neighbors = this.vertices[i].getneighbors();
				Vertex rndmNeighbor = neighbors.get((int)(Math.random() * neighbors.size()));
				if(vertices[i].getAgent().getSuccess() < rndmNeighbor.getAgent().getSuccess()) {
					double piN = rndmNeighbor.getAgent().getSuccess() / rndmNeighbor.getneighbors().size();
					double piV = vertices[i].getAgent().getSuccess() / vertices[i].getneighbors().size();
					double diff = super.getDiff(piN, piV);
					if(piN < piV)
						diff = -diff;
					this.pCopy = 1d/2 + (1d/2)*(diff / super.getDiff(
							Math.max(this.game.getT(),1d),Math.min(this.game.getS(),0d)));
					if(this.pCopy < 0 || this.pCopy > 1) {
						System.out.println("Error, pCopy out of range: " + this.pCopy);
						System.out.println("piN: " + piN + ", piV " + piV + ", diff: " + diff + ", delta: " + super.getDiff(this.game.getT(), this.game.getS()));	
					}
					if(Math.random() < this.pCopy)
						this.vertices[i].getAgent().setNextStrategy(rndmNeighbor.getAgent().getStrategy());
					else
						this.vertices[i].getAgent().setNextStrategy(this.vertices[i].getAgent().getStrategy());
				} else {
					this.vertices[i].getAgent().setNextStrategy(this.vertices[i].getAgent().getStrategy());
				}
			}
		}
		for(int i = 0; i < this.size; i++) {
			Agent uAgent = this.vertices[i].getAgent();
			uAgent.updateStrategy();
			uAgent.resetSuccess();
		}
	}


	/*
	 * build a lattice network
	 */
	@Override
	public void build() {
		if(this.gridSize <= 0) {
			System.out.println("Size must be positive.");
			return;
		}
		assert gridSize > 0;
		if(this.p < 0 || this.p > 1) {
			System.out.println("p must be between 0 and 1.");
			return;
		}
		assert 0 <= this.p && this.p <= 1;
		Vertex[][] lattice = new Vertex[this.gridSize][this.gridSize];
		this.vertices = new Vertex[this.size];
		for(int i = 0; i < this.gridSize; i++) {
			for(int j = 0; j < this.gridSize; j++) {
				lattice[i][j] = this.vertices[i * this.gridSize + j] = new Vertex();
			}
		}
		for(int i = 0; i < this.gridSize; i++) {
			for(int j = 0; j < this.gridSize; j++) {
				for(int x = -1; x < 2; x++) {
					loop: for(int y = -1; y < 2; y++) {
						if(x == 0 && y == 0)
							continue loop;
						this.vertices[i*this.gridSize + j].addNeighbour(lattice[modulo(i+x,this.gridSize)][modulo(j+y,this.gridSize)]);
					}
				}
			}
		}
		this.populate();
	}

	@Override
	public void setGame(double s, double t) {
		game = new DilemmaGame(s,t);
	}

	@Override
	public void setP(double p) {
		if(p < 0 || p > 1)
			return;
		assert p >= 0 && p <= 1;
		this.p = p;
	}

	@Override
	public void setSize(int size) {
		if(Math.pow((int)Math.sqrt(size), 2) != size) {
			System.out.println(
					"Size of lattice network must be a "
							+ "square number. Size is automatically "
							+ "rounded off to next "
							+ "square integer."); 
			size = (int)Math.pow((int)Math.sqrt(size), 2);
		}
		// size is a square integer
		this.size = size;
		this.gridSize = (int)(Math.sqrt(size));
	}

	public int getSize() {
		return this.size;
	}

	@Override
	public void setDensity(double dentsity) {
		// do nothing;
		// each vertex in the network has
		// 8 neighbors
	}

	@Override
	public void setRo(double ro) {
		if(ro < 0 || ro > 1) {
			System.out.println("ro must be between "
					+ "0 and 1.");
			return;
		}
		assert 0 <= ro && ro <= 1;
		this.ro = ro;
	}


	@Override
	public String toString() {
		String output = new String("");
		for(int i = 0; i < this.size; i++) {
			if(i % this.gridSize == 0)
				output += "\r\n";
			output += (vertices[i].getAgent().getStrategy().toString().equals("COOP") ?
					"+" : "o") + " ";
		}
		return output + "\r\n";
	}
	
	public String toVal() {
		String output = new String("");
		for(int i = 0; i < this.size; i++) {
			if(i % this.gridSize == 0)
				output += "\r\n";
			output += Math.round(vertices[i].getAgent().getSuccess()*10)/10d + " ";
		}
		return output + "\r\n";
	}

	@Override
	public void resetSuccess() {
		for(int i = 0; i < this.size; i++) 
			vertices[i].getAgent().resetSuccess();
	}
}
