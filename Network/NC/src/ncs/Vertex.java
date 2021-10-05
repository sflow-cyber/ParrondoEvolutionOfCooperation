package ncs;

import java.util.LinkedList;
import java.util.ListIterator;

public class Vertex {
	
	private static int instanceCounter;	// counts the instances of 'Vertex' created so far 

	private Agent agent;	// a vertex has a unique agent 
	private LinkedList<Vertex> neighbors;	// a vertex has 1-* neighbors
	private int index;		// a vertex has a unique index
	private LinkedList<Vertex> matched;

	public Vertex() {
		this.index = instanceCounter++;
		this.neighbors = new LinkedList<Vertex>();
	}

	public boolean addNeighbourMutually(Vertex v) {
		return this.neighbors.add(v) && 
				v.addNeighbour(this);
	}

	public boolean addNeighbour(Vertex v) {
		return this.neighbors.add(v);
	}

	public void populate(Agent agent) {
		this.agent = agent;
	}

	public LinkedList<Vertex> getneighbors() {
		return this.neighbors;
	}
	
	public int getIndex() {
		return this.index;
	}

	public Agent getAgent() {
		return this.agent;
	}
	
	public void addMatched(Vertex v) {
		if(this.matched == null)
			this.matched = new LinkedList<Vertex>();
		this.matched.add(v);
	}

	public boolean matched(Vertex v) {
		if(this.matched == null)
			return false;
		return this.matched.contains(v);
	}
	
	public void resetMatched() {
		this.matched = new LinkedList<Vertex>();
	}
	
	

	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		if(!(o instanceof Vertex))
			return false;
		Vertex v = (Vertex)o;
		// index is a unique identifier
		return this.index == v.getIndex();	
	}

	public int getEdgeCount() {
		return neighbors.size();
	}

	

	public void play(DilemmaGame game) {
		ListIterator<Vertex> iter =
				neighbors.listIterator();
		while(iter.hasNext()) {
			Vertex nextVertex = iter.next();
			if(nextVertex.getIndex() > this.index) {
				DoublePair db = game.gamble(this.agent, nextVertex.getAgent());
				this.agent.adaptSuccess((double)db.getA());
				nextVertex.getAgent().adaptSuccess((double)db.getB());
				nextVertex.play(game);
			}
		}
	}
	
	
	/*
	 * return the most successful neighbour from
	 * the neighbourhood
	 */
	public Agent getMSNeighbour() {
		Agent ms = this.agent;
		ListIterator<Vertex> iter = neighbors.listIterator();
		while(iter.hasNext()) {
			Agent nextAgent = iter.next().getAgent();
			if(nextAgent.getSuccess() > this.agent.getSuccess())
				ms = nextAgent;
		}
		return ms;
	}

}
