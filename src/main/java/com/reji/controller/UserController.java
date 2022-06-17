package com.reji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reji.bean.R;
import com.reji.bean.User;
import com.reji.service.UserService;
import com.reji.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取验证码
     */
    @PostMapping("/sendMsg")
    public R send(@RequestBody User user, HttpSession session) {
        log.error("{}", user.getPhone());
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成4位数验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            session.setAttribute(phone, code);
            log.error("{}", code);
            return R.success(code);
        }
        return R.error("短信发送失败");

    }

    /**
     * 用户登录验证
     */
    @PostMapping("/login")
    public R login(@RequestBody Map<String, String> map, HttpSession session) {
        String phone = map.get("phone");
        String code = map.get("code");
        String code1 = (String) session.getAttribute(phone);
        if (code1 != null && code.equalsIgnoreCase(code1)) {
            LambdaQueryWrapper<User> user = new LambdaQueryWrapper<>();
            user.eq(User::getPhone, phone);
            User one = userService.getOne(user);
            if (one == null) {
                User user1 = new User();
                user1.setPhone(phone);
                user1.setStatus(1);
                userService.save(user1);
                log.error("创建||登录成功");
            }
            User one1 = userService.getOne(user);
            session.setAttribute("user", one1.getId());
            log.error("登录成功");
            return R.success(one);
        }
        log.error("{}", map);
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
