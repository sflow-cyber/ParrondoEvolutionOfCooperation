package ncs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Simulation {

	public static void main(String[] args) {
		File file1 = null;
		File file2 = null;
		FileWriter fw1 = null;
		FileWriter fw2 = null;
		LatticeNetwork lattice = new LatticeNetwork();
		SFNetwork sfNetwork = new SFNetwork();
		lattice.setSize(10000);
		sfNetwork.setSize(10000);
		sfNetwork.setDensity(4);
		lattice.setP(0.5);
		sfNetwork.setP(0.5);
		lattice.build();
		sfNetwork.build();
		lattice.setGame(0.7, 1.99);
		sfNetwork.setGame(0.7, 1.99);
		file1 = new File("C:\\Users\\Friedrich\\Dropbox\\BAC14\\SimulationResults\\LatticeTaubFalk199.txt");
		file2 = new File("C:\\Users\\Friedrich\\Dropbox\\BAC14\\SimulationResults\\SFNetwork_roNewTaubFalk.txt");
		try {
			fw1 = new FileWriter(file1);
			fw2 = new FileWriter(file2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(double ro = 0d; ro <= 1; ro += 0.1) {
			lattice.setRo(ro);
			sfNetwork.setRo(ro);
			for(int i = 0; i < 10000; i++) {
				lattice.playRound();
				sfNetwork.playRound();
				lattice.updateStrategy();
				sfNetwork.updateStrategy();
			}
			try {
				fw1.write(lattice.getCoopFrac() + " ");
				fw2.write(sfNetwork.getCoopFrac() + " ");
				fw1.flush();
				fw2.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			lattice.populate();
			sfNetwork.populate();
		}
		try {
			fw1.write("\n");
			fw2.write("\n");
			fw1.flush();
			fw2.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}



	}

}
