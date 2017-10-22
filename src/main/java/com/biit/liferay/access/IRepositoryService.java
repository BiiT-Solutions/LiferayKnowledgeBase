package com.biit.liferay.access;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.RepositoryNotDeletedException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.model.IRepository;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface IRepositoryService {

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
	 */
	@Deprecated
	void createDLFoldersOfRepository(IRepository<Long> repository, IGroup<Long> company, IGroup<Long> site) throws ClientProtocolException,
			NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError;

}
