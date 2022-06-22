package com.reji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reji.bean.R;
import com.reji.bean.User;
import com.reji.service.UserService;
import com.reji.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取验证码
     */
    @PostMapping("/sendMsg")
    public R send(@RequestBody User user) {
        log.error("{}", user.getPhone());
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成4位数验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //往redis数据库中存入验证码,设置保存时间5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            log.error("{}", code);
            return R.success(code);
        }
        return R.error("短信发送失败");

    }

    /**
     * 用户登录验证
     */
    @PostMapping("/login")
    public R login(@RequestBody Map<String, String> map,HttpSession session) {
        String phone = map.get("phone");
        String code = map.get("code");
        String code1 = (String) redisTemplate.opsForValue().get(phone);
        if (code1 != null && code.equalsIgnoreCase(code1)) {
            LambdaQueryWrapper<User> user = new LambdaQueryWrapper<>();
            user.eq(User::getPhone, phone);
            User one = userService.getOne(user);
            if (one == null) {
                User user1 = new User();
                user1.setPhone(phone);
                user1.setStatus(1);
                userService.save(user1);
            }
            User one1 = userService.getOne(user);
            //登录成功则删除redis数据库中的数据
            redisTemplate.delete(phone);
            session.setAttribute("user", one1.getId());
            return R.success(one);
        }
        return R.error("登录失败");
    }

    /**
     * 用户退出
     */
    @PostMapping("/loginout")
    public R loginOut(HttpSession session){
        session.invalidate();
        return R.success("用户已退出");
    }
}
