package com.spscommerce.interview.service;

import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.dao.entity.ProductEntity;
import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import com.spscommerce.interview.dao.repo.ProductRepository;
import com.spscommerce.interview.error.ErrorCodes;
import com.spscommerce.interview.model.EntityType;
import com.spscommerce.interview.model.Warning;
import com.spscommerce.interview.model.product.CreateProductsResult;
import com.spscommerce.interview.model.product.CreateUpdateProductResult;
import com.spscommerce.interview.rules.PersistenceRules;
import com.spscommerce.interview.rules.RuleValue;
import com.spscommerce.interview.search.SearchResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService extends BaseService {

    private final ProductRepository productRepository;
    private final PersistenceRules persistenceRules;
    private final SearchService searchService;

    public ProductService(SearchService searchService, ProductRepository productRepository, PersistenceRules persistenceRules) {
        super("PRD");
        this.productRepository = productRepository;
        this.persistenceRules = persistenceRules;
        this.searchService = searchService;
    }


    public CreateUpdateProductResult createProduct(ProductEntity newProduct) {
        CreateProductsResult createProductsResult = createProducts(new HashSet<>(newProduct.getSubProducts()));
        newProduct.setSubProducts(createProductsResult.getResult());
        ProductEntity productEntity = this.productRepository.save(newProduct);
        this.searchService.addDocument(productEntity);
        return new CreateUpdateProductResult(productEntity, createProductsResult.getWarnings());
    }

    public CreateProductsResult createProducts(Set<ProductEntity> productEntitySet) {
        Map<String, ProductEntity> savedEntitiesMap = new HashMap<>();
        List<Warning> warnings = new ArrayList<>();
        if (persistenceRules.getRule("CREATE_SUB_PRODUCTS_ON_EXISTING_PRODUCTS").orElse(persistenceRules.getDefaultRuleValue()) == RuleValue.ALLOW) {
            Queue<ProductEntity> productEntityQueue = new LinkedList<>();
            addProducts(productEntityQueue, productEntitySet);
            List<ProductEntity> productEntitiesWithIds = productEntityQueue.stream().filter(product -> product.getId() != null).collect(Collectors.toList());
            List<String> existingProducts = productRepository.findExistingIds(productEntitiesWithIds.stream().map(ProductEntity::getId).collect(Collectors.toSet()));
            if (productEntitiesWithIds.stream().anyMatch(product -> !existingProducts.contains(product.getId()))) {
                ErrorCodes.SUB_PRD_NOT_FOUND.throwException();
            }
            productEntityQueue.forEach(productEntity -> {
                if (productEntity.getId() == null) {
                    productEntity.setId(generateId());
                    savedEntitiesMap.put(productEntity.getId(), productRepository.save(productEntity));
                } else {
                    ProductEntity existingProduct = productRepository.findById(productEntity.getId()).get();
                    existingProduct.setSubProducts(productEntity.getSubProducts());
                    savedEntitiesMap.put(productEntity.getId(), productRepository.save(existingProduct));
                    warnings.add(Warning.builder().id(existingProduct.getId()).created(false).duplicate(true).entityType(EntityType.PRODUCT).message("Product already exists.").build());
                }
            });
        } else {
            Queue<ProductEntity> productEntityQueue = new LinkedList<>();
            productEntitySet.forEach(product -> {
                if (product.getId() != null && product.getSubProducts() != null) {
                    List<ProductEntity> subProductsToRemove = product.getSubProducts().stream().filter(subProduct -> subProduct.getId() == null).toList();
                    product.getSubProducts().removeAll(subProductsToRemove);
                    subProductsToRemove.forEach(subProduct -> warnings.add(Warning.builder().id(null).entityType(EntityType.PRODUCT).created(false).duplicate(false).message("Product Name: " + subProduct.getName() + " Creating Sub Product of Existing Product Not Allowed.").build()));
                }
            });
            addProducts(productEntityQueue, productEntitySet);
            List<ProductEntity> productEntitiesWithIds = productEntityQueue.stream().filter(product -> product.getId() != null).collect(Collectors.toList());
            List<String> existingProducts = productRepository.findExistingIds(productEntitiesWithIds.stream().map(ProductEntity::getId).collect(Collectors.toSet()));
            if (productEntitiesWithIds.stream().anyMatch(product -> !existingProducts.contains(product.getId()))) {
                ErrorCodes.SUB_PRD_NOT_FOUND.throwException();
            }
            productEntityQueue.forEach(productEntity -> {
                if (productEntity.getId() == null) {
                    productEntity.setId(generateId());
                    savedEntitiesMap.put(productEntity.getId(), productRepository.save(productEntity));
                } else {
                    ProductEntity existingProduct = productRepository.findById(productEntity.getId()).get();
                    existingProduct.setSubProducts(productEntity.getSubProducts());
                    savedEntitiesMap.put(productEntity.getId(), productRepository.save(existingProduct));
                    warnings.add(Warning.builder().id(existingProduct.getId()).created(false).duplicate(true).entityType(EntityType.PRODUCT).message("Product already exists.").build());
                }
            });
        }
        List<ProductEntity> results = new ArrayList<>();
        productEntitySet.forEach(product -> results.add(savedEntitiesMap.get(product.getId())));
        return new CreateProductsResult(results, warnings);

    }

    public ProductEntity readProduct(String id) {
        ProductEntity productEntity = this.productRepository.findById(id).orElse(null);
        if (productEntity == null) {
            ErrorCodes.PRD_READ_NOT_FOUND.throwException();
        }
        return productEntity;
    }

    public CreateUpdateProductResult updateProduct(ProductEntity product) {
        //Add Validation
        validateUpdate(product);
        CreateProductsResult createProductsResult = createProducts(new HashSet<>(product.getSubProducts()));
        product.setSubProducts(createProductsResult.getResult());
        ProductEntity productEntity = this.productRepository.save(product);
        this.searchService.updateDocument(productEntity);
        return new CreateUpdateProductResult(productEntity, createProductsResult.getWarnings());
    }

    public void deleteProduct(String id) {
        validateDelete(id);
        this.productRepository.deleteById(id);
    }


    public SearchResult<ProductEntity> search(String query, int page, int limit, String sortColumn, boolean sortAsc) {
        if (query != null && !query.trim().isEmpty()) {
            Set<String> ids = this.searchService.search(supports(), query);
            SearchResult<ProductEntity> searchResult = new SearchResult<>();
            searchResult.setTotalResults(ids.size());
            searchResult.setPage(page);
            searchResult.setLimit(limit);
            if (!ids.isEmpty()) {
                searchResult.setTotalPages(Double.valueOf(Math.ceil(ids.size() / (limit * 1d))).intValue());
                Pageable pageable = sortAsc ? PageRequest.of(page, limit, Sort.by(sortColumn).ascending()) :
                        PageRequest.of(page, limit, Sort.by(sortColumn).descending());
                List<ProductEntity> entities = productRepository.findAllByIdIn(ids, pageable);
                searchResult.setResults(entities);
            }
            return searchResult;
        } else {
            long count = productRepository.count();
            SearchResult<ProductEntity> searchResult = new SearchResult<>();
            searchResult.setTotalResults(Long.valueOf(count).intValue());
            searchResult.setPage(page);
            searchResult.setLimit(limit);
            if (count > 0) {
                searchResult.setTotalPages(Double.valueOf(Math.ceil(count / (limit * 1d))).intValue());
                Pageable pageable = sortAsc ? PageRequest.of(page, limit, Sort.by(sortColumn).ascending()) :
                        PageRequest.of(page, limit, Sort.by(sortColumn).descending());
                List<ProductEntity> entities = productRepository.findAll(pageable);
                searchResult.setResults(entities);
            }
            return searchResult;
        }
    }
    @Override
    protected EntityType supports() {
        return EntityType.PRODUCT;
    }


    private void validateUpdate(ProductEntity product) {
        if (!productRepository.existsById(product.getId())) {
            ErrorCodes.PRD_UPDATE_NOT_FOUND.throwException();
        }
    }

    private void validateDelete(String id) {
        if (!productRepository.existsById(id)) {
            ErrorCodes.PRD_DELETE_NOT_FOUND.throwException();
        }
    }

    private void addProducts(Queue<ProductEntity> queue, Set<ProductEntity> productEntities) {
        productEntities.forEach(productEntity -> {
            if (productEntity.getSubProducts() != null && !productEntity.getSubProducts().isEmpty()) {
                productEntity.getSubProducts().forEach(subProduct -> addProducts(queue, subProduct));
            }
        });
        queue.addAll(productEntities);
    }

    private void addProducts(Queue<ProductEntity> queue, ProductEntity productEntity) {
        if (productEntity.getSubProducts() != null && !productEntity.getSubProducts().isEmpty()) {
            productEntity.getSubProducts().forEach(subProduct -> addProducts(queue, subProduct));
        }
        queue.add(productEntity);
    }


}
