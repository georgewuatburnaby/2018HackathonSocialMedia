import falcon
import json
from keras.preprocessing.sequence import pad_sequences
from keras.models import Sequential,Model
from keras.layers import Input, Dense, Embedding, Conv2D, MaxPool2D, Reshape, Flatten, Dropout, Concatenate
from keras.optimizers import Adam
import nltk
import numpy as np
import re

class NumpyEncoder(json.JSONEncoder):
    """ Special json encoder for numpy types """
    def default(self, obj):
        if isinstance(obj, (np.int_, np.intc, np.intp, np.int8,
            np.int16, np.int32, np.int64, np.uint8,
            np.uint16, np.uint32, np.uint64)):
            return int(obj)
        elif isinstance(obj, (np.float_, np.float16, np.float32, 
            np.float64)):
            return float(obj)
        elif isinstance(obj,(np.ndarray,)): #### This is the fix
            return obj.tolist()
        return json.JSONEncoder.default(self, obj)
class AICore(object):
    def __init__(self):
        SHAPE=50    
        index_dict = dict()
        embedding_matrix = []
        f = open('glove.6B.50d.txt')
        for idx,line in enumerate(f):
            values = line.split()
            word = values[0]
            coefs = np.asarray(values[1:], dtype='float32')
            index_dict[word] = idx
            embedding_matrix.append(coefs)
        f.close()
        VERB_SIZE=len(index_dict)
        MAX_LEN=60
        sequence_length = MAX_LEN
        vocabulary_size = len(index_dict)
        embedding_dim = SHAPE
        filter_sizes = [3,4,5]
        num_filters = 256
        drop = 0.1
        # this returns a tensor
        print("Creating Model...")
        inputs = Input(shape=(sequence_length,), dtype='int32')
        embedding = Embedding(input_dim=vocabulary_size, output_dim=embedding_dim, input_length=sequence_length)(inputs)
        reshape = Reshape((sequence_length,embedding_dim,1))(embedding)
        conv_0 = Conv2D(num_filters, kernel_size=(filter_sizes[0], embedding_dim), padding='valid', kernel_initializer='normal', activation='relu')(reshape)
        conv_1 = Conv2D(num_filters, kernel_size=(filter_sizes[1], embedding_dim), padding='valid', kernel_initializer='normal', activation='relu')(reshape)
        conv_2 = Conv2D(num_filters, kernel_size=(filter_sizes[2], embedding_dim), padding='valid', kernel_initializer='normal', activation='relu')(reshape)

        maxpool_0 = MaxPool2D(pool_size=(sequence_length - filter_sizes[0] + 1, 1), strides=(1,1), padding='valid')(conv_0)
        maxpool_1 = MaxPool2D(pool_size=(sequence_length - filter_sizes[1] + 1, 1), strides=(1,1), padding='valid')(conv_1)
        maxpool_2 = MaxPool2D(pool_size=(sequence_length - filter_sizes[2] + 1, 1), strides=(1,1), padding='valid')(conv_2)

        concatenated_tensor = Concatenate(axis=1)([maxpool_0, maxpool_1, maxpool_2])
        flatten = Flatten()(concatenated_tensor)
        dropout = Dropout(drop)(flatten)
        output = Dense(1, activation='sigmoid')(dropout)

        # this creates a model that includes
        model = Model(inputs=inputs, outputs=output)
        adam = Adam(lr=1e-4)
        model.compile(optimizer=adam, loss='binary_crossentropy', metrics=['accuracy'])
        FILE_SAVE_NAME='cnn_weights.h5'
        model.load_weights(FILE_SAVE_NAME)
        self.MAX_LEN = MAX_LEN
        self.model = model
        self.index_dict = index_dict
        print('Ready')
        
    def predict(self,text):
        evl = nltk.word_tokenize(self.clean_str(text.strip().lower()))
        evl = self.toModelData([evl],self.MAX_LEN)
        return self.model.predict(evl)[0][0]\
    
    def predict_list(self, array):        
        arr = []
        for l in array:
            arr.append(nltk.word_tokenize(self.clean_str(l.strip().lower())))
        evl = self.toModelData(arr,self.MAX_LEN)
        rst = self.model.predict(evl)
        return rst.flatten()
        
    def toModelData(self,record,length):
        encoded_docs = []
        for sidx, sentence in enumerate(record):
            row = []
            for idx, token in enumerate(sentence):
                if len(row) > length:
                    break
                position = self.index_dict.get(token)
                if position is not None:
                    row.append(position)
            encoded_docs.append(row)
        return pad_sequences(encoded_docs, maxlen=length, padding='post')        
       
    def clean_str(self,string):
        """
        Tokenization/string cleaning for datasets.
        Original taken from https://github.com/yoonkim/CNN_sentence/blob/master/process_data.py
        """
        string = re.sub(r"[^A-Za-z0-9(),!?\'\`]", " ", string)
        string = re.sub(r"\'s", " \'s", string)
        string = re.sub(r"\'ve", " \'ve", string)
        string = re.sub(r"n\'t", " n\'t", string)
        string = re.sub(r"\'re", " \'re", string)
        string = re.sub(r"\'d", " \'d", string)
        string = re.sub(r"\'ll", " \'ll", string)
        string = re.sub(r",", " , ", string)
        string = re.sub(r"!", " ! ", string)
        string = re.sub(r"\(", " \( ", string)
        string = re.sub(r"\)", " \) ", string)
        string = re.sub(r"\?", " \? ", string)
        string = re.sub(r"\s{2,}", " ", string)
        return string.strip().lower()
class SingleResource(object):
    def __init__(self, core):
        self.core = core
    
    def on_post(self, req, resp):
        """Handles GET requests"""
        resp.status = falcon.HTTP_200  # This is the default status
        text = req.stream.read().decode('utf-8') #req.get_param() #self.predict()
        result = {'rating': self.core.predict(text)}
        resp.body = json.dumps(result, cls=NumpyEncoder)
        
class MultiResource(SingleResource):
    def on_post(self, req, resp):
        """Handles GET requests"""
        resp.status = falcon.HTTP_200  # This is the default status
        text = req.stream.read().decode('utf-8') #req.get_param() #self.predict()
        result = []
        for i in self.core.predict_list(json.loads(text)):
            result.append({'rating': i})
        resp.body = json.dumps(result, cls=NumpyEncoder)
        
# # falcon.API instances are callable WSGI apps
app = falcon.API()

core = AICore()
# Resources are represented by long-lived class instances
single_r = SingleResource(core)
multi_r = MultiResource(core)

# things will handle all requests to the '/things' URL path
app.add_route('/hack', single_r)
app.add_route('/hack_list', multi_r)
# h = HackathonResource()
# h.predict('good job')
    
