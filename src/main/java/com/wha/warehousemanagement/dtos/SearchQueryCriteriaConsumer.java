package com.wha.warehousemanagement.dtos;

import com.wha.warehousemanagement.models.Inventory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryCriteriaConsumer implements Consumer<SearchCriteria> {

    private Predicate predicate;
    private CriteriaBuilder builder;
    private Root<Inventory> root;

    @Override
    public void accept(SearchCriteria param) {
        Path<?> path;
        if (param.getKey().contains(".")) {
            String[] parts = param.getKey().split("\\.");
            String entity = parts[0];
            String field = parts[1];

            if (entity.equalsIgnoreCase("product")) {
                path = root.join("product").get(field);
            } else if (entity.equalsIgnoreCase("zone")) {
                path = root.join("zone").get(field);
            } else if (entity.equalsIgnoreCase("category")) {
                path = root.join("product").join("category").get(field);
            } else {
                path = root.get(param.getKey());
            }
        } else {
            path = root.get(param.getKey());
        }
        if (param.getOperation().equalsIgnoreCase(":")) {
            if (path.getJavaType() == String.class) {
                predicate = builder.and(predicate, builder.like(path.as(String.class), "%" + param.getValue() + "%"));
            } else {
                predicate = builder.and(predicate, builder.equal(path, param.getValue()));
            }
        }
    }
}




