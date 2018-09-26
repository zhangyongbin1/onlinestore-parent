package com.onlinestore.order.controller;

import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.common.utils.CookieUtils;
import com.onlinestore.common.utils.JsonUtils;
import com.onlinestore.order.pojo.OrderInfo;
import com.onlinestore.order.service.OrderService;
import com.onlinestore.pojo.TbItem;
import com.onlinestore.pojo.TbUser;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成订单的controller
 */
@Controller
public class OrderController {

    @Value("${CART_KEY}")
    private String CART_KEY;
    @Autowired
    private OrderService orderService;
    @RequestMapping("/order/order-cart")
    public String showOrderCart(HttpServletRequest request){
        //用户必须是登录的
        //取用户id
        TbUser user = (TbUser) request.getAttribute("user");//因为在拦截器中已经设置了user这个attribute,所以可以使用request获取
        //System.out.println(user.getUsername());
        //从cookie中获取用户的token，然后调用sso服务获取用户的信息
        //根据用户id查询收货地址列表
        //从cookie中获取商品列表
        List<TbItem> cartList = getCartList(request);
        //将商品列表传递给页面
        request.setAttribute("cartList",cartList);
        //返回订单页面
        return "order-cart";
    }

    private List<TbItem> getCartList(HttpServletRequest request){
        String json = CookieUtils.getCookieValue(request, CART_KEY, true);
        if(StringUtils.isNotBlank(json)){
            List<TbItem> result = JsonUtils.jsonToList(json,TbItem.class);
            return result;
        }
        return new ArrayList<>();
    }

    @RequestMapping("/order/create")//提交订单就是把订单数据都更新到数据库中，并且显示提交订单成功页面
    public String createOrder(OrderInfo orderInfo, Model model){
        //生成订单
        OnlinStoreResult result = orderService.crateOrder(orderInfo);
        //返回逻辑视图
        model.addAttribute("orderId",result.getData().toString());
        model.addAttribute("payment",orderInfo.getPayment());
        //预计送达时间，预计三天后送达
        DateTime dateTime = new DateTime();
        dateTime=dateTime.plusDays(3);
        model.addAttribute("date",dateTime.toString("yyyy-MM-dd"));
        return "success";
    }
}
