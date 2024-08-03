package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.PageResponse;
import com.wha.warehousemanagement.dtos.SearchCriteria;
import com.wha.warehousemanagement.dtos.SearchQueryCriteriaConsumer;
import com.wha.warehousemanagement.dtos.responses.InventoryResponse;
import com.wha.warehousemanagement.mappers.InventoryMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.models.Inventory;
import com.wha.warehousemanagement.models.Product;
import com.wha.warehousemanagement.models.Warehouse;
import com.wha.warehousemanagement.models.Zone;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchRepository {

    private final InventoryMapper inventoryMapper;
    private final ProductMapper productMapper;
    private static final String SEARCH_OPERATOR = "([\\w.]+?)([:])(.*)";
    private static final String SORT_BY = "([\\w.]+?)(:)(asc|desc)";
    @PersistenceContext
    private EntityManager entityManager;

    public PageResponse<?> searchInventories(int pageNo, int pageSize, String sortBy, int warehouseId, String... search) {
        log.info("Search user with search={} and sortBy={} and warehouseId={}", search, sortBy, warehouseId);

        List<SearchCriteria> criteriaList = new ArrayList<>();

        if (search.length > 0) {
            Pattern pattern = Pattern.compile(SEARCH_OPERATOR);
            for (String s : search) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    if (matcher.group(1).equals("categoryId") && matcher.group(3).equals("0")) {
                        criteriaList.add(new SearchCriteria(matcher.group(1), ":", null));
                    } else if (matcher.group(1).equals("zoneName") && matcher.group(3).isBlank()) {
                        criteriaList.add(new SearchCriteria(matcher.group(1), ":", null));
                    } else {
                        criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                    }
                }
            }
        }


        List<Inventory> inventoryList = getInventories(pageNo, pageSize, criteriaList, sortBy, warehouseId);

        Long totalElements = getTotalElements(criteriaList, warehouseId);

        List<InventoryResponse> inventoryResponseList = inventoryList.stream().map(inventory -> {
            InventoryResponse inventoryResponse = inventoryMapper.toDto(inventory);
            inventoryResponse.setProduct(productMapper.toDto(inventory.getProduct()));
            return inventoryResponse;
        }).collect(Collectors.toList());

        Page<InventoryResponse> page = new PageImpl<>(inventoryResponseList, PageRequest.of(pageNo, pageSize), totalElements);

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .total(page.getTotalPages())
                .items(inventoryResponseList)
                .build();
    }

    public List<Inventory> getInventories(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String sortBy, int warehouseId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Inventory> query = builder.createQuery(Inventory.class);
        Root<Inventory> root = query.from(Inventory.class);

        Predicate predicate = builder.conjunction();
        SearchQueryCriteriaConsumer searchQueryCriteriaConsumer = new SearchQueryCriteriaConsumer(predicate, builder, root);
        criteriaList.forEach(searchQueryCriteriaConsumer);
        predicate = searchQueryCriteriaConsumer.getPredicate();

        Join<Inventory, Zone> zoneJoin = root.join("zone");
        Join<Zone, Warehouse> warehouseJoin = zoneJoin.join("warehouse");

        if (StringUtils.hasLength(String.valueOf(warehouseId))) {
            Predicate warehousePredicate = builder.equal(warehouseJoin.get("id"), warehouseId);
            query.where(predicate, warehousePredicate);
        } else {
            query.where(predicate);
        }

        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    if (columnName.equalsIgnoreCase("product.name")) {
                        Join<Inventory, Product> productJoin = root.join("product", JoinType.LEFT);
                        query.orderBy(builder.asc(productJoin.get("name")));
                    } else {
                        query.orderBy(builder.asc(root.get(columnName)));
                    }
                } else {
                    if (columnName.equalsIgnoreCase("product.name")) {
                        Join<Inventory, Product> productJoin = root.join("product", JoinType.LEFT);
                        query.orderBy(builder.desc(productJoin.get("name")));
                    } else {
                        query.orderBy(builder.desc(root.get(columnName)));
                    }
                }
            }
        }

        return entityManager.createQuery(query)
                .setFirstResult(pageNo * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    private Long getTotalElements(List<SearchCriteria> criteriaList, int warehouseId){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Inventory> root = query.from(Inventory.class);

        Predicate predicate = builder.conjunction();
        SearchQueryCriteriaConsumer searchQueryCriteriaConsumer = new SearchQueryCriteriaConsumer(predicate, builder, root);
        criteriaList.forEach(searchQueryCriteriaConsumer);
        predicate = searchQueryCriteriaConsumer.getPredicate();

        Join<Inventory, Zone> zoneJoin = root.join("zone");
        Join<Zone, Warehouse> warehouseJoin = zoneJoin.join("warehouse");


        if (StringUtils.hasLength(String.valueOf(warehouseId))) {
            Predicate warehousePredicate = builder.equal(warehouseJoin.get("id"), warehouseId);
            query.select(builder.count(root));
            query.where(predicate, warehousePredicate);
        } else {
            query.select(builder.count(root));
            query.where(predicate);
        }

        return entityManager.createQuery(query).getSingleResult();
    }
}
