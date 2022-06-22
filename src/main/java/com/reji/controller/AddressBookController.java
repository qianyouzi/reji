package com.reji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.reji.bean.AddressBook;
import com.reji.bean.R;
import com.reji.service.AddressBookService;
import com.reji.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * 地址管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增
     */
    @CacheEvict(value = "AddressBook", key = "'list_'+#session.getAttribute('user')")
    @PostMapping
    public R save(@RequestBody AddressBook addressBook, HttpSession session) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook, HttpSession session) {
        Set user = redisTemplate.keys("*" + session.getAttribute("user") + "*");
        if (user != null) {
            redisTemplate.delete(user);
        }
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(wrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    @Cacheable(value = "AddressBook", key = "'list_'+#session.getAttribute('user')+#id", unless = "#result.data==null")
    public R get(@PathVariable Long id, HttpSession session) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @Cacheable(value = "AddressBook", key = "'default_'+#session.getAttribute('user')", unless = "#result.data==null")
    @GetMapping("default")
    public R<AddressBook> getDefault(HttpSession session) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @Cacheable(value = "AddressBook", key = "'list_'+#session.getAttribute('user')", unless = "#result.data==null")
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook, HttpSession session) {
        addressBook.setUserId(BaseContext.getCurrentId());
        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getCreateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }

    /**
     * 修改地址
     */
    @PutMapping
    public R update(@RequestBody AddressBook addressBook, HttpSession session) {
        Set user = redisTemplate.keys("*" + "list_" + session.getAttribute("user") + "*");
        if (user != null) {
            redisTemplate.delete(user);
        }
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 删除地址
     */
    @DeleteMapping
    public R delete(Long ids, HttpSession session) {
        Set user = redisTemplate.keys("*" + "list_" + session.getAttribute("user") + "*");
        if (user != null) {
            redisTemplate.delete(user);
        }
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }
}
