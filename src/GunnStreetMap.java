import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GunnStreetMap {

	static Node[][] cell;
	static ArrayList<Node> pathList = new ArrayList<>();
	static ArrayList<Node> closedList = new ArrayList<>();
	static boolean additionalPath = false;
	static int size = 200;

	// draw the N-by-N boolean matrix to standard draw
	public static void show(boolean[][] a, boolean which) {
		int N = a.length;
		StdDraw.setCanvasSize(1000, 1000);
		StdDraw.picture(0.5, 0.5, "D:/Downloads/GSMap/FATDAN.jpg");
		StdDraw.setXscale(-1, N);
		StdDraw.setYscale(-1, N);
		StdDraw.setPenColor(StdDraw.BLACK);
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				if (a[i][j] == which) {
					// StdDraw.square(j, N - i - 1, .5);
				} else {
					// StdDraw.filledSquare(j, N - i - 1, .5);
				}
	}

	// draw the N-by-N boolean matrix to standard draw, including the points A (x1,
	// y1) and B (x2,y2) to be marked by a circle
	public static void show(boolean[][] a, boolean which, int x1, int y1, int x2, int y2) {
		int N = a.length;
		StdDraw.setXscale(-1, N);
		StdDraw.setYscale(-1, N);
		StdDraw.setPenColor(StdDraw.BLACK);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (a[i][j] == which) {
					if ((i == x1 && j == y1) || (i == x2 && j == y2)) {
						StdDraw.circle(j, N - i - 1, .5);
					} else
						StdDraw.square(j, N - i - 1, .5);
				} else {
					// StdDraw.filledSquare(j, N - i - 1, .5);
				}
			}
		}
	}

	// return a random N-by-N boolean matrix, where each entry is
	// true with probability p
	public static boolean[][] random(int N, double p) {
		boolean[][] a = new boolean[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				a[i][j] = StdRandom.bernoulli(p);
		return a;
	}

	public static boolean[][] succ() {
		boolean[][] a = new boolean[size][size];
		String savedGameFile = "D:/Downloads/GSMap/cow.txt";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(savedGameFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line = "";
		int row = 0;
		try {
			while ((line = reader.readLine()) != null) {
				String[] cols = line.split(","); // note that if you have used space as separator you have to split on "
													// "
				int col = 0;
				for (String c : cols) {
					a[row][col] = Boolean.parseBoolean(c);
					col++;
				}
				row++;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}

	/**
	 * @param matrix         The boolean matrix that the framework generates
	 * @param Ai             Starting point's x value
	 * @param Aj             Starting point's y value
	 * @param Bi             Ending point's x value
	 * @param Bj             Ending point's y value
	 * @param n              Length of one side of the matrix
	 * @param v              Cost between 2 cells located horizontally or vertically
	 *                       next to each other
	 * @param d              Cost between 2 cells located Diagonally next to each
	 *                       other
	 * @param additionalPath Boolean to decide whether to calculate the cost of
	 *                       through the diagonal path
	 * @param h              int value which decides the correct method to choose to
	 *                       calculate the Heuristic value
	 */
	public static void generateHValue(boolean matrix[][], int Ai, int Aj, int Bi, int Bj, int n, int v, int d,
			boolean additionalPath, int h) {

		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix.length; x++) {
				// Creating a new Node object for each and every Cell of the Grid (Matrix)
				cell[y][x] = new Node(y, x);
				// Checks whether a cell is Blocked or Not by checking the boolean value
				if (matrix[y][x]) {
					if (h == 1) {
						// Assigning the Chebyshev Heuristic value
						if (Math.abs(y - Bi) > Math.abs(x - Bj)) {
							cell[y][x].hValue = Math.abs(y - Bi);
						} else {
							cell[y][x].hValue = Math.abs(x - Bj);
						}
					} else if (h == 2) {
						// Assigning the Euclidean Heuristic value
						cell[y][x].hValue = Math.sqrt(Math.pow(y - Bi, 2) + Math.pow(x - Bj, 2));
					} else if (h == 3) {
						// Assigning the Manhattan Heuristic value by calculating the absolute length
						// (x+y) from the ending point to the starting point
						cell[y][x].hValue = Math.abs(y - Bi) + Math.abs(x - Bj);
					}
				} else {
					// If the boolean value is false, then assigning -1 instead of the absolute
					// length
					cell[y][x].hValue = -1;
				}
			}
		}
		generatePath(cell, Ai, Aj, Bi, Bj, n, v, d, additionalPath);
	}

	public static void drawMap() {
		// Generating a new Boolean Matrix according to the input values of n and p
		// (Length, Percolation value)
		boolean[][] randomlyGenMatrix = succ();

		// StdArrayIO.print(randomlyGenMatrix);
		show(randomlyGenMatrix, true);
	}

	public static void menu() {

		Scanner in = new Scanner(System.in);
		// System.out.println("Please choose N(Grid Size): ");
		int n = 200;
		// System.out.println("Please choose Obstacle ratio: ");
		// double p = 1;
		int gCost = 0;
		/* int fCost = 0; */

		boolean[][] randomlyGenMatrix = succ();

		// Creation of a Node type 2D array
		cell = new Node[randomlyGenMatrix.length][randomlyGenMatrix.length];

		RoomCoord C1 = new RoomCoord(109, 30, "C1");
		RoomCoord C2 = new RoomCoord(106, 30, "C2");
		RoomCoord C3 = new RoomCoord(103, 30, "C3");
		RoomCoord C4 = new RoomCoord(98, 32, "C4");
		RoomCoord C5 = new RoomCoord(100, 37, "C5");
		RoomCoord C6 = new RoomCoord(103, 37, "C6");
		RoomCoord C7 = new RoomCoord(108, 37, "C7");
		RoomCoord C8 = new RoomCoord(111, 37, "C8");
		RoomCoord D1 = new RoomCoord(109, 27, "D1");
		RoomCoord D2 = new RoomCoord(107, 27, "D2");
		RoomCoord E = new RoomCoord(95, 44, "E");
		RoomCoord F1 = new RoomCoord(98, 26, "F1");
		RoomCoord F2 = new RoomCoord(86, 25, "F2");
		RoomCoord F3 = new RoomCoord(86, 29, "F3");
		RoomCoord F4 = new RoomCoord(90, 30, "F4");
		RoomCoord F5 = new RoomCoord(94, 30, "F5");
		RoomCoord F6 = new RoomCoord(96, 30, "F6");
		RoomCoord G1 = new RoomCoord(83, 30, "G1");
		RoomCoord G2 = new RoomCoord(79, 30, "G2");
		RoomCoord G3 = new RoomCoord(73, 30, "G3");
		RoomCoord G4 = new RoomCoord(74, 37, "G4");
		RoomCoord G5 = new RoomCoord(76, 37, "G5");
		RoomCoord G6 = new RoomCoord(80, 37, "G6");
		RoomCoord G7 = new RoomCoord(83, 37, "G7");
		RoomCoord H1 = new RoomCoord(77, 21, "H1");
		RoomCoord H2 = new RoomCoord(76, 20, "H2");
		RoomCoord H3 = new RoomCoord(76, 21, "H3");
		RoomCoord H4 = new RoomCoord(75, 30, "H4");
		RoomCoord H5 = new RoomCoord(80, 30, "H5");
		RoomCoord J1 = new RoomCoord(54, 35, "J1");
		RoomCoord J2 = new RoomCoord(68, 35, "J2");
		RoomCoord J3 = new RoomCoord(54, 31, "J3");
		RoomCoord J4 = new RoomCoord(68, 31, "J4");
		RoomCoord J5 = new RoomCoord(54, 26, "J5");
		RoomCoord J6 = new RoomCoord(68, 26, "J6");
		RoomCoord J7 = new RoomCoord(54, 22, "J7");
		RoomCoord J8 = new RoomCoord(68, 21, "J8");
		RoomCoord J9 = new RoomCoord(54, 17, "J9");
		RoomCoord J10 = new RoomCoord(68, 17, "J10");
		RoomCoord K1 = new RoomCoord(76, 42, "K1");
		RoomCoord K2 = new RoomCoord(62, 41, "K2");
		RoomCoord K3 = new RoomCoord(65, 51, "K3");
		RoomCoord K4 = new RoomCoord(61, 48, "K4");
		RoomCoord K5 = new RoomCoord(62, 55, "K5");
		RoomCoord K6 = new RoomCoord(72, 55, "K6");
		RoomCoord K7 = new RoomCoord(72, 55, "K7");
		RoomCoord K8 = new RoomCoord(68, 55, "K8");
		RoomCoord K9 = new RoomCoord(63, 55, "K9");
		RoomCoord K10 = new RoomCoord(63, 63, "K10");
		RoomCoord K11 = new RoomCoord(66, 63, "K11");
		RoomCoord K12 = new RoomCoord(70, 63, "K12");
		RoomCoord K13 = new RoomCoord(73, 63, "K13");
		RoomCoord K14 = new RoomCoord(73, 53, "K14");
		RoomCoord K15 = new RoomCoord(73, 50, "K15");
		RoomCoord L1 = new RoomCoord(76, 69, "L1");
		RoomCoord L2 = new RoomCoord(60, 66, "L2");
		RoomCoord L4 = new RoomCoord(68, 63, "L4");
		RoomCoord L5 = new RoomCoord(76, 82, "L5");
		RoomCoord L6 = new RoomCoord(76, 77, "L6");
		RoomCoord L8 = new RoomCoord(76, 71, "L8");
		RoomCoord M1 = new RoomCoord(80, 91, "M1");
		RoomCoord M2 = new RoomCoord(74, 90, "M2");
		RoomCoord M3 = new RoomCoord(65, 90, "M3");
		RoomCoord M4 = new RoomCoord(71, 97, "M4");
		RoomCoord M5 = new RoomCoord(79, 97, "M5");
		RoomCoord N100 = new RoomCoord(71, 106, "N100");
		RoomCoord N101 = new RoomCoord(68, 105, "N101");
		RoomCoord N102 = new RoomCoord(66, 105, "N102");
		RoomCoord N103 = new RoomCoord(63, 105, "N103");
		RoomCoord N104 = new RoomCoord(60, 105, "N104");
		RoomCoord N105 = new RoomCoord(56, 106, "N105");
		RoomCoord N106 = new RoomCoord(58, 107, "N106");
		RoomCoord N107 = new RoomCoord(60, 108, "N107");
		RoomCoord N108 = new RoomCoord(63, 110, "N108");
		RoomCoord N109 = new RoomCoord(65, 112, "N109");
		RoomCoord N110 = new RoomCoord(68, 114, "N110");
		RoomCoord N111 = new RoomCoord(71, 117, "N111");
		RoomCoord N112 = new RoomCoord(74, 117, "N112");
		RoomCoord N113 = new RoomCoord(79, 117, "N113");
		RoomCoord N114 = new RoomCoord(82, 117, "N114");
		RoomCoord N115 = new RoomCoord(71, 111, "N115");
		RoomCoord N200 = new RoomCoord(71, 106, "N200");
		RoomCoord N201 = new RoomCoord(68, 105, "N201");
		RoomCoord N202 = new RoomCoord(66, 105, "N202");
		RoomCoord N203 = new RoomCoord(63, 105, "N203");
		RoomCoord N204 = new RoomCoord(60, 105, "N204");
		RoomCoord N205 = new RoomCoord(56, 106, "N205");
		RoomCoord N206 = new RoomCoord(58, 107, "N206");
		RoomCoord N207 = new RoomCoord(60, 108, "N207");
		RoomCoord N208 = new RoomCoord(63, 110, "N208");
		RoomCoord N209 = new RoomCoord(63, 112, "N209");
		RoomCoord N210 = new RoomCoord(68, 114, "N210");
		RoomCoord N211 = new RoomCoord(71, 117, "N211");
		RoomCoord N212 = new RoomCoord(74, 117, "N212");
		RoomCoord N213 = new RoomCoord(80, 117, "N213");
		RoomCoord N214 = new RoomCoord(82, 117, "N214");
		RoomCoord N215 = new RoomCoord(71, 111, "N215");
		RoomCoord P105 = new RoomCoord(98, 158, "P105");
		RoomCoord P106 = new RoomCoord(103, 67, "P106");
		RoomCoord P107 = new RoomCoord(93, 60, "P107");
		RoomCoord P108 = new RoomCoord(93, 60, "P108");
		RoomCoord P115 = new RoomCoord(88, 73, "P115");
		RoomCoord P116 = new RoomCoord(87, 59, "P116");
		RoomCoord P117 = new RoomCoord(84, 73, "P117");
		RoomCoord P209 = new RoomCoord(97, 61, "P209");
		RoomCoord P231 = new RoomCoord(97, 61, "P231");
		RoomCoord P233 = new RoomCoord(97, 61, "P233");
		RoomCoord S121 = new RoomCoord(95, 73, "S121");
		RoomCoord V1 = new RoomCoord(135, 131, "V1");
		RoomCoord V2 = new RoomCoord(140, 131, "V2");
		RoomCoord V3 = new RoomCoord(144, 131, "V3");
		RoomCoord V4 = new RoomCoord(147, 131, "V4");
		RoomCoord V5 = new RoomCoord(149, 131, "V5");
		RoomCoord V6 = new RoomCoord(152, 131, "V6");
		RoomCoord V7 = new RoomCoord(155, 131, "V7");
		RoomCoord V8 = new RoomCoord(153, 131, "V8");
		RoomCoord V9 = new RoomCoord(134, 146, "V9");
		RoomCoord V12 = new RoomCoord(139, 146, "V12");
		RoomCoord V15 = new RoomCoord(143, 146, "V15");
		RoomCoord V14 = new RoomCoord(152, 146, "V14");
		RoomCoord V17 = new RoomCoord(156, 1464, "V17");
		RoomCoord V18 = new RoomCoord(136, 146, "V18");
		RoomCoord V19 = new RoomCoord(138, 146, "V19");
		RoomCoord V20 = new RoomCoord(140, 146, "V20");
		RoomCoord V21 = new RoomCoord(143, 146, "V21");
		RoomCoord V22 = new RoomCoord(144, 146, "V22");
		RoomCoord V23 = new RoomCoord(147, 146, "V23");
		RoomCoord V24 = new RoomCoord(151, 146, "V24");
		RoomCoord V25 = new RoomCoord(155, 146, "V25");
		RoomCoord V26 = new RoomCoord(159, 146, "V26");
		RoomCoord Library = new RoomCoord(104, 26, "Library");
		RoomCoord Spangenberg = new RoomCoord(91, 73, "Spangenberg");
		RoomCoord MainOffice = new RoomCoord(116, 48, "MainOffice");
		RoomCoord BoysLockerRoom = new RoomCoord(104, 128, "BoysLockerRoom");
		RoomCoord GirlsLockerRoom = new RoomCoord(128, 127, "GirlsLockerRoom");
		RoomCoord BowGym = new RoomCoord(116, 122, "BowGym");
		RoomCoord TitanGym = new RoomCoord(135, 156, "TitanGym");
		RoomCoord Pool = new RoomCoord(126, 151, "Pool");
		RoomCoord Track = new RoomCoord(100, 163, "Track");

		RoomCoord[] directory = { C1, C2, C3, C4, C5, C6, C7, C8, D1, D2, E, F1, F2, F3, F4, F5, F6, G1, G2, G3, G4, G5,
				G6, G7, H1, H2, H3, H4, H5, J1, J2, J3, J4, J5, J6, J7, J8, J9, J10, K1, K2, K3, K4, K5, K6, K7, K8, K9,
				K10, K11, K12, K13, K14, K15, L1, L2, L4, L5, L6, L8, M1, M2, M3, M4, M5, N100, N101, N102, N103, N104,
				N105, N106, N107, N108, N109, N110, N111, N112, N113, N114, N115, N200, N201, N202, N203, N204, N205,
				N206, N207, N208, N209, N210, N211, N212, N213, N214, N215, P105, P106, P107, P108, P115, P116, P117,
				P209, P231, P233, S121, V1, V2, V3, V4, V5, V6, V7, V8, V9, V12, V15, V14, V17, V18, V19, V20, V21, V22, V23,
				V24, V25, V26, Library, Spangenberg, MainOffice, BoysLockerRoom, GirlsLockerRoom, BowGym, TitanGym,
				Pool, Track };

		int Ai = 0;
		int Aj = 0;
		int Bi = 0;
		int Bj = 0;
		Boolean check = false;

		// System.out.println("Enter Starting Room:");
		// String startingRoom = in.nextLine();

		while (check == false) {
			System.out.println("Enter Starting Room:");
			String startingRoom = in.nextLine();
			for (int i = 0; i < directory.length; i++) {
				if (startingRoom.equals(directory[i].getName())) {
					Ai = directory[i].getY();
					Aj = directory[i].getX();
					check = true;
				}

			}
			// System.out.println(Ai);
			if (check == false) {
				System.out.println("Oops! One or more of your inputs didn't match any records. Try again.");
			}
		}

		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.filledSquare(Aj, Ai, 2);

		check = false;

		while (check == false) {
			System.out.println("Enter Destination Room:");
			String endingRoom = in.nextLine();
			for (int i = 0; i < directory.length; i++) {
				if (endingRoom.equals(directory[i].getName())) {
					Bi = directory[i].getY();
					Bj = directory[i].getX();
					check = true;
				}

			}
			// System.out.println(Ai);
			if (check == false) {
				System.out.println("Oops! One or more of your inputs didn't match any records. Try again.");
			}
		}

		/**
		 * System.out.println("Enter Destination Room:"); String endingRoom =
		 * in.nextLine(); for (int i = 0; i < directory.length; i++) { if
		 * (endingRoom.equals(directory[i].getName())) { Bi = directory[i].getY(); Bj =
		 * directory[i].getX(); } }
		 **/
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.filledSquare(Bj, Bi, 2);

		// StdDraw.filledSquare(Ai,Aj, .5);
		// StdDraw.filledSquare(Bi,Bj, .5);

		// Aj=78;
		// Ai=37;
		// Bj=94;
		// Bi=36;

		int AiBefore = Ai;
		int BiBefore = Bi;

		Ai = n - Ai - 1;
		Bi = n - Bi - 1;

		System.out.println("Coordnates: " + Ai + ", " + Aj + " to " + Bi + ", " + Bj);

		if (Ai == 199) {
			System.out.println("Oops! One or more of your inputs didn't match any records. Try again.");
		}

		drawMap();
		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.filledSquare(Aj, AiBefore, 2);
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.filledSquare(Bj, BiBefore, 2);

		 show(randomlyGenMatrix, true, Ai, Aj, Bi, Bj);

		Stopwatch timerFlow = null;

		// Loop to find all 3 pathways and their relative Final Cost values
		for (int j = 0; j < 3; j++) {

			if (j == 0) {
				timerFlow = new Stopwatch();
				// Method to generate Chebyshev path. Both Horizontal and Diagonal pathways are
				// possible.
				generateHValue(randomlyGenMatrix, Ai, Aj, Bi, Bj, n, 10, 10, true, 1);

				// Checks whether the end point has been reach (Stored in the pathList)
				if (cell[Ai][Aj].hValue != -1 && pathList.contains(cell[Bi][Bj])) {
					StdDraw.setPenColor(Color.RED);
					// StdDraw.setPenColor(Color.BLUE);

					/* StdDraw.setPenRadius(0.006); */

					// Draws the path
					for (int i = 0; i < pathList.size(); i++) {
						/* System.out.println(pathList.get(i).x + " " + pathList.get(i).y); */
						StdDraw.filledSquare(pathList.get(i).y, n - pathList.get(i).x - 1, .5);
						/*
						 * StdDraw.line(pathList.get(i).y, n - 1 - pathList.get(i).x, pathList.get(i +
						 * 1).y, n - 1 - pathList.get(i + 1).x);
						 */
						// Adds the gValue of each and every Node object that's stored in the pathList
						gCost += pathList.get(i).gValue;
						/* fCost += pathList.get(i).fValue; */
					}

					// System.out.println("Chebyshev Path Found");
					// System.out.println("Total Cost: " + gCost/10.0);
					/* System.out.println("Total fCost: " + fCost); */
					// StdOut.println("Elapsed time = " + timerFlow.elapsedTime());
					gCost = 0;
					/* fCost = 0; */

				} else {

					// System.out.println("Chebyshev Path Not found");
					// StdOut.println("Elapsed time = " + timerFlow.elapsedTime());

				}

				// Clears Both the pathList and the closedList
				pathList.clear();
				closedList.clear();
			}

			if (j == 1) {
				timerFlow = new Stopwatch();
				generateHValue(randomlyGenMatrix, Ai, Aj, Bi, Bj, n, 10, 14, true, 2);

				if (cell[Ai][Aj].hValue != -1 && pathList.contains(cell[Bi][Bj])) {
					StdDraw.setPenColor(Color.CYAN);
					// StdDraw.setPenColor(Color.BLUE);

					StdDraw.setPenRadius(0.015);

					for (int i = 0; i < pathList.size() - 1; i++) {
						/* System.out.println(pathList.get(i).x + " " + pathList.get(i).y); */
						/* StdDraw.circle(pathList.get(i).y, n - pathList.get(i).x - 1, .4); */
						StdDraw.line(pathList.get(i).y, n - 1 - pathList.get(i).x, pathList.get(i + 1).y,
								n - 1 - pathList.get(i + 1).x);
						gCost += pathList.get(i).gValue;
						/* fCost += pathList.get(i).fValue; */
					}

					// System.out.println("Euclidean Path Found");
					// System.out.println("Total Cost: " + gCost/10.0);
					/* System.out.println("Total fCost: " + fCost); */
					// StdOut.println("Elapsed time = " + timerFlow.elapsedTime());
					gCost = 0;
					/* fCost = 0; */

				} else {

					// System.out.println("Euclidean Path Not found");
					// StdOut.println("Elapsed time = " + timerFlow.elapsedTime());

				}

				pathList.clear();
				closedList.clear();
			}

			if (j == 2) {
				timerFlow = new Stopwatch();
				generateHValue(randomlyGenMatrix, Ai, Aj, Bi, Bj, n, 10, 10, false, 3);

				if (cell[Ai][Aj].hValue != -1 && pathList.contains(cell[Bi][Bj])) {
					StdDraw.setPenColor(Color.orange);
					// StdDraw.setPenColor(Color.BLUE);

					StdDraw.setPenRadius(0.006);

					for (int i = 0; i < pathList.size() - 1; i++) {
						/* System.out.println(pathList.get(i).x + " " + pathList.get(i).y); */
						/* StdDraw.filledCircle(pathList.get(i).y, n - pathList.get(i).x - 1, .2); */
						StdDraw.line(pathList.get(i).y, n - 1 - pathList.get(i).x, pathList.get(i + 1).y,
								n - 1 - pathList.get(i + 1).x);
						gCost += pathList.get(i).gValue;
						/* fCost += pathList.get(i).fValue; */
					}

					// System.out.println("Manhattan Path Found");
					// System.out.println("Total Cost: " + gCost/10.0);
					/* System.out.println("Total fCost: " + fCost); */
					// StdOut.println("Elapsed time = " + timerFlow.elapsedTime());
					gCost = 0;
					/* fCost = 0; */

				} else {

					// System.out.println("Manhattan Path Not found");
					// StdOut.println("Elapsed time = " + timerFlow.elapsedTime());

				}

				pathList.clear();
				closedList.clear();
			}
		}

		menu();

	}

	/**
	 * @param hValue         Node type 2D Array (Matrix)
	 * @param Ai             Starting point's y value
	 * @param Aj             Starting point's x value
	 * @param Bi             Ending point's y value
	 * @param Bj             Ending point's x value
	 * @param n              Length of one side of the matrix
	 * @param v              Cost between 2 cells located horizontally or vertically
	 *                       next to each other
	 * @param d              Cost between 2 cells located Diagonally next to each
	 *                       other
	 * @param additionalPath Boolean to decide whether to calculate the cost of
	 *                       through the diagonal path
	 */
	public static void generatePath(Node hValue[][], int Ai, int Aj, int Bi, int Bj, int n, int v, int d,
			boolean additionalPath) {

		// Creation of a PriorityQueue and the declaration of the Comparator
		PriorityQueue<Node> openList = new PriorityQueue<>(11, new Comparator() {
			@Override
			// Compares 2 Node objects stored in the PriorityQueue and Reorders the Queue
			// according to the object which has the lowest fValue
			public int compare(Object cell1, Object cell2) {
				return ((Node) cell1).fValue < ((Node) cell2).fValue ? -1
						: ((Node) cell1).fValue > ((Node) cell2).fValue ? 1 : 0;
			}
		});

		// Adds the Starting cell inside the openList
		openList.add(cell[Ai][Aj]);

		// Executes the rest if there are objects left inside the PriorityQueue
		while (true) {

			// Gets and removes the objects that's stored on the top of the openList and
			// saves it inside node
			Node node = openList.poll();

			// Checks if whether node is empty and f it is then breaks the while loop
			if (node == null) {
				break;
			}

			// Checks if whether the node returned is having the same node object values of
			// the ending point
			// If it des then stores that inside the closedList and breaks the while loop
			if (node == cell[Bi][Bj]) {
				closedList.add(node);
				break;
			}

			closedList.add(node);

			// Left Cell
			try {
				if (cell[node.x][node.y - 1].hValue != -1 && !openList.contains(cell[node.x][node.y - 1])
						&& !closedList.contains(cell[node.x][node.y - 1])) {
					double tCost = node.fValue + v;
					cell[node.x][node.y - 1].gValue = v;
					double cost = cell[node.x][node.y - 1].hValue + tCost;
					if (cell[node.x][node.y - 1].fValue > cost || !openList.contains(cell[node.x][node.y - 1]))
						cell[node.x][node.y - 1].fValue = cost;

					openList.add(cell[node.x][node.y - 1]);
					cell[node.x][node.y - 1].parent = node;
				}
			} catch (IndexOutOfBoundsException e) {
			}

			// Right Cell
			try {
				if (cell[node.x][node.y + 1].hValue != -1 && !openList.contains(cell[node.x][node.y + 1])
						&& !closedList.contains(cell[node.x][node.y + 1])) {
					double tCost = node.fValue + v;
					cell[node.x][node.y + 1].gValue = v;
					double cost = cell[node.x][node.y + 1].hValue + tCost;
					if (cell[node.x][node.y + 1].fValue > cost || !openList.contains(cell[node.x][node.y + 1]))
						cell[node.x][node.y + 1].fValue = cost;

					openList.add(cell[node.x][node.y + 1]);
					cell[node.x][node.y + 1].parent = node;
				}
			} catch (IndexOutOfBoundsException e) {
			}

			// Bottom Cell
			try {
				if (cell[node.x + 1][node.y].hValue != -1 && !openList.contains(cell[node.x + 1][node.y])
						&& !closedList.contains(cell[node.x + 1][node.y])) {
					double tCost = node.fValue + v;
					cell[node.x + 1][node.y].gValue = v;
					double cost = cell[node.x + 1][node.y].hValue + tCost;
					if (cell[node.x + 1][node.y].fValue > cost || !openList.contains(cell[node.x + 1][node.y]))
						cell[node.x + 1][node.y].fValue = cost;

					openList.add(cell[node.x + 1][node.y]);
					cell[node.x + 1][node.y].parent = node;
				}
			} catch (IndexOutOfBoundsException e) {
			}

			// Top Cell
			try {
				if (cell[node.x - 1][node.y].hValue != -1 && !openList.contains(cell[node.x - 1][node.y])
						&& !closedList.contains(cell[node.x - 1][node.y])) {
					double tCost = node.fValue + v;
					cell[node.x - 1][node.y].gValue = v;
					double cost = cell[node.x - 1][node.y].hValue + tCost;
					if (cell[node.x - 1][node.y].fValue > cost || !openList.contains(cell[node.x - 1][node.y]))
						cell[node.x - 1][node.y].fValue = cost;

					openList.add(cell[node.x - 1][node.y]);
					cell[node.x - 1][node.y].parent = node;
				}
			} catch (IndexOutOfBoundsException e) {
			}

			if (additionalPath) {

				// TopLeft Cell
				try {
					if (cell[node.x - 1][node.y - 1].hValue != -1 && !openList.contains(cell[node.x - 1][node.y - 1])
							&& !closedList.contains(cell[node.x - 1][node.y - 1])) {
						double tCost = node.fValue + d;
						cell[node.x - 1][node.y - 1].gValue = d;
						double cost = cell[node.x - 1][node.y - 1].hValue + tCost;
						if (cell[node.x - 1][node.y - 1].fValue > cost
								|| !openList.contains(cell[node.x - 1][node.y - 1]))
							cell[node.x - 1][node.y - 1].fValue = cost;

						openList.add(cell[node.x - 1][node.y - 1]);
						cell[node.x - 1][node.y - 1].parent = node;
					}
				} catch (IndexOutOfBoundsException e) {
				}

				// TopRight Cell
				try {
					if (cell[node.x - 1][node.y + 1].hValue != -1 && !openList.contains(cell[node.x - 1][node.y + 1])
							&& !closedList.contains(cell[node.x - 1][node.y + 1])) {
						double tCost = node.fValue + d;
						cell[node.x - 1][node.y + 1].gValue = d;
						double cost = cell[node.x - 1][node.y + 1].hValue + tCost;
						if (cell[node.x - 1][node.y + 1].fValue > cost
								|| !openList.contains(cell[node.x - 1][node.y + 1]))
							cell[node.x - 1][node.y + 1].fValue = cost;

						openList.add(cell[node.x - 1][node.y + 1]);
						cell[node.x - 1][node.y + 1].parent = node;
					}
				} catch (IndexOutOfBoundsException e) {
				}

				// BottomLeft Cell
				try {
					if (cell[node.x + 1][node.y - 1].hValue != -1 && !openList.contains(cell[node.x + 1][node.y - 1])
							&& !closedList.contains(cell[node.x + 1][node.y - 1])) {
						double tCost = node.fValue + d;
						cell[node.x + 1][node.y - 1].gValue = d;
						double cost = cell[node.x + 1][node.y - 1].hValue + tCost;
						if (cell[node.x + 1][node.y - 1].fValue > cost
								|| !openList.contains(cell[node.x + 1][node.y - 1]))
							cell[node.x + 1][node.y - 1].fValue = cost;

						openList.add(cell[node.x + 1][node.y - 1]);
						cell[node.x + 1][node.y - 1].parent = node;
					}
				} catch (IndexOutOfBoundsException e) {
				}

				// BottomRight Cell
				try {
					if (cell[node.x + 1][node.y + 1].hValue != -1 && !openList.contains(cell[node.x + 1][node.y + 1])
							&& !closedList.contains(cell[node.x + 1][node.y + 1])) {
						double tCost = node.fValue + d;
						cell[node.x + 1][node.y + 1].gValue = d;
						double cost = cell[node.x + 1][node.y + 1].hValue + tCost;
						if (cell[node.x + 1][node.y + 1].fValue > cost
								|| !openList.contains(cell[node.x + 1][node.y + 1]))
							cell[node.x + 1][node.y + 1].fValue = cost;

						openList.add(cell[node.x + 1][node.y + 1]);
						cell[node.x + 1][node.y + 1].parent = node;
					}
				} catch (IndexOutOfBoundsException e) {
				}
			}
		}

		/*
		 * for (int i = 0; i < n; ++i) { for (int j = 0; j < n; ++j) {
		 * System.out.print(cell[i][j].fValue + "    "); } System.out.println(); }
		 */

		// Assigns the last Object in the closedList to the endNode variable
		Node endNode = closedList.get(closedList.size() - 1);

		// Checks if whether the endNode variable currently has a parent Node. if it
		// doesn't then stops moving forward.
		// Stores each parent Node to the PathList so it is easier to trace back the
		// final path
		while (endNode.parent != null) {
			Node currentNode = endNode;
			pathList.add(currentNode);
			endNode = endNode.parent;
		}

		pathList.add(cell[Ai][Aj]);
		// Clears the openList
		openList.clear();

		System.out.println();

	}

	public static void main(String[] args) {

		drawMap();
		menu();

	}
}