package com.cool.grabber.conversion;

import com.cool.config.RootConfig;
import com.cool.grabber.GrabberConfig;
import org.springframework.core.env.Environment;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by codelover on 18/3/7.
 */
public class GrabberConfigEditor extends PropertyEditorSupport {

    private Environment environment = RootConfig.rootApplicationContext.getEnvironment();

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        List<GrabberConfig> grabbers = new ArrayList<>(5);
        for (int i = 0; ; i++) {
            try {
                String key = text + "["+i+"]";
                String name = environment.getRequiredProperty(key +".name");
                String beanName = environment.getRequiredProperty(key +".beanName");
                String startLink = environment.getRequiredProperty(key +".startLink");
                String referer = environment.getRequiredProperty(key +".referer");
                grabbers.add(new GrabberConfig(name, beanName,startLink,referer));
            } catch (IllegalStateException e) {
                break;
            }catch (Exception e){
                throw new IllegalArgumentException("指定的配置key: " + text +" 无法转换成类型: " + GrabberConfig.class.getTypeName(), e);
            }
        }
        this.setValue(grabbers);
    }
}
