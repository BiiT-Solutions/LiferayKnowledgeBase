package com.biit.liferay.access;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.biit.liferay.access.exceptions.ArticleNotDeletedException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.log.LiferayClientLogger;
import com.biit.liferay.model.IArticle;
import com.biit.liferay.model.KbArticle;
import com.biit.usermanager.entity.IElement;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArticleService extends ServiceAccess<IArticle<Long>, KbArticle> implements IArticleService {
	private final static long ARTICLE_PARENT_RESOURCE_PRIMKEY = 0l;
	private final static long ARTICLE_PARENT_RESOURCE_CLASSNAME = 41603l;
	private final static String ARTICLE_PARENT_CLASSNAME = "com.liferay.knowledgebase.model.KBFolder";
	private final static String DIR_NAME = "";

	private SiteService siteService;
	private CompanyService companyService;
	private ClassNameService classNameService;

	public ArticleService() {
		serverConnection();
	}

	@Override
	public void reset() {
		ArticlePool.getInstance().reset();
	}

	@Override
	public void disconnect() {
		super.disconnect();
		siteService.disconnect();
		companyService.disconnect();
		classNameService.disconnect();
	}

	@Override
	public void authorizedServerConnection(String address, String protocol, int port, String webservicesPath, String authenticationToken, String loginUser,
			String password) {
		// Standard behavior.
		super.authorizedServerConnection(address, protocol, port, webservicesPath, authenticationToken, loginUser, password);
		// Disconnect previous connections.
		try {
			siteService.disconnect();
			companyService.disconnect();
			classNameService.disconnect();
		} catch (Exception e) {

		}
		// Sites are needed for some services.
		siteService = new SiteService();
		siteService.authorizedServerConnection(address, protocol, port, webservicesPath, authenticationToken, loginUser, password);
		// Sites are needed for some services.
		companyService = new CompanyService();
		companyService.authorizedServerConnection(address, protocol, port, webservicesPath, authenticationToken, loginUser, password);
		// classNames are needed to add an article.
		classNameService = new ClassNameService();
		classNameService.authorizedServerConnection(address, protocol, port, webservicesPath, authenticationToken, loginUser, password);
	}

	@Override
	public Set<IArticle<Long>> decodeListFromJson(String json, Class<KbArticle> objectClass) throws JsonParseException, JsonMappingException, IOException {
		Set<IArticle<Long>> myObjects = new ObjectMapper().readValue(json, new TypeReference<Set<KbArticle>>() {
		});
		return myObjects;
	}

	/**
	 * Gets latest published articled.
	 * 
	 * @param resourceKey
	 * @return
	 * @throws NotConnectedToWebServiceException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws AuthenticationRequired
	 * @throws WebServiceAccessError
	 */
	@Override
	public IArticle<Long> getLatestArticle(long resourcePrimKey) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError {
		return getLatestArticle(resourcePrimKey, 0);
	}

	/**
	 * Gets an article by its resource key and the status.
	 * 
	 * @param resourceKey
	 * @param status
	 *            0 if published.
	 * @return
	 * @throws NotConnectedToWebServiceException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws AuthenticationRequired
	 * @throws WebServiceAccessError
	 */
	@Override
	public IArticle<Long> getLatestArticle(long resourcePrimKey, int status) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError {

		IArticle<Long> article = ArticlePool.getInstance().getElement(resourcePrimKey);
		if (article != null) {
			return article;
		}
		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("resourcePrimKey", Long.toString(resourcePrimKey)));
		params.add(new BasicNameValuePair("status", Integer.toString(status)));

		String result = getHttpResponse("knowledge-base-portlet.kbarticle/get-latest-kb-article", params);

		LiferayClientLogger.debug(this.getClass().getName(), "Data retrieved: '" + result + "'.");

		if (result != null) {
			// A Simple JSON Response Read
			article = decodeFromJson(result, KbArticle.class);
			ArticlePool.getInstance().addElement(article);
			return article;
		}
		return null;
	}

	@Override
	public IArticle<Long> addArticle(IArticle<Long> article, IGroup<Long> site) throws ClientProtocolException, NotConnectedToWebServiceException, IOException,
			AuthenticationRequired, WebServiceAccessError {
		Long parentResourcePrimKey = 0l;
		Long parentResourceClassId = 0l;
		String urlTitle = "";
		String sourceURL = "";
		List<String> selectedFileNames = null;
		if (article instanceof KbArticle) {
			parentResourcePrimKey = ((KbArticle) article).getParentResourcePrimKey();
			parentResourceClassId = ((KbArticle) article).getParentResourceClassNameId();
			urlTitle = ((KbArticle) article).getUrlTitle();
			sourceURL = ((KbArticle) article).getSourceURL();
			selectedFileNames = ((KbArticle) article).getSelectedFileNames();
		}
		return addArticle(parentResourcePrimKey, parentResourceClassId, article.getTitle(), urlTitle, article.getContent(), article.getDescription(),
				sourceURL, article.getSections(), selectedFileNames, site);
	}

	@Override
	public IArticle<Long> addArticle(IArticle<Long> article, String siteName, String virtualHost) throws ClientProtocolException,
			NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError {
		Long parentResourcePrimKey = 0l;
		Long parentResourceClassId = 0l;
		String urlTitle = "";
		String sourceURL = "";
		List<String> selectedFileNames = null;
		if (article instanceof KbArticle) {
			parentResourcePrimKey = ((KbArticle) article).getParentResourcePrimKey();
			parentResourceClassId = ((KbArticle) article).getParentResourceClassNameId();
			urlTitle = ((KbArticle) article).getUrlTitle();
			sourceURL = ((KbArticle) article).getSourceURL();
			selectedFileNames = ((KbArticle) article).getSelectedFileNames();
		}
		return addArticle(parentResourcePrimKey, parentResourceClassId, article.getTitle(), urlTitle, article.getContent(), article.getDescription(),
				sourceURL, article.getSections(), selectedFileNames, siteName, virtualHost);
	}

	@Override
	public IArticle<Long> addArticle(Long parentResourcePrimKey, Long parentResourceClassNameId, String title, String urlTitle, String content,
			String description, String sourceURL, List<String> sections, List<String> selectedFileNames, String siteName, String virtualHost)
			throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired, WebServiceAccessError {
		checkConnection();

		IGroup<Long> company = companyService.getCompanyByVirtualHost(virtualHost);
		IGroup<Long> site = siteService.getSite(company, siteName);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("portletId", PortletId.KNOWLEDGEBASE_PORTLET.getId()));
		if (parentResourcePrimKey != null) {
			params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(parentResourcePrimKey)));
		} else {
			params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(ARTICLE_PARENT_RESOURCE_PRIMKEY)));
		}
		if (parentResourceClassNameId != null) {
			params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(parentResourceClassNameId)));
		} else {
			// get className id from another webservice.
			IElement<Long> className = classNameService.getClassName(ARTICLE_PARENT_CLASSNAME);
			if (className != null) {
				params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(className.getId())));
			} else {
				params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(ARTICLE_PARENT_RESOURCE_CLASSNAME)));
			}
		}
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("urlTitle", urlTitle));
		params.add(new BasicNameValuePair("content", content));
		params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("sourceURL", sourceURL));
		params.add(new BasicNameValuePair("sections", convertListToJsonString(sections)));
		params.add(new BasicNameValuePair("selectedFileNames", convertListToJsonString(selectedFileNames)));
		params.add(new BasicNameValuePair("serviceContext.scopeGroupId", Long.toString(site.getId())));

		String result = getHttpResponse("knowledge-base-portlet.kbarticle/add-kb-article", params);

		if (result != null) {
			// A Simple JSON Response Read
			IArticle<Long> article = decodeFromJson(result, KbArticle.class);
			ArticlePool.getInstance().addElement(article);
			return article;
		}
		return null;
	}

	@Override
	public IArticle<Long> addArticle(Long parentResourcePrimKey, Long parentResourceClassNameId, String title, String urlTitle, String content,
			String description, String sourceURL, List<String> sections, List<String> selectedFileNames, IGroup<Long> site)
			throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired, WebServiceAccessError {
		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("portletId", PortletId.KNOWLEDGEBASE_PORTLET.getId()));
		if (parentResourcePrimKey != null) {
			params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(parentResourcePrimKey)));
		} else {
			params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(ARTICLE_PARENT_RESOURCE_PRIMKEY)));
		}
		if (parentResourceClassNameId != null) {
			params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(parentResourceClassNameId)));
		} else {
			// get className id from another webservice.
			IElement<Long> className = classNameService.getClassName(ARTICLE_PARENT_CLASSNAME);
			if (className != null) {
				params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(className.getId())));
			} else {
				params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(ARTICLE_PARENT_RESOURCE_CLASSNAME)));
			}
		}
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("urlTitle", urlTitle));
		params.add(new BasicNameValuePair("content", content));
		params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("sourceURL", sourceURL));
		params.add(new BasicNameValuePair("sections", convertListToJsonString(sections)));
		params.add(new BasicNameValuePair("selectedFileNames", convertListToJsonString(selectedFileNames)));
		params.add(new BasicNameValuePair("serviceContext.scopeGroupId", Long.toString(site.getId())));

		String result = getHttpResponse("knowledge-base-portlet.kbarticle/add-kb-article", params);

		if (result != null) {
			// A Simple JSON Response Read
			IArticle<Long> article = decodeFromJson(result, KbArticle.class);
			ArticlePool.getInstance().addElement(article);
			return article;
		}
		return null;
	}

	@Override
	public IArticle<Long> deleteArticle(IArticle<Long> article) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, ArticleNotDeletedException, WebServiceAccessError {
		if (article != null) {
			checkConnection();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("resourcePrimKey", article.getResourcePrimKey() + ""));

			String result = getHttpResponse("knowledge-base-portlet.kbarticle/delete-kb-article", params);
			if (result != null) {
				// A Simple JSON Response Read
				IArticle<Long> articleDeleted = decodeFromJson(result, KbArticle.class);
				ArticlePool.getInstance().addElement(articleDeleted);
				LiferayClientLogger.info(this.getClass().getName(), "Article '" + articleDeleted.getTitle() + "' deleted.");
				return articleDeleted;
			} else {
				throw new ArticleNotDeletedException("Article '" + article.getTitle() + "' (id:" + article.getId() + ") not deleted correctly. ");
			}
		}
		return null;
	}

	@Override
	public IArticle<Long> editArticle(IArticle<Long> article) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError {
		if (article != null) {
			checkConnection();

			List<String> selectedFileNames = null;
			String sourceURL = "";
			if (article instanceof KbArticle) {
				sourceURL = ((KbArticle) article).getSourceURL();
				selectedFileNames = ((KbArticle) article).getSelectedFileNames();
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (article.getResourcePrimKey() != null) {
				params.add(new BasicNameValuePair("resourcePrimKey", Long.toString(article.getResourcePrimKey())));
			} else {
				params.add(new BasicNameValuePair("resourcePrimKey", Long.toString(ARTICLE_PARENT_RESOURCE_PRIMKEY)));
			}
			params.add(new BasicNameValuePair("title", article.getTitle()));
			params.add(new BasicNameValuePair("content", article.getContent()));
			params.add(new BasicNameValuePair("description", article.getDescription()));
			params.add(new BasicNameValuePair("sourceURL", sourceURL));
			params.add(new BasicNameValuePair("sections", convertListToJsonString(article.getSections())));
			params.add(new BasicNameValuePair("selectedFileNames", convertListToJsonString(selectedFileNames)));
			params.add(new BasicNameValuePair("removeFileEntryIds", convertListToJsonString(new ArrayList<String>())));
			params.add(new BasicNameValuePair("dirName", DIR_NAME));

			String result = getHttpResponse("knowledge-base-portlet.kbarticle/update-kb-article", params);

			if (result != null) {
				// A Simple JSON Response Read
				IArticle<Long> articleUpdated = decodeFromJson(result, KbArticle.class);
				ArticlePool.getInstance().addElement(articleUpdated);
				return articleUpdated;
			}
		}
		return null;
	}

	@Override
	public IArticle<Long> createArticle(String title, String content, String description, List<String> sections) throws ClientProtocolException,
			NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError {
		KbArticle article = new KbArticle();
		IElement<Long> className = classNameService.getClassName(ARTICLE_PARENT_CLASSNAME);
		if (className != null) {
			article.setParentResourceClassNameId(className.getId());
			article.setParentResourcePrimKey(0l);
		}
		article.setTitle(title);
		article.setContent(content);
		article.setDescription(description);
		article.setSections(sections);

		return article;
	}

}
