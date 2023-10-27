/* Assignment 1: Artificial Intelligence
 * Hopfer Maximilian
 * 9/26/2023
 *  Solving and 18 tile puzzle in best possible way
 * A* algorithm and Iterative Deepening A* algorithm.
 * 
 * This code was generated from a framework that was
 * written by Dr. Simon. The minPQ code is adapted from Sedgewick
 * and not my own work. All changes in this code are done by
 * myself.
 * 
 */


import java.io.FileNotFoundException;
import java.util.*;

public class HexPuzzle {
    /*
     * The HexPuzzle will internally be displayed as an 1D array of size 19.
     * The blank is displayed as a 0.
     * 
     * One way to optimize this algorithm would be to minimize the space that each
     * node takes.
     * Furthermore, I could only store a long in each node that maps to the state.
     * to be able to do this, I must first compute all 19! possible states and map
     * them to each long.
     * 
     * A second optimization would be to use f instead of g and h. Since f = g+h
     * Due to time constraints, these mentioned optimizations are not implemented.
     */

    final static int SIZE = 19;
    final static int POSSIBLEMOVES = 6;

    short tiles[];
    short blankPos;

    static short[] goalArr = new short[SIZE];

    // maping an index to an x-coordinate
    public static HashMap<Integer, Integer> posInX = new HashMap<>();
    static {
        posInX.put(1, 2);
        posInX.put(2, 4);
        posInX.put(3, 6);
        posInX.put(4, 1);
        posInX.put(5, 3);
        posInX.put(6, 5);
        posInX.put(7, 7);
        posInX.put(8, 0);
        posInX.put(9, 2);
        posInX.put(10, 6);
        posInX.put(11, 8);
        posInX.put(12, 1);
        posInX.put(13, 3);
        posInX.put(14, 5);
        posInX.put(15, 7);
        posInX.put(16, 2);
        posInX.put(17, 4);
        posInX.put(18, 6);

        short count = 1;
        for (int i = 0; i < SIZE; i++) {
            if (i == 9) {
                goalArr[i] = 0;
            } else {
                goalArr[i] = count++;
            }
        }
    }

    public HexPuzzle(short[] x) {
        tiles = Arrays.copyOf(x, x.length);
        for (int i = 0; i < SIZE; i++) {
            if (tiles[i] == 0) {
                blankPos = (short) i;
                return;
            }
        }
        System.out.println("ERROR IN PROCESSING BLANKPOSITION");
    }

    public HexPuzzle(short tiles[], short blankPos) {
        this.tiles = Arrays.copyOf(tiles, tiles.length);
        this.blankPos = blankPos;
    }

    public String toString() {

        String s = "";

        for (int i = 0; i < SIZE; i++) {
            if (i == 0) {
                s += String.format(" %-6s", "");
                s += String.format(" %-6s", tiles[i]);
            } else if (i == 3) {
                s += String.format(" %-3s", "");
                s += String.format(" %-5s", tiles[i]);
            } else if (i == 7) {
                // s += String.format(" %-3s", "");
                s += String.format(" %-5s", tiles[i]);
            } else if (i == 12) {
                s += String.format(" %-3s", "");
                s += String.format(" %-5s", tiles[i]);
            } else if (i == 16) {
                s += String.format(" %-6s", "");
                s += String.format(" %-5s", tiles[i]);
            } else {
                s += String.format(" %-5s", tiles[i]);
            }

            if (i == 2 || i == 6 || i == 11 || i == 15) {
                s += "\n";
            }
        }
        return s;
    }

    public boolean equals(Object o) {
        HexPuzzle r = (HexPuzzle) o;
        return blankPos == r.blankPos && Arrays.equals(tiles, r.tiles);
    }

    public int hashCode() {
        return Arrays.hashCode(tiles);
    }

    interface MoveAction {
        boolean valid();

        void move();
    }

