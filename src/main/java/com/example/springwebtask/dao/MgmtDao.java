package com.example.springwebtask.dao;

import com.example.springwebtask.entity.CategoryRecord;
import com.example.springwebtask.entity.ProductRecord;
import com.example.springwebtask.entity.UsersRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MgmtDao implements ManagementDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public UsersRecord findById(String id) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id", id);
        var list = jdbcTemplate.query("SELECT id, login_id, password, name, role, created_rd, created_ud FROM users WHERE login_id = :id", param,
                new DataClassRowMapper<>(UsersRecord.class));
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<ProductRecord> findAll() {
        var list = jdbcTemplate.query("SELECT p.id, p.product_id AS productId, c.name AS category, p.name, p.price, " +
                        "p.image_path AS imgPath, p.description, p.created_rd, p.created_ud FROM products AS p " +
                        "LEFT OUTER JOIN categories AS c ON p.category_id = c.id",
                new DataClassRowMapper<>(ProductRecord.class));
        return list;
    }

    @Override
    public List<ProductRecord> findByName(String name) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("name", "%" + name + "%");
        var list = jdbcTemplate.query("SELECT p.product_id AS id, p.name, p.price, c.name AS category FROM products AS p " +
                "LEFT OUTER JOIN categories AS c ON p.category_id = c.id WHERE p.name LIKE :name", param,
                new DataClassRowMapper<>(ProductRecord.class));
        return list;
    }

    @Override
    public List<CategoryRecord> findCategories() {
        return jdbcTemplate.query("SELECT id, name, created_rd, created_ud FROM categories", new DataClassRowMapper<>(CategoryRecord.class));
    }

    @Override
    public int insert(ProductRecord productAddRecord) {
        //category_idを受け取る
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("category", productAddRecord.category());

        Integer categoryId = jdbcTemplate.queryForObject("SELECT id FROM categories WHERE name = :category", param, Integer.class);
        //System.out.println(categoryId);

        param = new MapSqlParameterSource();
        param.addValue("id", productAddRecord.productId());
        param.addValue("name", productAddRecord.name());
        param.addValue("categoryId", categoryId);
        param.addValue("price", productAddRecord.price());
        param.addValue("description", productAddRecord.description());
        param.addValue("img", productAddRecord.imgPath());

        return jdbcTemplate.update("INSERT INTO products(product_id, category_id, name, price, image_path, description, created_rd, created_ud)" +
                "VALUES(:id, :categoryId, :name, :price, :img, :description, now(), now())", param);

    }
}
