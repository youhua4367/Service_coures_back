package com.example.elm_m.Service.Impl;

import com.example.elm_m.Constant.MessageConstant;
import com.example.elm_m.Context.ThreadContext;
import com.example.elm_m.DTO.OrderSubmitDTO;
import com.example.elm_m.Entity.Address;
import com.example.elm_m.Entity.Cart;
import com.example.elm_m.Entity.OrderDetail;
import com.example.elm_m.Entity.Orders;
import com.example.elm_m.Exception.AddressBusinessException;
import com.example.elm_m.Exception.CartBusinessException;
import com.example.elm_m.Exception.OrderBusinessException;
import com.example.elm_m.Exception.ParamException;
import com.example.elm_m.Mapper.AddressMapper;
import com.example.elm_m.Mapper.CartMapper;
import com.example.elm_m.Mapper.OrderDetailMapper;
import com.example.elm_m.Mapper.OrdersMapper;
import com.example.elm_m.Service.OrderService;
import com.example.elm_m.VO.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private CartMapper cartMapper;


    /**
     * 提交订单
     * @param orderSubmitDTO 订单提交对象
     * @return 订单响应对象
    */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO) {

        if (orderSubmitDTO == null || orderSubmitDTO.getBusinessId() == null) {
            throw new ParamException(MessageConstant.PARAM_ERROR);
        }

        String userId = ThreadContext.getCurrentId();

        // 1.业务异常：地址不存在、地址不属于当前用户、当前商家购物车为空
        Address address = addressMapper.getByAddressIdAndUserId(
                orderSubmitDTO.getAddressId(), userId);
        if (address == null) {
            throw new AddressBusinessException(MessageConstant.ADDRESS_IS_NULL);
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setBusinessId(orderSubmitDTO.getBusinessId());
        List<Cart> carts = cartMapper.list(cart);
        if (carts.isEmpty()) {
            throw new CartBusinessException(MessageConstant.CART_IS_NULL);
        }

        // 2.向订单表中插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(orderSubmitDTO, orders);

        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setOrderStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));

        ordersMapper.insert(orders);

        // 3.向订单明细表插入数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (Cart cart1 : carts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart1, orderDetail);

            orderDetail.setOrderId(orders.getOrderId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        // 4.只清空本次下单商家的购物车，保留其他商家的商品
        cartMapper.deleteByUserIdAndBusinessId(userId, orderSubmitDTO.getBusinessId());

        // 5.封装 VO 返回结果
        return OrderSubmitVO.builder()
                .id(orders.getOrderId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getOrderTotal().add(orders.getPackAmount()))
                .build();

    }

    /**
     * 获取历史订单列表
     * @return 订单列表
     */
    @Override
    public List<Orders> getOrders() {

        return ordersMapper.getOrdersByUserId(ThreadContext.getCurrentId());
    }

    /**
     * 获取历史订单详情
     * @param id 订单id
     * @return 历史订单详情
     */
    @Override
    public List<OrderDetail> getOrderDetail(Long id) {

        return orderDetailMapper.getDetailByOrderId(id);
    }

    /**
     * 取消订单
     * @param id 订单号
     */
    @Override
    public void CancelById(Long id) {
        // 查询订单
        Orders orders = ordersMapper.getById(id);

        // 校验订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (orders.getOrderStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders1 = new Orders();
        orders1.setOrderId(id);

        // 如果处于等待接单状态下取消，需要进行退款

        orders1.setOrderStatus(Orders.CANCELLED);
        orders1.setCancelTime(LocalDateTime.now());
        ordersMapper.update(orders1);
    }

    /**
     * 再来一单
     * @param id 订单号
     */
    @Override
    public void repeatOne(Long id) {
        String userId = ThreadContext.getCurrentId();

        // 根据订单号查询订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getDetailByOrderId(id);

        // 将订单详情转化为购物车对象
        List<Cart> cartList =
                orderDetailList.stream().map(x ->{
                    Cart cart = new Cart();
                    BeanUtils.copyProperties(x, cart, "cartId");

                    cart.setUserId(userId);
                    return cart;
                }).toList();

        cartMapper.insertBatch(cartList);
    }

    /**
     * 支付订单
     * @param id 订单 id
     */
    @Override
    public void payOrder(Long id) {
        ordersMapper.setStatus(id, Orders.PAID, Orders.COMPLETED);
    }
}
