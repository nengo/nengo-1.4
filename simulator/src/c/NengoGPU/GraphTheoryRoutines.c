#include <stdlib.h>
#include <limits.h>
#include <string.h>

#include "GraphTheoryRoutines.h"
#include "fheap.h"

Graph* new_graph()
{
  Graph* new = (Graph*) malloc(sizeof(Graph));

  new->vertexList = NULL;
  new->edgeList = NULL;

  new->nextAvailableVertexId = 0;
  new->nextAvailableEdgeId = 0;
  new->numVertices = 0;
  new->numEdges = 0;

  return new;
}

void free_graph(Graph* G)
{
  if(G)
  {
    free_edge_list(G->edgeList, 1);
    free_vertex_list(G->vertexList, 1);
    free(G);
  }
}

Vertex* get_vertex_from_graph(Graph* G, int id)
{
  VertexList* vertices = G->vertexList;
  while(vertices && vertices->first->id != id)
  {
    vertices = vertices->next;
  }
  
  return vertices ? vertices->first : NULL;
}

Edge* get_edge_from_graph(Graph* G, int id)
{
  EdgeList* edges = G->edgeList;
  while(edges && edges->first->id != id)
  {
    edges = edges->next;
  }
  
  return edges ? edges->first : NULL;
}

int set_edge_capacity(Edge* edge, int val)
{
  edge->capacity = val;
  edge->opposite->capacity = val;

  return 1;
}

EdgeList* remove_edge_from_list(EdgeList* edgeList, Edge* edge)
{
  EdgeList* current = edgeList, *previous = NULL;

  while(current && current->first->id != edge->id)
  {
    current = current->next;
    previous = previous ? previous->next : edgeList;
  }

  if(current)
  {
    if(previous)
    {
      previous->next = current->next;
      current->next = NULL;
      free_edge_list(current, 0);
      return edgeList;
    }
    else
    {
      EdgeList* temp = current->next;
      current->next = NULL;
      free_edge_list(current, 0);
      return temp;
    }
  }
  else
  {
    return NULL;
  }
}

int remove_edge_from_graph(Graph* G, Edge* edge)
{
  G->edgeList = remove_edge_from_list(G->edgeList, edge);
  edge->sink->edges = remove_edge_from_list(edge->sink->edges, edge);
  edge->source->edges = remove_edge_from_list(edge->source->edges, edge);

  G->numEdges--;

  free_edge(edge);

  return 1;
}
    
void print_vertex_list(VertexList* list)
{
  printf("printing vertex list: \n");
  while(list)
  {
    printf("%d, ", list->first->id);
    list = list->next;
  }
}

void print_edge_list(EdgeList* list)
{
  printf("printing edge list: \n");
  while(list)
  {
    printf("id: %d, source: %d, sink: %d", list->first->id, list->first->source->id, list->first->sink->id);
    list = list->next;
  }
}

VertexList* cons_vertex_list(Vertex* first, VertexList* next)
{
  VertexList* new = (VertexList*)malloc(sizeof(VertexList));

  new->size = next ? next->size + 1 : 1;

  new->first = first;
  new->next = next;

  return new;
}

VertexList* append_vertex_list(VertexList* list1, VertexList* list2)
{
  VertexList *current = list1, *previous = NULL;
  while(current)
  {
    current->size += list2 ? list2->size : 0;
    previous = previous ? previous->next : list1;
    current = current->next;
  }

  if(previous)
  {
    previous->next = list2;
    return list1;
  }
  else
  {
    return list2;
  }
}

EdgeList* append_edge_list(EdgeList* list1, EdgeList* list2)
{
  EdgeList *current = list1, *previous = NULL;
  while(current)
  {
    current->size += list2 ? list2->size : 0;
    previous = previous ? previous->next : list1;
    current = current->next;
  }

  if(previous)
  {
    previous->next = list2;
    return list1;
  }
  else
  {
    return list2;
  }
}

void free_vertex(Vertex* vertex)
{
  if(vertex)
  {
    free_edge_list(vertex->edges, 0);
    free_vertex_list(vertex->representedVertices, 1);

    free(vertex);
  }
}

void free_vertex_list(VertexList* list, int freeVertices)
{
  if(list)
  {
    if(freeVertices)
      free_vertex(list->first);
    
    VertexList* temp = list->next;
    free(list);
    free_vertex_list(temp, freeVertices);
  }
}

EdgeList* cons_edge_list(Edge* first, EdgeList* next)
{
  EdgeList* new = (EdgeList*)malloc(sizeof(EdgeList));

  new->size = next ? next->size + 1 : 1;

  new->first = first;
  new->next = next;
  return new;
}

