package gov.nist.toolkit.repository.simple.search;

import static org.junit.Assert.fail;
import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.simple.SimpleId;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria;
import gov.nist.toolkit.repository.simple.search.client.SearchTerm;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria.Criteria;
import gov.nist.toolkit.repository.simple.search.client.SearchTerm.Operator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet {

	/**
	 * 
	 */
	
	// TODO: setup Installation in case Gwt onLoad hasn't started yet
	//
	private static final long serialVersionUID = 8326366092753151300L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		 /*		
		http://127.0.0.1:8888/search/search?repositoryId=ee332a45-4c5f-4762-a62d-c6f7e217e93a&assetId=172-7ce2-4c5b-b994-a0123&level=2
		  *
		  */
		 
		String reposId = request.getParameter("repositoryId");
		String assetId = request.getParameter("assetId");
		String levelStr = request.getParameter("level"); // 1, 2, or 3
		
		/**
		 *  
		levels=n

		which indicates the depth of the display.  
		levels=1 would show the asset requested.  
		levels=2 would show the the asset requested and its immediate children.  
		levels=3 would include the grandchildren.  The default value should be levels=1.  if levels=0 is given, interpret it as levels=1


		 */
		 
		if (assetId!=null && reposId!=null) {
			
			int level = 0;
			if (levelStr!=null && !"".equals(levelStr)) {
				level = Integer.parseInt(levelStr);
				if (level<0 || level>3)
					level = 0;
			}
				
			
			String result = getAsset(reposId, assetId, level, level);
			response.getWriter().write(result);
			
		 
		}
		
		
		
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
				sb.append("<table "+ ((topLevel!=level)? "style='margin-left:"+ 25*(topLevel-level) + "px'":"")  +">");
				sb.append("<tr bgcolor='#d2b48c'><td>"+ ((topLevel!=level)?"Child ":"")   +" Asset Id</td><td>Type</td><td>Created Date</td><td>Mime Type</td></tr>");
			}
			
			int rowCt=0;
			while (iter.hasNextAsset()) {
				int levelCt = topLevel;
				Asset a = iter.nextAsset();
				
				sb.append(
						"<tr bgcolor='" + ((++rowCt%2 == 0)?"#E6E6FA":"")   + "'><td>"	+ a.getId().toString() + "</td>"
						+ "<td>"	+ a.getAssetType().getKeyword() + "</td>"
						+ "<td>"	+ a.getCreatedDate() + "</td>"
						+ "<td>"	+ a.getMimeType() + "</td>"
						+"</tr>"
						);
				
				while (--levelCt>0) {
					String child = getAsset(reposId,a.getId().getIdString(),topLevel,levelCt);
					
					if (child!=null && !"".equals(child)) {
						sb.append("<tr><td align='right' colspan=4>" + child + "</td><tr>");
					}					
				}
				
			}
			
			if (rowCt>0) {
				sb.append("</table>");
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return sb.toString();

	}
	
	private void print(String s) {
		System.out.println(s);
		
//		System.out.println("found asset: " +  a.getId().toString() + ", of type: "+ a.getAssetType().getKeyword() +", in repos:" + a.getRepository().getIdString() );
//		System.out.println("life: " + a.getAssetType().getLifetime());

	}

}
