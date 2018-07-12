package com.truck.controller.backend;

import com.truck.common.Const;
import com.truck.common.ResponseCode;
import com.truck.common.ServerResponse;
import com.truck.pojo.Admin;
import com.truck.pojo.Customer;
import com.truck.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/customer/")
public class CustomerManageController {

    @Autowired
    private ICustomerService iCustomerService;

    /**
     * 新增客户信息
     * @param session
     * @param customer
     * @return
     */
    @RequestMapping("add_customer.do")
    @ResponseBody
    public ServerResponse addCustomer(HttpSession session, Customer customer ){
        Admin admin = (Admin)session.getAttribute(Const.CURRENT_ADMIN);
        if(admin == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员用户未登录，请登录");
        }
        return iCustomerService.addCustomer(customer);
    }

    /**
     * 修改客户信息
     * @param session
     * @param customer
     * @return
     */
    @RequestMapping("update_customer.do")
    @ResponseBody
    public ServerResponse updateCustomer(HttpSession session,Customer customer){
        Admin admin = (Admin)session.getAttribute(Const.CURRENT_ADMIN);
        if(admin == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员用户未登录，请登录");
        }
        return iCustomerService.updateCustomer(customer);
    }

    /**
     * 更改用户信息状态
     * @param session
     * @param customerId
     * @param status
     * @return
     */
    @RequestMapping("disable_customer.do")
    @ResponseBody
    public ServerResponse disableCustomer(HttpSession session,Integer customerId,Integer status){
        Admin admin = (Admin)session.getAttribute(Const.CURRENT_ADMIN);
        if(admin == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员用户未登录，请登录");
        }
        return iCustomerService.disableCustomer(customerId,status);
    }

    /**
     * 查询客户信息列表
     * @param session
     * @param customerNo
     * @param ptName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("get_customer_list.do")
    @ResponseBody
    public ServerResponse getCustomerList(HttpSession session,
                                          @RequestParam(value = "customerNo",required = false) String customerNo,
                                          @RequestParam(value = "ptName",required = false) String ptName,
                                          @RequestParam(value = "status",required = false) Integer status,
                                          @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        Admin admin = (Admin)session.getAttribute(Const.CURRENT_ADMIN);
        if(admin == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员用户未登录，请登录");
        }
        return iCustomerService.getCustomerList(customerNo,ptName,status,pageNum,pageSize);
    }
}
