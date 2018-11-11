package no.ntnu.idi.ir;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;

/*
 * The task is to update the given `MyDocument' class (i.e implement the 'Document(File F)' method) to index the following fields per document:
 *
 * - path, the path of the file
 * - from, whatever is stored in the from field of the given message
 * - subject, the subject of the e-mail
 * - contents, the actual e-mail contents
 *
 * All `from', `subject', and `contents' should be searchable, i.e. store their re-spective term vectors. 
 * Look at the given `NewsDocument' class that reads a text file and has better methods for `from', `subject', and `contents'.
 *
 * =============
 *
 * Lucene official documentation: 
 * http://lucene.apache.org/core/4_10_1/index.html
 *
 * Useful resources: 
 *   http://oak.cs.ucla.edu/cs144/projects/lucene/
 *   http://lingpipe-blog.com/2014/03/08/lucene-4-essentials-for-text-search-and-indexing/
 * 
 */


public class MyDocument{

	public static Document Document (File f) throws java.io.FileNotFoundException{

		// make a new, empty document
		Document doc = new Document();

		// use the news document wrapper
		NewsDocument newsDocument = new NewsDocument(f);

		//TODO: create structured lucene document

		//Adding all the fields that should be indexable.
		doc.add(new StringField("id", newsDocument.getId(), Field.Store.YES));
		doc.add(new TextField("from", newsDocument.getFrom(), Field.Store.YES));
		doc.add(new TextField("subject", newsDocument.getSubject(), Field.Store.YES));
		doc.add(new TextField("content", newsDocument.getContent(), Field.Store.YES));

		return doc;
	}



}
