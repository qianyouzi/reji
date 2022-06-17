package com.reji.service.impl;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reji.bean.AddressBook;
import com.reji.dao.AddressBookDao;
import com.reji.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 地址簿
 */

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService {

    @Autowired
    private AddressBookDao addressBookDao;

    public void add(){
        addressBookDao.selectList(null);

    }

}
