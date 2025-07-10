package com.minis.web.bind;

import com.minis.beans.*;
import com.minis.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;


// TODO: 【目的属性对象，继承自源属性对象，可对原属性进行观察，并操作目的属性赋值】 BeanWrapperImpl -> PropertyEditorRegistrySupport
// TODO: 【更高层封装】：WebDataBinder
//  注册属性编辑器；从request获取源属性Map；为目属性对象赋值（调用 BeanWrapperImpl 的 setPropertyValue 方法）
public class WebDataBinder extends PropertyEditorRegistrySupport {
    //private List<Object> targetList;  // 暂未使用，像是后续的目的属性值
    //private Class<?> clazz; // 暂未使用
    //private List<String> objectNames;
    private BeanWrapperImpl beanWrapper; //TODO：继承自目的属性操作器；真正的类型是其子类：目的属性包装器

    public WebDataBinder(List<Object> target, List<String> targetName) {
        //this.targetList = target;
        //this.objectNames = targetName;
        //this.clazz = target.get(0).getClass();
        this.beanWrapper = new BeanWrapperImpl(target);
    }
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        this.beanWrapper.registerCustomEditor(requiredType, propertyEditor);
    }
    public void bind(HttpServletRequest request) {
        PropertyValues pvs = assignParameters(request);
        this.beanWrapper.setPropertyValues(pvs);
    }
    private PropertyValues assignParameters(HttpServletRequest request) {
        Map<String, Object> map = WebUtils.getParametersStartingWith(request, "");
        return new PropertyValues(map);
    }

}
