package ncs;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class SFNetwork extends Network {

	private int size;	// number of vertices
	private double density;	    // avg. number of edges (neighborhood connections) between vertices
	private double ro;	// prob. for unconditional imitation rule
	private double p;	// prob. for agent to have defective strategy
	private DilemmaGame game;   // the game that the agents in the network play
	private Vertex[] vertices;	// vertices of network
	private int[] noN;			// number of neighbors of each vertex
	private double[] pdf;		// fraction of connections per vertex to total number of connections


	@Override
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public void setDensity(double density) {
		this.density = density;

	}

	@Override
	public void setRo(double ro) {
		this.ro = ro;

	}

	@Override
	public void setGame(double s, double t) {
		this.game = new DilemmaGame(s,t);

	}

	@Override
	public void setP(double p) {
		this.p = p;

	}

	/*
	 * 
	 * build a scale free network according to
	 * the  Barabási–Albert model
	 */
	@Override
	public void build() {
		this.vertices = new Vertex[this.size];
		this.noN = new int[this.size];
		this.pdf = new double[this.size];
		for(int i = 0; i < this.vertices.length; i++) {
			this.vertices[i] = new Vertex();
		}
		int counter = 1;
		// start with 'density' initially connected vertices
		while(counter < this.density) {
			int rndm = (int)(Math.random() * counter);
			assert rndm < counter;
			this.vertices[counter].addNeighbourMutually(this.vertices[rndm]);
			this.noN[counter++]++;
			this.noN[rndm]++;
		}
		// add vertices to the network and connect each one to
		// 'density' existing nodes according to the power law
		while(counter < this.size) {
			for(int i = 0; i < this.density; i++) {
				updatePdf(counter);
				int rndm = 0;
				do {
					rndm = this.getRndm(pdf);
				} while(this.vertices[counter].getneighbors().contains(this.vertices[rndm]) ||
						this.vertices[rndm].getneighbors().contains(this.vertices[counter]));	
				// draw new rndm while connection already exists --> 
				// however, this should not happen (p->0)
				this.vertices[counter].addNeighbourMutually(this.vertices[rndm]);
				this.noN[counter]++;
				this.noN[rndm]++;
			}
			counter++;
		}
		this.populate();

		assert !allNDist();

	}

	private void updatePdf(int k) {
		int noC = 0;	// total number of connections
		for(int i = 0; i < noN.length; i++) {
			if(!this.vertices[i].getneighbors().contains(this.vertices[k]) && i != k)
				noC += this.noN[i];
		}
		for(int i = 0; i < this.pdf.length; i++) {
			if(!this.vertices[i].getneighbors().contains(vertices[k]) && i != k)
				this.pdf[i] = (double)this.noN[i] / noC;
			else
				this.pdf[i] = 0d;
		}
	}


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
					double pCopy = 1d/2 + (1d/2)*(diff / super.getDiff(
							Math.max(this.game.getT(),1d),Math.min(this.game.getS(),0d)));
					if(pCopy < 0 || pCopy > 1)
						System.out.println("Error, pCopy out of range: " + pCopy);
					if(Math.random() < pCopy)
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

	private boolean allNDist() {
		for(int i = 0; i < this.size; i++) {
			this.vertices[i].getneighbors().sort(new Comparator<Vertex>() {

				@Override
				public int compare(Vertex v1, Vertex v2) {
					return v1.getIndex() < v2.getIndex() ? 1 :
						v1.getIndex() > v2.getIndex() ? -1 : 0;
				}

			});
			Iterator<Vertex> iter = this.vertices[i].getneighbors().iterator();
			Vertex actVert = iter.next();
			while(iter.hasNext()) {
				Vertex nextVert = iter.next();
				if(actVert == nextVert) {
					return false;
				}
				actVert = nextVert;
			}
		}
		return true;
	}

	@Override
	public void populate() {
		for(int i = 0; i < this.size; i++) 
			this.vertices[i].populate(new Agent(this.p));
	}
	
	@Override
	public void resetSuccess() {
		for(int i = 0; i < this.size; i++) 
			vertices[i].getAgent().resetSuccess();
	}

}
