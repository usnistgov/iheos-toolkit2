package gov.nist.toolkit.repository.simple.index.test;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class MockServletContext {
	private static String basePath = "";
	
	
	static public ServletContext getServletContext(String reposPath) {
		basePath = reposPath;
		
		return new ServletContext() {

			
			@Override
			public String getRealPath(String path) {
				return basePath + path;
			}

			@Override
			public ServletContext getContext(String uripath) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getContextPath() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getMajorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getMinorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getMimeType(String file) {
				// TODO Auto-generated method stub
				return null;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public Set getResourcePaths(String path) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public URL getResource(String path) throws MalformedURLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getResourceAsStream(String path) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public RequestDispatcher getRequestDispatcher(String path) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public RequestDispatcher getNamedDispatcher(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Servlet getServlet(String name) throws ServletException {
				// TODO Auto-generated method stub
				return null;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public Enumeration getServlets() {
				// TODO Auto-generated method stub
				return null;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public Enumeration getServletNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void log(String msg) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void log(Exception exception, String msg) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void log(String message, Throwable throwable) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getServerInfo() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getInitParameter(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public Enumeration getInitParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getAttribute(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public Enumeration getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setAttribute(String name, Object object) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removeAttribute(String name) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getServletContextName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			
		};

	}
}