void free_edge(Edge* edge)
{

  if(edge)
  { 
    if(edge->opposite)
      free(edge->opposite);

    free(edge);
  }
}

void free_edge_list(EdgeList* list, int freeEdges)
{
  if(list)
  {
    if(freeEdges)
      free_edge(list->first);
    
    EdgeList* temp = list->next;
    free(list);
    free_edge_list(temp, freeEdges);
  }
}


int member_edge_list(Edge* edge, EdgeList* list)
{
  EdgeList* current = list;

  while(current)
  {
    if(current->first->id == edge->id || current->first->opposite->id == edge->id)
    {
      return 1;
    }

    current = current->next;
  }

  return 0;
}

void merge_edge_lists(Graph* G, Vertex* v1, Vertex* v2)
{
  EdgeList* temp1 = v1->edges, *temp2;

  while(temp1)
  {
    temp2 = v2->edges;
    while(temp2)
    {
      if(temp1->first->sink == temp2->first->sink)
      {
        // we found a duplicate, so we add the edge weights in list1 and delete the extra edge in list2
        set_edge_capacity(temp1->first, temp1->first->capacity + temp2->first->capacity);

        remove_edge_from_graph(G, temp2->first);
        break;
      }
      
      temp2 = temp2->next;
    }

    temp1 = temp1->next;
  }
  
  temp2 = v2->edges;
  while(temp2)
  {
    if(temp2->first->sink->id == v1->id)
    {
      remove_edge_from_graph(G, temp2->first);
      break;
    }
    
    temp2 = temp2->next;
  }

  // change the vertices in the second list
  temp2 = v2->edges;
  while(temp2)
  {
    temp2->first->source = v1;
    temp2->first->opposite->sink = v1;
    temp2 = temp2->next;
  }

  v1->edges = append_edge_list(v1->edges, v2->edges);
}

void merge_vertices(Graph* G, int s, int t)
{
  Vertex* s_v = get_vertex_from_graph(G, s);
  Vertex* t_v = get_vertex_from_graph(G, t);

  merge_edge_lists(G, s_v, t_v);

  s_v->representedVertices = append_vertex_list(s_v->representedVertices, t_v->representedVertices);
  s_v->representedVertices = cons_vertex_list(t_v, s_v->representedVertices);
  t_v->edges = NULL;
  t_v->representedVertices = NULL;

  // this removes the vertex from the graph but doesn't delete it
  remove_vertex_from_graph(G, t);
}

int add_vertex_to_graph(Graph* G)
{
  Vertex* new = (Vertex*) malloc(sizeof(Vertex));
  G->vertexList = cons_vertex_list(new, G->vertexList);

  new->edges = NULL;
  new->representedVertices = NULL;
  new->id = G->nextAvailableVertexId; 
  G->nextAvailableVertexId++;
  G->numVertices++;

  return 1;
}

// Remove the vertex with id t from the graph G. Deletes the VertexList cell that
// the graph used to hold the vertex, but not the vertex itself. Does not remove any edges.
int remove_vertex_from_graph(Graph* G, int t)
{
  VertexList* current = G->vertexList, *previous = NULL;

  while(current && current->first->id != t)
  {
    previous = previous ? previous->next : G->vertexList;
    current = current->next;
  }

  if(current)
  {
    G->numVertices--;
    previous ? (previous->next = current->next) : (G->vertexList = current->next);

    current->next = NULL;
    free_vertex_list(current, 0);
    return 1;
  }

  return 0;
}
      

// Add an edge from vertex s to vertex t in graph G with the given weight
// If the edge already exists or one of the specified vertices doesn't exist, returns 0.
// Otherwise returns 1.
int add_edge_to_graph(Graph* G, int s, int t, int weight)
{
  Vertex* v1 = get_vertex_from_graph(G, s);
  Vertex* v2 = get_vertex_from_graph(G, t);

  if(!v1 || !v2)
    return 0;

  // Check whether the edge we want to add is already in the graph
  EdgeList* current = v1->edges;
  while(current)
  {
    if(current->first->sink == v2)
    {
      return 0;
    }
    current = current->next;
  }

  Edge* edge1 = (Edge*) malloc(sizeof(Edge));  
  Edge* edge2 = (Edge*) malloc(sizeof(Edge));  

  edge1->id = G->numEdges; 
  edge2->id = G->numEdges; 
  G->numEdges++;

  edge1->source = v1;
  edge1->sink = v2;

  edge2->source = v2;
  edge2->sink = v1;

  edge1->opposite = edge2;
  edge2->opposite = edge1;

  edge1->flow = 0;
  edge2->flow = 0;

  edge1->capacity = weight;
  edge2->capacity = weight;

  v1->edges = cons_edge_list(edge1, v1->edges);
  v2->edges = cons_edge_list(edge2, v2->edges);

  G->edgeList = cons_edge_list(edge1, G->edgeList);

  return 1;
}

