package nl.tudelft.graphalytics.neo4j.pr;

import nl.tudelft.graphalytics.domain.algorithms.PageRankParameters;
import nl.tudelft.graphalytics.neo4j.ValidationGraphLoader;
import nl.tudelft.graphalytics.validation.GraphStructure;
import nl.tudelft.graphalytics.validation.algorithms.pr.PageRankOutput;
import nl.tudelft.graphalytics.validation.algorithms.pr.PageRankValidationTest;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.HashMap;
import java.util.Map;

import static nl.tudelft.graphalytics.neo4j.Neo4jConfiguration.ID_PROPERTY;
import static nl.tudelft.graphalytics.neo4j.pr.PageRankComputation.PAGERANK;

/**
 * Test case for the PageRank implementation on Neo4j.
 *
 * @author Tim Hegeman
 */
public class PageRankComputationTest extends PageRankValidationTest {

	@Override
	public PageRankOutput executeDirectedPageRank(GraphStructure graph, PageRankParameters parameters) throws Exception {
		return executePagerank(graph, parameters);
	}

	@Override
	public PageRankOutput executeUndirectedPageRank(GraphStructure graph, PageRankParameters parameters) throws Exception {
		return executePagerank(graph, parameters);
	}

	private PageRankOutput executePagerank(GraphStructure graph, PageRankParameters parameters) {
		GraphDatabaseService database = ValidationGraphLoader.loadValidationGraphToDatabase(graph);
		new PageRankComputation(database, parameters.getNumberOfIterations(), parameters.getDampingFactor(),
				graph.getVertices().size()).run();

		Map<Long, Double> output = new HashMap<>();
		try (Transaction ignored = database.beginTx()) {
			for (Node node : GlobalGraphOperations.at(database).getAllNodes()) {
				output.put((long)node.getProperty(ID_PROPERTY), (double)node.getProperty(PAGERANK));
			}
		}
		database.shutdown();
		return new PageRankOutput(output);
	}

}
