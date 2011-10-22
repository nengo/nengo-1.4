import os
import nef
import hrr
from numeric import array, dot

class WriteToFileNode(nef.SimpleNode):
    ## Simple node class to write network output to file (csv file format)
    # Constructor parameters:
    # - name:          name of this simplenode
    # - filename:      filename (full path) of the output file
    # - network:       network the WriteToFileNode is in (for auto network connections)
    # - vocab:         default vocabulary to use
    # - log_interval:  interval (in seconds) to log output
    # - start:         recording start time (default 0s)
    # - stop:          recording stop time (default INF)
    # - delimiter:     delimiter to use for the output file (default "," for csv format)
    # - delim_replace: replacement string for characters already using the delimiter 
    #                  (e.g. array output is [1,2,3] ==> [1;2;3])
    # - overwrite:     option to overwrite the output file. If set to False, node will append to 
    #                  existing file
    # - pstc:          default pstc to use for the terminations
    #
    def __init__(self, name, filename, network = None, vocab = None, log_interval = 0.1, start = 0, \
                 stop = 1e3000, delimiter = ',', delim_replace = ";", overwrite = False, \
                 pstc = 0.001):
         
        self.epsilon = 0.0000000000001 # Minimum value for output (values smaller than this will 
                                       # be regarded as 0)
        self.log_interval = log_interval
        self.start = start
        self.end   = stop
        self.next_log = start

        self.delimiter = delimiter
        self.delim_replace = delim_replace

        self.filename = filename
        if( len(filename) == 0 ):
            raise RuntimeError("WriteToFileNode.__init__ - Invalid filename specified")

        self.vocab_master = vocab       # Master vocab (if non is specified for the termination)
        self.vocabs = dict()            # Vocabs used for each vocab termination
        self.vocab_list = dict()        # Vocab strings used for each vocab termination
        self.vocab_threshold = dict()   # Vocab detection threshold for each vocab termination
        self.term_list = []

        self.message_list = dict()

        self.overwrite = overwrite
        self.def_pstc = pstc

        nef.SimpleNode.__init__(self, name)

        # Handle difference between nef.Network and regular ca.nengo.model.Network classes
        if( isinstance(network, nef.Network) ):
            self.network = network.network
        else:
            self.network = network
        
        # Add node to the network
        if( not self.network is None ):
            self.network.addNode(self)          

    def template_Termination(self, x):
        return

    def addMessages(self, messages = [], timestamps = []):
        ## addMessages - Adds a list of messages that will appear at specified timestamps
        # Parameters:
        # - messages :  List of messages that will appear
        # - timestamps: List of timestamps for each message in the message list (lengths must match)
        if( len(messages) != len(timestamps) ):
            raise RuntimeError("WriteToFileNode.addMessages - Messages and timestamps length mismatch")
        for i,timestamp in enumerate(timestamps):
            self.message_list[timestamp] = messages[i]
    
    def addValueTermination(self, name, origin = "X", pstc = None):
        ## addValueTermination - Adds a termination to the writeToFileNode to handle regular value 
        #                        (array type) outputs
        # Parameters: There are two modes of operation 
        # Mode 1: Specify the name of the node and origin to record from and let the system do the rest
        # - name(str):   name of the node to record from
        # - origin(str): name of the origin of the node to record from
        # NOTE: All projections will be made automatically in Mode 1
        #
        # Mode 2: Specify the origin object to record from
        # - name(str):   name of the termination
        # - origin(ca.nengo.model.Origin): origin to record from
        # NOTE: All projections will be made automatically if default network is specified in 
        #       constructor
        #
        # - pstc: Termination pstc (if set to None, will use default)
        #
        if( pstc is None ):
            pstc = self.def_pstc
        if( isinstance(origin, str) ):
            if( self.network is None ):
                raise TypeError("WriteToFileNode.addValueTermination - Network not specified: " + \
                                "origin must be type ca.nengo.model.Origin")
            else:
                origin_name = origin
                origin = self.network.getNode(name).getOrigin(origin_name)
                name = name + "_" + origin_name + "_va" # Tag with _va to indicate a value termination
        else:
            origin_name = origin.name

        self.term_list.append(name)
        self.create_termination(name, self.template_Termination)

        self.getTermination(name).setDimensions(origin.getDimensions())
        self.getTermination(name).setTau(pstc)

        if( not self.network is None ):
            self.network.addProjection(origin, self.getTermination(name))            

    def addVocabTermination(self, name, origin = "X", vocab = None, vocab_strs = [], threshold = 0.3, \
                            pstc = None):
        ## addVocabTermination - Adds a termination to the writeToFileNode to handle vocab outputs
        #                        (similar to the vocab graphs in interactive mode)
        # Parameters: The two modes of operation from addValueTermination apply here too
        # - vocab:      vocabulary to use (if None, termination will use self.vocab_master as vocabulary)
        # - vocab_strs: vocabulary strings to compare the input to (if empty list, termination will use
        #               all the string in the vocabulary)
        # - threshold:  cutoff threshold for dot product comparison
        #
        if( pstc is None ):
            pstc = self.def_pstc
        if( isinstance(origin, str) ):
            if( self.network is None ):
                raise TypeError("WriteToFileNode.addValueTermination - Network not specified: " + \
                                "origin must be type ca.nengo.model.Origin")
            else:
                origin_name = origin
                origin = self.network.getNode(name).getOrigin(origin_name)
                name = name + "_" + origin_name + "_vc" # Tag with _vc to indicate a vocab termination
        else:
            origin_name = origin.name

        if( self.vocab_master is None and vocab is None ):
            raise RuntimeError( "WriteToFileNode.addVocabTermination - Vocabulary not specified.")
        if( vocab is None ):
            vocab = self.vocab_master
        if( len(vocab_strs) == 0 ):
            vocab_strs = vocab.keys

        self.term_list.append(name)
        self.create_termination(name, self.template_Termination)

        self.getTermination(name).setDimensions(origin.getDimensions())
        self.getTermination(name).setTau(pstc)

        self.vocabs[name] = vocab
        self.vocab_list[name] = vocab_strs
        self.vocab_threshold[name] = threshold

        if( not self.network is None ):
            self.network.addProjection(origin, self.getTermination(name))            

    def reset(self,randomize=False):
        self.next_log = self.start

        # Create file only if does not exist
        if( not os.path.isfile(self.filename) or self.overwrite ):
            self.log_file = open(self.filename, 'w')
        else:
            self.log_file = open(self.filename, 'a')

        self.log_file.write("timestamp,")
        for n in range(len(self.term_list)):
            self.log_file.write(self.term_list[n] + self.delimiter)
        self.log_file.write("\n")
        self.log_file.close()

        nef.SimpleNode.reset(self, randomize)

    def run(self,start,end):
        # Check if file exists, if it doesn't, create it
        if( start == 0 and not os.path.isfile(self.filename) ):
            self.reset(False)

        nef.SimpleNode.run(self, start, end)
        if( start < self.start or end > self.end ):
            return

        for msg_time in self.message_list.keys():
            print(msg_time)
            if( msg_time <= self.t_end and msg_time >= self.t_start ):
                self.log_file = open(self.filename, 'a')
                self.log_file.write("# (%.5f) - " % (msg_time) + self.message_list[msg_time] + "\n")
                self.log_file.close()

        if( self.t_end + self.epsilon >= self.next_log ):
            self.log_file = open(self.filename, 'a')
            self.log_file.write("%.5f" % (self.next_log) + self.delimiter)
            for n in range(len(self.term_list)):
                termination = self.getTermination(self.term_list[n])
                term_name = termination.getName()
                term_output = array(termination._filtered_values)

                if( term_name in self.vocab_list.keys() ):
                    # Termination is a vocabTermination
                    vocab = self.vocabs[term_name]
                    vocab_list = self.vocab_list[term_name]               
                    vocab_list_len = len(vocab_list)
                    vocab_threshold = self.vocab_threshold[term_name]
                    vocab_list_range = range(vocab_list_len)
                    dot_prods = [dot(vocab.hrr[vocab_list[n]].v, term_output, False) for n in vocab_list_range]
                    max_dot = max(dot_prods)
                    if( max_dot >= vocab_threshold ):
                        dot_prods = [dot_prods[n] * (dot_prods[n] > vocab_threshold) for n in vocab_list_range]
                    else:
                        dot_prods = [dot_prods[n] * (dot_prods[n] == max_dot) for n in vocab_list_range]
                    dot_prods_out = []
                    vocab_list_out = []
                    for n in range(vocab_list_len):
                        if( dot_prods[n] > 0 ):
                            dot_prods_out.append(dot_prods[n])
                            vocab_list_out.append(vocab_list[n])
                    str_output = "+"
                    str_output = str_output.join(["%0.2f%s" % (dot_prods_out[n], vocab_list_out[n]) for n in range(len(dot_prods_out))])
                    if( len(str_output) == 0 ):
                        str_output = "0"
                else:
                    # Termination is a valueTermination
                    term_dim = termination.getDimensions()

                    def min_val(input):
                        if( abs(input) < self.epsilon ):
                            return 0
                        else:
                            return input

                    term_output = [min_val(term_output[d]) for d in range(term_dim)]
                    str_output = str(term_output)
                    str_output = str_output.replace(self.delimiter, self.delim_replace)            
                self.log_file.write(str(str_output) + self.delimiter)
            self.log_file.write("\n")
            self.log_file.close()
            self.next_log += self.log_interval

