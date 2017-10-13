package com.biit.liferay.access;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.RepositoryNotDeletedException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.log.LiferayClientLogger;
import com.biit.liferay.model.IRepository;
import com.biit.liferay.model.Repository;
import com.biit.usermanager.entity.IElement;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RepositoryService extends ServiceAccess<IRepository<Long>, Repository> implements IRepositoryService {
	private final static String RESPOSITORY_CLASSNAME = "com.liferay.portal.repository.liferayrepository.LiferayRepository";

	private ClassNameService classNameService;

	@Override
	public void disconnect() {
		super.disconnect();
		classNameService.disconnect();
	}

	@Override
	public void authorizedServerConnection(String address, String protocol, int port, String webservicesPath, String authenticationToken, String loginUser,
			String password) {
		// Standard behavior.
		super.authorizedServerConnection(address, protocol, port, webservicesPath, authenticationToken, loginUser, password);
		// Disconnect previous connections.
		try {
			classNameService.disconnect();
		} catch (Exception e) {

		}
		// classNames are needed to add an article.
		classNameService = new ClassNameService();
		classNameService.authorizedServerConnection(address, protocol, port, webservicesPath, authenticationToken, loginUser, password);
	}

	@Override
	public Set<IRepository<Long>> decodeListFromJson(String json, Class<Repository> objectClass) throws JsonParseException, JsonMappingException, IOException {
		Set<IRepository<Long>> myObjects = new ObjectMapper().readValue(json, new TypeReference<Set<Repository>>() {
		});
		return myObjects;
	}

	@Override
	public IRepository<Long> addRespository(IGroup<Long> site, String name, String description) throws NotConnectedToWebServiceException,
			ClientProtocolException, IOException, AuthenticationRequired, WebServiceAccessError {
		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// get className id from another webservice.
		IElement<Long> className = classNameService.getClassName(RESPOSITORY_CLASSNAME);
		if (className == null) {
			throw new WebServiceAccessError("Class name '" + RESPOSITORY_CLASSNAME + "' not found!");
		}
		params.add(new BasicNameValuePair("groupId", Long.toString(site.getId())));
		params.add(new BasicNameValuePair("classNameId", Long.toString(className.getId())));
		params.add(new BasicNameValuePair("parentFolderId", "1"));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("portletId", PortletId.DOCUMENT_AND_MEDIA.getId()));
		params.add(new BasicNameValuePair("typeSettingsProperties", "{}"));

		String result = getHttpResponse("repository/add-repository", params);

		if (result != null) {
			// A Simple JSON Response Read
			IRepository<Long> repository = decodeFromJson(result, Repository.class);
			RepositoryPool.getInstance().addElement(repository);
			return repository;
		}
		return null;

	}

	@Override
	public boolean deleteRepository(IRepository<Long> repository) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, RepositoryNotDeletedException {
		if (repository != null) {
			checkConnection();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("repositoryId", repository.getId() + ""));

			String result = getHttpResponse("repository/delete-repository", params);

			if (result == null || result.length() < 3) {
				RepositoryPool.getInstance().removeElement(repository.getId());
				LiferayClientLogger.info(this.getClass().getName(), "Repository '" + repository.getUniqueName() + "' deleted.");
				return true;
			} else {
				throw new RepositoryNotDeletedException("Repository '" + repository.getUniqueName() + "' (id:" + repository.getId()
						+ ") not deleted correctly. ");
			}

		}
		return false;
	}

	@Override
	public IRepository<Long> getRespository(long repositoryId) throws JsonParseException, JsonMappingException, IOException, NotConnectedToWebServiceException,
			WebServiceAccessError, AuthenticationRequired {
		IRepository<Long> repository = RepositoryPool.getInstance().getElement(repositoryId);
		if (repository != null) {
			return repository;
		}
		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("repositoryId", Long.toString(repositoryId)));

		String result = getHttpResponse("repository/get-repository", params);

		LiferayClientLogger.debug(this.getClass().getName(), "Data retrieved: '" + result + "'.");

		if (result != null) {
			// A Simple JSON Response Read
			repository = decodeFromJson(result, Repository.class);
			RepositoryPool.getInstance().addElement(repository);
			return repository;
		}
		return null;
	}

}