    private MoveAction[] moveActions = new MoveAction[] {
            new MoveAction() { // upright
                public boolean valid() {
                    return blankPos > 2 && blankPos != 6 && blankPos != 11;
                }

                public void move() {
                    // upright

                    if (blankPos < 7) {
                        tiles[blankPos] = tiles[blankPos - 3];
                        blankPos -= 3;
                        tiles[blankPos] = 0;
                    } else if (blankPos < 12) {
                        tiles[blankPos] = tiles[blankPos - 4];
                        blankPos -= 4;
                        tiles[blankPos] = 0;
                    } else if (blankPos < 16) {
                        tiles[blankPos] = tiles[blankPos - 4];
                        blankPos -= 4;
                        tiles[blankPos] = 0;
                    } else {
                        tiles[blankPos] = tiles[blankPos - 3];
                        blankPos -= 3;
                        tiles[blankPos] = 0;
                    }

                }
            },
            new MoveAction() { // upleft
                public boolean valid() {
                    return blankPos > 3 && blankPos != 7;
                }

                public void move() {
                    // upleft
                    if (blankPos < 7) {
                        tiles[blankPos] = tiles[blankPos - 4];
                        blankPos -= 4;
                        tiles[blankPos] = 0;
                    } else if (blankPos < 12) {
                        tiles[blankPos] = tiles[blankPos - 5];
                        blankPos -= 5;
                        tiles[blankPos] = 0;
                    } else if (blankPos < 16) {
                        tiles[blankPos] = tiles[blankPos - 5];
                        blankPos -= 5;
                        tiles[blankPos] = 0;
                    } else {
                        tiles[blankPos] = tiles[blankPos - 4];
                        blankPos -= 4;
                        tiles[blankPos] = 0;
                    }
                }
            },
            new MoveAction() { // downright
                public boolean valid() {
                    return blankPos < 15 && blankPos != 11 && blankPos != 15;
                }

                public void move() {
                    if (blankPos < 3) {
                        tiles[blankPos] = tiles[blankPos + 4];
                        blankPos += 4;
                        tiles[blankPos] = 0;
                    } else if (blankPos < 7) {
                        tiles[blankPos] = tiles[blankPos + 5];
                        blankPos += 5;
                        tiles[blankPos] = 0;
                    } else if (blankPos < 12) {
                        tiles[blankPos] = tiles[blankPos + 5];
                        blankPos += 5;
                        tiles[blankPos] = 0;
                    } else if (blankPos < 16) {
                        tiles[blankPos] = tiles[blankPos + 4];
                        blankPos += 4;
                        tiles[blankPos] = 0;
                    }
                }
            },
            new MoveAction() { // downleft = 3
                public boolean valid() {
                    return blankPos < 16 && blankPos != 7 && blankPos != 12;
                }

                public void move() {
                    if (blankPos < 3) {
                        tiles[blankPos] = tiles[blankPos + 3];
                        blankPos += 3;
                        tiles[blankPos] = 0;
                    } else if (blankPos < 7) {
                        tiles[blankPos] = tiles[blankPos + 4];
                        blankPos += 4;
                        tiles[blankPos] = 0;
                    } else if (blankPos < 12) {
                        tiles[blankPos] = tiles[blankPos + 4];
                        blankPos += 4;
                        tiles[blankPos] = 0;
                    } else if (blankPos < 16) {
                        tiles[blankPos] = tiles[blankPos + 3];
                        blankPos += 3;
                        tiles[blankPos] = 0;
                    }
                }
            },
            new MoveAction() { // left
                public boolean valid() {
                    return blankPos != 0 && blankPos != 3 && blankPos != 7 && blankPos != 12 && blankPos != 16;
                }

                public void move() {
                    tiles[blankPos] = tiles[blankPos - 1];
                    blankPos -= 1;
                    tiles[blankPos] = 0;
                }
            },
            new MoveAction() { // right
                public boolean valid() {
                    return blankPos != 2 && blankPos != 6 && blankPos != 11 && blankPos != 15 && blankPos != 18;
                }

                public void move() {
                    tiles[blankPos] = tiles[blankPos + 1];
                    blankPos += 1;
                    tiles[blankPos] = 0;
                }
            }
    };

    private static int[] opp = { 3, 2, 1, 0, 5, 4 };

    static class Node implements Comparable<Node>, Denumerable {
        public HexPuzzle state;
        public Node parent;
        public short g, h;// g = moves to get to this state | h = moves it takes to goal state
        public boolean inFrontier;
        public int x;

        Node(HexPuzzle state, Node parent, short g, short h) {
            this.state = state;
            this.parent = parent;
            this.g = g;
            this.h = h;
            inFrontier = true;
            x = 0;
        }

        public int compareTo(Node a) {
            return g + h - a.g - a.h;
        }

        public int getNumber() {
            return x;
        }

        public void setNumber(int x) {
            this.x = x;
        }

