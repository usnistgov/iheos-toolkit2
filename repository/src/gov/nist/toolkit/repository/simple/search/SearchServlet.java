package gov.nist.toolkit.repository.simple.search;

import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.simple.SimpleId;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria.Criteria;
import gov.nist.toolkit.repository.simple.search.client.SearchTerm;
import gov.nist.toolkit.repository.simple.search.client.SearchTerm.Operator;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet {

	/**
	 * @author Sunil.Bhaskarla
	 * 
	 * Simple Asset Report
	 * 	Supports multiple levels of hierarchy
	 */
	
	// TODO: setup Installation in case Gwt onLoad hasn't started yet
	//
	private static final long serialVersionUID = 8326366092753151300L;
	private static int reportType = 2; 
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		 /*		
		  * 
		http://127.0.0.1:8888/repository/search?reposId=ee332a45-4c5f-4762-a62d-c6f7e217e93a&assetId=172-7ce2-4c5b-b994-a0123&level=2
		  *
		  */
		 
		String reposId = request.getParameter("reposId");
		String assetId = request.getParameter("assetId");
		String levelStr = request.getParameter("level"); // 1, 2, or 3
		String reportTypeStr = request.getParameter("reportType"); // 1 or 2
		
		if (reportTypeStr!=null) {
			int rpValue = Integer.parseInt(reportTypeStr);
			if (rpValue>0 && rpValue <3) {
				reportType = rpValue;
			}
		}
		
		/**
		 *  
		levels=n

		which indicates the depth of the display.  
		levels=1 would show the asset requested.  
		levels=2 would show the the asset requested and its immediate children.  
		levels=3 would include the grandchildren.  The default value should be levels=1.  if levels=0 is given, interpret it as levels=1

		(Bill)

		 */
		 
		if (assetId!=null && reposId!=null) {
			
			int level = 0;
			if (levelStr!=null && !"".equals(levelStr)) {
				level = Integer.parseInt(levelStr);
				if (level<0 || level>3)
					level = 0;
			}

			String result = getAsset(reposId, assetId, level, level);
			response.getWriter().write(printReport(result));
		 
		} else {
			throw new ServletException("Usage: ?reposId=value&assetId=value&[level=<1,2,3>]&[reportType=<1,2>]");			
		}
		
	}
	
	private String printReport(String rpt) {
		// Exclude this wrapper if snippet is requested 
		
		return "<html><body style='font-family:arial,verdana,sans-serif;'>" + rpt + "</body></html>";
	}

	private String getAsset(String reposId, String assetId, int topLevel, int level) {
		StringBuffer sb = new StringBuffer();
		
		try {
			SearchCriteria criteria = new SearchCriteria(Criteria.AND);
			
			String nest = (topLevel==level)?"id":"parentId";
			criteria.append(new SearchTerm(nest,Operator.EQUALTO,assetId));
			
			RepositoryFactory fact = new RepositoryFactory();		
			Repository repos1 = fact.getRepository(new SimpleId(reposId)); 
			
			AssetIterator iter = new SearchResultIterator(new Repository[]{repos1}, criteria );
			
			if (iter.hasNextAsset()) {
				reportBeginHeader(topLevel, level, sb); // ul
				
			} 
			
			
			int rowCt=0;
			while (iter.hasNextAsset()) {
				int levelCt = topLevel;
				Asset a = iter.nextAsset();
				

				reportBeginAddDetail(sb,a);			// li
				
				rowCt = reportAddDetail(sb, rowCt, a);
				
				while (--levelCt>0) {
					String child = getAsset(reposId,a.getId().getIdString(),topLevel,levelCt);		// ul
					
					if (child!=null && !"".equals(child)) {
						reportAddChild(sb, child);
					}					
				}
				
				reportEndAddDetail(sb);			// li

				
			}
			
			/*
			 * ul
			 * 	li parent
			 * 		ul li child
			 * 		li ul
			 * 	li
			 * ul
			 */
			
			/*
			 * 
			 * <html><body><ul><li>172-7ce2-4c5b-b994-a0123 - This is my document
			 * 							<ul><li>172-7ce2-4c5b-b994-a0125 - This is my document</li>
			 * 										<li>172-7ce2-4c5b-b994-a0124 - This is my document</li>
			 * 									</ul>
			 * 							</li></ul></body></html>
			 */
			
			if (rowCt>0) {

				reportEndHeader(sb);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return sb.toString();

	}

	/**
	 * @param sb
	 */
	protected void reportEndHeader(StringBuffer sb) {
		if (reportType==1) {
			sb.append("</table>");
		} else {
			sb.append("</ul>");
		}
	}

	/**
	 * @param sb
	 * @param child
	 */
	protected void reportAddChild(StringBuffer sb, String child) {
		if ("".equals(child)) return;
		
		if (reportType==1) {
			sb.append("<tr><td align='right' colspan=4>" + child + "</td><tr>");
		} else {
			sb.append( child );
		}
	}
	
	protected void reportBeginAddDetail(StringBuffer sb, Asset a) throws RepositoryException {
		if (reportType==2) {
			
			
			sb.append("<li title='"); 
			
			sb.append(  
					 "Type: " + a.getAssetType().getKeyword() 
					+ "&nbsp;Created Date: " + a.getCreatedDate()
					+((a.getExpirationDate()!=null)
					 ?"&nbsp;Expiration Date: " + a.getExpirationDate():"")
					+ "&nbsp;Mime Type: " + a.getMimeType());
			
			sb.append("'>");
		}
	}
	protected void reportEndAddDetail(StringBuffer sb) {
		if (reportType==2) {
			sb.append("</li>");
		}
	}

	/**
	 * @param sb
	 * @param rowCt
	 * @param a
	 * @return
	 * @throws RepositoryException
	 */
	protected int reportAddDetail(StringBuffer sb, int rowCt, Asset a)
			throws RepositoryException {
		rowCt++;
		
		if (reportType==1) {
		sb.append(
				"<tr bgcolor='" + ((rowCt%2 == 0)?"#E6E6FA":"")   + "'><td>"	+ a.getId().toString() + "</td>"
				+ "<td>"	+ a.getAssetType().getKeyword() + "</td>"
				+ "<td>"	+ a.getCreatedDate() + "</td>"
				+ "<td>"	+ a.getMimeType() + "</td>"
				+"</tr>"
				);
		} else {
			sb.append( a.getId().toString() + " - " + a.getDescription());
			if (a.getMimeType()!=null) {
				sb.append("&nbsp;<font family='arial,verdana,sans-serif' size='2'><sup><a href='" + this.getServletContext().getContextPath() + "downloadAsset?reposId=" + a.getRepository().getIdString() + "&assetId=" + a.getId().getIdString() + "'>" 
				+ a.getMimeType()
				+ "</a></sup></font>");
			}
		}
		return rowCt;
	}

	/**
	 * @param topLevel
	 * @param level
	 * @param sb
	 */
	protected void reportBeginHeader(int topLevel, int level, StringBuffer sb) {
		
		if (reportType==1) {
			sb.append("<table "+ ((topLevel!=level)? "style='margin-left:"+ 25*(topLevel-level) + "px'":"")  +">");
			sb.append("<tr bgcolor='#d2b48c'><td>"+ ((topLevel!=level)?"Child ":"")   +" Asset Id</td><td>Type</td><td>Created Date</td><td>Mime Type</td></tr>");			
		} else {
			sb.append("<ul>");
		}
		
	}
	
	private void print(String s) {
		System.out.println(s);
		
//		System.out.println("found asset: " +  a.getId().toString() + ", of type: "+ a.getAssetType().getKeyword() +", in repos:" + a.getRepository().getIdString() );
//		System.out.println("life: " + a.getAssetType().getLifetime());

	}

}
