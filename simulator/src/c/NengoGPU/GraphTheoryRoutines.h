#include <stdio.h>

#include "fheap.h"

typedef struct Graph_t Graph;
typedef struct VertexList_t VertexList;
typedef struct Vertex_t Vertex;
typedef struct EdgeList_t EdgeList;
typedef struct Edge_t Edge;

struct Graph_t
{
  VertexList* vertexList;
  EdgeList* edgeList;

  int numVertices;
  int nextAvailableVertexId;
  int numEdges;
  int nextAvailableEdgeId;
};

struct Vertex_t
{
  int id;
  EdgeList* edges;
  VertexList* representedVertices;
};

struct VertexList_t
{
  Vertex* first;
  VertexList* next;
  int size;
};

struct EdgeList_t
{
  Edge* first;
  EdgeList* next;
  int size;
};

struct Edge_t
{
  int id;
  Vertex* source;
  Vertex* sink;

  Edge* opposite;
  int flow;
  int capacity;
};


Graph* new_graph();
void free_graph(Graph* G);
Vertex* get_vertex_from_graph(Graph* G, int id);
Edge* get_edge_from_graph(Graph* G, int id);
int set_edge_capacity(Edge* edge, int val);
EdgeList* remove_edge_from_list(EdgeList* edgeList, Edge* edge);
int remove_edge_from_graph(Graph* G, Edge* edge);
void print_vertex_list(VertexList* list);
void print_edge_list(EdgeList* list);
VertexList* cons_vertex_list(Vertex* first, VertexList* next);
VertexList* append_vertex_list(VertexList* list1, VertexList* v2);
EdgeList* append_edge_list(EdgeList* list1, EdgeList* list2);
void free_vertex(Vertex* vertex);
void free_vertex_list(VertexList* list, int freeVertices);
EdgeList* cons_edge_list(Edge* first, EdgeList* next);
void free_edge(Edge* edge);

void free_edge_list(EdgeList* list, int freeEdges);
int member_edge_list(Edge* edge, EdgeList* list);
void merge_edge_lists(Graph* G, Vertex* v1, Vertex* v2);
void merge_vertices(Graph* G, int s, int t);
int add_vertex_to_graph(Graph* G);
int remove_vertex_from_graph(Graph* G, int t);
int add_edge_to_graph(Graph* G, int s, int t, int weight);
void constructVertexPartition(Graph* G, int* partition, Vertex* currentSource, int* cutEdges);
EdgeList* findFlowAugmentingPath(Graph* G, int s, int t, EdgeList* path, int* verticesVisited);
int fordFulkerson_findMinCut(Graph* G, int s, int t, int** cutEdges, int* numEdges);
void gusfield_allPairsMinCut(Graph* G, int** cutValues, int*** cutEdges, int** numCutEdges, int*** cutPartition);
void free_gusfield_results(int* cutValues, int** cutEdges, int* numCutEdges, int** cutPartition, int n);
Graph* convertAdjacencyMatrixToGraph(int* adj, int n);
int stoerWagner_minCutPhase(Graph* G, int** cutPartition, int n, fheap_t* fib_heap);
void stoerWagner_allPairsMinCut(Graph* G, int** cutValues, int*** cutPartition);
int sorted_insert(int num, int length, int* array, int order);
void free_stoerWagnerResults(int* cutValues, int** cutPartition, int totalNumEnsembles);
