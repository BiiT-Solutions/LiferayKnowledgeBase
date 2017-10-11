package com.biit.liferay.access;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.log.LiferayClientLogger;
import com.biit.liferay.model.FileEntry;
import com.biit.liferay.model.IFileEntry;
import com.biit.liferay.model.IFileService;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileEntryService extends ServiceAccess<IFileEntry<Long>, FileEntry> implements IFileService {
	private SiteService siteService;
	private CompanyService companyService;

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
	public void disconnect() {
		super.disconnect();
		siteService.disconnect();
		companyService.disconnect();
	}

	@Override
	public Set<IFileEntry<Long>> decodeListFromJson(String json, Class<FileEntry> arg1) throws JsonParseException, JsonMappingException, IOException {
		Set<IFileEntry<Long>> myObjects = new ObjectMapper().readValue(json, new TypeReference<Set<FileEntry>>() {
		});
		return myObjects;
	}

	@Override
	public IFileEntry<Long> addFile(long repositoryId, long folderId, String sourceFileName, String mimeType, String title, String description,
			String changeLog, File file, String siteName, String virtualHost) throws ClientProtocolException, IOException, NotConnectedToWebServiceException,
			AuthenticationRequired, WebServiceAccessError {
		checkConnection();

		IGroup<Long> company = companyService.getCompanyByVirtualHost(virtualHost);
		IGroup<Long> site = siteService.getSite(company, siteName);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		builder.addPart("repositoryId", new StringBody(Long.toString(repositoryId), ContentType.TEXT_PLAIN));
		builder.addPart("folderId", new StringBody(Long.toString(folderId), ContentType.TEXT_PLAIN));
		builder.addPart("sourceFileName", new StringBody(sourceFileName, ContentType.TEXT_PLAIN));
		builder.addPart("mimeType", new StringBody(mimeType, ContentType.TEXT_PLAIN));
		builder.addPart("title", new StringBody(title, ContentType.TEXT_PLAIN));
		builder.addPart("description", new StringBody(description, ContentType.TEXT_PLAIN));
		builder.addPart("changeLog", new StringBody(changeLog, ContentType.TEXT_PLAIN));
		builder.addBinaryBody("file", file);

		String result = getHttpResponse("dlapp/add-file-entry", builder);

		if (result != null) {
			// A Simple JSON Response Read
			IFileEntry<Long> fileEntry = decodeFromJson(result, FileEntry.class);
			FileEntryPool.getInstance().addElement(fileEntry);
			return fileEntry;
		}
		return null;
	}

	@Override
	public IFileEntry<Long> geFileDefinition(long fileEntryId) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired, WebServiceAccessError {

		IFileEntry<Long> fileEntry = FileEntryPool.getInstance().getElement(fileEntryId);
		if (fileEntry != null) {
			return fileEntry;
		}
		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("fileEntryId", Long.toString(fileEntryId)));

		String result = getHttpResponse("dlapp/get-file-entry", params);

		LiferayClientLogger.debug(this.getClass().getName(), "Data retrieved: '" + result + "'.");

		if (result != null) {
			// A Simple JSON Response Read
			fileEntry = decodeFromJson(result, FileEntry.class);
			FileEntryPool.getInstance().addElement(fileEntry);
			return fileEntry;
		}
		return null;
	}

	@Override
	public String getFileRelativeUrl(long fileEntryId) throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired,
			WebServiceAccessError {
		IFileEntry<Long> fileEntry = geFileDefinition(fileEntryId);
		if (fileEntry == null) {
			return "";
		}
		return "/documents/" + fileEntry.getGroupId() + File.separator + fileEntry.getFolderId() + File.separator + fileEntry.getTitle() + File.separator
				+ fileEntry.getUuid();
	}

	public class Message {
		public byte[] value;
	}

}
