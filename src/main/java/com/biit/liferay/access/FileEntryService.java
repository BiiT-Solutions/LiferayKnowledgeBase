package com.biit.liferay.access;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import com.biit.liferay.access.exceptions.DocumentNotDeletedException;
import com.biit.liferay.access.exceptions.DuplicatedFileException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.configuration.LiferayConfigurationReader;
import com.biit.liferay.log.LiferayClientLogger;
import com.biit.liferay.model.FileEntry;
import com.biit.liferay.model.IFileEntry;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Named
public class FileEntryService extends ServiceAccess<IFileEntry<Long>, FileEntry> implements IFileEntryService {
    private final static String DUPLICATED_FILE = "DuplicateFileException";
    private final static String NOT_FOUND = "404 Not Found";

    @Override
    public Set<IFileEntry<Long>> decodeListFromJson(String json, Class<FileEntry> arg1)
            throws IOException {
        Set<FileEntry> myObjects = new ObjectMapper().readValue(json, new TypeReference<Set<FileEntry>>() {
        });
        return new HashSet<>(myObjects);
    }

    @Override
    public IFileEntry<Long> addFile(long siteGroupId, long folderId, String sourceFileName, String mimeType,
                                    String title, String description, String changeLog, File file)
            throws IOException, NotConnectedToWebServiceException, AuthenticationRequired,
            WebServiceAccessError, DuplicatedFileException {
        checkConnection();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.addPart("repositoryId", new StringBody(Long.toString(siteGroupId), ContentType.TEXT_PLAIN));
        builder.addPart("folderId", new StringBody(Long.toString(folderId), ContentType.TEXT_PLAIN));
        builder.addPart("sourceFileName", new StringBody(sourceFileName, ContentType.TEXT_PLAIN));
        builder.addPart("mimeType", new StringBody(mimeType, ContentType.TEXT_PLAIN));
        builder.addPart("title", new StringBody(title, ContentType.TEXT_PLAIN));
        builder.addPart("description", new StringBody(description, ContentType.TEXT_PLAIN));
        builder.addPart("changeLog", new StringBody(changeLog, ContentType.TEXT_PLAIN));
        builder.addBinaryBody("file", file);

        String result = getHttpPostResponse("dlapp/add-file-entry", builder);

        if (result != null) {
            // A Simple JSON Response Read
            try {
                IFileEntry<Long> fileEntry = decodeFromJson(result, FileEntry.class);
                FileEntryPool.getInstance().addElement(fileEntry);
                return fileEntry;
            } catch (WebServiceAccessError wsae) {
                if (wsae.getMessage().contains(DUPLICATED_FILE)) {
                    throw new DuplicatedFileException("File '" + sourceFileName + "' already exists on this folder.");
                }
                throw wsae;
            } catch (JsonParseException e) {
                if (e.getMessage().contains(NOT_FOUND)) {
                    throw new FileNotFoundException("File '" + sourceFileName + "' not found.");
                }
                throw e;
            }
        }
        return null;
    }

    @Override
    public Set<IFileEntry<Long>> getFileEntries(long siteGroupId, long folderId)
            throws NotConnectedToWebServiceException, IOException, AuthenticationRequired {
        Set<IFileEntry<Long>> files = new HashSet<>();

        // Look up files in the liferay.
        checkConnection();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("repositoryId", Long.toString(siteGroupId)));
        params.add(new BasicNameValuePair("folderId", Long.toString(folderId)));

        String result = getHttpPostResponse("dlapp/get-file-entries", params);
        if (result != null) {
            // A Simple JSON Response Read
            files = decodeListFromJson(result, FileEntry.class);
            LiferayClientLogger.debug(this.getClass().getName(), "Obtained '" + files + "'.");
        }

        return files;
    }

    @Override
    public void deleteFile(IFileEntry<Long> fileEntry) throws DocumentNotDeletedException,
            NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired {
        if (fileEntry != null) {
            checkConnection();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("fileEntryId", fileEntry.getUniqueId() + ""));

            String result = getHttpPostResponse("dlapp/delete-file-entry", params);

            if (result == null || result.length() < 3) {
                FileEntryPool.getInstance().removeElement(fileEntry.getUniqueId());
                LiferayClientLogger.info(this.getClass().getName(),
                        "Document '" + fileEntry.getUniqueName() + "' deleted.");
            } else {
                throw new DocumentNotDeletedException("Document '" + fileEntry.getUniqueName() + "' (id:"
                        + fileEntry.getUniqueId() + ") not deleted correctly. ");
            }
        }
    }

    @Override
    public IFileEntry<Long> geFileEntry(long fileEntryId) throws NotConnectedToWebServiceException,
            ClientProtocolException, IOException, AuthenticationRequired, WebServiceAccessError {

        IFileEntry<Long> fileEntry = FileEntryPool.getInstance().getElement(fileEntryId);
        if (fileEntry != null) {
            return fileEntry;
        }
        checkConnection();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("fileEntryId", Long.toString(fileEntryId)));

        String result = getHttpPostResponse("dlapp/get-file-entry", params);

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
    public String getFileRelativeUrl(long fileEntryId) throws ClientProtocolException,
            NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError {
        IFileEntry<Long> fileEntry = geFileEntry(fileEntryId);
        return getFileRelativeUrl(fileEntry);
    }

    public static String getFileRelativeUrl(IFileEntry<Long> fileEntry) {
        if (fileEntry == null) {
            return "";
        }
        return "/documents/" + fileEntry.getGroupId() + File.separator + fileEntry.getFolderId() + File.separator
                + fileEntry.getTitle() + File.separator + fileEntry.getUuid();
    }

    public static String getFileAbsoluteUrl(IFileEntry<Long> fileEntry) {
        return LiferayConfigurationReader.getInstance().getLiferayProtocol() + "://"
                + LiferayConfigurationReader.getInstance().getHost() + ":"
                + LiferayConfigurationReader.getInstance().getConnectionPort() + "/"
                + LiferayConfigurationReader.getInstance().getVirtualHost() + getFileRelativeUrl(fileEntry);
    }

    public class Message {
        public byte[] value;
    }

    @Override
    public void reset() {
        FileEntryPool.getInstance().reset();
    }

}
