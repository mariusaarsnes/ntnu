from random import seed
import codecs
import string
import re
from gensim.corpora import Dictionary
from nltk.stem.porter import PorterStemmer

# Set random seed 
seed(123)

def generate_documents(file_path):
    stemmer = PorterStemmer()
    
    with codecs.open(file_path,"r","utf-8") as f:
        raw_data = f.read()
        
    raw_documents = [elem.lower() for elem in raw_data.split("\r\n\r\n") 
                    if len(elem) >0 and "Gutenberg".lower() not in elem.lower()
                    ] # split the raw data on double line-breaks, remove parts containing 'Gutenberg' and  make everything lowercase
    documents = [
        [stemmer.stem("".join(c for c in word if c not in string.punctuation)) # Remove punctuation from each word in a document
            for word in documents.replace("\r\n", " ").strip().split() # replace linebreaks with spaces and remove leading and trailing whitespaces before splitting into single words 
        ] 
        for documents in raw_documents]
    
    return raw_documents, documents


def build_dictionary(documents):
    dictionary = Dictionary(documents)



def main():
    raw_documents, documents = generate_documents("pg3300-txt")

    return 




if __name__=="__main__":
    main()