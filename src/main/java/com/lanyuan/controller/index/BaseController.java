package com.lanyuan.controller.index;

import java.util.Enumeration;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.lanyuan.entity.ResFormMap;
import com.lanyuan.mapper.ResourcesMapper;
import com.lanyuan.plugin.PageView;
import com.lanyuan.util.Common;
import com.lanyuan.util.FormMap;

/**
 * 
 * @author lanyuan
 * Email：mmm333zzz520@163.com
 * date：2014-2-17
 */
public class BaseController {
	@Inject
	private ResourcesMapper resourcesMapper;
	
	public PageView pageView = null;
	public PageView getPageView(String pageNow,String pageSize) {
		if (Common.isEmpty(pageNow)) {
			pageView = new PageView(1);
		} else {
			pageView = new PageView(Integer.parseInt(pageNow));
		}
		if (Common.isEmpty(pageSize)) {
			pageSize = "10";
		} 
		pageView.setPageSize(Integer.parseInt(pageSize));
		return pageView;
	}
	
	public <T> T toFormMap(T t,String pageNow,String pageSize){
		@SuppressWarnings("unchecked")
		FormMap<String, Object> formMap = (FormMap<String, Object>) t;
		formMap.put("paging", getPageView(pageNow, pageSize));
		return t;
	}
	
	/**
	 * 获取返回某一页面的按扭组,
	 * <br/>
	 *<b>author：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsplanyuan</b><br/> 
	 *<b>date：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-04-01</b><br/> 
	 *<b>version：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp3.0v</b>
	 * @return Class<T>
	 * @throws Exception
	 */
	public List<ResFormMap> findByRes(){
		String id = getPara("id");
		List<ResFormMap> rse = resourcesMapper.findByAttribute("parentId", id, ResFormMap.class);
		for (ResFormMap resFormMap : rse) {
			Object o =resFormMap.get("description");
			if(o!=null&&!Common.isEmpty(o.toString())){
				resFormMap.put("description",Common.stringtohtml(o.toString()));
			}
		}
		return rse;
	}
	
	/**
	 * 获取页面传递的某一个参数值,
	 * <br/>
	 *<b>author：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsplanyuan</b><br/> 
	 *<b>date：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-04-01</b><br/> 
	 *<b>version：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp3.0v</b>
	 */
	public String getPara(String key){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		return request.getParameter(key);
	}
	
	/**
	 * 获取页面传递的某一个数组值,
	 * <br/>
	 *<b>author：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsplanyuan</b><br/> 
	 *<b>date：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-04-01</b><br/> 
	 *<b>version：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp3.0v</b>
	 * @return Class<T>
	 * @throws Exception
	 */
	public String[] getParaValues(String key){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		return request.getParameterValues(key);
	}
	/*
	 * @ModelAttribute
	 * 这个注解作用.每执行controllor前都会先执行这个方法
	 * @author lanyuan
	 * Email：mmm333zzz520@163.com
	 * date：2015-4-05
	 * @param request
	 * @throws Exception 
	 * @throws  
	 */
	/*@ModelAttribute
	public void init(HttpServletRequest request){
		String path = Common.BACKGROUND_PATH;
		Object ep = request.getSession().getAttribute("basePath");
		if(ep!=null){
			if(!path.endsWith(ep.toString())){
				Common.BACKGROUND_PATH = "/WEB-INF/jsp/background"+ep;
			}
		}
		
	}*/
	
	/**
	 * 获取传递的所有参数,
	 * 反射实例化对象，再设置属性值
	 * 通过泛型回传对象.
	 * <br/>
	 *<b>author：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsplijianning</b><br/> 
	 *<b>date：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-04-01</b><br/> 
	 *<b>version：</b><br/> 
	 *<b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp3.0v</b>
	 * @return Class<T>
	 * @throws Exception
	 */
	public <T> T getFormMap(Class<T> clazz){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		Enumeration<String> en = request.getParameterNames();
		T t = null;
		try {
			t = clazz.newInstance();
			@SuppressWarnings("unchecked")
			FormMap<String, Object> map = (FormMap<String, Object>) t;
			while (en.hasMoreElements()) {
				String nms = en.nextElement().toString();
				if(nms.endsWith("[]")){
					String[] as = request.getParameterValues(nms);
					if(as!=null&&as.length!=0&&as.toString()!="[]"){
						String mname = t.getClass().getSimpleName().toUpperCase();
						if(nms.toUpperCase().startsWith(mname)){
							nms=nms.substring(nms.toUpperCase().indexOf(mname)+1);
							map.put( nms,as);
						}
					}
				}else{
					String as = request.getParameter(nms);
					if(!Common.isEmpty(as)){
						String mname = t.getClass().getSimpleName().toUpperCase();
						if(nms.toUpperCase().startsWith(mname)){
							nms=nms.substring(mname.length()+1);
							map.put( nms, as);
						}
						
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return  t;
	}
}