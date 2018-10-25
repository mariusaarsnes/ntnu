from random import seed
import codecs
import string
import re
from gensim.corpora import Dictionary
from gensim.models import TfidfModel, LsiModel
from gensim.similarities import MatrixSimilarity
from nltk.stem.porter import PorterStemmer
from copy import copy

# Task 1.0
seed(123)

# Task 1.1
def read_file(file_path,whole=True):
    """
    Read a file given a file path. if whole is true we read the whole document. if not we read a single line
    """
    with codecs.open(file_path,"r","utf-8") as f:
        return f.read() if whole else f.readline()

# Task 1.2
def split_into_paragraphs(raw_data):
    """
    takes raw_data (text) and parses it into an array containing paragraphs
    split on double line breaks (\r\n\r\n)
    """
    return [paragraph for paragraph in raw_data.split("\r\n\r\n") if len(paragraph) > 0]

# Task 1.3
def remove_containing_word(paragraphs,word):
    """
    remove paragraphs which contain a given word. i.e. "Gutenberg
    """
    return [paragraph for paragraph in paragraphs if not word.lower() in paragraph.lower()]

# Task 1.4
def tokenize(paragraphs):
    """
    split each paragraph into an array of words splitting into sperarate words
    """
    return [re.split('\W+', paragraph) for paragraph in paragraphs]

# Task 1.5
def remove_punctuation(paragraphs):
    """
    remove string.punctuation +"\n\r\t" from each paragraph in paragraphs
    """
    for i,paragraph in enumerate(paragraphs):
        stripped_paragraph = remove_punctuation_from_paragraph(paragraph)
        paragraphs[i] = stripped_paragraph
    return paragraphs


def remove_punctuation_from_paragraph(paragraph):
    """
    Remove string.punctuation + "\n\r\t" from a specific paragraph. Also change each word to lowercase
    """
    new_paragraph = []

    for word in paragraph:
        new_word = ""
        for c in word:
            if c not in string.punctuation+"\n\r\t":
                new_word += c
            
        if new_word == "":
            continue
        else:
            new_paragraph.append(new_word.lower())
            new_word == ""
    return new_paragraph

# Task 1.6
def stem_paragraphs(paragraphs):
    """
    stem each word in the paragraphs
    """
    stemmer = PorterStemmer()
    return [[stemmer.stem(word) for word in paragraph] for paragraph in paragraphs]

# Task 1
def generate_documents(file_path):
    raw_data = read_file(file_path)
    raw_paragraphs = split_into_paragraphs(raw_data)
    paragraphs = copy(raw_paragraphs)
    paragraphs = remove_containing_word(paragraphs,"Gutenberg")
    paragraphs = tokenize(paragraphs)
    paragraphs = remove_punctuation(paragraphs)
    paragraphs = stem_paragraphs(paragraphs)
    return raw_paragraphs, paragraphs


# Task 2.1
def generate_dictionary(paragraphs):
    return Dictionary(paragraphs)

# Task 2.1
def generate_stopwords(file_path):
    raw_stopwords = read_file(file_path)
    stopwords = raw_stopwords.split(",")
    return stopwords

# Task 2
def generate_bag_of_words(documents,stopwords):
    dictionary = Dictionary(documents)
    stopword_ids = []
    for stopword in stopwords:
        try:
            stopword_ids.append(dictionary.token2id[stopword])
        except:
            pass
    dictionary.filter_tokens(stopword_ids)
    return dictionary, [dictionary.doc2bow(doc) for doc in documents] # Task 2.2


# Task 3
def retrieve_models(bags,dictionary):
    # Task 3.1
    tfidf_model = TfidfModel(bags)

    # Task 3.2
    tfidf_corpus = tfidf_model[bags]

    # Task 3.3
    matrix_sim = MatrixSimilarity(tfidf_corpus)
    
    # Task 3.4 
    lsi_model = LsiModel(tfidf_corpus, id2word=dictionary, num_topics=100)

    lsi_corpus = lsi_model[bags]

    lsi_matrix = MatrixSimilarity(lsi_corpus)

    # Task 3.5
    print("\n\n-------------- Task 3.5 --------------")
    for i in range(3):
        print(lsi_model.show_topics(3)[i])
    print()
    return tfidf_model, tfidf_corpus, matrix_sim, lsi_model, lsi_corpus, lsi_matrix


def pre_process(string):
    stemmer = PorterStemmer()

    list_of_words = string.split()
    list_of_words = remove_punctuation_from_paragraph(list_of_words)
    return [stemmer.stem(word) for word in list_of_words]


def show_topics(topics, lsi_model):
    
    for topic in enumerate(topics):
        t = topic[1][0]
        print("\n[Topic " + t.__str__() + "]")
        print(lsi_model.show_topics()[t])
    print()

def show_docs(docs, paragraphs):
    for doc in docs:
        p = doc[0]
        print("\n[Paragraph " + p.__str__() + "]")
        first_five = paragraphs[p].split("\r\n")[:5]
        for elem in first_five:
            print(elem)
    print()


# Task 4
def query(dictionary, tfidf_model, tfidf_corpus, matrix_sim,lsi_model, lsi_matrix, paragraphs):

    # Task 4.1
    query = "What is the function of money?"
    query = pre_process(query)
    query = dictionary.doc2bow(query)


    # Task 4.2
    tfidf_index = tfidf_model[query]
    print("\n\n-------------- Task 4.2 --------------")
    print([dictionary[word_index]+": "+ str(val) for (word_index,val) in tfidf_index])
    
    # Task 4.3
    print("\n\n-------------- Task 4.3 --------------")
    docsim = enumerate(matrix_sim[tfidf_index])
    docs = sorted(docsim, key=lambda kv: -kv[1])[:3]
    show_docs(docs, paragraphs)

    # Task 4.4
    lsi_query = lsi_model[query]
    topics = sorted(lsi_query, key=lambda kv: -abs(kv[1]))[:3]
    show_topics(topics, lsi_model)
    docsim = enumerate(lsi_matrix[lsi_query])
    docs = sorted(docsim, key=lambda kv: -kv[1])[:3]
    show_docs(docs, paragraphs)

    

def main():
    #raw_documents, documents = generate_documents("pg3300.txt")
    #stopwords = generate_stopwords("common-english-words.txt")
    #build_dictionary(documents,stopwords)

    raw_paragraphs, paragraphs = generate_documents("pg3300.txt")
    stopwords= generate_stopwords("common-english-words.txt")
    dictionary, bags = generate_bag_of_words(paragraphs, stopwords)
    tfidf_model, tfidf_corpus, matrix_sim, lsi_model, lsi_corpus, lsi_matrix = retrieve_models(bags, dictionary)
    query(dictionary, tfidf_model,tfidf_corpus, matrix_sim,lsi_model,lsi_matrix,raw_paragraphs)




if __name__=="__main__":
    main()