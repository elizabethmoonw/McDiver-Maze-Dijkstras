package diver;

import datastructures.PQueue;
import datastructures.SlowPQueue;
import game.*;
import graph.ShortestPaths;


import java.util.*;


/** This is the place for your implementation of the {@code SewerDiver}.
 */
public class McDiver implements SewerDiver {

    /** See {@code SewerDriver} for specification. */

    /**
     * Set that contains the locations already visited
     */
    HashSet<Long> wasVisited = new HashSet<>();

    @Override
    public void seek(SeekState state) {
        // TODO : Look for the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method (it may be recursive) elsewhere, with a
        // good specification, and call it from this one.
        //
        // Working this way provides you with flexibility. For example, write
        // one basic method, which always works. Then, make a method that is a
        // copy of the first one and try to optimize in that second one.
        // If you don't succeed, you can always use the first one.
        //
        // Use this same process on the second method, scram.
        helpdfs(state);
    }
    // List of unvisited locations and sort by their distance to ring


    /**
     * Helper method that calls DFS recursively. McDiver decides where to go based off of a priority queue
     * that is sorted by based off of distance to the coin
     *
     * @param state
     */
    public void helpdfs(SeekState state) {
        long isOn = state.currentLocation();
        wasVisited.add(isOn);

        PQueue<NodeStatus> close = new SlowPQueue<>();
        if (state.distanceToRing() == 0) {
            return;
        }
        for (NodeStatus neighbor : state.neighbors()) {
            if (!wasVisited.contains(neighbor.getId())) {
                close.add(neighbor, neighbor.getDistanceToRing());
            }
        }
        while (!close.isEmpty()) {
            NodeStatus neighbor = close.extractMin();
            if (!wasVisited.contains(neighbor.getId())) {
                state.moveTo(neighbor.getId());
                helpdfs(state);
                if (state.distanceToRing() == 0) {
                    return;
                }
                state.moveTo(isOn);
            }
        }
    }


    /**
     * See {@code SewerDriver} for specification.
     */
    @Override
    public void scram(ScramState state) {
        // TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        // with a good specification, and call it from this one.
        priorityCoinPaths(state);
        return;
    }

    public List<Edge> shortestPathFinder(ScramState state, Node target) {
        Node curr = state.currentNode();

        Maze maze = new Maze((Set<Node>) state.allNodes());

        ShortestPaths path = new ShortestPaths(maze);
        path.singleSourceDistances(curr);
        List<Edge> shortestPath = path.bestPath(target);
        for (int i = 0; i < shortestPath.size(); i++) {
            state.moveTo(shortestPath.get(i).destination());
        }
        return shortestPath;
    }


    public void priorityCoinPaths(ScramState state) {
        Node exit = state.exit();
        Maze maze = new Maze((Set<Node>) state.allNodes());
        ShortestPaths path = new ShortestPaths(maze);

        Map<Node, List<Edge>> possiblePaths = new HashMap<>();
        Map<Node, Integer> pathValue = new HashMap<>();

        for (Node n : state.allNodes()){
            path.singleSourceDistances(n);
            possiblePaths.put(n, path.bestPath(exit));
        }

        while (true) {
            Node curr = state.currentNode();
            int maxPathSize = state.stepsToGo();
            path = new ShortestPaths(maze);
            path.singleSourceDistances(curr);
            int bestValue = 0;
            Node next = exit;
            List<Edge> nextPath = possiblePaths.get(curr);
//            System.out.println(calcWeight(nextPath) <= maxPathSize);
            for (Node n: state.allNodes()) {
               if (!curr.equals(n)){
                   List<Edge> nToExit = possiblePaths.get(n);
                   List<Edge> currToN = path.bestPath(n);
                   pathValue.put(n, coinComp(nToExit) + coinComp(currToN));
                   if (coinComp(nToExit) + coinComp(currToN) > bestValue &&
                           calcWeight(nToExit) + calcWeight(currToN) <= maxPathSize) {
                       bestValue = coinComp(nToExit) + coinComp(currToN);
                       next = n;
                       nextPath = currToN;
                   }
               }
            }
            System.out.println(calcWeight(nextPath));
            for (int i = 0; i < nextPath.size(); i++) {
                state.moveTo(nextPath.get(i).destination());
            }
            if (next.equals(exit)) {return;}

            }
        }

        public int calcWeight (List<Edge> path) {
            int steps = 0;
            for (Edge e : path) {
                steps += e.length();
            }
            return steps;
        }

        public int coinComp (List<Edge> d) {
        int coinSoFar = 0;
        for (Edge e : d){
            int coins = e.destination().getTile().coins();
            if (coins > 0) {
                    coinSoFar += coins; }
            }
        return coinSoFar;
        }
}








