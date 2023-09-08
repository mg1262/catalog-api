package com.spscommerce.interview.search;

import com.spscommerce.interview.error.ErrorCodes;
import com.spscommerce.interview.model.CatalogEntity;
import com.spscommerce.interview.model.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Slf4j
public abstract class Indexer<T extends CatalogEntity> {

    protected Directory index;
    protected StandardAnalyzer analyzer;
    protected IndexWriterConfig indexWriterConfig;

    protected IndexWriter indexWriter;

    public Indexer() {
        this.index = new RAMDirectory();
        this.analyzer = new StandardAnalyzer();
        this.indexWriterConfig = new IndexWriterConfig(analyzer);

    }

    @PostConstruct
    public void init() throws IOException {
        IndexWriter writer = getIndexWriter();
        writer.commit();
    }

    public Set<String> search(String queryString) {
        IndexReader indexReader = null;
        IndexSearcher indexSearcher = null;
        Set<String> results = new HashSet<>();
        try {
            indexReader = DirectoryReader.open(index);
            indexSearcher = new IndexSearcher(indexReader);
            QueryParser parser = new QueryParser(null, analyzer);

            Query query = parser.parse(queryString);
            TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = indexSearcher.doc(scoreDoc.doc);
                results.add(document.get("id"));
            }
            return results;
        } catch (ParseException e) {
            ErrorCodes.SEARCH_QUERY_ERROR.throwException(e);
        } catch (IOException e) {
            ErrorCodes.SEARCH_IO_ERROR.throwException(e);
        } finally {
            try {
                indexReader.close();
            } catch (IOException e) {
                log.error("Error while closing index reader", e);
            }
        }
        return results;
    }

    public IndexWriter getIndexWriter() throws IOException {
        if (this.indexWriter == null || !this.indexWriter.isOpen()) {
            this.indexWriter = new IndexWriter(this.index, this.indexWriterConfig);

        }
        return indexWriter;
    }

    public abstract void addDocument(T entity);

    public abstract void removeDocument(T entity);

    public abstract EntityType supports();


}
