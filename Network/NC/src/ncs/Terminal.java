package ncs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Terminal {

	public static void main(String[] args) {

		Network network = null;
		HashMap<String, Network> networks = new HashMap<String, Network>();
		networks.put("L", new LatticeNetwork());
		networks.put("S", new SFNetwork());

		Scanner sc = new Scanner(System.in);

		System.out.println("Enter type of network (L(attice),R(andom),S(cale free)):");
		network = networks.get(sc.next());
		System.out.println("Enter network size:");
		network.setSize(sc.nextInt());	// number of vertices
		if(network instanceof SFNetwork) {
			System.out.println("Enter network density:");
			network.setDensity(sc.nextDouble());	// avg. number of edges
		}
		System.out.println("Enter game parameters (s,t):");
		// sucker and temptation payoff
		network.setGame(sc.nextDouble(), sc.nextDouble()); 
		System.out.println("Enter prob. for unconditional imitation rule:");
		network.setRo(sc.nextDouble());
		System.out.println("Enter prob. for an agent to have defective "
				+ "strategy in the beginning:");
		network.setP(sc.nextDouble());
		network.build();
		System.out.println("Enter number of iterations:");
		int numbIter = sc.nextInt();
		System.out.println("Enter file path if you want to save "
				+ "(skip with \"n\"):");
		String filePath = sc.next();
		File file = null;
		FileWriter fw = null;
		boolean writeToFile = false;
		if(!filePath.equals("n")) {
			writeToFile = true;
			file = new File(filePath);
			fw = null;
			try {
				fw = new FileWriter(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(network instanceof LatticeNetwork) {
			System.out.println(network.toString());
			System.out.println(((LatticeNetwork)(network)).toVal());
		}
		if(writeToFile) {
			try {
				fw.write(network.toString());
				fw.write(((LatticeNetwork)(network)).toVal());
				fw.flush();
			} catch(IOException e1) {
				e1.printStackTrace();
			}
		}
		loop: for(int i = 0; i < numbIter; i++) {
			network.playRound();
			if(i < 10 || i % 100 == 0) {
				if(network instanceof LatticeNetwork) {
					System.out.println(((LatticeNetwork)(network)).toVal());
				}
			}
			if(writeToFile) {
				try {
					fw.write(((LatticeNetwork)(network)).toVal()+ "\r\n");
					fw.flush();
				} catch(IOException e1) {
					e1.printStackTrace();
				}
			}
			network.updateStrategy();
			double coopFrac = network.getCoopFrac();
			System.out.println(coopFrac);
			if(writeToFile) {
				try {
					fw.write(coopFrac + "\r\n");
				} catch(IOException e1) {
					e1.printStackTrace();
				}
			}
			if(i < 10 || i % 100 == 0) {
				if(network instanceof LatticeNetwork) {
					System.out.println(network.toString());
				}
				if(writeToFile) {
					try {
						fw.write(network.toString() + "\r\n");
						fw.flush();
					} catch(IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			if(coopFrac == 0 || coopFrac == 1)
				break loop;
		}
		try{
			sc.close();
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}

}
