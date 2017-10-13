package com.biit.liferay.access;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.biit.liferay.access.LiferayService;
import com.biit.liferay.access.exceptions.ArticleNotDeletedException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.model.IArticle;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;

public interface IKnowledgeBaseService extends LiferayService {

	IArticle<Long> getLatestArticle(long resourcePrimKey) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> getLatestArticle(long resourcePrimKey, int status) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> addArticle(IArticle<Long> article, IGroup<Long> site) throws ClientProtocolException, NotConnectedToWebServiceException,
			IOException, AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> addArticle(String portletId, Long parentResourcePrimKey, Long parentResourceClassNameId, String title, String urlTitle, String content,
			String description, String sourceURL, List<String> sections, List<String> selectedFileNames, IGroup<Long> site)
			throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired, WebServiceAccessError;

	void deleteArticle(IArticle<Long> article) throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
			ArticleNotDeletedException;

	void reset();

	IArticle<Long> editArticle(IArticle<Long> article) throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired,
			WebServiceAccessError;

	IArticle<Long> editArticle(String portletId, IArticle<Long> article) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> createArticle(String title, String content, String description, List<String> sections) throws ClientProtocolException,
			NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError;

}
