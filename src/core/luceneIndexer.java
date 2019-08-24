package core;

/*Basic Concepts
Lucene is a full-text search library in Java which makes it easy to add search functionality to an application or website.

It does so by adding content to a full-text index. It then allows you to perform queries on this index, returning results
ranked by either the relevance to the query or sorted by an arbitrary field such as a document's last modified date.

The content you add to Lucene can be from various sources, like a SQL/NoSQL database, a filesystem, or even from websites.

Searching and Indexing
Lucene is able to achieve fast search responses because, instead of searching the text directly, it searches an index
instead. This would be the equivalent of retrieving pages in a book related to a keyword by searching the index at the
back of a book, as opposed to searching the words in each page of the book.

This type of index is called an inverted index, because it inverts a page-centric data structure (page->words) to a
keyword-centric data structure (word->pages).

Documents
In Lucene, a Document is the unit of search and index.

An index consists of one or more Documents.

Indexing involves adding Documents to an IndexWriter, and searching involves retrieving Documents from an index via an
IndexSearcher.

A Lucene Document doesn't necessarily have to be a document in the common English usage of the word. For example, if
you're creating a Lucene index of a database table of users, then each user would be represented in the index as a
Lucene Document.

Fields
A Document consists of one or more Fields. A Field is simply a name-value pair. For example, a Field commonly found in
applications is title. In the case of a title Field, the field name is title and the value is the title of that content
item.

Indexing in Lucene thus involves creating Documents comprising of one or more Fields, and adding these Documents to an
IndexWriter.

Searching
Searching requires an index to have already been built. It involves creating a Query (usually via a QueryParser) and
handing this Query to an IndexSearcher, which returns a list of Hits.

Queries
Lucene has its own mini-language for performing searches. Read more about the Lucene Query Syntax

The Lucene query language allows the user to specify which field(s) to search on, which fields to give more weight to
(boosting), the ability to perform boolean queries (AND, OR, NOT) and other functionality.

*/

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.nio.file.Paths;

import java.util.ArrayList;

public class luceneIndexer {
    private StandardAnalyzer analyzer;
    private String indexPath = "D:/documents/intellijProjects/luceneResults";
    private IndexWriterConfig indexWriterConfig;
    private Directory indexDirectory;
    private IndexWriter indexWriter;

// ftiaxnw ena index apo ena database apo polhs me  stiatoria ara kathe estiatorio tha einai document
    public void initialize() throws IOException {
        analyzer = new StandardAnalyzer();
        indexWriterConfig = new IndexWriterConfig(analyzer);
        indexDirectory = FSDirectory.open(Paths.get(indexPath)); //file system directory takes a path as an argument
        indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
    }

    public void addFileToIndex(String resultSet1, ArrayList<String> resultSet2, ArrayList<String> resultSet3)
            throws java.io.IOException, NullPointerException {


        Document document = new Document();
        String[] businessToSplit;
        String[] reviewsToSplit;
        String[] tipsToSplit;
//komple

        businessToSplit = resultSet1.split("\t");
        System.out.println("FOR ID:  " + businessToSplit[0]);
        document.add(
                new TextField("name", businessToSplit[1], Field.Store.YES));//use textfield for content i want to be tokenized
        document.add(
                new StringField("stars", businessToSplit[2], Field.Store.YES)); //use StringField for content i dont want to be tokenized
        document.add(
                new TextField("categories", businessToSplit[3], Field.Store.YES));
        document.add(
                new StringField("review_count", businessToSplit[4], Field.Store.YES));
        //System.out.println("resultSet1  " + businessToSplit[0] + businessToSplit[1] + businessToSplit[2] + businessToSplit[3] + businessToSplit[4]);

        for(String  i : resultSet2) {
            reviewsToSplit = i.split("\t");
            document.add(
                    new StringField("review_stars", reviewsToSplit[0], Field.Store.YES));
            document.add(
                    new TextField("review_text", reviewsToSplit[1], Field.Store.YES));
            System.out.println("resultSet2  "+"\t"+ reviewsToSplit[0] +"\t"+ reviewsToSplit[1]);
        }

        for(String  i : resultSet3) {
            tipsToSplit = i.split("\t");
            document.add(
                    new StringField("date", tipsToSplit[0], Field.Store.YES));
            document.add(
                    new TextField("tip_text", tipsToSplit[1], Field.Store.YES));
            //System.out.println("resultSet3  " + tipsToSplit[0] + tipsToSplit[1] );
        }
        //Adds a document to this index, aka indexWriter.
        indexWriter.addDocument(document);

    }

    public void terminate() throws IOException {indexWriter.close();}

}
