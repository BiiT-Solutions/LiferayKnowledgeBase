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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.RepositoryNotCreatedException;
import com.biit.liferay.access.exceptions.RepositoryNotDeletedException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.log.LiferayClientLogger;
import com.biit.liferay.model.IFolder;
import com.biit.liferay.model.IRepository;
import com.biit.liferay.model.Repository;
import com.biit.usermanager.entity.IElement;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.entity.IUser;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.biit.usermanager.security.exceptions.UserDoesNotExistException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Named
public class FileRepositoryService extends ServiceAccess<IRepository<Long>, Repository>
        implements IFileRepositoryService {
    private final static String RESPOSITORY_CLASSNAME = "com.liferay.portal.repository.liferayrepository.LiferayRepository";
    private final static String DEFAULT_DLFOLDER_DESCRIPTION = "This is a repository folder";

    private ClassNameService classNameService;
    private FolderService folderService;
    private UserService userService;

    @Override
    public void disconnect() {
        super.disconnect();
        if (classNameService != null) {
            classNameService.disconnect();
        }
        if (folderService != null) {
            folderService.disconnect();
        }
        if (userService != null) {
            userService.disconnect();
        }
    }

    @Override
    public void authorizedServerConnection(String address, String protocol, int port, String proxyPrefix, String webservicesPath,
                                           String authenticationToken, String loginUser, String password) {
        // Standard behavior.
        super.authorizedServerConnection(address, protocol, port, proxyPrefix, webservicesPath, authenticationToken, loginUser,
                password);
        // Disconnect previous connections.
        try {
            classNameService.disconnect();
        } catch (Exception e) {

        }
        // classNames are needed to add an article.
        classNameService = new ClassNameService();
        classNameService.authorizedServerConnection(address, protocol, port, proxyPrefix, webservicesPath, authenticationToken,
                loginUser, password);

        // Repository needs some basic folders.
        try {
            folderService.disconnect();
        } catch (Exception e) {

        }
        folderService = new FolderService();
        folderService.authorizedServerConnection(address, protocol, port, proxyPrefix, webservicesPath, authenticationToken,
                loginUser, password);
        // Repository needs some basic folders.
        try {
            userService.disconnect();
        } catch (Exception e) {

        }
        userService = new UserService();
        userService.authorizedServerConnection(address, protocol, port, proxyPrefix, webservicesPath, authenticationToken, loginUser,
                password);
    }

    @Override
    public Set<IRepository<Long>> decodeListFromJson(String json, Class<Repository> objectClass)
            throws JsonParseException, JsonMappingException, IOException {
        Set<Repository> myObjects = new ObjectMapper().readValue(json, new TypeReference<Set<Repository>>() {
        });
        return new HashSet<>(myObjects);
    }

    @Override
    public IRepository<Long> addRespository(IGroup<Long> site, String name, String description)
            throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
            WebServiceAccessError {
        checkConnection();

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // get className id from another webservice.
        IElement<Long> className = classNameService.getClassName(RESPOSITORY_CLASSNAME);
        if (className == null) {
            throw new WebServiceAccessError("Class name '" + RESPOSITORY_CLASSNAME + "' not found!");
        }
        params.add(new BasicNameValuePair("groupId", Long.toString(site.getUniqueId())));
        params.add(new BasicNameValuePair("classNameId", Long.toString(className.getUniqueId())));
        params.add(new BasicNameValuePair("parentFolderId", "0"));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("description", description));
        params.add(new BasicNameValuePair("portletId", PortletId.DOCUMENT_AND_MEDIA.getId()));
        params.add(new BasicNameValuePair("typeSettingsProperties", "{}"));

        String result = getHttpPostResponse("repository/add-repository", params);

        if (result != null) {
            // A Simple JSON Response Read
            IRepository<Long> repository = decodeFromJson(result, Repository.class);
            FileRepositoryPool.getInstance().addElement(repository);
            return repository;
        }
        return null;
    }

    @Override
    @Deprecated
    public void createDLFoldersOfRepository(IRepository<Long> repository, IGroup<Long> company, IGroup<Long> site)
            throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired,
            WebServiceAccessError, RepositoryNotCreatedException {
        // Get user for folders
        IUser<Long> user;
        try {
            user = userService.getUserByEmailAddress(company, userService.getConnectionUser());
            // Create basic site DLFolder.
            IFolder<Long> parentFolder = folderService.getFolder(((Repository) repository).getDlFolderId());

            IFolder<Long> repositoryFolder = folderService.addFolder(site.getUniqueId(), repository.getUniqueId(),
                    false, parentFolder.getUniqueId(), Long.toString(user.getUniqueId()), DEFAULT_DLFOLDER_DESCRIPTION,
                    site);
            if (repositoryFolder == null) {
                throw new RepositoryNotCreatedException("Repository '" + repository.getUniqueName() + "' (id:"
                        + repository.getUniqueId() + ") not created correctly. ");
            }

            // Create adminPortlet Folder.
            IFolder<Long> portletFolder = folderService.addFolder(site.getUniqueId(), repository.getUniqueId(), false,
                    repositoryFolder.getUniqueId(), PortletId.ADMIN_PORTLET.getId(), DEFAULT_DLFOLDER_DESCRIPTION,
                    site);
            if (portletFolder == null) {
                throw new RepositoryNotCreatedException(
                        "Folder for site '" + site.getUniqueName() + "' and repository '" + repository.getUniqueName()
                                + "' (id:" + repository.getUniqueId() + ") not created correctly. ");
            }
        } catch (UserDoesNotExistException e) {
            LiferayClientLogger.debug(this.getClass().getName(),
                    "Cannot conect with user '" + userService.getConnectionUser() + "'.");
        }
    }

    @Override
    public boolean deleteRepository(IRepository<Long> repository) throws NotConnectedToWebServiceException,
            ClientProtocolException, IOException, AuthenticationRequired, RepositoryNotDeletedException {
        if (repository != null) {
            checkConnection();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("repositoryId", repository.getUniqueId() + ""));

            String result = getHttpPostResponse("repository/delete-repository", params);

            if (result == null || result.length() < 3) {
                FileRepositoryPool.getInstance().removeElement(repository.getUniqueId());
                LiferayClientLogger.info(this.getClass().getName(),
                        "Repository '" + repository.getUniqueName() + "' deleted.");
                return true;
            } else {
                throw new RepositoryNotDeletedException("Repository '" + repository.getUniqueName() + "' (id:"
                        + repository.getUniqueId() + ") not deleted correctly. ");
            }

        }
        return false;
    }

    @Override
    public IRepository<Long> getRespository(long repositoryId) throws JsonParseException, JsonMappingException,
            IOException, NotConnectedToWebServiceException, WebServiceAccessError, AuthenticationRequired {
        IRepository<Long> repository = FileRepositoryPool.getInstance().getElement(repositoryId);
        if (repository != null) {
            return repository;
        }
        checkConnection();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("repositoryId", Long.toString(repositoryId)));

        String result = getHttpPostResponse("repository/get-repository", params);

        LiferayClientLogger.debug(this.getClass().getName(), "Data retrieved: '" + result + "'.");

        if (result != null) {
            // A Simple JSON Response Read
            repository = decodeFromJson(result, Repository.class);
            FileRepositoryPool.getInstance().addElement(repository);
            return repository;
        }
        return null;
    }

    @Override
    public void reset() {
        FileRepositoryPool.getInstance().reset();
    }

}
