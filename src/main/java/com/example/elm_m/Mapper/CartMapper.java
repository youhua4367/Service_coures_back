package com.example.elm_m.Mapper;

import com.example.elm_m.Entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {
    /**
     * 查询购物车
     * @param cart 购物车
     * @return 购物车列表
     */
    List<Cart> list(Cart cart);

    /**
     * 更新商品的数量
     * @param cart1 购物车对象
     */
    @Update("update cart set quantity = #{quantity}, amount = #{amount} where cartId = #{cartId}")
    void updateQuantityById(Cart cart1);

    @Insert("insert into cart (foodId, businessId, userId, quantity, setmealId, flavor, img, name, amount) " +
            "values (#{foodId}, #{businessId}, #{userId}, #{quantity},#{setMealId}, #{flavor}, #{img}, #{name}, #{amount})")
    void insert(Cart cart);

    /**
     * 根据用户 id 进行删除
     * @param userId
     */
    @Delete("delete from cart where userId = #{userId}")
    void deleteById(String userId);

    /**
     * 清空当前用户在指定商家的购物车
     */
    @Delete("delete from cart where userId = #{userId} and businessId = #{businessId}")
    void deleteByUserIdAndBusinessId(@Param("userId") String userId,
                                     @Param("businessId") Long businessId);

    /**
     * 根据 id 删除项
     * @param cartId
     */
    @Delete("delete from cart where cartId = #{cartId}")
    void deleteByCartId(Long cartId);

    void insertBatch(List<Cart> cartList);
}