// Must be called with the array partition initialized to have length n, filled with 0's.
// Starts at a source vertex and records in the array partition all vertices reachable from
// the source without traversing edges in cutEdges. Used to construct a partition of a graph
// from a graph cut.
void constructVertexPartition(Graph* G, int* partition, Vertex* currentSource, int* cutEdges)
{
  EdgeList* current = currentSource->edges; 

  int sink_id;

  while(current)
  {
    sink_id = current->first->sink->id;
    
    if(!cutEdges[current->first->id/2] && !partition[sink_id])
    {
      partition[sink_id] = 1;
      constructVertexPartition(G, partition, current->first->sink, cutEdges);
    }

    current = current->next;
  }
}


// Find a path in the graph which is not at flow capacity.
EdgeList* findFlowAugmentingPath(Graph* G, int s, int t, EdgeList* path, int* verticesVisited)
{
  if(s == t)
  {
    return path;
  }

  Vertex* root = get_vertex_from_graph(G, s);

  EdgeList* adjacentEdges = root->edges, *newPath;

  int residual;
  Edge* currentEdge;
  
  while(adjacentEdges)
  {
    currentEdge = adjacentEdges->first;
    residual = currentEdge->capacity - currentEdge->flow;
    
    if(!verticesVisited[currentEdge->sink->id] && residual > 0 && !member_edge_list(currentEdge, path))
    {
      path = cons_edge_list(currentEdge, path);
      
      newPath = findFlowAugmentingPath(G, currentEdge->sink->id, t, path, verticesVisited);
      
      if(newPath)
        return newPath;

      path->next = NULL;
      free_edge_list(path, 0);
    }

    adjacentEdges = adjacentEdges->next;
  }

  verticesVisited[s] = 1; 
  return NULL;
}

// Ford-fulkerson method for finding min st-cut in an undirected graph
int fordFulkerson_findMinCut(Graph* G, int s, int t, int** cutEdges, int* numEdges)
{
  int i;
  for(i = 0; i < 2 * G->numEdges; i++)
  {
    // has to be redone since i changed the graph to have a list of edges instead of an array
    //(G->edgeList + i)->flow = 0;
  }

  EdgeList* path, *current; 

  int flow, residual;
  Edge* cutEdge;
  *cutEdges = (int*)malloc(G->numEdges * sizeof(int));

  for(i = 0; i < G->numEdges; i++)
  {
    (*cutEdges)[i] = 0;
  }

  int* verticesVisited = (int*) malloc(G->numVertices * sizeof(int));
  memset(verticesVisited, '\0', G->numVertices * sizeof(int));

  while((path = findFlowAugmentingPath(G, s, t, NULL, verticesVisited)))
  {
    current = path;
    flow = INT_MAX;
    cutEdge = current->first;

    // find the maximum amount of flow we can add along the flow augmenting path we found
    while(current)
    {
      residual = current->first->capacity - current->first->flow;

      if(residual < flow)
      {
        flow = residual;
        cutEdge = current->first;
      }
      
      // None of the edges in the current path will be cut edges except the one with the minimum capacity
      (*cutEdges)[current->first->id / 2] = 0;

      current = current->next;
    }

    (*cutEdges)[cutEdge->id] = 1;

    // add the flow to the path
    current = path;
    while(current)
    {
      current->first->flow += flow;
      current->first->opposite->flow -= flow;

      current = current->next;
    }

    free_edge_list(path, 0);
    memset(verticesVisited, '\0', G->numVertices * sizeof(int));
  }

  free(verticesVisited);

  int sum = 0;

  *numEdges = 0;
  for(i = 0; i < G->numEdges; i++)
  {
    if((*cutEdges)[i])
    {
      (*numEdges)++;
      //sum += max(G->edgeList[2*i].flow, G->edgeList[2*i + 1].flow);
    }
  }

  return sum;
}

/*
 * Computes  the n-1 min cuts of G. Returns their values in the array cutValues, the indices of
 * the edges that make up the min cut in the array cutEdges, the number of edges in each
 * min cut in the array numCutEdges and the partition of the vertices made by each cut in cutPartition.
 */
