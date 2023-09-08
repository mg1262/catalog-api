package com.spscommerce.interview.search;

import com.spscommerce.interview.dao.entity.ProductEntity;
import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import com.spscommerce.interview.model.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SubscriptionIndexer extends Indexer<SubscriptionEntity>{


    @Override
    public void addDocument(SubscriptionEntity entity) {
        try {
            IndexWriter writer = getIndexWriter();
            Term term = new Term("id", entity.getId());
            writer.deleteDocuments(term);
            Document newDocument = new Document();
            newDocument.add(new TextField("id", entity.getId(), Field.Store.YES));
            newDocument.add(new DoublePoint("discount", entity.getDiscount()));
            newDocument.add(new DoublePoint("totalPrice", entity.getTotalPrice()));
            if (entity.getProducts() != null) {
                entity.getProducts().forEach(productEntity -> {
                    newDocument.add(new TextField("product.id", productEntity.getId(), Field.Store.NO));
                    newDocument.add(new TextField("product.name", productEntity.getName(), Field.Store.NO));
                    newDocument.add(new TextField("product.description", productEntity.getDescription(), Field.Store.NO));
                    newDocument.add(new DoublePoint("product.price", productEntity.getPrice()));
                    if (productEntity.getSubProducts() != null) {
                        productEntity.getSubProducts().forEach(subProduct -> addSubProducts(newDocument, subProduct));
                    }
                });
            }
            writer.addDocument(newDocument);
            writer.commit();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addSubProducts(Document document, ProductEntity productEntity) {
        document.add(new TextField("product.sub_product.id", productEntity.getId(), Field.Store.NO));
        document.add(new TextField("product.sub_product.name", productEntity.getName(), Field.Store.NO));
        document.add(new TextField("product.sub_product.description", productEntity.getDescription(), Field.Store.NO));
        document.add(new DoublePoint("product.sub_product.price", productEntity.getPrice()));
        if (productEntity.getSubProducts() != null) {
            productEntity.getSubProducts().forEach(subProduct -> addSubProducts(document, subProduct));
        }
    }

    @Override
    public void removeDocument(SubscriptionEntity entity) {

    }

    @Override
    public EntityType supports() {
        return EntityType.SUBSCRIPTION;
    }
}
