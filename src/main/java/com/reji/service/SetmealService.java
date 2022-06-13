package com.reji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reji.bean.Setmeal;
import com.reji.dto.DishDto;
import com.reji.dto.SetmealDto;

import java.util.List;

/**
 * 套餐管理
 * @author 74545
 */

public interface SetmealService extends IService<Setmeal> {
    /**
     * 增加套餐
     * @param
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐
     */
    void removeWithDish(List<Long> ids);

    /**
     * 回显套餐数据
     */
    SetmealDto myGetId(Long id);

    /**
     * 修改套餐
     * @param setmealDto
     */
    void updateSetmeal(SetmealDto setmealDto);
}
