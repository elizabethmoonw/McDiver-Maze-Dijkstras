package graph;

import cms.util.maybe.Maybe;
import cms.util.maybe.NoMaybeValue;
import datastructures.PQueue;
import datastructures.SlowPQueue;
import game.Edge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** This object computes and remembers shortest paths through a
 *  weighted, directed graph. Once shortest paths are computed from
 *  a specified source vertex, it allows querying the distance to
 *  arbitrary vertices and the best paths to arbitrary destination
 *  vertices.
 *<p>
 *  Types Vertex and Edge are parameters, so their operations are
 *  supplied by a model object supplied to the constructor.
 */
public class ShortestPaths<Vertex, Edge> {

    /** The model for treating types Vertex and Edge as forming
     * a weighted directed graph.
     */
    private final WeightedDigraph<Vertex, Edge> graph;

    /** The distance to each vertex from the source.
     */
    private Maybe<Map<Vertex, Double>> distances;

    /** The incoming edge for the best path to each vertex from the
     *  source vertex.
     */
    private Maybe<Map<Vertex, Edge>> bestEdges;

    /** Creates: a single-source shortest-path finder for a weighted graph.
     *
     * @param graph The model that supplies all graph operations.
     */
    public ShortestPaths(WeightedDigraph<Vertex, Edge> graph) {
        this.graph = graph;
    }

    /** Effect: Computes the best paths from a given source vertex, which
     *  can then be queried using bestPath().
     */
    public void singleSourceDistances(Vertex source) {
        // Implementation: uses Dijkstra's single-source shortest paths
        //   algorithm.
        PQueue<Vertex> frontier = new SlowPQueue<>();
        Map<Vertex, Double> distances = new HashMap<>();
        Map<Vertex, Edge> bestEdges = new HashMap<>();
           // TODO: Complete computation of distances and best-path edges

        bestEdges.put(source, null);
        distances.put(source, 0.0);
        for (Edge E: graph.outgoingEdges(source)){ // Add first set of vertices to queue
            frontier.add(graph.dest(E), graph.weight(E));
            distances.put(graph.dest(E), graph.weight(E));
            bestEdges.put(graph.dest(E), E);
        }

        while(!frontier.isEmpty()) {
            Vertex g = frontier.extractMin();
            for (Edge E : graph.outgoingEdges(g)) {
                Vertex dest= graph.dest(E);
                if (distances.containsKey(dest) && distances.get(dest) >
                        (graph.weight(E) + distances.get(g))) {
                    distances.put(dest, graph.weight(E) + distances.get(g));
                    frontier.changePriority(dest, graph.weight(E) + distances.get(g));
                    bestEdges.put(dest, E);
//                    System.out.println("g = " + g.toString());
//                    System.out.println("dest = " + graph.dest(E).toString() + "\n");
                } else if (!distances.containsKey(dest)) {
//                    System.out.println("Adding key");
                    frontier.add(dest, graph.weight(E) + distances.get(g));
                    distances.put(dest, graph.weight(E) + distances.get(g));
                    bestEdges.put(dest, E);

                }
            }
        }
        this.bestEdges = Maybe.some(bestEdges);
        this.distances = Maybe.some(distances);


    }

    /** Returns: the distance from the source vertex to the given vertex.
     *  Checks: distances have been computed from a source vertex,
     *    and vertex v is reachable from that vertex.
     */
    public double getDistance(Vertex v) {
        try {
            Double d = distances.get().get(v);
            assert d != null : "Implementation incomplete";
            return d;
        } catch (NoMaybeValue exc) {
            throw new Error("Distances not computed yet");
        }
    }

    /**
     * Returns: the best path from the source vertex to a given target
     * vertex. The path is represented as a list of edges.
     * Requires: singleSourceDistances() has already been used to compute
     * best paths.
     */
    public List<Edge> bestPath(Vertex target) {

        LinkedList<Edge> path = new LinkedList<>();
        Map<Vertex, Edge> bestEdges = this.bestEdges.orElseGet(() -> {
            throw new Error("best distances not computed yet");
        });
        Vertex v = target;
        while (true) {
            Edge e = bestEdges.get(v);
            if (e == null) break; // must be the source vertex
            path.addFirst(e);
            v = graph.source(e);
        }
        return path;
    }
}
