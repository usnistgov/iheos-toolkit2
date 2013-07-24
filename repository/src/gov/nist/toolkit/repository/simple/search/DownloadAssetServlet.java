package gov.nist.toolkit.repository.simple.search;

import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.simple.SimpleId;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadAssetServlet extends HttpServlet {

	/**
	 * @author Sunil.Bhaskarla 
	 */
	private static final long serialVersionUID = -2233759886953787817L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException  {
		
		String reposId = request.getParameter("reposId");
		String assetId = request.getParameter("assetId");

		if (assetId!=null && reposId!=null) {
			try {
				RepositoryFactory fact = new RepositoryFactory();		
				Repository repos = fact.getRepository(new SimpleId(reposId));
				
				Asset a = repos.getAsset(new SimpleId(assetId));
				if (a!=null) {
					  response.setHeader("Cache-Control", "no-cache");
					  response.setDateHeader("Expires", 0);
					  response.setHeader("Pragma", "no-cache");
					  response.setDateHeader("Max-Age", 0);
					  
					  response.setHeader("Content-Disposition", "attachment;filename=\""+ a.getId().getIdString() + "." + a.getContentExtension()[2] + "\"");
					  if (a.getMimeType()!=null) {
						  response.setContentType(a.getMimeType());
					  } else {
						  response.setContentType("application/xml");
					  }
					  
					  byte[] content = a.getContent();
					  if (content==null) {
						  throw new ServletException("The requested content file does not exist or it could not be loaded.");  
					  }
					  
					  
					  OutputStream os = response.getOutputStream();

					  os.write(content);
					  
					  os.close();

				}				
			} catch (RepositoryException re) {
				throw new ServletException("Error: " + re.toString());
			}
			
			
		
		} else {
			throw new ServletException("Usage: ?reposId=value&assetId=value");			
		}
	}
	
	
}
