package com.reji.dto;


import com.reji.bean.OrderDetail;
import com.reji.bean.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    //private String userName;
    //

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
