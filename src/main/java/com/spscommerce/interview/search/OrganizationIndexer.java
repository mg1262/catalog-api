package com.spscommerce.interview.search;

import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.dao.entity.ProductEntity;
import com.spscommerce.interview.model.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OrganizationIndexer extends Indexer<OrganizationEntity> {


    @Override
    public void addDocument(OrganizationEntity entity) {
        try {
            IndexWriter writer = getIndexWriter();
            Term term = new Term("id", entity.getId());
            writer.deleteDocuments(term);
            Document newDocument = new Document();
            newDocument.add(new TextField("id", entity.getId(), Field.Store.YES));
            newDocument.add(new TextField("name", entity.getName(), Field.Store.NO));
            newDocument.add(new TextField("location.address", entity.getLocation().getAddress(), Field.Store.NO));
            newDocument.add(new TextField("location.city", entity.getLocation().getCity(), Field.Store.NO));
            newDocument.add(new TextField("location.state", entity.getLocation().getState(), Field.Store.NO));
            newDocument.add(new TextField("location.zipCode", entity.getLocation().getZipCode(), Field.Store.NO));
            if (entity.getSubscriptions() != null) {
                entity.getSubscriptions().forEach(subscriptionEntity -> {
                    newDocument.add(new TextField("subscription.id", subscriptionEntity.getId(), Field.Store.NO));
                    newDocument.add(new DoublePoint("subscription.discount", subscriptionEntity.getDiscount()));
                    newDocument.add(new DoublePoint("subscription.totalPrice", subscriptionEntity.getTotalPrice()));
                    if (subscriptionEntity.getProducts() != null) {
                        subscriptionEntity.getProducts().forEach(productEntity -> {
                            newDocument.add(new TextField("subscription.product.id", productEntity.getId(), Field.Store.NO));
                            newDocument.add(new TextField("subscription.product.name", productEntity.getName(), Field.Store.NO));
                            newDocument.add(new TextField("subscription.product.description", productEntity.getDescription(), Field.Store.NO));
                            newDocument.add(new DoublePoint("subscription.product.price", productEntity.getPrice()));
                            if (productEntity.getSubProducts() != null) {
                                productEntity.getSubProducts().forEach(subProduct -> addSubProducts(newDocument, subProduct));
                            }
                        });
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
        document.add(new TextField("subscription.product.sub_product.id", productEntity.getId(), Field.Store.NO));
        document.add(new TextField("subscription.product.sub_product.name", productEntity.getName(), Field.Store.NO));
        document.add(new TextField("subscription.product.sub_product.description", productEntity.getDescription(), Field.Store.NO));
        document.add(new DoublePoint("subscription.product.sub_product.price", productEntity.getPrice()));
        if (productEntity.getSubProducts() != null) {
            productEntity.getSubProducts().forEach(subProduct -> addSubProducts(document, subProduct));
        }
    }

    @Override
    public void removeDocument(OrganizationEntity entity) {
        try {
            Term term = new Term("id", entity.getId());
            IndexWriter writer = getIndexWriter();
            writer.deleteDocuments(term);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EntityType supports() {
        return EntityType.ORGANIZATION;
    }
}
