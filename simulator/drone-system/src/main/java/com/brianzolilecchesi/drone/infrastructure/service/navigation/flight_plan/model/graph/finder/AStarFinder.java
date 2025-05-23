package com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.graph.finder;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.FlightPlan;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.geo.GeoCalculator;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.geo.GeoCalculatorSingleton;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.graph.CellGraph;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.zone.Cell;

public class AStarFinder extends PathFinder {
	
	private GeoCalculator dc;

	public AStarFinder(GeoCalculator dc) {
        super();
        this.dc = dc;
    }
	
	public AStarFinder() {
		this(GeoCalculatorSingleton.INSTANCE.getInstance());
	}
	
	@Override
	public String algorithm() {
		return "A*";
	}

	@Override
	public FlightPlan computePath(CellGraph graph) {
		assert graph != null;
		assert graph.getGraph() != null;
		assert graph.getSource() != null;
		assert graph.getDest() != null;
		
		AStarAdmissibleHeuristic<Cell> h = (Cell s, Cell d) -> {
			return heuristic(s, d);
		};
		
		AStarShortestPath<Cell, DefaultWeightedEdge> aStar = new AStarShortestPath<>(graph.getGraph(), h);
		GraphPath<Cell, DefaultWeightedEdge> path = aStar.getPath(graph.getSource(), graph.getDest());
		if (path == null) {
			return new FlightPlan();
		}
		
		FlightPlan fp = new FlightPlan(path.getVertexList());
		return fp;
	}

	private double heuristic(final Cell source, final Cell dest) {
		return dc.distance(source.getCenter(), dest.getCenter());
	}

	@Override
	public String toString() {
		return String.format("AStarFinder[%s, dc=%s]", super.toString(), dc.toString());
	}
}