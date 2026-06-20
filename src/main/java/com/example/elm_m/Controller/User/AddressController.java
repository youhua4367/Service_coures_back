package com.example.elm_m.Controller.User;

import com.example.elm_m.Entity.Address;
import com.example.elm_m.Result.Result;
import com.example.elm_m.Service.AddressService;
import com.example.elm_m.VO.AddressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/address")
@Slf4j
@Tag(name = "地址簿相关接口", description = "管理地址簿")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取地址信息
     * @return 地址信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取地址信息", description = "获取当前用户的地址信息")
    public Result<List<Address>> list() {
        log.info("查询当前用户所有地址信息");

        List<Address> addresses = addressService.list();
        return Result.success(addresses);
    }

    @PostMapping()
    @Operation(summary = "新增地址")
    public Result<String> save(@RequestBody Address address) {
        log.info("新增地址:{}", address);
        addressService.save(address);

        return Result.success("添加地址成功");
    }

    /**
     * 根据 id 获得地址
     * @param id 地址 id
     * @return 地址对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 id 查询地址")
    public Result<Address> getById(@PathVariable Long id) {
        log.info("查询地址{}", id);

        Address address = addressService.getById(id);
        return Result.success(address);
    }

    /**
     * 根据 id 修改地址
     * @param address 地址实体
     * @return 成功标志
     */
    @PutMapping()
    @Operation(summary = "根据 id 修改地址")
    public Result<String> update(@RequestBody Address address) {
        log.info("修改地址:{}", address);
        addressService.update(address);

        return Result.success("修改成功");
    }

    /***
     * 设置默认地址
     * @param address 地址实体
     * @return 成功标志
     */
    @PutMapping("/default")
    @Operation(summary = "设置默认地址")
    public Result<String> setDefault(@RequestBody Address address) {
        log.info("设置默认地址：:{}", address);
        addressService.setDefault(address);
        return Result.success("修改默认地址成功");
    }

    /**
     * 根据 id 删除地址
     * @param id 地址id
     * @return 成功标志
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "根据 id 删除地址")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除地址:{}", id);

        addressService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 查询默认地址
     * @return address 实体
     */
    @GetMapping("/default")
    @Operation(summary = "查询默认地址")
    public Result<Address> getDefault() {
        log.info("查询默认地址");

        Address address = addressService.getDefault();
        return Result.success(address);
    }

    /**
     * 获取地理位置
     * @return 地理位置
     */
    @GetMapping("/api/{position}")
    @Operation(summary = "获取地理位置")
    public Result<AddressVO> getAddress(@PathVariable String position) {
        log.info("获取地理位置");

        AddressVO address = addressService.getAddress(position);
        return Result.success(address);
    }
}
