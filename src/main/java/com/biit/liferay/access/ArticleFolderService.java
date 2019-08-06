package com.biit.liferay.access;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.biit.liferay.access.exceptions.FolderNotDeletedException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.log.LiferayClientLogger;
import com.biit.liferay.model.IFolder;
import com.biit.liferay.model.KbFolder;
import com.biit.usermanager.entity.IElement;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Named
public class ArticleFolderService extends ServiceAccess<IFolder<Long>, KbFolder> implements IArticleFolderService {
	private final static long FOLDER_PARENT_RESOURCE_PRIMKEY = 0l;
	private final static long FOLDER_DEFAULT_GROUP = 0l;
	private final static String FOLDER_PARENT_CLASSNAME = "com.liferay.knowledgebase.model.KBFolder";

	private ClassNameService classNameService;

	public ArticleFolderService() {
		super();
		reset();
	}

	@Override
	public Set<IFolder<Long>> decodeListFromJson(String json, Class<KbFolder> objectClass)
			throws JsonParseException, JsonMappingException, IOException {
		Set<IFolder<Long>> myObjects = new ObjectMapper().readValue(json, new TypeReference<Set<KbFolder>>() {
		});
		return myObjects;
	}

	@Override
	public void disconnect() {
		super.disconnect();
		if (classNameService != null) {
			classNameService.disconnect();
		}
	}

	@Override
	public void authorizedServerConnection(String address, String protocol, int port, String webservicesPath,
			String authenticationToken, String loginUser, String password) {
		// Standard behavior.
		super.authorizedServerConnection(address, protocol, port, webservicesPath, authenticationToken, loginUser,
				password);
		// Disconnect previous connections.
		try {
			classNameService.disconnect();
		} catch (Exception e) {

		}
		// classNames are needed to add a folder.
		classNameService = new ClassNameService();
		classNameService.authorizedServerConnection(address, protocol, port, webservicesPath, authenticationToken,
				loginUser, password);
	}

