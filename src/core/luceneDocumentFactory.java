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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.*;


import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class luceneDocumentFactory {
    private static StandardAnalyzer analyzer;
    private static String indexPath = "D:\\documents\\intellijProjects\\";
    private static IndexWriterConfig indexWriterConfig;
    private static Directory indexDirectory;
    private static IndexWriter indexWriter;

// ftiaxnw ena index apo ena database apo polhs me  stiatoria ara kathe stiatorio tha einai document

    private static void addFileToIndex(String filepath) throws java.io.IOException{
        Path path = Paths.get(filepath);
        File file = path.toFile();
        analyzer = new StandardAnalyzer();


        indexWriterConfig = new IndexWriterConfig(analyzer);
        indexDirectory = FSDirectory.open(Paths.get(indexPath));
        indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
        Document document = new Document();

        FileReader fileReader = new FileReader(file);
        //me to .add vazw fields sto document
        document.add(
                new TextField("contents", fileReader));
        document.add(
                new StringField("path", file.getPath(), Field.Store.YES));
        document.add(
                new StringField("filename", file.getName(), Field.Store.YES));
        document.add(
                new StringField("tips_text", file.getName(), Field.Store.YES));
        document.add(
                new StringField("reviews_text", file.getName(), Field.Store.YES));
        document.add(
                new StringField("place_id", file.getName(), Field.Store.YES));



        document.add(
                new StringField("name", file.getName(), Field.Store.YES));
        document.add(
                new StringField("stars", file.getPath(), Field.Store.YES));
        document.add(
                new StringField("categories", file.getName(), Field.Store.YES));
        document.add(
                new StringField("review_stars", file.getName(), Field.Store.YES));
        document.add(
                new StringField("review_count", file.getName(), Field.Store.YES));
        document.add(
                new StringField("review_text", file.getName(), Field.Store.YES));
        document.add(
                new StringField("tip_text", file.getName(), Field.Store.YES));


        //Adds a document to this index, aka indexWriter.
        indexWriter.addDocument(document);
        indexWriter.close();
    }
//TODO prepei na kanw enan tokenizer gia na diavazw ta arxeia me ta esteiatoria kai na ftiaxnw 3exoristes listes gia ta reviews tips kai oti allo xreiastei
//TODO na diavazei polla arxeia kai na epistrefei ta documents pou periexoun auth t le3h
    public static List<Document> search(String inField, String queryString) throws java.io.IOException, org.apache.lucene.queryparser.classic.ParseException
    {
        Query query = new QueryParser(inField, analyzer).parse(queryString);
        Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 10);

        return Arrays.stream(topDocs.scoreDocs).map(scoreDoc -> {
            try {
                return searcher.doc(scoreDoc.doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }


    public static void test() throws java.io.IOException, org.apache.lucene.queryparser.classic.ParseException{

        String dataPath = "D:\\documents\\intellijProjects\\yelpProcessed\\details_El Palenque.txt";
        String dataPath1 = "D:\\documents\\intellijProjects\\yelpProcessed\\details_Tequila Jaxx.txt";
        addFileToIndex(dataPath);
        addFileToIndex(dataPath1);
        List<Document> docs = search("contents", "pizza");
        List<Document> docs1 = search("contents", "bar");
        System.out.println(docs);
        System.out.println(docs1);
    }

    public static void main(String[] args) throws java.io.IOException, org.apache.lucene.queryparser.classic.ParseException{
        test();
    }

}
