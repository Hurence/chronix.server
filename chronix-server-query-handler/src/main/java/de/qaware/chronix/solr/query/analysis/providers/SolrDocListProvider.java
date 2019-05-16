/*
 * Copyright (C) 2016 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.qaware.chronix.solr.query.analysis.providers;

import de.qaware.chronix.solr.query.analysis.DocListProvider;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.util.SolrPluginUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Solr DocList provider implementation.
 *
 * @author f.lautenschlager
 */
public class SolrDocListProvider implements DocListProvider {
    /**
     * Calls apache solr to answer the given user request.
     *
     * @param q     the user query
     * @param req   the solr query request object
     * @param start start of the query
     * @param limit the document limit
     * @return the result of the query
     * @throws IOException if bad things happen
     */
    @Override
    public DocList doSimpleQuery(String q, SolrQueryRequest req, int start, int limit) throws IOException {
        return SolrPluginUtils.doSimpleQuery(q, req, start, limit);
    }


    /**
     * Convert a DocList to a SolrDocumentList
     *
     * The optional param "ids" is populated with the lucene document id
     * for each SolrDocument.
     *
     * @param docs The {@link org.apache.solr.search.DocList} to convert
     * @param searcher The {@link org.apache.solr.search.SolrIndexSearcher} to use to load the docs from the Lucene index
     * @param fields The names of the Fields to load
     * @param ids A map to store the ids of the docs
     * @return The new {@link org.apache.solr.common.SolrDocumentList} containing all the loaded docs
     * @throws java.io.IOException if there was a problem loading the docs
     * @since solr 1.4
     * @deprecated TODO in 7.0 remove this. It was inlined into ClusteringComponent. DWS: 'ids' is ugly.
     */
    @Override
    public SolrDocumentList docListToSolrDocumentList(
            DocList docs,
            SolrIndexSearcher searcher,
            Set<String> fields,
            Map<SolrDocument, Integer> ids ) throws IOException
    {
    /*  DWS deprecation note:
     It's only called by ClusteringComponent, and I think the "ids" param aspect is a bit messy and not worth supporting.
     If someone wants a similar method they can speak up and we can add a method to SolrDocumentFetcher.
     */
        IndexSchema schema = searcher.getSchema();

        SolrDocumentList list = new SolrDocumentList();
        list.setNumFound(docs.matches());
        list.setMaxScore(docs.maxScore());
        list.setStart(docs.offset());

        DocIterator dit = docs.iterator();

        while (dit.hasNext()) {
            int docid = dit.nextDoc();

            Document luceneDoc = searcher.doc(docid, fields);
            SolrDocument doc = new SolrDocument();

            for( IndexableField field : luceneDoc) {
                if (null == fields || fields.contains(field.name())) {
                    SchemaField sf = schema.getField( field.name() );
                    doc.addField( field.name(), sf.getType().toObject( field ) );
                }
            }
            if (docs.hasScores() && (null == fields || fields.contains("score"))) {
                doc.addField("score", dit.score());
            }

            list.add( doc );

            if( ids != null ) {
                ids.put( doc, new Integer(docid) );
            }
        }
        return list;
    }
}
