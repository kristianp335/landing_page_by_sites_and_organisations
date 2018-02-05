package com.liferay.kris.portal.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;



@Component(
		immediate = true, 
		property = {"key=login.events.post"},
	service = LifecycleAction.class
 )

public class LandingRedirectAction implements LifecycleAction {
	
	@Reference
	UserLocalService userlocalService;
	
	
	 @Override
	 public void processLifecycleEvent(LifecycleEvent lifecycleEvent) throws ActionException {
		 System.out.println("The login event post action is running");
		 HttpServletRequest request = lifecycleEvent.getRequest();
		 HttpSession session = request.getSession();
		 User user;
			try {
				user = PortalUtil.getUser(request);
				String path="";
				String PUBLIC_PAGE_CONTEXT = "/web";
				String PRIVATE_PAGE_CONTEXT = "/group";
				List<Organization> orgs;
				try {
					orgs = user.getOrganizations();
					if(orgs != null && !orgs.isEmpty()) {
				         for(Organization org : orgs) {
				             Group orgSite = org.getGroup();
				             int publicPageCount = orgSite.getPublicLayoutsPageCount();
				             int privatePageCount = orgSite.getPrivateLayoutsPageCount();
				             if(publicPageCount > 0) {
				                 path = PUBLIC_PAGE_CONTEXT+ orgSite.getFriendlyURL();
				                 break;
				             } else if(privatePageCount > 0) {
				                 path = PRIVATE_PAGE_CONTEXT + orgSite.getFriendlyURL();
				                 break;
				             }
				         }
				     }
				      
				     //Sites the user has access to     
				     if(Validator.isNull(path)) {
				         List<Group> sites = user.getGroups();
				         if(sites != null && !sites.isEmpty()) {
				             for(Group site : sites) {
				                 int publicPageCount = site.getPublicLayoutsPageCount();
				                 int privatePageCount = site.getPrivateLayoutsPageCount();
				                 if(publicPageCount > 0) {
				                     path = PUBLIC_PAGE_CONTEXT + site.getFriendlyURL();
				                     break;
				                 } else if(privatePageCount > 0) {
				                     path = PRIVATE_PAGE_CONTEXT + site.getFriendlyURL();
				                     break;
				                 }
				             }
				         }
				     }
				      
				     //Default landing page to the main instance site
				     if(Validator.isNull(path)) {
				         path = PrefsPropsUtil.getString(PortalUtil.getCompanyId(request), PropsKeys.DEFAULT_LANDING_PAGE_PATH);
				     }
				     
				     session.setAttribute("LAST_PATH", new LastPath(StringPool.BLANK, path));
					 
				} catch (PortalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	 }
}
	    
	
	
	 