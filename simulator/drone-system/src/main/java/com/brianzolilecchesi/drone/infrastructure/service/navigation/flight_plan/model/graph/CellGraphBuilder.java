package com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.graph;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.brianzolilecchesi.drone.domain.model.Position;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.FlightPlan;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.ThreeDBoundingBox;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.geo.GeoCalculator;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.geo.GeoCalculatorSingleton;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.zone.Cell;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.zone.GridZone;
import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.zone.Zone;

public class CellGraphBuilder {

	private List<Zone> zones;

	public CellGraphBuilder(final List<Zone> zones) {
		setZones(zones);
	}

	public CellGraphBuilder() {
		this(new ArrayList<>());
	}

	public List<Zone> getZones() {
		return zones;
	}

	public void setZones(final List<Zone> zones) {
		this.zones = zones;
	}
	
	//TODO: set cellHeight dynamically
	public CellGraph build(
			final GridZone grid,
			final List<Double> altitudeLevels,
			double cellWidth,
			Position source,
			Position dest
			) {

		assert altitudeLevels != null;
		assert altitudeLevels.size() > 0;
		assert cellWidth > 0;
		assert source != null;
		assert dest != null;

		GeoCalculator dc = GeoCalculatorSingleton.INSTANCE.getInstance();
		ThreeDBoundingBox bb = grid.getBounds().getBoundingBox();

		int width = (int) Math.ceil(dc.distance(bb.getBNO(), bb.getBNE()) / cellWidth);
		int height = (int) Math.ceil(dc.distance(bb.getBNO(), bb.getBSO()) / cellWidth);
		int nAltitudes = altitudeLevels.size();

		Position tl = bb.getBNO();

		Cell[][][] gridList = new Cell[height][width][nAltitudes];

		Cell s = null; // source
		Cell d = null; // destination

		boolean isZoneBlocked = false;

		Graph<Cell, DefaultWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);

		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				for (int k = 0; k < nAltitudes; ++k) {
					isZoneBlocked = false;
					
					double cellHeight = altitudeLevels.get(k);
					if (k > 0)
						cellHeight -= altitudeLevels.get(k - 1);
				    cellHeight *= 2;
					
					Position center = tl.move(
							-(i * cellWidth) + (cellWidth / 2),
							(j * cellWidth) + (cellWidth / 2),
							altitudeLevels.get(k));

					gridList[i][j][k] = new Cell(i, j, k, center, cellWidth, cellHeight);
						
					for (Zone z : this.zones) {
						if (z.getBounds().contains(gridList[i][j][k].getBounds(), true)) {
							isZoneBlocked = true;
							break;
						}
					}
						
					if (isZoneBlocked)
						continue;

					graph.addVertex(gridList[i][j][k]);

					if (gridList[i][j][k].getBounds().contains(source)) {
						s = gridList[i][j][k];
					}
					if (gridList[i][j][k].getBounds().contains(dest)) {
						d = gridList[i][j][k];
					}

				}
			}
		}
		
		DefaultWeightedEdge e = null;
		
		// All neighbours of cells with at least one lower index
		// The neighbours with only greater indices are added in the following loops
		int[][] offsets = {
			    {-1, -1, 0}, 	// x-1, y-1, z
			    {-1, 0, -1}, 	// x-1, y, z-1
			    {0, -1, -1}, 	// x, y-1, z-1
			    {-1, -1, -1}, 	// x-1, y-1, z-1
			    {1, -1, 0}, 	// x+1, y-1, z
			    {-1, 1, 0}, 	// x-1, y+1, z
			    {0, -1, 1}, 	// x, y-1, z+1
			    {-1, 0, 1}, 	// x-1, y, z+1
			    {0, 1, -1}, 	// x, y+1, z-1
			    {1, 0, -1}, 	// x+1, y, z-1
			    {1, -1, 1}, 	// x+1, y-1, z+1
			    {1, 1, -1}, 	// x+1, y+1, z-1
			    {0, 0, -1}, 	// x, y, z-1
			    {-1, 0, 0}, 	// x-1, y, z
			    {0, -1, 0}, 	// x, y-1, z
			    {-1, -1, 1}, 	// x-1, y-1, z+1
			    {-1, 1, 1}, 	// x-1, y+1, z+1
			    {1, -1, -1}, 	// x+1, y-1, z-1
			    {1, 1, -1}, 	// x+1, y+1, z-1
			    {-1, 1, -1} 	// x-1, y+1, z-1
			};

		for (int x = 0; x < height; ++x) {
			for (int y = 0; y < width; ++y) {
				for (int z = 0; z < nAltitudes; ++z) {
					for (int[] offset : offsets) {
					    int dx = offset[0];
					    int dy = offset[1];
					    int dz = offset[2];

					    int newX = x + dx;
					    int newY = y + dy;
					    int newZ = z + dz;

					    if (
					    		newX >= 0 && newY >= 0 && newZ >= 0 && 
					    		newX < height && newY < width && newZ < nAltitudes &&
					    		graph.containsVertex(gridList[x][y][z]) &&
					    		graph.containsVertex(gridList[newX][newY][newZ])
				    		) {
					            
					    		e = graph.addEdge(gridList[x][y][z], gridList[newX][newY][newZ]);
					    		if (e != null) {
						    		graph.setEdgeWeight(e, dc.distance(
						    				gridList[x][y][z].getCenter(),
				                            gridList[newX][newY][newZ].getCenter()
				                            ));
					    		}
					        }
					}
				}
			}
		}

		return new CellGraph(graph, s, d);
	}

	public CellGraph build(
			final FlightPlan flightPlan,
			double cellWidth,
			Position source,
			Position destination,
			double acceptableSensitivity,
			double tollerance
			) {

		assert flightPlan != null;
		assert cellWidth > 0;
		assert source != null;
		assert destination != null;
		assert acceptableSensitivity > 0;
		assert tollerance > 0;

		GeoCalculator dc = GeoCalculatorSingleton.INSTANCE.getInstance();
		Graph<Cell, DefaultWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);

		Cell s = null; // new source
		Cell d = null; // new destination

		Cell x11, x12, x21, x22;
		DefaultWeightedEdge e = null;
		Cell xPrev11 = null, xPrev12 = null, xPrev21 = null, xPrev22 = null;
		Cell xPrev = null;
		boolean firstIter = true;
		
		List<Cell> path = flightPlan.getPath();
		
		for (Cell x : path) {
			x11 = new Cell(
					x.getX() * 2,
					x.getY() * 2,
					x.getZ(),
					x.getCenter().move(-cellWidth / 2, -cellWidth / 2, 0),
					cellWidth + tollerance,
					x.getHeight());
			x21 = new Cell(
					x.getX() * 2 + 1,
					x.getY() * 2,
					x.getZ(),
					x.getCenter().move(-cellWidth / 2, cellWidth / 2, 0),
					cellWidth + tollerance,
					x.getHeight());
			x12 = new Cell(
					x.getX() * 2,
					x.getY() * 2 + 1,
					x.getZ(),
					x.getCenter().move(cellWidth / 2, -cellWidth / 2, 0),
					cellWidth + tollerance,
					x.getHeight());
			x22 = new Cell(
					x.getX() * 2 + 1,
					x.getY() * 2 + 1,
					x.getZ(),
					x.getCenter().move(cellWidth / 2, cellWidth / 2, 0),
					cellWidth + tollerance,
					x.getHeight());
			

			if (s == null) {
				if (x11.getBounds().contains(source))
					s = x11;
				else if (x12.getBounds().contains(source))
					s = x12;
				else if (x21.getBounds().contains(source))
					s = x21;
				else if (x22.getBounds().contains(source))
					s = x22;
			}
			if (d == null) {
				if (x11.getBounds().contains(destination))
					d = x11;
				else if (x12.getBounds().contains(destination))
					d = x12;
				else if (x21.getBounds().contains(destination))
					d = x21;
				else if (x22.getBounds().contains(destination))
					d = x22;
			}
			
			graph.addVertex(x11);
			graph.addVertex(x12);
			graph.addVertex(x21);
			graph.addVertex(x22);

			e = graph.addEdge(x11, x21);
			graph.setEdgeWeight(e, dc.distance(x11.getCenter(), x21.getCenter()));
			e = graph.addEdge(x11, x12);
			graph.setEdgeWeight(e, dc.distance(x11.getCenter(), x12.getCenter()));
			e = graph.addEdge(x21, x22);
			graph.setEdgeWeight(e, dc.distance(x21.getCenter(), x22.getCenter()));
			e = graph.addEdge(x12, x22);
			graph.setEdgeWeight(e, dc.distance(x12.getCenter(), x22.getCenter()));
			
			// Diagonals
			e = graph.addEdge(x11, x22);
			graph.setEdgeWeight(e, dc.distance(x11.getCenter(), x22.getCenter()));
			e = graph.addEdge(x12, x21);
			graph.setEdgeWeight(e, dc.distance(x12.getCenter(), x21.getCenter()));
			
			if (firstIter) {
				firstIter = false;

				xPrev11 = x11;
				xPrev12 = x12;
				xPrev21 = x21;
				xPrev22 = x22;

				xPrev = x;
				continue;
			}

			assert xPrev != null;

			if (x.getX() == xPrev.getX() - 1) {		// x -- xPrev
				e = graph.addEdge(x21, xPrev11);
				graph.setEdgeWeight(e, dc.distance(x21.getCenter(), xPrev11.getCenter()));

				e = graph.addEdge(x22, xPrev12);
				graph.setEdgeWeight(e, dc.distance(x22.getCenter(), xPrev12.getCenter()));
				
				// Diagonals
				e = graph.addEdge(x21, xPrev12);
				graph.setEdgeWeight(e, dc.distance(x21.getCenter(), xPrev12.getCenter()));
				e = graph.addEdge(x22, xPrev11);
				graph.setEdgeWeight(e, dc.distance(x22.getCenter(), xPrev11.getCenter()));

			} else if (x.getX() == xPrev.getX() + 1) {		// xPrev -- x
				e = graph.addEdge(x11, xPrev21);
				graph.setEdgeWeight(e, dc.distance(x11.getCenter(), xPrev21.getCenter()));

				e = graph.addEdge(x12, xPrev22);
				graph.setEdgeWeight(e, dc.distance(x12.getCenter(), xPrev22.getCenter()));
				
				// Diagonals
				e = graph.addEdge(x11, xPrev22);
				graph.setEdgeWeight(e, dc.distance(x11.getCenter(), xPrev22.getCenter()));
				
				e = graph.addEdge(x12, xPrev21);
				graph.setEdgeWeight(e, dc.distance(x12.getCenter(), xPrev21.getCenter()));

			} else if (x.getY() == xPrev.getY() - 1) {			// x is under xPrev
				e = graph.addEdge(x12, xPrev11);
				graph.setEdgeWeight(e, dc.distance(x12.getCenter(), xPrev11.getCenter()));

				e = graph.addEdge(x22, xPrev21);
				graph.setEdgeWeight(e, dc.distance(x22.getCenter(), xPrev21.getCenter()));
				
				// Diagonals
				e = graph.addEdge(x12, xPrev21);
				graph.setEdgeWeight(e, dc.distance(x12.getCenter(), xPrev21.getCenter()));
				
				e = graph.addEdge(x22, xPrev11);
				graph.setEdgeWeight(e, dc.distance(x22.getCenter(), xPrev11.getCenter()));

			} else if (x.getY() == xPrev.getY() + 1) {			// x is over xPrev
				e = graph.addEdge(x11, xPrev12);
				graph.setEdgeWeight(e, dc.distance(x11.getCenter(), xPrev12.getCenter()));

				e = graph.addEdge(x21, xPrev22);
				graph.setEdgeWeight(e, dc.distance(x21.getCenter(), xPrev22.getCenter()));
				
				// Diagonals
				e = graph.addEdge(x11, xPrev22);
				graph.setEdgeWeight(e, dc.distance(x11.getCenter(), xPrev22.getCenter()));
				
				e = graph.addEdge(x21, xPrev12);
				graph.setEdgeWeight(e, dc.distance(x21.getCenter(), xPrev12.getCenter()));

			} else if (x.getZ() == xPrev.getZ() - 1 || x.getZ() == xPrev.getZ() + 1) {
				e = graph.addEdge(x11, xPrev11);
				graph.setEdgeWeight(e, dc.distance(x11.getCenter(), xPrev11.getCenter()));

				e = graph.addEdge(x12, xPrev12);
				graph.setEdgeWeight(e, dc.distance(x12.getCenter(), xPrev12.getCenter()));

				e = graph.addEdge(x21, xPrev21);
				graph.setEdgeWeight(e, dc.distance(x21.getCenter(), xPrev21.getCenter()));

				e = graph.addEdge(x22, xPrev22);
				graph.setEdgeWeight(e, dc.distance(x22.getCenter(), xPrev22.getCenter()));
				
				// Diagonals
				e = graph.addEdge(x11, xPrev22);
				graph.setEdgeWeight(e, dc.distance(x11.getCenter(), xPrev22.getCenter()));
				
				e = graph.addEdge(x12, xPrev21);
				graph.setEdgeWeight(e, dc.distance(x12.getCenter(), xPrev21.getCenter()));
				
				e = graph.addEdge(x21, xPrev12);
				graph.setEdgeWeight(e, dc.distance(x21.getCenter(), xPrev12.getCenter()));
				
				e = graph.addEdge(x22, xPrev11);
				graph.setEdgeWeight(e, dc.distance(x22.getCenter(), xPrev11.getCenter()));
			}

			xPrev11 = x11;
			xPrev12 = x12;
			xPrev21 = x21;
			xPrev22 = x22;
			xPrev = x;

		}
		
		return new CellGraph(graph, s, d);
	}

	@Override
	public String toString() {
		return String.format("CellGraphBuilder[zones=%s]", zones);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		CellGraphBuilder other = (CellGraphBuilder) obj;
		return zones.equals(other.zones);
	}
}