        public String toString() {
            return state + "";
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        short[] inputarr = new short[SIZE];

        HexPuzzle goal = new HexPuzzle(goalArr);
       // File input = new File("input.txt");
        Scanner in = new Scanner(System.in);

        for (int j = 0; j < SIZE; j++) { //
            // fill array with given tiles
            inputarr[j] = (short) in.nextShort();

        }
        in.close();

        HexPuzzle startState = new HexPuzzle(inputarr);
        // Node startNode = new Node(startState, null, LENGTH, LENGTH);

        long startTime = System.nanoTime();
        Node myStartstateNode = new Node(startState, null, (short) 0, h(startState, goal));

        // ids(myStartstateNode, goal);
        astar(startState, goal);

        long elapsedTime = System.nanoTime() - startTime;

        System.out.println("Total execution in millis: "
                + elapsedTime / 1000000);

        // System.out.println("total # of states generated: " + countStatesGenerated);

    }

    public static int ids(Node startStateNode, HexPuzzle goal) {

        HashSet<HexPuzzle> alreadyseen = new HashSet<>();

        for (int limit = startStateNode.h;; limit++) {

            System.out.println("The limit is: " + limit + " States generated: " + countStatesGenerated);
            int result = bdfs(startStateNode, goal, limit, alreadyseen);
            if (result == 2) {
                System.out.println(result);
                return result;
            }
            System.out.println();
        }
    }

    public static int countStatesGenerated;

    public static int testMoves(Node r, HexPuzzle goal) {
        // helper function to check if moves are functioning properly
        for (int i = 0; i < POSSIBLEMOVES; i++) {
            System.out.println(i);
            System.out.println(r);
            if (r.state.moveActions[i].valid()) {
                r.state.moveActions[i].move();
                System.out.println(r);
                r.state.moveActions[opp[i]].move();
                System.out.println();
            }

        }
        return 2;
    }

    public static int bdfs(Node r, HexPuzzle goal, int limit,
            HashSet<HexPuzzle> alreadyseen) {
        // returns 0: failure, 1: cutoff, 2: success

        if (r.state.equals(goal)) {
            printAnswer(r);
            return 2;
        } else if (r.g + r.h > limit)
            return 1;
        else {
            boolean cutoff = false;
            for (int i = 0; i < POSSIBLEMOVES; i++) {

                int newPosOfPuzzleTile = r.state.blankPos;

                if (r.state.moveActions[i].valid()) {
                    r.state.moveActions[i].move();

                    if (!alreadyseen.contains(r.state)) {
                        // calculate heuristic for new state

                        int puzzletileMoved = r.state.tiles[newPosOfPuzzleTile];
                        int oldpositionofPuzzleTile = r.state.blankPos;
                        short newHeuristic = (short) (r.h - hOneTile(oldpositionofPuzzleTile,
                                newPosOfPuzzleTile, puzzletileMoved));

                        if ((r.g + 1 + newHeuristic) <= limit) {

                            HexPuzzle hP = new HexPuzzle(r.state.tiles, r.state.blankPos);

                            Node newNode = new Node(hP, r, (short) (r.g + 1), newHeuristic);

                            alreadyseen.add(newNode.state);
                            countStatesGenerated++;

                            switch (bdfs(newNode, goal, limit, alreadyseen)) {
                                case 1:
                                    cutoff = true;
                                    break;
                                case 2:
                                    return 2;
                                default:
                            }
                            alreadyseen.remove(newNode.state);
                        }
                    }

                    r.state.moveActions[opp[i]].move();
                }
            }
            return (cutoff ? 1 : 0);
        }
    }

    public static short hOneTile(int oldPos, int newPos, int puzzletile) { // manhatten distance

        // COLUMNS = Y

        int dyold = Math.abs(getDistaceToGoalinColumns(oldPos, puzzletile));

        // ROWS = Y
        int dxold = Math.abs(getDistanceToGoalinRows(oldPos, puzzletile));

        int manhattenDistOld = Math.max(dyold - dxold, 0) / 2 + dxold;

        int dyNew = Math.abs(getDistaceToGoalinColumns(newPos, puzzletile));

        // ROWS = Y
        int dxNew = Math.abs(getDistanceToGoalinRows(newPos, puzzletile));

        int manhattenDistNew = Math.max(dyNew - dxNew, 0) / 2 + dxNew;

        short differenceH = (short) (manhattenDistOld - manhattenDistNew);
        return differenceH;

    }

