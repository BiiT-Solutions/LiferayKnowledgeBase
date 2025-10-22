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

import com.biit.liferay.access.*;
import com.biit.liferay.access.exceptions.*;
import com.biit.liferay.model.IFolder;
import com.biit.liferay.model.IRepository;
import com.biit.liferay.model.KbFolder;
import com.biit.liferay.model.Repository;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class FolderTest {
    private static final String LOGIN_USER = "webservices@test.com";
    private static final String LOGIN_PASSWORD = "my-pass";
    private static final String HOST = "testing.test.com";

    private static final String FOLDER_NAME = "TestingFolder";
    private static final String FOLDER_DESCRIPTION = "testing.test.com";

    private static final String LIFERAY_PROTOCOL = "https";
    private static final int PORT = 443;
    private static final String PROXY_PREFIX = "liferay/";
    private static final String WEBSERVICES_PATH = "api/jsonws/";
    private static final String AUTHENTICATION_TOKEN = "11111111";
    private static final String COMPANY_VIRTUALHOST = "testing.test.com";

    private static final String SITE_NAME = "siteWithFolders";
    private static final String SITE_DESCRIPTION = "This site is created with the automated testing.";
    private static final String SITE_URL = "/folder-site";

    private static final String REPOSITORY_DESCRIPTION = "This is one repository";

    private CompanyService companyService = new CompanyService();
    private SiteService siteService = new SiteService();
    private ArticleFolderService knowledgeBaseFolderService = new ArticleFolderService();
    private FileRepositoryService repositoryService = new FileRepositoryService();

    private IGroup<Long> company;
    private IGroup<Long> site;
    private IRepository<Long> repository;
    private IFolder<Long> folder;

    @Test(groups = {"connection"})
    public void authorized() throws NotConnectedToWebServiceException,
            IOException, AuthenticationRequired, WebServiceAccessError {
        companyService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH, AUTHENTICATION_TOKEN,
                LOGIN_USER, LOGIN_PASSWORD);
        companyService.getCompanyByVirtualHost(COMPANY_VIRTUALHOST);
    }

    @Test(groups = {"companyAccess"}, dependsOnGroups = {"connection"})
    public void companyAccess() throws NotConnectedToWebServiceException,
            IOException, AuthenticationRequired, WebServiceAccessError {
        company = companyService.getCompanyByVirtualHost(COMPANY_VIRTUALHOST);
        Assert.assertNotNull(company);
    }

    @Test(groups = {"siteAccess"}, dependsOnGroups = {"connection"})
    public void siteAccess() {
        siteService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH, AUTHENTICATION_TOKEN,
                LOGIN_USER, LOGIN_PASSWORD);
        Assert.assertFalse(siteService.isNotConnected());
    }

    @Test(groups = {"siteAccess"}, dependsOnMethods = {"siteAccess"})
    public void addSite() throws NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError, DuplicatedLiferayElement {
        site = siteService.addSite(SITE_NAME, SITE_DESCRIPTION, SiteType.DEFAULT_PARENT_GROUP_ID, SITE_URL);
        Assert.assertNotNull(site);
    }

    @Test(groups = {"repository"}, dependsOnGroups = {"companyAccess", "siteAccess"})
    public void connectToRepositoryService() {
        repositoryService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH,
                AUTHENTICATION_TOKEN, LOGIN_USER, LOGIN_PASSWORD);
        Assert.assertFalse(repositoryService.isNotConnected());
    }

    @Test(groups = {"repository"}, dependsOnMethods = {"connectToRepositoryService"})
    public void addRepository() throws NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError {
        repository = repositoryService.addRespository(site, FileRepositoryName.SITE.getName(), REPOSITORY_DESCRIPTION);
        Assert.assertNotNull(((Repository) repository).getUuid());
        Assert.assertNotNull((repository).getUniqueId());
    }

    @Test(groups = {"repository"}, dependsOnMethods = {"addRepository"})
    public void getRepository() throws NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError {
        IRepository<Long> storedRepository = repositoryService.getRespository(repository.getUniqueId());
        Assert.assertNotNull(storedRepository);
    }

    @Test(groups = {"articleFolderAccess"}, dependsOnGroups = {"companyAccess", "siteAccess", "repository"})
    public void connectToKnowledgeBaseWebService() {
        knowledgeBaseFolderService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH,
                AUTHENTICATION_TOKEN, LOGIN_USER, LOGIN_PASSWORD);
        Assert.assertFalse(knowledgeBaseFolderService.isNotConnected());
    }

    @Test(groups = {"articleFolderAccess"}, dependsOnMethods = {"connectToKnowledgeBaseWebService"})
    public void addFolder() throws NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError {
        Assert.assertEquals((int) knowledgeBaseFolderService.getFoldersCount(site.getUniqueId(), null, site), 0);
        folder = knowledgeBaseFolderService.addFolder(site.getUniqueId(), Long.valueOf(0), null, FOLDER_NAME,
                FOLDER_DESCRIPTION, site);
        Assert.assertEquals((int) knowledgeBaseFolderService.getFoldersCount(site.getUniqueId(), null, site), 1);
        Assert.assertEquals(folder.getUniqueName(), FOLDER_NAME.toLowerCase() + "_" + folder.getUniqueId());
    }

    @Test(groups = {"articleFolderAccess"}, dependsOnMethods = {"addFolder"})
    public void getFolder() throws NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError {
        Assert.assertEquals((int) knowledgeBaseFolderService.getFoldersCount(null, null, site), 1);
        Assert.assertNotNull(knowledgeBaseFolderService.getFolder(folder.getUniqueId()));
    }

    @Test(groups = {"articleFolderAccess"}, dependsOnMethods = {"addFolder"})
    public void getFolderByUrl() throws NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError {
        Assert.assertEquals((int) knowledgeBaseFolderService.getFoldersCount(null, null, site), 1);
        Assert.assertNotNull(knowledgeBaseFolderService.getFolder(((KbFolder) folder).getUrlTitle(), null, null));
    }

    @Test(alwaysRun = true, groups = {"articleFolderAccess"}, dependsOnMethods = {"getFolder", "getFolderByUrl"})
    public void deleteFolder() throws NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError, FolderNotDeletedException {
        Assert.assertEquals((int) knowledgeBaseFolderService.getFoldersCount(site.getUniqueId(), null, site), 1);
        knowledgeBaseFolderService.deleteFolder(folder);
        Assert.assertEquals((int) knowledgeBaseFolderService.getFoldersCount(site.getUniqueId(), null, site), 0);
    }

    @Test(alwaysRun = true, groups = {"deleteData"}, dependsOnGroups = {"articleFolderAccess",
            "siteAccess"}, dependsOnMethods = {"deleteFolder"})
    public void deleteRepository() throws NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, RepositoryNotDeletedException {
        Assert.assertTrue(repositoryService.deleteRepository(repository));
    }

    @Test(alwaysRun = true, groups = {"deleteData"}, dependsOnGroups = {"articleFolderAccess",
            "siteAccess"}, dependsOnMethods = {"deleteFolder"})
    public void deleteSite() throws NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError {
        IGroup<Long> site = siteService.getSite(company, SITE_NAME);
        Assert.assertTrue(siteService.deleteSite(site));
    }

    @AfterClass
    public void closeConnections() {
        knowledgeBaseFolderService.disconnect();
        companyService.disconnect();
        siteService.disconnect();
    }

}