void gusfield_allPairsMinCut(Graph* G, int** cutValues, int*** cutEdges, int** numCutEdges, int*** cutPartition)
{
  // Initalize the tree which maintains which pairwise cuts we have to check. Because we know
  // its a tree, we do not have to store it as a normal graph, we can just store the index of
  // each vertices parent in the tree. Index 0 is the root.

  int i, n = G->numVertices;
  int* tree = (int*)malloc(n * sizeof(int));
  for(i = 0; i < n; i++)
  {
    tree[i] = 0;
  }

  // Initialize the arrays used to return the data
  *cutPartition = (int**)malloc((n - 1) * sizeof(int*));
  *cutEdges = (int**)malloc((n - 1) * sizeof(int*));
  *cutValues = (int*)malloc((n - 1) * sizeof(int));
  *numCutEdges = (int*)malloc((n - 1) * sizeof(int));
  
  int s, t;
  Vertex* s_vertex;

  for(s = 1; s < n; s++)
  {
    t = tree[s];
    
    // find the min cut, its value, and the number of edges therein
    (*cutValues)[s-1] = fordFulkerson_findMinCut(G, s, t, (*cutEdges + s-1), (*numCutEdges + s-1));

    // determine which vertices are on either side of the cut
    (*cutPartition)[s-1] = (int*)malloc(n * sizeof(int));
    memset((*cutPartition)[s-1], '\0', n * sizeof(int));
    s_vertex = get_vertex_from_graph(G, s);
    constructVertexPartition(G, (*cutPartition)[s-1], s_vertex, (*cutEdges)[s-1]);

    // go through all the vertices with id bigger than the id of s. If its on the same side of the
    // cut as s and their parent in the tree is t, make its parent s.
    for(i = s; i < n; i++)
    {
      if((*cutPartition)[s-1][i] && tree[i] == t)
        tree[i] = s;
    }
  }

  free(tree);
}

void free_gusfield_results(int* cutValues, int** cutEdges, int* numCutEdges, int** cutPartition, int n)
{
  int i;

  free(cutValues);
  free(numCutEdges);

  for(i = 0; i < n-1; i++)
  {
    free(*cutEdges);
    free(*cutPartition);
  }

  free(cutEdges);
  free(cutPartition);
}

// converts an adjacency matrix stored in lower triangular form to a graph
Graph* convertAdjacencyMatrixToGraph(int* adj, int n)
{
  Graph* G = new_graph();

  int i, j;
  for(i = 0; i < n; i++)
  {
    add_vertex_to_graph(G); 
  }

  for(i = 0; i < n; i++)
  {
    for(j = 0; j < n; j++)
    {
      if(i > j && adj[i * n + j] > 0)
      {
        add_edge_to_graph(G, i, j, adj[i * n + j]); 
      }
    }
  }

  return G;
}


