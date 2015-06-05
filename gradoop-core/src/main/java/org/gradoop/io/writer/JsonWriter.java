package org.gradoop.io.writer;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.gradoop.model.Attributed;
import org.gradoop.model.Edge;
import org.gradoop.model.GraphElement;
import org.gradoop.model.Vertex;

/**
 * Converts a vertex into a Json representation.
 */
public class JsonWriter implements VertexLineWriter {
  /**
   * Key for the vertex id.
   */
  public static final String VERTEX_ID = "id";
  /**
   * Key for vertex label.
   */
  public static final String LABEL = "label";
  /**
   * Key for vertex and edge properties.
   */
  public static final String PROPERTIES = "properties";
  /**
   * Key for outgoing edges.
   */
  public static final String OUT_EDGES = "out-edges";
  /**
   * Key for incoming edges.
   */
  public static final String IN_EDGES = "in-edges";
  /**
   * Key for graphs.
   */
  public static final String GRAPHS = "graphs";
  /**
   * Key for otherid of an edge.
   */
  public static final String EDGE_OTHER_ID = "otherid";
  /**
   * Key for edge label.
   */
  public static final String EDGE_LABEL = "label";

  /**
   * {@inheritDoc}
   */
  @Override
  public String writeVertex(Vertex vertex) {
    JSONObject json = new JSONObject();
    try {
      json.put(VERTEX_ID, vertex.getID());
      json.put(LABEL, vertex.getLabel());
      json = writeProperties(json, vertex);
      json = writeEdges(json, OUT_EDGES, vertex.getOutgoingEdges());
      json = writeEdges(json, IN_EDGES, vertex.getIncomingEdges());
      json = writeGraphs(json, vertex);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return json.toString();
  }

  /**
   * Write properties to the given json object.
   *
   * @param json       json object to write to
   * @param attributed entity with attributes (graphs, vertex, edge)
   * @return updated json object
   * @throws JSONException
   */
  private JSONObject writeProperties(final JSONObject json,
    final Attributed attributed) throws JSONException {
    JSONObject properties = new JSONObject();
    if (attributed.getPropertyCount() > 0) {
      for (String propertyKey : attributed.getPropertyKeys()) {
        properties.put(propertyKey, attributed.getProperty(propertyKey));
      }
      json.put(PROPERTIES, properties);
    }
    return json;
  }

  /**
   * Write either outgoing or incoming edges of to the given json object.
   *
   * @param json  json object to write edges to
   * @param key   used to differ incoming and outgoing edges
   * @param edges edges to write
   * @return updated json object
   * @throws JSONException
   */
  private JSONObject writeEdges(final JSONObject json, final String key,
    final Iterable<Edge> edges) throws JSONException {
    JSONArray edgeArray = new JSONArray();
    for (Edge e : edges) {
      JSONObject jsonEdge = new JSONObject();
      jsonEdge.put(EDGE_OTHER_ID, e.getOtherID());
      jsonEdge.put(EDGE_LABEL, e.getLabel());
      jsonEdge = writeProperties(jsonEdge, e);
      edgeArray.put(jsonEdge);
    }
    json.put(key, edgeArray);
    return json;
  }

  /**
   * Write graphs to the given json object.
   *
   * @param json         json object to write graphs to
   * @param graphElement entity that is part of graphs
   * @return updated json object
   * @throws JSONException
   */
  private JSONObject writeGraphs(final JSONObject json,
    final GraphElement graphElement) throws JSONException {
    JSONArray graphArray = new JSONArray();
    if (graphElement.getGraphCount() > 0) {
      for (Long graph : graphElement.getGraphs()) {
        graphArray.put(graph);
      }
      json.put(GRAPHS, graphArray);
    }
    return json;
  }
}