	@Override
	public IFolder<Long> addFolder(Long groupId, Long parentResourcePrimKey, Long parentResourceClassNameId,
			String name, String description, IGroup<Long> site) throws ClientProtocolException,
			NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError {
		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (groupId != null) {
			params.add(new BasicNameValuePair("groupId", Long.toString(groupId)));
		} else {
			params.add(new BasicNameValuePair("groupId", Long.toString(FOLDER_DEFAULT_GROUP)));
		}
		if (parentResourcePrimKey != null) {
			params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(parentResourcePrimKey)));
		} else {
			params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(FOLDER_PARENT_RESOURCE_PRIMKEY)));
		}
		if (parentResourceClassNameId != null) {
			params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(parentResourceClassNameId)));
		} else {
			// get className id from another webservice.
			IElement<Long> className = classNameService.getClassName(FOLDER_PARENT_CLASSNAME);
			if (className != null) {
				params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(className.getUniqueId())));
			} else {
				params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(0)));
			}
		}

		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("serviceContext.scopeGroupId", Long.toString(site.getUniqueId())));

		String result = getHttpResponse("knowledge-base-portlet.kbfolder/add-kb-folder", params);

		if (result != null) {
			// A Simple JSON Response Read
			IFolder<Long> folder = decodeFromJson(result, KbFolder.class);
			FolderPool.getInstance().addElement(folder);
			return folder;
		}
		return null;
	}

	@Override
	public IFolder<Long> getFolder(String urlTitle, Long groupId, Long parentKBFolderId)
			throws JsonParseException, JsonMappingException, IOException, NotConnectedToWebServiceException,
			WebServiceAccessError, AuthenticationRequired {
		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("urlTitle", urlTitle));
		if (groupId != null) {
			params.add(new BasicNameValuePair("groupId", Long.toString(groupId)));
		} else {
			params.add(new BasicNameValuePair("groupId", Long.toString(FOLDER_DEFAULT_GROUP)));
		}
		if (parentKBFolderId != null) {
			params.add(new BasicNameValuePair("parentKBFolderId", Long.toString(parentKBFolderId)));
		} else {
			params.add(new BasicNameValuePair("parentKBFolderId", Long.toString(FOLDER_PARENT_RESOURCE_PRIMKEY)));
		}

		String result = getHttpResponse("knowledge-base-portlet.kbfolder/get-kb-folder-by-url-title", params);

		LiferayClientLogger.debug(this.getClass().getName(), "Data retrieved: '" + result + "'.");

		if (result != null) {
			// A Simple JSON Response Read
			try {
				IFolder<Long> folder = decodeFromJson(result, KbFolder.class);
				FolderPool.getInstance().addElement(folder);
				return folder;
			} catch (WebServiceAccessError e) {
				if (!e.getMessage().startsWith("No KBFolder exists")) {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public IFolder<Long> getFolder(long folderId) throws JsonParseException, JsonMappingException, IOException,
			NotConnectedToWebServiceException, WebServiceAccessError, AuthenticationRequired {
		IFolder<Long> folder = FolderPool.getInstance().getElement(folderId);
		if (folder != null) {
			return folder;
		}

		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("folderId", Long.toString(folderId)));

		String result = getHttpResponse("knowledge-base-portlet.kbfolder/get-kb-folder", params);

		LiferayClientLogger.debug(this.getClass().getName(), "Data retrieved: '" + result + "'.");

		if (result != null) {
			// A Simple JSON Response Read
			folder = decodeFromJson(result, KbFolder.class);
			FolderPool.getInstance().addElement(folder);
			return folder;
		}
		return null;
	}

	@Override
	public boolean deleteFolder(IFolder<Long> folder)
			throws ClientProtocolException, IOException, NotConnectedToWebServiceException, AuthenticationRequired,
			FolderNotDeletedException, WebServiceAccessError {
		if (folder != null) {
			checkConnection();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("kbFolderId", folder.getUniqueId() + ""));

			String result = getHttpResponse("knowledge-base-portlet.kbfolder/delete-kb-folder", params);

			if (result != null) {
				// A Simple JSON Response Read
				IFolder<Long> folderDeleted = decodeFromJson(result, KbFolder.class);

				if (folderDeleted != null) {
					LiferayClientLogger.info(this.getClass().getName(),
							"Folder '" + folder.getUniqueName() + "' deleted.");
					FolderPool.getInstance().removeElement(folder.getUniqueId());
					return true;
				}
			}
			throw new FolderNotDeletedException("Folder '" + folder.getUniqueName() + "' (id:" + folder.getUniqueId()
					+ ") not deleted correctly. ");
		}
		return false;
	}

	@Override
	public Integer getFoldersCount(Long groupId, Long parentKBFolderId, IGroup<Long> site)
			throws ClientProtocolException, IOException, NotConnectedToWebServiceException, AuthenticationRequired {
		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (groupId != null) {
			params.add(new BasicNameValuePair("groupId", Long.toString(groupId)));
		} else {
			params.add(new BasicNameValuePair("groupId", Long.toString(FOLDER_DEFAULT_GROUP)));
		}
		if (parentKBFolderId != null) {
			params.add(new BasicNameValuePair("parentKBFolderId", Long.toString(parentKBFolderId)));
		} else {
			params.add(new BasicNameValuePair("parentKBFolderId", Long.toString(FOLDER_PARENT_RESOURCE_PRIMKEY)));
		}
		params.add(new BasicNameValuePair("serviceContext.scopeGroupId", Long.toString(site.getUniqueId())));

		String result = getHttpResponse("knowledge-base-portlet.kbfolder/get-kb-folders-count", params);

		if (result != null) {
			try {
				return Integer.parseInt(result);
			} catch (NumberFormatException nfe) {
				LiferayClientLogger.errorMessage(this.getClass().getName(), nfe);
			}
		}
		return null;
	}

	@Override
	public void reset() {
		FolderPool.getInstance().reset();
	}

	@Override
	public IElement<Long> getFolderClassName() throws ClientProtocolException, NotConnectedToWebServiceException,
			IOException, AuthenticationRequired, WebServiceAccessError {
		return classNameService.getClassName(KbFolder.FOLDER_LIFERAY_CLASSNAME);
	}
}