// Computes a cut_of_the_phase for the graph G. For what exactly this means, see the paper
// mentioned in the comments of function stoerWagner_allPairsMinCut. Basically, it computes a
// min cut in the graph G, which corresponds to a min cut in the original graph. We pass back information 
// about which side of the cut the vertices fall on in the array cutPartition. n is the number
// of vertices in the original graph.
int stoerWagner_minCutPhase(Graph* G, int** cutPartition, int n, fheap_t* fib_heap)
{
  if(G->numVertices <= 2)
  {
    *cutPartition = (int*)malloc(n * sizeof(int));
    memset(*cutPartition, '\0', n * sizeof(int));

    (*cutPartition)[G->vertexList->first->id] = 1;

    VertexList* represented = G->vertexList->first->representedVertices;
    while(represented)
    {
      (*cutPartition)[represented->first->id] = 1;
      represented = represented->next;
    }

    return G->edgeList ? G->edgeList->first->capacity : 0;
  }
    
  // initialize fib heap with all vertices other than 'a' and their negative connecting weights to 'a'
  int a = G->vertexList->first->id;

  char* vertexChecked = (char*)malloc(n * sizeof(char));
  memset(vertexChecked, '\0', n * sizeof(char));

  EdgeList* edgeList = get_vertex_from_graph(G, a)->edges;

  // go through 'a' 's neighbours, adding each one the fib_heap with the weight between it and 'a' as the key 
  while(edgeList)
  {
    vertexChecked[edgeList->first->sink->id] = '1';

    //printf("inserting key %d with weight %d\n", edgeList->first->sink->id, INT_MAX - edgeList->first->capacity);
    fh_insert(fib_heap, edgeList->first->sink->id, INT_MAX - edgeList->first->capacity);
    edgeList = edgeList->next;
  }

  // add vertices that aren't neighbours of 'a' with key 0
  VertexList* vertexList = G->vertexList;
  while(vertexList)
  {
    if(!vertexChecked[vertexList->first->id])
    {
      //printf("inserting key %d with weight %d. NExt pointer: %d\n", vertexList->first->id, INT_MAX, (int)vertexList->next);
      fh_insert(fib_heap, vertexList->first->id, INT_MAX);
    }
    vertexList = vertexList->next;
  }

  free(vertexChecked);
  
  char* in_A = (char*)malloc(n * sizeof(char));
  memset(in_A, '\0', n * sizeof(char));
  in_A[0] = '1';

  int min_index, second_last = 0;

  /*
  The cut phase proceeds as follows. We maintain a subset A of vertices in G. Initially, a is the only element of A.
  One at a time, we choose the vertex b in G \ A which is most heavily connected to A (the sum of the weights of its
  connections with vertices in A is highest), and add b to A. To perform this computation efficiently, we use a fibonacci heap. 
  At all times, we want the vertex that will be extracted by fh_deleteMin to be the next vertex that we add to A.
  This means that the key in the heap for each remaining vertex is the sum of the weights of its connections with vertices in A.
  This also means that if we add a vertex b to A, we have to decrease the keys of all of b's neighouring vertices that aren't in A.
  */
  while(fib_heap->n > 1)
  {
    min_index = fh_delete_min(fib_heap);
    //printf("deleting min - index = %d\n", min_index);
    in_A[min_index] = 1;

    if(fib_heap->n == 2)
      second_last = min_index;

    edgeList = get_vertex_from_graph(G, min_index)->edges;

    while(edgeList)
    {
      // make sure the vertex whose key we are going to decrease has not already been deleted
      if(!in_A[edgeList->first->sink->id])
      {
        //printf("decreasing key of index %d by %d\n", edgeList->first->sink->id, edgeList->first->capacity);
        fh_decrease_key(fib_heap, edgeList->first->sink->id, edgeList->first->capacity);
      }

      edgeList = edgeList->next;
    }
  }

  free(in_A);

  int last = fh_delete_min(fib_heap);
  int sum = 0;

  edgeList = get_vertex_from_graph(G, last)->edges;

  // calculate the value of the cut
  while(edgeList)
  {
    sum += edgeList->first->capacity;
    edgeList = edgeList->next;
  }

// now set the partition. This basically says, for each vertex, which side of the cut it falls on
  *cutPartition = (int*)malloc(n * sizeof(int));

  if(!*cutPartition)
  {
    printf("couldnt allocate memory\n");
    exit(EXIT_FAILURE);
  }
  memset(*cutPartition, '\0', n * sizeof(int));

  (*cutPartition)[last] = 1;
  Vertex* vertex = get_vertex_from_graph(G, last);
  vertexList = vertex->representedVertices;

  while(vertexList)
  {
    //printf("%d ", vertexList->first->id);
    (*cutPartition)[vertexList->first->id] = 1;
    vertexList = vertexList->next;
  }

  // reduce the graph
  merge_vertices(G, second_last, last);

  return sum;
}
/*
// insert a value in order in a pre-sorted array
int sorted_insert(int num, int length, int* array, int order)
{
  int temp, i = 0; 
  while(i < length && ((order && num < array[i]) || (!order && num > array[i])))
  {
    i++;
  }

  int insertionIndex = i;

  while(i < length)
  {
    temp = array[i];
    array[i] = num;
    num = temp;
    i++;
  }
  
  return insertionIndex;
}
*/
// returns the index of the min cut. Returns by reference all computed cuts and their
// associated partitions.
void stoerWagner_allPairsMinCut(Graph* G, int** cutValues, int*** cutPartition)
{
  printf("in stoerWagner\n");
  printf("graph data\n");
  printf("numEdges: %d\n", G->numEdges);
  printf("numVertices: %d\n", G->numVertices);

  int i = 0, n = G->numVertices;
  // Initialize the arrays used to return the data
  *cutPartition = (int**)malloc((n - 1) * sizeof(int*));
  *cutValues = (int*)malloc((n - 1) * sizeof(int));
  
  fheap_t* fib_heap = fh_alloc(n);
  
  for(i = 0; i < n - 1; i++)
  {
    (*cutValues)[i] = stoerWagner_minCutPhase(G, ((*cutPartition) + i), n, fib_heap);
  }

  fh_free(fib_heap);
}

void free_stoerWagnerResults(int* cutValues, int** cutPartition, int totalNumEnsembles)
{
  free(cutValues);

  int i;
  for(i = 0; i < totalNumEnsembles - 1; i++)
  {
    free(cutPartition[i]);
  }
}
