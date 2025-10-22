package com.biit.liferay.access;

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

import org.apache.http.client.ClientProtocolException;

import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.RepositoryNotCreatedException;
import com.biit.liferay.access.exceptions.RepositoryNotDeletedException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.model.IRepository;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface IFileRepositoryService {

	IRepository<Long> addRespository(IGroup<Long> site, String name, String description) throws NotConnectedToWebServiceException, ClientProtocolException,
			IOException, AuthenticationRequired, WebServiceAccessError;

	IRepository<Long> getRespository(long repositoryId) throws JsonParseException, JsonMappingException, IOException, NotConnectedToWebServiceException,
			WebServiceAccessError, AuthenticationRequired;

	boolean deleteRepository(IRepository<Long> repository) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, RepositoryNotDeletedException;

	/**
	 * A repository needs some default folders to allow the upload.
	 *
	 * @param repository
	 * @param company
	 * @param site
	 * @throws ClientProtocolException
	 * @throws NotConnectedToWebServiceException
	 * @throws IOException
	 * @throws AuthenticationRequired
	 * @throws WebServiceAccessError
	 * @throws RepositoryNotCreatedException 
	 */
	@Deprecated
	void createDLFoldersOfRepository(IRepository<Long> repository, IGroup<Long> company, IGroup<Long> site) throws ClientProtocolException,
			NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError, RepositoryNotCreatedException;

}
