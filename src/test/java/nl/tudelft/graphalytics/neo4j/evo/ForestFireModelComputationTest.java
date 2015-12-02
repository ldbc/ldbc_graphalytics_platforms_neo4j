/**
 * Copyright 2015 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.tudelft.graphalytics.neo4j.evo;

import nl.tudelft.graphalytics.domain.algorithms.ForestFireModelParameters;
import nl.tudelft.graphalytics.neo4j.ValidationGraphLoader;
import nl.tudelft.graphalytics.validation.GraphStructure;
import nl.tudelft.graphalytics.validation.evo.ForestFireModelValidationTest;
import org.neo4j.graphdb.*;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static nl.tudelft.graphalytics.neo4j.Neo4jConfiguration.EDGE;
import static nl.tudelft.graphalytics.neo4j.Neo4jConfiguration.ID_PROPERTY;

/**
 * Test case for the forest fire model implementation on Neo4j.
 *
 * @author Tim Hegeman
 */
public class ForestFireModelComputationTest extends ForestFireModelValidationTest {

	@Override
	public GraphStructure executeDirectedForestFireModel(GraphStructure graph,
			ForestFireModelParameters parameters) throws Exception {
		return executeForestFireModel(graph, parameters, true);
	}

	@Override
	public GraphStructure executeUndirectedForestFireModel(GraphStructure graph,
			ForestFireModelParameters parameters) throws Exception {
		return executeForestFireModel(graph, parameters, false);
	}

	private GraphStructure executeForestFireModel(GraphStructure graph, ForestFireModelParameters parameters,
			boolean directed) {
		GraphDatabaseService database = ValidationGraphLoader.loadValidationGraphToDatabase(graph);
		new ForestFireModelComputation(database, parameters, directed).run();

		Map<Long, Set<Long>> edgeLists = new HashMap<>();
		try (Transaction ignored = database.beginTx()) {
			for (Node node : GlobalGraphOperations.at(database).getAllNodes()) {
				edgeLists.put((long)node.getProperty(ID_PROPERTY), new HashSet<Long>());
			}
			for (Relationship relationship : GlobalGraphOperations.at(database).getAllRelationships()) {
				if (relationship.isType(EDGE)) {
					edgeLists.get(relationship.getStartNode().getProperty(ID_PROPERTY))
							.add((long)relationship.getEndNode().getProperty(ID_PROPERTY));
				}
			}
		}
		database.shutdown();
		return new GraphStructure(edgeLists);
	}

}
