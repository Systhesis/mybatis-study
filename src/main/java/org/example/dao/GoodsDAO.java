package org.example.dao;

import org.apache.ibatis.annotations.*;
import org.example.dto.GoodsDTO;
import org.example.entity.Goods;

import java.util.List;

public interface GoodsDAO {
    @Select("select * from t_goods\n" +
            "    where current_price between #{min} and #{max}\n" +
            "    order by current_price\n" +
            "    limit 0, #{limit}")
    public List<Goods> selectByPriceRange(@Param("min") Float min, @Param("max") Float max, @Param("limit") Integer limit);

    @Insert("INSERT INTO t_goods(title, sub_title, original_cost, current_price, discount, is_free_delivery, category_id)\n" +
            "        VALUES (#{title} , #{subTitle} , #{originalCost}, #{currentPrice}, #{discount}, #{isFreeDelivery}, #{categoryId})")
    @SelectKey(statement = "select last_insert_id()", before = false, keyProperty = "goodsId", resultType = Integer.class)
    public int insert(Goods goods);

    @Select("select g.* , c.*,'1' as test from t_goods g , t_category c\n" +
            "        where g.category_id = c.category_id limit 0, 20")
    //<resultMap>
    @Results({
            @Result(property="goods.goodsId", column="goods_id", id = true),
            @Result(property="goods.title", column="title"),
            @Result(property="goods.originalCost", column="original_cost"),
            @Result(property="goods.discount", column="discount"),
            @Result(property="goods.isFreeDelivery", column="is_free_delivery"),
            @Result(property="goods.categoryId", column="category_id"),
            @Result(property="category.categoryId", column="category_id"),
            @Result(property="category.categoryName", column="category_name"),
            @Result(property="category.parentId", column="parent_id"),
            @Result(property="category.categoryLevel", column="category_level"),
            @Result(property="category.categoryOrder", column="category_order"),
            @Result(property="test", column="test")
    })
    public List<GoodsDTO> selectAll();

}
