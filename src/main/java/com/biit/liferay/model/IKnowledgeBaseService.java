package com.biit.liferay.model;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.biit.liferay.access.LiferayService;
import com.biit.liferay.access.exceptions.ArticleNotDeletedException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;

public interface IKnowledgeBaseService extends LiferayService {

	IArticle<Long> getLatestArticle(long resourcePrimKey) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> getLatestArticle(long resourcePrimKey, int status) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> addArticle(KbArticle article, String siteName, String virtualHost) throws ClientProtocolException, NotConnectedToWebServiceException,
			IOException, AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> addArticle(String portletId, Long parentResourcePrimKey, String title, String content, String description, List<String> sections,
			String dirName, String siteName, String virtualHost) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError;

	void deleteArticle(IArticle<Long> article) throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
			ArticleNotDeletedException;

	void reset();

}
