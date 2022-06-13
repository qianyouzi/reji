package com.reji.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/*这个注解是由lombok提供的,idea2020.3以下的版本要安装插件,并且使用过注解处理器*/
@Slf4j
@Configuration
/**
 *  配置静态资源映射
 * @author 74545
 */
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("访问了静态资源");
        //设置用户访问的指定路径都到类路径的指定路径去找资源
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        super.addResourceHandlers(registry);
    }

    /**
     * 扩展object转json格式转换器,解决转换Long类型数据页面显示时精度丢失问题
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        MappingJackson2HttpMessageConverter mapp = new MappingJackson2HttpMessageConverter();
        mapp.setObjectMapper(new JacksonObjectMapper());
        //通过索引,把我们自己的消息转换器放在前面
        converters.add(0,mapp);

    }
}
