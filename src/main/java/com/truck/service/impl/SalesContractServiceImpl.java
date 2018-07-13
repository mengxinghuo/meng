package com.truck.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.truck.common.Const;
import com.truck.common.ServerResponse;
import com.truck.dao.*;
import com.truck.pojo.*;
import com.truck.service.IOutService;
import com.truck.service.ISalesContractService;
import com.truck.util.DateTimeUtil;
import com.truck.vo.OutVo;
import com.truck.vo.SalesContractVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service("iSalesContractService")
public class SalesContractServiceImpl implements ISalesContractService {

    @Autowired
    private SalesContractMapper salesContractMapper;
    @Autowired
    private OutMapper outMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerAddressMapper customerAddressMapper;
    @Autowired
    private CustomerContactMapper customerContactMapper;
    @Autowired
    private OutDetailMapper outDetailMapper;
    @Autowired
    private IOutService iOutService;

    public ServerResponse addSalesContract(SalesContract salesContract){
        if(StringUtils.isEmpty(salesContract.getCustomerId()) || StringUtils.isEmpty(salesContract.getAddressId()) || StringUtils.isEmpty(salesContract.getContactId()) ||
                StringUtils.isEmpty(salesContract.getBpkNo()) || StringUtils.isEmpty(salesContract.getOutNo()) || StringUtils.isEmpty(salesContract.getSalesContractNo()) ||
                StringUtils.isEmpty(salesContract.getSalesDate())){
            return ServerResponse.createByErrorMessage("信息不完整");
        }
        Out out = outMapper.selectByOutNo(salesContract.getOutNo());
        if(out == null){
            return ServerResponse.createByErrorMessage("出库单不存在");
        }
        SalesContract searchBpk = salesContractMapper.checkOutSalesContract(salesContract.getBpkNo(),null);
        if(searchBpk != null){
            return ServerResponse.createByErrorMessage("BPK单号已存在");
        }
        SalesContract searchOut = salesContractMapper.checkOutSalesContract(null,salesContract.getOutNo());
        if(searchOut != null){
            return ServerResponse.createByErrorMessage("出库单号重复");
        }
        salesContract.setOutId(out.getId());
        salesContract.setDate(DateTimeUtil.strToDate(salesContract.getSalesDate()));
        salesContract.setStatus(Const.SalesContractStatusEnum.NORMAL.getCode());
        //预留type类型字段
        int resultCount = salesContractMapper.insertSelective(salesContract);
        if(resultCount > 0){
            return ServerResponse.createBySuccess("新增成功");
        }else{
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    public ServerResponse getSalesContractDetail(Integer salesContractId){
        if(StringUtils.isEmpty(salesContractId)){
            return ServerResponse.createByErrorMessage("请选择要查看的记录");
        }
        SalesContract salesContract = salesContractMapper.selectByPrimaryKey(salesContractId);
        if(salesContract == null){
            return ServerResponse.createByErrorMessage("未查到该记录信息");
        }
        SalesContractVo salesContractVo = this.assembleSalesContract(salesContract);
        return ServerResponse.createBySuccess(salesContractVo);
    }

    public ServerResponse getCustomerSalesContract(Integer customerId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        if(StringUtils.isEmpty(customerId)){
            return ServerResponse.createByErrorMessage("请选择要查询的客户");
        }
        List<SalesContract> salesContractList = salesContractMapper.selectByCustomer(customerId);
        if(salesContractList.size() == 0){
            return ServerResponse.createByErrorMessage("未查到结果");
        }
        List<SalesContractVo> salesContractVoList = Lists.newArrayList();
        for(SalesContract salesContractItem : salesContractList){
            SalesContractVo salesContractVo = this.assembleSalesContract(salesContractItem);
            salesContractVoList.add(salesContractVo);
        }
        PageInfo pageInfo = new PageInfo(salesContractList);
        pageInfo.setList(salesContractVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse getSalesContractList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<SalesContract> salesContractList = salesContractMapper.selectSalesContractList();
        if(salesContractList.size() == 0){
            return ServerResponse.createByErrorMessage("未查到结果");
        }
        List<SalesContractVo> salesContractVoList = Lists.newArrayList();
        for(SalesContract salesContractItem : salesContractList){
            SalesContractVo salesContractVo = this.assembleSalesContract(salesContractItem);
            salesContractVoList.add(salesContractVo);
        }
        PageInfo pageInfo = new PageInfo(salesContractList);
        pageInfo.setList(salesContractVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public SalesContractVo assembleSalesContract(SalesContract salesContract){
        SalesContractVo salesContractVo = new SalesContractVo();
        salesContractVo.setSalesContractId(salesContract.getSalesContractId());
        salesContractVo.setCustomerId(salesContract.getCustomerId());
        Customer customer = customerMapper.selectByPrimaryKey(salesContract.getCustomerId());
        salesContractVo.setCustomer(customer);
        salesContractVo.setAddressId(salesContract.getAddressId());
        CustomerAddress customerAddress = customerAddressMapper.selectByPrimaryKey(salesContract.getAddressId());
        salesContractVo.setCustomerAddress(customerAddress);
        salesContractVo.setContactId(salesContract.getContactId());
        CustomerContact customerContact = customerContactMapper.selectByPrimaryKey(salesContract.getContactId());
        salesContractVo.setCustomerContact(customerContact);
        salesContractVo.setDate(DateTimeUtil.dateToStr(salesContract.getDate()));
        salesContractVo.setOutId(salesContract.getOutId());
        Out out = outMapper.selectByPrimaryKey(salesContract.getOutId());
        OutVo outVo = iOutService.assembleOut(out);
        salesContractVo.setOutVo(outVo);
        List<OutDetail> outDetailList = outDetailMapper.selectByOutId(out.getId());
        salesContractVo.setOutDetailList(outDetailList);
        salesContractVo.setOutNo(salesContract.getOutNo());
        salesContractVo.setBpkNo(salesContract.getBpkNo());
        salesContractVo.setSalesContractNo(salesContract.getSalesContractNo());
        salesContractVo.setStatus(salesContract.getStatus());
        salesContractVo.setStatusDesc(Const.SalesContractStatusEnum.codeOf(salesContract.getStatus()).getValue());
        salesContractVo.setCreateTime(DateTimeUtil.dateToStr(salesContract.getCreateTime()));
        salesContractVo.setUpdateTime(DateTimeUtil.dateToStr(salesContract.getUpdateTime()));
        return salesContractVo;
    }
}