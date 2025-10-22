package com.biit.liferay.test;

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

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.biit.liferay.access.CompanyService;
import com.biit.liferay.access.FileEntryService;
import com.biit.liferay.access.FileRepositoryService;
import com.biit.liferay.access.SiteService;
import com.biit.liferay.access.SiteType;
import com.biit.liferay.access.exceptions.DocumentNotDeletedException;
import com.biit.liferay.access.exceptions.DuplicatedFileException;
import com.biit.liferay.access.exceptions.DuplicatedLiferayElement;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.RepositoryNotDeletedException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.model.IFileEntry;
import com.biit.liferay.model.IRepository;
import com.biit.liferay.model.Repository;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.biit.utils.file.FileReader;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.liferay.portal.model.Site;

public class FileEntryTest {
    private static final String LOGIN_USER = "webservices@test.com";
    private static final String LOGIN_PASSWORD = "my-pass";
    private static final String HOST = "testing.test.com";

    private static final String LIFERAY_PROTOCOL = "https";
    private static final int PORT = 443;
    private static final String PROXY_PREFIX = "liferay/";
    private static final String WEBSERVICES_PATH = "api/jsonws/";
    private static final String AUTHENTICATION_TOKEN = "11111111";
    private static final String COMPANY_VIRTUALHOST = "testing.test.com";

    private static final String SITE_NAME = "siteWithFiles";
    private static final String SITE_DESCRIPTION = "This site is created with the automated testing.";
    private static final String SITE_URL = "/file-site";

    private static final String FILE_NAME = "BiiT.png";

    private static final String REPOSITORY_NAME = "The Repository";
    private static final String REPOSITORY_DESCRIPTION = "This is a repository";

    private static final long DEFAULT_FOLDER = 0l;

    private CompanyService companyService = new CompanyService();
    private FileEntryService fileService = new FileEntryService();
    private SiteService siteService = new SiteService();
    private FileRepositoryService repositoryService = new FileRepositoryService();

    private IGroup<Long> company;
    private IFileEntry<Long> addedFile;
    private Site site;
    private IRepository<Long> repository;

    @Test(groups = {"connection"})
    public void authorized() {
        companyService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH, AUTHENTICATION_TOKEN, LOGIN_USER, LOGIN_PASSWORD);
    }

    @Test(groups = {"companyAccess"}, dependsOnGroups = {"connection"})
    public void companyAccess() throws NotConnectedToWebServiceException, IOException, AuthenticationRequired,
            WebServiceAccessError {
        company = companyService.getCompanyByVirtualHost(COMPANY_VIRTUALHOST);
        Assert.assertNotNull(company);
    }

    @Test(groups = {"siteAccess"}, dependsOnGroups = {"connection"})
    public void siteAccess() {
        siteService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH, AUTHENTICATION_TOKEN, LOGIN_USER, LOGIN_PASSWORD);
        Assert.assertFalse(siteService.isNotConnected());
    }

    @Test(groups = {"siteAccess"}, dependsOnMethods = {"siteAccess"})
    public void addSite() throws NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError,
            DuplicatedLiferayElement {
        siteService.addSite(SITE_NAME, SITE_DESCRIPTION, SiteType.DEFAULT_PARENT_GROUP_ID, SITE_URL);
    }

    @Test(groups = {"siteAccess"}, dependsOnMethods = {"addSite"})
    public void getSite() throws NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError {
        site = (Site) siteService.getSiteByFriendlyUrl(company, SITE_URL);
        Assert.assertNotNull(site);
    }

    @Test(groups = {"repository"}, dependsOnGroups = {"companyAccess", "siteAccess"})
    public void connectToRepositoryService() {
        repositoryService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH, AUTHENTICATION_TOKEN, LOGIN_USER, LOGIN_PASSWORD);
        Assert.assertFalse(repositoryService.isNotConnected());
    }

    @Test(groups = {"repository"}, dependsOnMethods = {"connectToRepositoryService"})
    public void addRepository() throws NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError {
        repository = repositoryService.addRespository(site, REPOSITORY_NAME, REPOSITORY_DESCRIPTION);
        Assert.assertNotNull(((Repository) repository).getUuid());
        Assert.assertNotNull(((Repository) repository).getUniqueId());
    }

    @Test(groups = {"repository"}, dependsOnMethods = {"addRepository"})
    public void getRepository() throws NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError {
        IRepository<Long> storedRepository = repositoryService.getRespository(repository.getUniqueId());
        Assert.assertNotNull(storedRepository);
    }

    @Test(groups = {"fileEntry"}, dependsOnGroups = {"repository"})
    public void connectToFileEntryWebservices() {
        fileService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH, AUTHENTICATION_TOKEN, LOGIN_USER, LOGIN_PASSWORD);
        Assert.assertFalse(fileService.isNotConnected());
    }

    @Test(groups = {"fileEntry"}, dependsOnMethods = {"connectToFileEntryWebservices"})
    public void addImage() throws ClientProtocolException, IOException, NotConnectedToWebServiceException, AuthenticationRequired, WebServiceAccessError,
            DuplicatedFileException {
        File fileExample = FileReader.getResource(FILE_NAME);
        // Note, title must be unique. Check file does not exists in Liferay.
        int previousFiles = fileService.getFileEntries(site.getGroupId(), DEFAULT_FOLDER).size();
        addedFile = fileService.addFile(site.getGroupId(), DEFAULT_FOLDER, fileExample.getName(), "image/png", "Logo", "This is the logo of BiiT",
                "First version", fileExample);
        Assert.assertNotNull(addedFile.getUuid());
        Assert.assertEquals(previousFiles + 1, fileService.getFileEntries(site.getGroupId(), DEFAULT_FOLDER).size());
    }

    @Test(groups = {"deleteData"}, dependsOnGroups = {"fileEntry", "repository", "siteAccess"})
    public void deleteImage() throws ClientProtocolException, DocumentNotDeletedException, NotConnectedToWebServiceException, IOException,
            AuthenticationRequired {
        Assert.assertFalse(fileService.isNotConnected());
        int previousFiles = fileService.getFileEntries(site.getGroupId(), DEFAULT_FOLDER).size();
        fileService.deleteFile(addedFile);
        Assert.assertEquals(previousFiles - 1, fileService.getFileEntries(site.getGroupId(), DEFAULT_FOLDER).size());
    }

    @Test(alwaysRun = true, groups = {"deleteData"}, dependsOnGroups = {"fileEntry", "repository", "siteAccess"}, dependsOnMethods = {"deleteImage"})
    public void deleteRepository() throws NotConnectedToWebServiceException, IOException, AuthenticationRequired,
            RepositoryNotDeletedException {
        Assert.assertTrue(repositoryService.deleteRepository(repository));
    }

    @Test(alwaysRun = true, groups = {"deleteData"}, dependsOnGroups = {"fileEntry", "repository", "siteAccess"}, dependsOnMethods = {"deleteRepository"})
    public void deleteSite() throws NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError {
        IGroup<Long> site = siteService.getSite(company, SITE_NAME);
        Assert.assertTrue(siteService.deleteSite(site));
    }

    @AfterTest(groups = {"deleteData", "fileEntry", "repository", "siteAccess", "companyAccess", "connection"})
    public void closeConnections() {
        companyService.disconnect();
        siteService.disconnect();
        repositoryService.disconnect();
        fileService.disconnect();
    }
}
