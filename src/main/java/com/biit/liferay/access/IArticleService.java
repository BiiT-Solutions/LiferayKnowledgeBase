package com.biit.liferay.access;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;

import com.biit.liferay.access.LiferayService;
import com.biit.liferay.access.exceptions.ArticleNotDeletedException;
import com.biit.liferay.access.exceptions.ArticleNotFoundException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.model.IArticle;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;

public interface IArticleService extends LiferayService {

	IArticle<Long> getLatestArticle(long resourcePrimKey)
			throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
			WebServiceAccessError, ArticleNotFoundException;

	IArticle<Long> getLatestArticle(long resourcePrimKey, int status)
			throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
			WebServiceAccessError, ArticleNotFoundException;

	IArticle<Long> addArticle(IArticle<Long> article, IGroup<Long> site) throws ClientProtocolException,
			NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> addArticle(Long parentResourcePrimKey, Long parentResourceClassNameId, String title, String urlTitle,
			String content, String description, String sourceURL, List<String> sections, List<String> selectedFileNames,
			IGroup<Long> site) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> deleteArticle(IArticle<Long> article)
			throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
			ArticleNotDeletedException, WebServiceAccessError;

	void reset();

	IArticle<Long> editArticle(IArticle<Long> article) throws ClientProtocolException,
			NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> createArticle(String title, String content, String description, List<String> sections)
			throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired,
			WebServiceAccessError;

	IArticle<Long> addArticle(IArticle<Long> article, String siteName, String virtualHost)
			throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired,
			WebServiceAccessError;

	IArticle<Long> addArticle(Long parentResourcePrimKey, Long parentResourceClassNameId, String title, String urlTitle,
			String content, String description, String sourceURL, List<String> sections, List<String> selectedFileNames,
			String siteName, String virtualHost) throws NotConnectedToWebServiceException, ClientProtocolException,
			IOException, AuthenticationRequired, WebServiceAccessError;

	Integer getArticlesCount(IGroup<Long> site)
			throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired;

	Set<IArticle<Long>> getArticles(IGroup<Long> site, int start, int end)
			throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired;

	Set<IArticle<Long>> getArticles(IGroup<Long> site)
			throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired;

	void moveArticle(long articleId, Long folderId) throws NotConnectedToWebServiceException, ClientProtocolException,
			IOException, AuthenticationRequired, WebServiceAccessError;

}
