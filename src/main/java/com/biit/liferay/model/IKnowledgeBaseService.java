package com.biit.liferay.model;

/*-
 * #%L
 * Access to Liferay Knowledge Base
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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

	IArticle<Long> addArticle(IArticle<Long> article, String siteName, String virtualHost) throws ClientProtocolException, NotConnectedToWebServiceException,
			IOException, AuthenticationRequired, WebServiceAccessError;

	IArticle<Long> addArticle(String portletId, Long parentResourcePrimKey, Long parentResourceClassNameId, String title, String urlTitle, String content,
			String description, String sourceURL, List<String> sections, List<String> selectedFileNames, String siteName, String virtualHost)
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