#### EXAMPLE USAGE ####
#def test_w2fnode():
#    num_dim = 256
#
#    network = nef.Network("Test W2FNode")
#
#    file_path = "FILEPATH HERE"
#    func_in = FunctionInput("Func In", [ConstantFunction(1,0)], Units.UNK)
#    network.add(func_in)
#
#    vocab = hrr.Vocabulary(num_dim, max_similarity = 0.05, include_pairs = False)
#    vocab_strs = ["ZER", "ONE", "TWO", "THR", "FOR", "FIV", "SIX", "SEV", "EIG", "NIN"]
#    for item in vocab_strs:
#        vocab.parse(item)
#    
#    class ControlInput(nef.SimpleNode):
#        def __init__(self, name, num_dim, vocab = None, output_str = None):
#            self.dimension = num_dim
#            self.vocab = vocab
#            self.output_str = output_str
#            nef.SimpleNode.__init__(self, name)
#        
#        def origin_X(self):
#            if( self.vocab is None or self.output_str is None ):
#                return util_funcs.zeros(1, self.dimension)
#            else:
#                return self.vocab.hrr[self.output_str].v
#
#    vocab_in = ControlInput("Func In", num_dim, vocab, "ZER")
#    network.add(vocab_in)
#    
#    # Let the w2fnode automatically connect stuff
#    w2f = w2fnode.WriteToFileNode("W2F AutoAdd+Connect", file_path + "test.csv", network, overwrite = True)
#    w2f.addVocabTermination("Func In", "origin", vocab = vocab, vocab_strs = vocab_strs)
#    w2f.addValueTermination("Vocab In", "X")
#
#    # Do the stuff yourself
#    w2f2 = w2fnode.WriteToFileNode("W2F ManualStuff", file_path + "test2.csv", overwrite = True)
#    w2f2.addVocabTermination("Func In", func_in.getOrigin("origin"), vocab = vocab, vocab_strs = vocab_strs)
#    w2f2.addValueTermination("Vocab In", vocab_in.getOrigin("X"))
#    network.add(w2f2)
#    network.connect(func_in.getOrigin("origin"), w2f2.getTermination("Func In"))
#    network.connect(vocab_in.getOrigin("X"), w2f2.getTermination("Vocab In"))
#
#    # Add messages
#    w2f.addMessages(["Message @ 0.5s","Message @ 1.0s"], [0.5,1])
#    
#    network.add_to_nengo()