    public static short h(HexPuzzle currentState, HexPuzzle goal) { // manhatten distance
        short totalManhattenSum = 0;
        int index = 0;

        while (index < SIZE) {
            int currentStatePuzzle = currentState.tiles[index];

            if (currentStatePuzzle == 0) {
                index++;
                continue;
            }

            // COLUMNS = Y
            int dy = Math.abs(getDistaceToGoalinColumns(index, currentStatePuzzle));

            // ROWS = Y
            int dx = Math.abs(getDistanceToGoalinRows(index, currentStatePuzzle));

            totalManhattenSum += Math.max(dy - dx, 0) / 2 + dx;

            index++;
        }

        return totalManhattenSum;

    }

    public static int getDistaceToGoalinColumns(int index, int puzzletile) {
        int xstart = -1;
        if (index == 9) {
            xstart = 4;
            return Math.abs(xstart - posInX.get(puzzletile));

        } else if (index < 9) {
            index += 1;
        }

        return Math.abs(posInX.get(index) - posInX.get(puzzletile));
    }

    public static int getDistanceToGoalinRows(int index, int puzzletile) {
        if (index < 10) {
            index += 1;
        }

        int starty;
        if (puzzletile < 4)
            starty = 0;
        else if (puzzletile < 8)
            starty = 1;
        else if (puzzletile < 12)
            starty = 2;
        else if (puzzletile < 16)
            starty = 3;
        else
            starty = 4;

        int goaly;
        if (index < 4)
            goaly = 0;
        else if (index < 8)
            goaly = 1;
        else if (index < 12)
            goaly = 2;
        else if (index < 16)
            goaly = 3;
        else
            goaly = 4;

        return Math.abs(starty - goaly);
    }

    public static void printAnswer(Node x) {
        Stack<Node> stack = new Stack<>();
        int numMoves = 0;
        for (Node y = x; y != null; y = y.parent) {
            stack.push(y);
            numMoves++;
        }
        int counter = numMoves;
        while (!stack.isEmpty()) {
            counter--;
            if (counter == 1) {
                stack.pop();
                continue;
            }
            System.out.println(stack.pop());// printing the moves the program made!
        }
        System.out.println((numMoves - 1) + " moves.");
    }

    public static int astar(HexPuzzle start, HexPuzzle goal) {
        // returns 0: failure, 2: success
        System.out.println("  f    |frontier|  |explored|");
        int maxF = 0;
        Node z = new Node(start, null, (short) 0, h(start, goal));
        IndexMinPQ<Node> frontier = new IndexMinPQ<>();
        frontier.add(z);
        HashMap<HexPuzzle, Node> explored = new HashMap<>();
        explored.put(start, z);

        while (true) {
            if (frontier.isEmpty()) {
                System.out.println(" FAILURE: FRONTIER IS EMPTY, could not find a path");
                return 0;
            }

            Node x = frontier.remove();
            x.inFrontier = false;
            if (x.g + x.h > maxF) {
                maxF = x.g + x.h;
                System.out.printf("%3d %10d %10d\n", maxF, frontier.size(), explored.size());
            }
            if (x.state.equals(goal)) {
                printAnswer(x);
                return 2;
            }
            for (int i = 0; i < POSSIBLEMOVES; i++) {
                int newPosOfPuzzleTile = x.state.blankPos;

                if (x.state.moveActions[i].valid()) {
                    x.state.moveActions[i].move();
                    Node n = explored.get(x.state);
                    // if state was never explored before:
                    if (n == null) {

                        int puzzletileMoved = x.state.tiles[newPosOfPuzzleTile];
                        int oldpositionofPuzzleTile = x.state.blankPos;
                        short a = x.h;
                        short newHeuristic = (short) (a - hOneTile(oldpositionofPuzzleTile,
                                newPosOfPuzzleTile, puzzletileMoved));

                        HexPuzzle s = new HexPuzzle(x.state.tiles, x.state.blankPos);
                        n = new Node(s, x, (short) (x.g + 1), newHeuristic);
                        explored.put(s, n);
                        frontier.add(n);
                    }
                    // if this path to state is better than previous one, update state in frontier
                    else if (n.inFrontier) {
                        if (x.g + 1 < n.g) {
                            n.parent = x;
                            n.g = (short) (x.g + 1);
                            frontier.update(n);
                        }
                    }

                    // undo move and try next one
                    x.state.moveActions[opp[i]].move();
                }
            }

        }

    }

}