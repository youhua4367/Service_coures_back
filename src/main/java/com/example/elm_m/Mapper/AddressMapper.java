package com.example.elm_m.Mapper;

import com.example.elm_m.Entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {
    @Select("select * from address where userId = #{userId}")
    List<Address> getByUserId(String userId);

    @Insert("insert into address" +
            "(userId, name, sex, phone, provinceCode, provinceName, cityCode, cityName, districtCode, districtName, detail, label, isDefault) " +
            "values " +
            "(#{userId}, #{name}, #{sex}, #{phone}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(Address address);


    @Select("select * from address where addressId = #{id}")
    Address getByAddressId(Long id);

    @Select("select * from address where addressId = #{addressId} and userId = #{userId}")
    Address getByAddressIdAndUserId(@Param("addressId") Long addressId,
                                    @Param("userId") String userId);

    void update(Address address);

    @Update("update address set isDefault = #{isDefault} where userId = #{userId}")
    void updateIsDefaultByUserId(Address address1);

    @Delete("delete from address where addressId = #{addressId} and userId = #{userId}")
    void deleteByIdAndUserId(@Param("addressId") Long addressId,
                             @Param("userId") String userId);

    @Select("select * from address where userId = #{userId} and isDefault = #{isDefault}")
    Address getDefault(Address address);
}
