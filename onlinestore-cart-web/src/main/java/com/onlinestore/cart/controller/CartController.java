package com.onlinestore.cart.controller;

import com.alibaba.dubbo.common.json.JSON;
import com.onlinestore.common.pojo.OnlinStoreResult;
import com.onlinestore.common.utils.CookieUtils;
import com.onlinestore.common.utils.JsonUtils;
import com.onlinestore.pojo.TbItem;
import com.onlinestore.pojo.TbUser;
import com.onlinestore.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    private ItemService itemService;
    @Value("${CART_KEY}")
    private String CART_KEY;
    @Value("${CART_EXPIER}")
    private Integer CART_EXPIER;

    @RequestMapping("/cart/add/{itemId}")//num是url传递过来的参数
    public String addCart(@PathVariable long itemId, @RequestParam(defaultValue = "1") int num,
                          HttpServletRequest request, HttpServletResponse response){
        //从cookie中获取购物车商品列表
        List<TbItem> cartItemList = getCartItemList(request);
        //判断商品在购物车中是否已经存在
        boolean flag = false;
        for(TbItem item : cartItemList){
            if(item.getId() == itemId){
                //如果存在，那么就直接将数量加1即可
                item.setNum(item.getNum()+num);
                flag = true;
                break;
            }
        }
        if(!flag){//如果不存在，那么添加一个商品到cookie中
            TbItem item = itemService.getItemById(itemId);
            //设置购买的数量
            item.setNum(num);
            //取一张图片
            String image = item.getImage();
            if(StringUtils.isNotBlank(image)){
                String[] images = image.split(",");
                item.setImage(images[0]);
            }
            //把商品添加到购物车中
            cartItemList.add(item);
        }
        //把购物车列表写入到cookie中，设置失效时间，默认是关闭浏览器失效
        CookieUtils.setCookie(request,response,CART_KEY, JsonUtils.objectToJson(cartItemList),CART_EXPIER,true);
        //返回加入购物车成功页面
        return "cartSuccess";
    }

    public List<TbItem> getCartItemList(HttpServletRequest request){
        //从cookie中取购物车列表商品
        String json = CookieUtils.getCookieValue(request,CART_KEY,true);
        if(StringUtils.isBlank(json)){
            return new ArrayList<TbItem>();
        }
        List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
        return list;
    }

    @RequestMapping("/cart/cart")
    public String showCartList(HttpServletRequest request,Model model){
        //获取购物车商品列表
        List<TbItem> cartItemList = getCartItemList(request);
        model.addAttribute("cartList",cartItemList);
        return "cart";
    }
    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public OnlinStoreResult updateItemNum(@PathVariable Long itemId, @PathVariable Integer num,
                                          HttpServletRequest request, HttpServletResponse response){
        //从cookie中取购物车列表
        List<TbItem> cartItemList = getCartItemList(request);
        //遍历购物车列表，根据itemId查询到商品
        for(TbItem item : cartItemList){
            if(item.getId() == itemId.longValue()){
                //更新商品数量
                item.setNum(num);
                break;
            }
        }
        //把购物车列表写入到cookie
        CookieUtils.setCookie(request,response,CART_KEY,JsonUtils.objectToJson(cartItemList),CART_EXPIER,true);
        return OnlinStoreResult.ok();
    }
    @RequestMapping("/cart/delete/{itemId}")
    public String deleteCartItem(@PathVariable Long itemId, HttpServletRequest request,HttpServletResponse response){
        //从cookie中获取购物车列表
        List<TbItem> cartItemList = getCartItemList(request);
        for(TbItem tbItem : cartItemList){
            if(tbItem.getId() == itemId.longValue()){
                cartItemList.remove(tbItem);
                break;
            }
        }
        CookieUtils.setCookie(request,response,CART_KEY,JsonUtils.objectToJson(cartItemList),CART_EXPIER,true);
        //重定向到购物车列表页面
        return "redirect:/cart/cart.html";
    }
}
