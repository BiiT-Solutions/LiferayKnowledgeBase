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
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KnowledgeBaseService extends ServiceAccess<IArticle<Long>, KbArticle> {
	private final static String PORTLET_ID = "2_WAR_knowledgebaseportlet";
	private SiteService siteService;
	private CompanyService companyService;

	public KnowledgeBaseService() {
		serverConnection();
	}

	public void reset() {
		ArticlePool.getInstance().reset();
	}

	@Override
	public void disconnect() {
		super.disconnect();
		siteService.disconnect();
		companyService.disconnect();
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
		} catch (Exception e) {

		}
		// Sites are needed for some services.
		siteService = new SiteService();
		siteService.authorizedServerConnection(address, protocol, port, webservicesPath, authenticationToken, loginUser, password);
		// Sites are needed for some services.
		companyService = new CompanyService();
		companyService.authorizedServerConnection(address, protocol, port, webservicesPath, authenticationToken, loginUser, password);
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
	public IArticle<Long> getLatestArticle(long resourcePrimKey, int status) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError {

		IArticle<Long> article = ArticlePool.getInstance().getArticleByResourceKey(resourcePrimKey);
		if (article != null) {
			return article;
		}
		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("resourcePrimKey", Long.toString(resourcePrimKey)));
		params.add(new BasicNameValuePair("status", Integer.toString(status)));

		String result = getHttpResponse("knowledge-base-portlet.kbarticle/get-latest-kb-article", params);

		if (result != null) {
			// A Simple JSON Response Read
			article = decodeFromJson(result, KbArticle.class);
			ArticlePool.getInstance().addArticle(article);
			return article;
		}
		return null;
	}

	public IArticle<Long> addArticle(KbArticle article, String siteName, String virtualHost) throws ClientProtocolException, NotConnectedToWebServiceException,
			IOException, AuthenticationRequired, WebServiceAccessError {
		return addArticle(PORTLET_ID, article.getParentResourcePrimKey(), article.getTitle(), article.getContent(), article.getDescription(),
				article.getSections(), "", siteName, virtualHost);
	}

	public IArticle<Long> addArticle(String portletId, Long parentResourcePrimKey, String title, String content, String description, List<String> sections,
			String dirName, String siteName, String virtualHost) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError {
		checkConnection();

		String sectonsAsString = "";
		if (!sections.isEmpty()) {
			sectonsAsString = "[";
		}
		for (String section : sections) {
			if (sectonsAsString.length() > 1) {
				sectonsAsString += ",";
			}
			sectonsAsString += section;
		}
		if (sectonsAsString.length() > 0) {
			sectonsAsString += "]";
		}

		IGroup<Long> company = companyService.getCompanyByVirtualHost(virtualHost);
		IGroup<Long> site = siteService.getSite(company, siteName);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("portletId", portletId));
		if (parentResourcePrimKey != null) {
			params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(parentResourcePrimKey)));
		} else {
			params.add(new BasicNameValuePair("parentResourcePrimKey", ""));
		}
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("content", content));
		params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("sections", sectonsAsString));
		params.add(new BasicNameValuePair("dirName", dirName));
		params.add(new BasicNameValuePair("serviceContext.scopeGroupId", Long.toString(site.getId())));

		String result = getHttpResponse("knowledge-base-portlet.kbarticle/add-kb-article", params);

		if (result != null) {
			// A Simple JSON Response Read
			IArticle<Long> article = decodeFromJson(result, KbArticle.class);
			ArticlePool.getInstance().addArticle(article);
			return article;
		}
		return null;
	}

	public void deleteArticle(IArticle<Long> article) throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
			ArticleNotDeletedException {
		if (article != null) {
			checkConnection();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("resourcePrimKey", article.getResourcePrimKey() + ""));

			String result = getHttpResponse("role/delete-role", params);

			if (result == null || result.length() < 3) {
				ArticlePool.getInstance().removeArticle(article.getId());
				LiferayClientLogger.info(this.getClass().getName(), "Article '" + article.getTitle() + "' deleted.");
			} else {
				throw new ArticleNotDeletedException("Organization '" + article.getTitle() + "' (id:" + article.getId() + ") not deleted correctly. ");
			}

		}
	}

}
